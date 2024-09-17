package umg.edu.gt.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import umg.edu.gt.DTO.DatosLogginDTO;

public class ConsultaLogginDAO {
    ConexionDAO con = new ConexionDAO();

    public DatosLogginDTO validarLogin(DatosLogginDTO login) {
        DatosLogginDTO usuario = null;
        Connection conexion = null;
        PreparedStatement pst = null;
        ResultSet r = null;

        try {
            // Paso 1: Establecer la conexión
            conexion = con.conexionMysql();
            
            // Paso 2: Crear la consulta SQL
            String query = "SELECT * FROM usuarios WHERE username = ?";
            pst = conexion.prepareStatement(query);
            pst.setString(1, login.getUsername());
            
            // Paso 3: Ejecutar la consulta y obtener el resultado
            r = pst.executeQuery();

            if (r.next()) {
                // Paso 4: Obtener la contraseña encriptada desde la base de datos
                String storedPasswordHash = r.getString("password");

                // Paso 5: Comparar la contraseña proporcionada con la encriptada almacenada usando BCrypt
                if (BCrypt.checkpw(login.getPassword(), storedPasswordHash)) {
                    usuario = new DatosLogginDTO();
                    usuario.setUsername(r.getString("username"));
                    // No es necesario devolver la contraseña
                }
            }
        } catch (Exception ex) {
            System.out.println("Error al realizar la consulta: " + ex.getMessage());
        } finally {
            // Cerrar ResultSet
            if (r != null) {
                try {
                    r.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar ResultSet: " + e.getMessage());
                }
            }

            // Cerrar PreparedStatement
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar PreparedStatement: " + e.getMessage());
                }
            }

            // Cerrar la conexión
            if (conexion != null) {
                con.cerrarConexion(conexion);
            }
        }
        return usuario;
    }
    
    
    
    
}
