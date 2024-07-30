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
            query = "SELECT * FROM productos WHERE id_producto = ? AND listado = ?";
        } else if (listadoParam != null) {
            // Si no se proporciona idParam, se usa el valor de listadoParam si está presente
            query = "SELECT * FROM productos WHERE listado = ?";
        } else {
            query = "SELECT * FROM productos"; // Consulta por defecto si no se proporciona listadoParam
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
                        resultSet.getLong("medida_busto"),
                        resultSet.getLong("medida_cintura"),
                        resultSet.getLong("medida_cadera"),
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

        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO productos (cliente, nombre_producto, id_categoria, medida_busto, medida_cintura, medida_cadera, precio_molde_base,  precio_molde_digital, precio_molde_cartulina,cantidad_talles, listado) VALUES (?, ?, ?, ?, ?, ?,?,?,?,?,?)", 
                Statement.RETURN_GENERATED_KEYS)) {

            ObjectMapper mapper = new ObjectMapper();
            ProductoAdmin producto = mapper.readValue(request.getInputStream(), ProductoAdmin.class);

            statement.setString(1, producto.getCliente());
            statement.setString(2, producto.getNombre());
            statement.setLong(3, producto.getIdCategoria());
            statement.setLong(4, producto.getMedidaBusto());
            statement.setLong(5, producto.getMedidaCintura());
            statement.setLong(6, producto.getMedidaCadera());
            statement.setDouble(7, producto.getPrecioMoldeBase());
            statement.setDouble(8, producto.getPrecioMoldeDigital());
            statement.setDouble(9, producto.getPrecioMoldeCartulina());
            statement.setLong(10, producto.getCantidadTalles());
            statement.setInt(11, producto.getListado());
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
            manejarError(response, e);
        } catch (IOException e) {
            manejarError(response, e);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);
        String query = "UPDATE productos SET cliente = ?, nombre_producto = ?, id_categoria = ?, medida_busto = ?,medida_cintura = ?,medida_cadera = ?, precio_molde_base = ?, precio_molde_digital = ?, precio_molde_cartulina = ?, cantidad_talles = ?, listado = ? WHERE id_producto = ?";
        try(Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            ObjectMapper mapper = new ObjectMapper();  // Crear un objeto ObjectMapper para convertir JSON a objetos Java
            ProductoAdmin producto = mapper.readValue(request.getInputStream(), ProductoAdmin.class);  // Convertir el JSON de la solicitud a un objeto Pelicula


            // Establecer los parámetros de la consulta de actualización
            statement.setString(1, producto.getCliente());
            statement.setString(2, producto.getNombre());
            statement.setLong(3, producto.getIdCategoria());
            statement.setLong(4, producto.getMedidaBusto());
            statement.setLong(5, producto.getMedidaCintura());
            statement.setLong(6, producto.getMedidaCadera());
            statement.setDouble(7, producto.getPrecioMoldeBase());
            statement.setDouble(8, producto.getPrecioMoldeDigital());
            statement.setDouble(9, producto.getPrecioMoldeCartulina());
            statement.setLong(10, producto.getCantidadTalles());
            statement.setInt(11, producto.getListado());
            statement.setLong(12, producto.getId());

            // Ejecutar la consulta de actualización
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Producto actualizado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Producto no encontrado.\"}");
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
        String query = "DELETE FROM productos WHERE id_producto = ?";

        try (Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            String idParam = request.getParameter("id");  // Obtener el parámetro de consulta 'id'
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
                response.getWriter().write("{\"message\": \"ID de producto no proporcionado.\"}");
                return;
            }

            int idProducto = Integer.parseInt(idParam);

            // Establecer los parámetros de la consulta de eliminación
            statement.setInt(1, idProducto);

            // Ejecutar la consulta de eliminación
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK); // Configurar el código de estado de la respuesta HTTP como 200 (OK)
                response.getWriter().write("{\"message\": \"Producto eliminado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); // Configurar el código de estado de la respuesta HTTP como 404 (NOT FOUND)
                response.getWriter().write("{\"message\": \"Producto no encontrado.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con la base de datos
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL SERVER ERROR)
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Imprimir el error en caso de problemas con el formato del número
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
            response.getWriter().write("{\"message\": \"ID de producto inválido.\"}");
        }
    }
        
}
