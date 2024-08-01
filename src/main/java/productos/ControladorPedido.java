package productos;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/pedidos")
public class ControladorPedido extends ControladorBase {

    private ClienteService clienteService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.clienteService = new ClienteService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query;
        String idParam = request.getParameter("id");
        String estadoParam = request.getParameter("estado");

        // Construir la consulta SQL de manera dinámica
        if (idParam != null) {
            query = "SELECT p.*, c.nombre AS nombre_cliente FROM pedidos p JOIN clientes c ON p.id_cliente = c.id_cliente WHERE p.id_pedido = ?";
        } else if (estadoParam != null) {
            query = "SELECT p.*, c.nombre AS nombre_cliente FROM pedidos p JOIN clientes c ON p.id_cliente = c.id_cliente WHERE p.estado = ?";
        } else {
            query = "SELECT p.*, c.nombre AS nombre_cliente FROM pedidos p JOIN clientes c ON p.id_cliente = c.id_cliente";
        }

        try (Connection conn = obtenerConexion();
                PreparedStatement statement = conn.prepareStatement(query)) {

            if (idParam != null) {
                statement.setLong(1, Long.parseLong(idParam));
            } else if (estadoParam != null) {
                statement.setString(1, estadoParam);
            }

            ResultSet resultSet = statement.executeQuery();

            List<Pedido> pedidos = new ArrayList<>();

            while (resultSet.next()) {
                Date sqlFechaRecibido = resultSet.getDate("fecha_recibido");
                LocalDate fechaRecibido = sqlFechaRecibido != null ? sqlFechaRecibido.toLocalDate() : null;

                Date sqlFechaFinalizado = resultSet.getDate("fecha_finalizado");
                LocalDate fechaFinalizado = sqlFechaFinalizado != null ? sqlFechaFinalizado.toLocalDate() : null;

                Pedido pedido = new Pedido(
                        resultSet.getLong("id_pedido"),
                        resultSet.getLong("id_cliente"),
                        resultSet.getString("nombre_cliente"),
                        fechaRecibido,
                        fechaFinalizado,
                        resultSet.getString("estado"),
                        resultSet.getString("descripcion"));
                pedidos.add(pedido);
            }

            ObjectMapper mapper = JsonConfig.createObjectMapper();
            String json = mapper.writeValueAsString(pedidos);

            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Error al obtener los pedidos.\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);

        ObjectMapper mapper = JsonConfig.createObjectMapper();
        Pedido pedido = mapper.readValue(request.getInputStream(), Pedido.class);

        // Verificar si el cliente existe antes de realizar la operación
        if (!clienteService.clienteExiste(pedido.getIdCliente())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"El ID de cliente no existe.\"}");
            return;
        }

        // Verificar que la fecha de recibido no sea mayor que la fecha de finalizado
        if (pedido.getFechaRecibido() != null && pedido.getFechaFinalizado() != null &&
                pedido.getFechaRecibido().isAfter(pedido.getFechaFinalizado())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter()
                    .write("{\"message\": \"La fecha de recibido no puede ser mayor que la fecha de finalizado.\"}");
            return;
        }

        String query = "INSERT INTO pedidos (id_cliente, fecha_recibido, fecha_finalizado, estado, descripcion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = obtenerConexion();
                PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, pedido.getIdCliente());

            if (pedido.getFechaRecibido() != null) {
                statement.setDate(2, Date.valueOf(pedido.getFechaRecibido()));
            } else {
                statement.setNull(2, java.sql.Types.DATE);
            }

            if (pedido.getFechaFinalizado() != null) {
                statement.setDate(3, Date.valueOf(pedido.getFechaFinalizado()));
            } else {
                statement.setNull(3, java.sql.Types.DATE);
            }

            statement.setString(4, pedido.getEstado());
            statement.setString(5, pedido.getDescripcion());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Long idPedido = rs.getLong(1);

                    // Crear una respuesta JSON simple con solo el ID generado
                    String jsonResponse = String.format("{\"id\": %d}", idPedido);
                    response.setContentType("application/json");
                    response.getWriter().write(jsonResponse);
                } else {
                    throw new SQLException("Error al crear el pedido, ningún ID obtenido");
                }
            }

            response.setStatus(HttpServletResponse.SC_CREATED);

        } catch (SQLException e) {
            manejarError(response, e);
        } catch (IOException e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);

        ObjectMapper mapper = JsonConfig.createObjectMapper();
        Pedido pedidoActualizado = mapper.readValue(request.getInputStream(), Pedido.class);

        // Verificar si el cliente existe antes de realizar la operación
        if (!clienteService.clienteExiste(pedidoActualizado.getIdCliente())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"El ID de cliente no existe.\"}");
            return;
        }

        // Verificar que la fecha de recibido no sea mayor que la fecha de finalizado
        if (pedidoActualizado.getFechaRecibido() != null && pedidoActualizado.getFechaFinalizado() != null &&
                pedidoActualizado.getFechaRecibido().isAfter(pedidoActualizado.getFechaFinalizado())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter()
                    .write("{\"message\": \"La fecha de recibido no puede ser mayor que la fecha de finalizado.\"}");
            return;
        }

        String query = "UPDATE pedidos SET id_cliente = ?, fecha_recibido = ?, fecha_finalizado = ?, estado = ?, descripcion = ? WHERE id_pedido = ?";

        try (Connection conn = obtenerConexion();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, pedidoActualizado.getIdCliente());

            if (pedidoActualizado.getFechaRecibido() != null) {
                statement.setDate(2, Date.valueOf(pedidoActualizado.getFechaRecibido()));
            } else {
                statement.setNull(2, java.sql.Types.DATE);
            }

            if (pedidoActualizado.getFechaFinalizado() != null) {
                statement.setDate(3, Date.valueOf(pedidoActualizado.getFechaFinalizado()));
            } else {
                statement.setNull(3, java.sql.Types.DATE);
            }

            statement.setString(4, pedidoActualizado.getEstado());
            statement.setString(5, pedidoActualizado.getDescripcion());
            statement.setLong(6, pedidoActualizado.getIdPedido());

            int filasActualizadas = statement.executeUpdate();

            if (filasActualizadas > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Pedido actualizado correctamente.\"}");
            } else {
                throw new SQLException("Error al actualizar el pedido, ninguna fila afectada");
            }

        } catch (SQLException e) {
            manejarError(response, e);
        } catch (IOException e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"ID de pedido no proporcionado.\"}");
            return;
        }

        String query = "DELETE FROM pedidos WHERE id_pedido = ?";

        try (Connection conn = obtenerConexion();
                PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, Long.parseLong(idParam));
            int filasEliminadas = statement.executeUpdate();

            if (filasEliminadas > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Pedido eliminado correctamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\": \"El ID de pedido no existe.\"}");
            }

        } catch (SQLException e) {
            manejarError(response, e);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"ID de pedido inválido.\"}");
        } catch (IOException e) {
            manejarError(response, e);
        }
    }

}
