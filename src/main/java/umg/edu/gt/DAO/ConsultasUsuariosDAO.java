package umg.edu.gt.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import umg.edu.gt.DTO.DatosUsuarios;
import org.mindrot.jbcrypt.BCrypt;

public class ConsultasUsuariosDAO {
    
    // Es mejor hacer transient si algún día se usa en un ManagedBean (no necesita ser serializable)
    private transient ConexionDAO con = new ConexionDAO();

    // Método para obtener todos los usuarios
    public List<DatosUsuarios> findAllUsuarios() throws Exception {
        List<DatosUsuarios> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        
        try (Connection conexion = con.conexionMysql();
             PreparedStatement pst = conexion.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                DatosUsuarios usuario = new DatosUsuarios();
                usuario.setIdUsuario((int) rs.getLong("id_usuario"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPassword(rs.getString("password")); // La contraseña se guarda encriptada
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setEmail(rs.getString("email"));
                usuario.setFechaRegistro(rs.getTimestamp("fecha_registro"));
                lista.add(usuario);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar usuarios: " + e.getMessage());
        }
        return lista;
    }

    // Método para insertar un nuevo usuario
    public void insertUsuario(Connection conexion, DatosUsuarios usuario) {
        if (usuario != null && validarDatosUsuario(usuario)) {
            String sql = "INSERT INTO usuarios (username, password, nombre, apellido, email, fecha_registro) VALUES (?, ?, ?, ?, ?, NOW())";
            
            try (PreparedStatement pst = conexion.prepareStatement(sql)) {
                pst.setString(1, usuario.getUsername());
                // Encriptar la contraseña antes de guardarla
                String hashedPassword = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
                pst.setString(2, hashedPassword);
                pst.setString(3, usuario.getNombre());
                pst.setString(4, usuario.getApellido());
                pst.setString(5, usuario.getEmail());
                pst.executeUpdate();
                System.out.println("Usuario ingresado correctamente");
            } catch (SQLException e) {
                System.out.println("Error al insertar el usuario: " + e.getMessage());
            }
        } else {
            System.out.println("ERROR EN DATOS PROPORCIONADOS");
        }
    }

    // Método para verificar la contraseña
    public boolean verificarPassword(String passwordIngresada, String passwordAlmacenada) {
        return BCrypt.checkpw(passwordIngresada, passwordAlmacenada);
    }

    // Método para actualizar un usuario
    public void actualizarUsuario(Connection conexion, DatosUsuarios usuario) {
        String sql = "UPDATE usuarios SET username = ?, password = ?, nombre = ?, apellido = ?, email = ? WHERE id_usuario = ?";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setString(1, usuario.getUsername());
            
            // Verificar si la contraseña necesita ser actualizada (cifrada nuevamente)
            if (BCrypt.checkpw(usuario.getPassword(), usuario.getPassword())) {
                pst.setString(2, usuario.getPassword()); // Ya cifrada
            } else {
                String hashedPassword = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());
                pst.setString(2, hashedPassword); // Cifrar si no lo está
            }

            pst.setString(3, usuario.getNombre());
            pst.setString(4, usuario.getApellido());
            pst.setString(5, usuario.getEmail());
            pst.setLong(6, usuario.getIdUsuario());

            pst.executeUpdate();
            System.out.println("Usuario actualizado correctamente");
        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
        }
    }

    // Método auxiliar para validar los datos del usuario
    private boolean validarDatosUsuario(DatosUsuarios usuario) {
        return (usuario.getUsername() != null && !usuario.getUsername().isEmpty() &&
                usuario.getPassword() != null && !usuario.getPassword().isEmpty() &&
                usuario.getNombre() != null && !usuario.getNombre().isEmpty() &&
                usuario.getApellido() != null && !usuario.getApellido().isEmpty() &&
                usuario.getEmail() != null && !usuario.getEmail().isEmpty());
    }
}
