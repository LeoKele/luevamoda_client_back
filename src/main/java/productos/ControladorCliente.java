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
public class ControladorCliente extends ControladorBase{

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        String query;
        String idParam = request.getParameter("id");

        if (idParam != null){
            query = "SELECT * FROM clientes WHERE id_cliente = ?";
        } else {
            query = "SELECT * FROM clientes";
        }

        //Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {

            if (idParam != null){
                statement.setLong(1, Long.parseLong(idParam));
            }

            ResultSet resultSet = statement.executeQuery();
            List<Cliente> clientes = new ArrayList<>();

            while (resultSet.next()) {
                Cliente cliente = new Cliente(
                        resultSet.getLong("id_cliente"),
                        resultSet.getString("nombre"),
                        resultSet.getString("telefono"),
                        resultSet.getString("mail")
                );
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
            manejarError(response, e);
        } catch (IOException e) {
            manejarError(response, e);
        }
    }
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);
        String query = "UPDATE clientes SET nombre = ?, telefono = ?, mail = ? WHERE id_cliente = ?";
        try(Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            ObjectMapper mapper = new ObjectMapper();  // Crear un objeto ObjectMapper para convertir JSON a objetos Java
            Cliente cliente = mapper.readValue(request.getInputStream(), Cliente.class);  // Convertir el JSON de la solicitud a un objeto Pelicula


            // Establecer los parámetros de la consulta de actualización
            statement.setString(1, cliente.getNombre());
            statement.setString(2, cliente.getTelefono());
            statement.setString(3, cliente.getMail());
            statement.setLong(4, cliente.getId());

            // Ejecutar la consulta de actualización
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Cliente actualizado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Cliente no encontrado.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con la base de datos
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
        } catch (IOException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas de entrada/salida
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Configurar cabeceras CORS
        configurarCORS(response);
        String query = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            String idParam = request.getParameter("id");  // Obtener el parámetro de consulta 'id'
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
                response.getWriter().write("{\"message\": \"ID de cliente no proporcionado.\"}");
                return;
            }

            int idCategoria = Integer.parseInt(idParam);

            // Establecer los parámetros de la consulta de eliminación
            statement.setInt(1, idCategoria);

            // Ejecutar la consulta de eliminación
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Cliente eliminado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Cliente no encontrado.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con la base de datos
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con el formato del número
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
            response.getWriter().write("{\"message\": \"ID de cliente inválido.\"}");
        }
    }
}
