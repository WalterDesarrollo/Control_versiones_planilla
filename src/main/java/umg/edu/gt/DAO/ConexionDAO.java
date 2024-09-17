package umg.edu.gt.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author X
 */
public class ConexionDAO {
    
    private String url = "jdbc:mysql://localhost:3306/planilla"; // URL de la base de datos
    private String usuario = "root";
    private String contraseña = "";

    // Método para obtener la conexión
    public Connection conexionMysql() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conexion = DriverManager.getConnection(url, usuario, contraseña);
        System.out.println("La conexión es exitosa: " + conexion);
        return conexion;
    }

    // Método para cerrar la conexión
    public void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    // Getters y setters de los atributos de la conexión
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
}