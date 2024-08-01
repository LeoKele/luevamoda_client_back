package productos;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/talles")
public class ControladorTalle extends ControladorBase {
    private ProductoService productoService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productoService = new ProductoService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        String query;
        String idParam = request.getParameter("id");

        if (idParam != null) {
            query = "SELECT * FROM talles_productos WHERE id = ?";
        } else {
            query = "SELECT * FROM talles_productos ORDER BY id_producto, talle";
        }

        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {

            if (idParam != null) {
                statement.setLong(1, Long.parseLong(idParam));
            }
            ResultSet resultSet = statement.executeQuery();
            List<TalleConId> talles = new ArrayList<>();

            while (resultSet.next()) {
                TalleConId talle = new TalleConId(
                        resultSet.getLong("id"),
                        resultSet.getLong("id_producto"),
                        resultSet.getString("talle"),
                        resultSet.getDouble("medida_busto"),
                        resultSet.getDouble("medida_cintura"),
                        resultSet.getDouble("medida_cadera")
                );
                talles.add(talle);
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(talles);

            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        ObjectMapper mapper = new ObjectMapper();
        TalleConId talle = mapper.readValue(request.getInputStream(), TalleConId.class);

        // Verificar si el producto existe antes de insertar en stock
        if (!productoService.productoExiste(talle.getIdProducto())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"El ID de producto no existe.\"}");
            return;
        }

        String query = "INSERT INTO talles_productos (id_producto, talle, medida_busto, medida_cintura, medida_cadera) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(
                query, 
                Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, talle.getIdProducto()); 
            statement.setString(2, talle.getTalle());
            statement.setDouble(3, talle.getMedidaBusto());
            statement.setDouble(4, talle.getMedidaCintura());
            statement.setDouble(5, talle.getMedidaCadera());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Long idTalle = rs.getLong(1);

                    response.setContentType("application/json");
                    String json = mapper.writeValueAsString(idTalle);
                    response.getWriter().write(json);
                } else {
                    throw new SQLException("Error al crear talle, ningún ID fue obtenido.");
                }
            }
            response.setStatus(HttpServletResponse.SC_CREATED);

        } catch (SQLException e) {
            String mensajeError = e.getMessage();
            if (e.getSQLState().startsWith("23")) { 
                response.setStatus(HttpServletResponse.SC_CONFLICT); 
                response.getWriter().write("{\"message\": \"Error: Ya existe una combinación de id_producto y talle.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
                response.getWriter().write("{\"message\": \"" + mensajeError + "\"}");
            }
        } catch (IOException e) {
            String mensajeError = "Error de entrada/salida: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
            response.getWriter().write("{\"message\": \"" + mensajeError + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);
        String query = "UPDATE talles_productos SET id_producto = ?, talle = ?, medida_busto = ?, medida_cintura = ?, medida_cadera = ? WHERE id = ?";

        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {

            ObjectMapper mapper = new ObjectMapper();
            TalleConId talle = mapper.readValue(request.getInputStream(), TalleConId.class);


            // Verificar si el nuevo idProducto existe
            if (!productoService.productoExiste(talle.getIdProducto())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"El ID de producto no existe.\"}");
                return;
            }

            statement.setLong(1, talle.getIdProducto());
            statement.setString(2, talle.getTalle());
            statement.setDouble(3, talle.getMedidaBusto());
            statement.setDouble(4, talle.getMedidaCintura());
            statement.setDouble(5, talle.getMedidaCadera());
            statement.setLong(6, talle.getId()); 

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK); 
                response.getWriter().write("{\"message\": \"Talle actualizado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); 
                response.getWriter().write("{\"message\": \"Talle no encontrado.\"}");
            }
        } catch (SQLException e) {
            String mensajeError = e.getMessage();
            if (e.getSQLState().startsWith("23")) { 
                response.setStatus(HttpServletResponse.SC_CONFLICT); 
                response.getWriter().write("{\"message\": \"Error: Ya existe una combinación de id_producto y talle.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
                response.getWriter().write("{\"message\": \"" + mensajeError + "\"}");
            }
        } catch (IOException e) {
            String mensajeError = "Error de entrada/salida: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
            response.getWriter().write("{\"message\": \"" + mensajeError + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);
        String query = "DELETE FROM talles_productos WHERE id = ?";

        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {

            String idParam = request.getParameter("id");  
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
                response.getWriter().write("{\"message\": \"ID de talle no proporcionado.\"}");
                return;
            }

            long idTalle = Long.parseLong(idParam);

            statement.setLong(1, idTalle);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK); 
                response.getWriter().write("{\"message\": \"Talle eliminado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); 
                response.getWriter().write("{\"message\": \"Talle no encontrado.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
        } catch (NumberFormatException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            response.getWriter().write("{\"message\": \"ID de talle inválido.\"}");
        }
    }

    
}
