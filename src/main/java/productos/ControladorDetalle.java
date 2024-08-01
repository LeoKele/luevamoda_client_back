package productos;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//Lo necesitamos para que nos devuelva el JSON que utilizamos en la página web, en el index.html
@WebServlet("/productos/detalle")
public class ControladorDetalle extends ControladorBase {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);

        String query = "SELECT p.id_producto, p.nombre_producto, p.id_categoria, p.listado, COUNT(tp.id) AS cantidad_talles, GROUP_CONCAT(CONCAT(tp.talle, ':', tp.medida_busto, ':', tp.medida_cintura, ':', tp.medida_cadera) SEPARATOR '|') AS talles_info, COALESCE(i.imagenes, '') AS imagenes FROM productos p LEFT JOIN talles_productos tp ON p.id_producto = tp.id_producto LEFT JOIN (SELECT id_producto, GROUP_CONCAT(img_path ORDER BY img_path SEPARATOR ',') AS imagenes FROM imagenes_productos GROUP BY id_producto) i ON p.id_producto = i.id_producto WHERE p.listado = 1 GROUP BY p.id_producto, p.nombre_producto, p.id_categoria, p.listado;";

        // Try-with-resources para cerrar correctamente la conexion
        try (Connection conn = obtenerConexion();
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {

            List<ProductoDetalle> productos = new ArrayList<>();
            while (resultSet.next()) {
                Long id = resultSet.getLong("id_producto");
                String nombre = resultSet.getString("nombre_producto");
                Long idCategoria = resultSet.getLong("id_categoria");
                String listado = resultSet.getString("listado");
                Long cantidadTalles = resultSet.getLong("cantidad_talles");
                String imagenes = resultSet.getString("imagenes");

                String tallesInfo = resultSet.getString("talles_info");
                List<Talle> talles = new ArrayList<>();
                if (tallesInfo != null && !tallesInfo.isEmpty()) {
                    String[] tallesArray = tallesInfo.split("\\|");
                    for (String talleStr : tallesArray) {
                        String[] talleData = talleStr.split(":");
                        String talle = talleData[0];
                        double medidaBusto = Double.parseDouble(talleData[1]);
                        double medidaCintura = Double.parseDouble(talleData[2]);
                        double medidaCadera = Double.parseDouble(talleData[3]);
                        talles.add(new Talle(talle, medidaBusto, medidaCintura, medidaCadera));
                    }
                }

                ProductoDetalle productoDetalle = new ProductoDetalle(id, nombre, idCategoria, listado, cantidadTalles, talles, imagenes);
                productos.add(productoDetalle);
            }

            // Convertir a JSON y enviar la respuesta
            ObjectMapper mapper = JsonConfig.createObjectMapper();
            String json = mapper.writeValueAsString(productos);
            response.setContentType("application/json");
            response.getWriter().write(json);

        } catch (Exception e) {
            manejarError(response, e);
        }
    }

}
