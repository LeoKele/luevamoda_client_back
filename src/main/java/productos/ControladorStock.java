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




@WebServlet("/stock")
public class ControladorStock extends ControladorBase{

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

        if (idParam != null){
            query = "SELECT s.id_stock, s.id_producto,  p.cliente, p.nombre_producto, s.cantidad FROM stock s INNER JOIN productos p ON s.id_producto = p.id_producto WHERE id_stock = ? ORDER BY s.id_producto ASC";
        } else {
            query = "SELECT s.id_stock, s.id_producto,  p.cliente, p.nombre_producto, s.cantidad FROM stock s INNER JOIN productos p ON s.id_producto = p.id_producto ORDER BY s.id_producto ASC";
        }

        //Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
            
            if (idParam != null){
                statement.setLong(1, Long.parseLong(idParam));
            }
            ResultSet resultSet = statement.executeQuery();
            List<Stock> stocks = new ArrayList<>();

            while (resultSet.next()) {
                Stock stock = new Stock(
                        resultSet.getLong("id_stock"),
                        resultSet.getLong("id_producto"),
                        resultSet.getString("cliente"),
                        resultSet.getString("nombre_producto"),
                        resultSet.getLong("cantidad")
                );
                stocks.add(stock);
            }

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(stocks);

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
        Stock stock = mapper.readValue(request.getInputStream(), Stock.class);
    
        // Verificar si el producto existe antes de insertar en stock
        if (!productoService.productoExiste(stock.getIdProducto())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\": \"El ID de producto no existe.\"}");
            return;
        }
        
        String query = "INSERT INTO stock (id_producto, cantidad) VALUES (?, ?)";
    
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
    
            statement.setLong(1, stock.getIdProducto());
            statement.setLong(2, stock.getCantidad());
            statement.executeUpdate();
    
            // Obtener el id generado automáticamente
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long idStock = generatedKeys.getLong(1);
                    // Crear una respuesta JSON simple con solo el ID generado
                    String jsonResponse = String.format("{\"id\": %d}", idStock);
                    response.setContentType("application/json");
                    response.getWriter().write(jsonResponse);
                } else {
                    throw new SQLException("Creating stock failed, no ID obtained.");
                }
            }
    
            response.setStatus(HttpServletResponse.SC_CREATED);
    
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (errorCode == 1062) { // Error de clave única específica de MySQL
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"Ya existe un registro con este ID de producto.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Error de entrada/salida: " + e.getMessage() + "\"}");
        }
    }
    
    
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        configurarCORS(response);
        String query = "UPDATE stock SET id_producto = ?, cantidad = ? WHERE id_stock = ?";
        
        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {
    
            ObjectMapper mapper = new ObjectMapper();
            Stock stock = mapper.readValue(request.getInputStream(), Stock.class);
    
            // Verificar si el producto existe antes de actualizar el stock
            if (!productoService.productoExiste(stock.getIdProducto())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"El ID de producto no existe.\"}");
                return;
            }
    
            statement.setLong(1, stock.getIdProducto());
            statement.setLong(2, stock.getCantidad());
            statement.setLong(3, stock.getId());
    
            int rowsUpdated = statement.executeUpdate();
    
            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Cantidad/ID Producto actualizado exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\": \"Stock no encontrado.\"}");
            }
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            if (errorCode == 1062) { // Error de clave única específica de MySQL
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\": \"Ya existe un registro con este ID de producto.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"message\": \"" + e.getMessage() + "\"}");
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"Error de entrada/salida: " + e.getMessage() + "\"}");
        }
    }
    
      
    
    
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Configurar cabeceras CORS
        configurarCORS(response);
        String query = "DELETE FROM stock WHERE id_stock = ?";

        try (Connection conn = obtenerConexion();
        PreparedStatement statement = conn.prepareStatement(query)) {

            String idParam = request.getParameter("id");  // Obtener el parámetro de consulta 'id'
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Configurar el código de estado de la respuesta HTTP como 400 (BAD REQUEST)
                response.getWriter().write("{\"message\": \"ID de stock no proporcionado.\"}");
                return;
            }

            int idStock = Integer.parseInt(idParam);

            // Establecer los parámetros de la consulta de eliminación
            statement.setInt(1, idStock);

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
            response.getWriter().write("{\"message\": \"ID de stock inválido.\"}");
        }
    }
      
}
