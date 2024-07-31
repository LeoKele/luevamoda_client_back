package productos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteService {

    public boolean clienteExiste(long idCliente) {
        String query = "SELECT 1 FROM clientes WHERE id_cliente = ?";

        try (Connection conn = obtenerConexion();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setLong(1, idCliente);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Connection obtenerConexion() {
        // Tu lógica para obtener la conexión a la base de datos
        Conexion conexion = new Conexion();
        return conexion.getConnection();
    }
}
