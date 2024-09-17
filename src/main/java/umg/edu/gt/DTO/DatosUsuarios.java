package umg.edu.gt.DTO;

import java.io.Serializable;
import java.sql.Timestamp;

public class DatosUsuarios implements Serializable {

    private static final long serialVersionUID = 1L;  // Es un identificador único para la versión de la clase

    private int id_usuario;
    private String username;
    private String password;
    private String nombre;
    private String apellido;
    private String email;
    private Timestamp fecha_registro;

    // Getters y Setters

    public int getIdUsuario() {
        return id_usuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.id_usuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getFechaRegistro() {
        return fecha_registro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fecha_registro = fechaRegistro;
    }
}
