package productos;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/clientes")
public class ControladorCliente extends ControladorBase {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);

        String query;
        String idParam = request.getParameter("id");

        if (idParam != null) {
            query = "SELECT * FROM clientes WHERE id_cliente = ?";
        } else {
            query = "SELECT * FROM clientes";
        }

        // Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
                PreparedStatement statement = conn.prepareStatement(query)) {

            if (idParam != null) {
                statement.setLong(1, Long.parseLong(idParam));
            }

            ResultSet resultSet = statement.executeQuery();
            List<Cliente> clientes = new ArrayList<>();

            while (resultSet.next()) {
                Cliente cliente = new Cliente(
                        resultSet.getLong("id_cliente"),
                        resultSet.getString("nombre"),
                        resultSet.getString("telefono"),
                        resultSet.getString("mail"));
                clientes.add(cliente);
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(clientes);

            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);
        String query = "INSERT INTO clientes (nombre, telefono, mail) VALUES (?, ?, ?)";

        try (Connection conn = obtenerConexion();
                PreparedStatement statement = conn.prepareStatement(
                        query,
                        Statement.RETURN_GENERATED_KEYS)) {

            ObjectMapper mapper = new ObjectMapper();
            Cliente cliente = mapper.readValue(request.getInputStream(), Cliente.class);

            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getTelefono());
            statement.setString(3, cliente.getMail());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Long idCliente = rs.getLong(1);

                    response.setContentType("application/json");
                    String json = mapper.writeValueAsString(idCliente);
                    response.getWriter().write(json);
                }
            }

            response.setStatus(HttpServletResponse.SC_CREATED);

        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getSQLState().startsWith("23")) { // Código de estado SQL para violaciones de restricción
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
                response.getWriter().write("{\"message\": \"Error: El nombre del cliente ya existe.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
                response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);
        String query = "UPDATE clientes SET nombre = ?, telefono = ?, mail = ? WHERE id_cliente = ?";
        try (Connection conn = obtenerConexion();
                PreparedStatement statement = conn.prepareStatement(query)) {

            ObjectMapper mapper = new ObjectMapper();
            Cliente cliente = mapper.readValue(request.getInputStream(), Cliente.class);

            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getTelefono());
            statement.setString(3, cliente.getMail());
            statement.setLong(4, cliente.getId());

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Cliente actualizado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\": \"Cliente no encontrado.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getSQLState().startsWith("23")) { // Código de estado SQL para violaciones de restricción
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
                response.getWriter().write("{\"message\": \"Error: El nombre del cliente ya existe.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
                response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);
        String query = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection conn = obtenerConexion();
                PreparedStatement statement = conn.prepareStatement(query)) {

            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                response.getWriter().write("{\"message\": \"ID de cliente no proporcionado.\"}");
                return;
            }

            int idCliente = Integer.parseInt(idParam);

            statement.setInt(1, idCliente);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Cliente eliminado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\": \"Cliente no encontrado.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getSQLState().startsWith("23000")) { // Código de estado SQL para violaciones de restricción de clave
                                                       // externa
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
                response.getWriter().write(
                        "{\"message\": \"Error: El cliente está relacionado con otros registros y no puede ser eliminado.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
                response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            response.getWriter().write("{\"message\": \"ID de cliente inválido.\"}");
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

}
