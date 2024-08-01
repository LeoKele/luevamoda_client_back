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

@WebServlet("/productos/admin")
public class ControladorAdmin extends ControladorBase {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);

        String query;
        String idParam = request.getParameter("id");
        String listadoParam = request.getParameter("listado");
        //String tituloParam = request.getParameter("titulo") //titulo like %tituloParam%

        // Construir la consulta SQL de manera dinámica
        if (idParam != null) {
            // Si se proporciona idParam, se incluye en la consulta
            query = "SELECT p.*, COUNT(tp.id) AS cantidad_talles FROM productos p LEFT JOIN talles_productos tp ON p.id_producto = tp.id_producto WHERE p.id_producto = ? AND p.listado = ? GROUP BY p.id_producto ";
        } else if (listadoParam != null) {
            // Si no se proporciona idParam, se usa el valor de listadoParam si está presente
            query = "SELECT p.*, COUNT(tp.id) AS cantidad_talles FROM productos p LEFT JOIN talles_productos tp ON p.id_producto = tp.id_producto WHERE listado = ? GROUP BY p.id_producto ";
        } else {
            query = "SELECT p.*, COUNT(tp.id) AS cantidad_talles FROM productos p LEFT JOIN talles_productos tp ON p.id_producto = tp.id_producto GROUP BY p.id_producto"; // Consulta por defecto si no se proporciona listadoParam
        }

        //Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
            PreparedStatement statement = conn.prepareStatement(query)) {
            
            // Establecer los parámetros de la consulta según el caso
            int paramIndex = 1;
            if (idParam != null) {
                statement.setLong(paramIndex++, Long.parseLong(idParam));
                statement.setInt(paramIndex, Integer.parseInt(listadoParam != null ? listadoParam : "1"));
            } else if (listadoParam != null) {
                statement.setInt(paramIndex, Integer.parseInt(listadoParam));
            }

            ResultSet resultSet = statement.executeQuery();
            
            List<ProductoAdmin> productos = new ArrayList<>();

            while (resultSet.next()) {
                ProductoAdmin producto = new ProductoAdmin(
                        resultSet.getLong("id_producto"),
                        resultSet.getString("cliente"),
                        resultSet.getString("nombre_producto"),
                        resultSet.getLong("id_categoria"),
                        resultSet.getDouble("precio_molde_base"),
                        resultSet.getDouble("precio_molde_digital"),
                        resultSet.getDouble("precio_molde_cartulina"),
                        resultSet.getLong("cantidad_talles"),
                        resultSet.getInt("listado")
                );
                productos.add(producto);
            }
            

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(productos);

            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            manejarError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);
    
        String query = "INSERT INTO productos (cliente, nombre_producto, id_categoria, precio_molde_base, precio_molde_digital, precio_molde_cartulina, listado) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
    
            ObjectMapper mapper = new ObjectMapper();
            ProductoAdmin producto = mapper.readValue(request.getInputStream(), ProductoAdmin.class);
    
            statement.setString(1, producto.getCliente());
            statement.setString(2, producto.getNombre());
            statement.setLong(3, producto.getIdCategoria());
            statement.setDouble(4, producto.getPrecioMoldeBase());
            statement.setDouble(5, producto.getPrecioMoldeDigital());
            statement.setDouble(6, producto.getPrecioMoldeCartulina());
            statement.setInt(7, producto.getListado());
            statement.executeUpdate();
    
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    Long idProducto = rs.getLong(1);
    
                    response.setContentType("application/json");
                    String json = mapper.writeValueAsString(idProducto);
                    response.getWriter().write(json);
                }
            }
    
            response.setStatus(HttpServletResponse.SC_CREATED);
    
        } catch (SQLException e) {
            String mensajeError = e.getMessage();
            int errorCode = e.getErrorCode();
    
            if (errorCode == 1452) { // Error de clave foránea específica de MySQL
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"Error: ID de categoría inexistente.\"}");
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
    
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);
        String query = "UPDATE productos SET cliente = ?, nombre_producto = ?, id_categoria = ?, precio_molde_base = ?, precio_molde_digital = ?, precio_molde_cartulina = ?, listado = ? WHERE id_producto = ?";
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
    
            ObjectMapper mapper = new ObjectMapper();
            ProductoAdmin producto = mapper.readValue(request.getInputStream(), ProductoAdmin.class);
    
            // Establecer los parámetros de la consulta de actualización
            statement.setString(1, producto.getCliente());
            statement.setString(2, producto.getNombre());
            statement.setLong(3, producto.getIdCategoria());
            statement.setDouble(4, producto.getPrecioMoldeBase());
            statement.setDouble(5, producto.getPrecioMoldeDigital());
            statement.setDouble(6, producto.getPrecioMoldeCartulina());
            statement.setInt(7, producto.getListado());
            statement.setLong(8, producto.getId());
    
            // Ejecutar la consulta de actualización
            int rowsUpdated = statement.executeUpdate();
    
            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Producto actualizado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\": \"Producto no encontrado.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            int errorCode = e.getErrorCode();
    
            if (errorCode == 1452) { // Error de clave foránea específica de MySQL
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"Error: ID de categoría inexistente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }
    
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);
        String query = "DELETE FROM productos WHERE id_producto = ?";
    
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
    
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"ID de producto no proporcionado.\"}");
                return;
            }
    
            int idProducto = Integer.parseInt(idParam);
    
            // Establecer los parámetros de la consulta de eliminación
            statement.setInt(1, idProducto);
    
            // Ejecutar la consulta de eliminación
            int rowsDeleted = statement.executeUpdate();
    
            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Producto eliminado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\": \"Producto no encontrado.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            int errorCode = e.getErrorCode();
    
            if (errorCode == 1451) { // Error de clave foránea específica de MySQL
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"message\": \"Error: El producto está en uso y no puede ser eliminado.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"ID de producto inválido.\"}");
        } catch (IOException e) {
            String mensajeError = "Error de entrada/salida: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"" + mensajeError + "\"}");
        }
    }
    
        
}
