package principal;

import java.io.Serializable;
import java.sql.Connection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import umg.edu.gt.DAO.ConexionDAO;
import umg.edu.gt.DAO.ConsultasUsuariosDAO;
import umg.edu.gt.DTO.DatosUsuarios;

@ManagedBean(name = "bkn_Usuarios")
@ViewScoped
public class UsuariosUI implements Serializable {

    private static final long serialVersionUID = 1L;

    private String confirmPassword; // Campo de confirmación de contraseña
    private List<DatosUsuarios> lista; // Lista de usuarios
    private DatosUsuarios datosInsert = new DatosUsuarios(); // Datos del usuario a insertar

    private transient ConexionDAO con;
    private transient ConsultasUsuariosDAO consulta;

    // Getters y Setters
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public List<DatosUsuarios> getLista() {
        return lista;
    }

    public void setLista(List<DatosUsuarios> lista) {
        this.lista = lista;
    }

    public DatosUsuarios getDatosInsert() {
        return datosInsert;
    }

    public void setDatosInsert(DatosUsuarios datosInsert) {
        this.datosInsert = datosInsert;
    }

    @PostConstruct
    public void init() {
        con = new ConexionDAO(); // Inicializar la conexión
        consulta = new ConsultasUsuariosDAO(); // Inicializar el DAO
        cargarListaUsuarios(); // Cargar la lista de usuarios
    }

    // Cargar lista de usuarios desde la base de datos
    public void cargarListaUsuarios() {
        try {
            lista = consulta.findAllUsuarios();
            System.out.println("Lista de usuarios cargada, total: " + lista.size());
        } catch (Exception ex) {
            System.out.println("Error al cargar la lista de usuarios: " + ex.getMessage());
        }
    }

    // Validar el registro
    public void validarRegistro() {
        // Validar si las contraseñas coinciden
        if (!datosInsert.getPassword().equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Las contraseñas no coinciden."));
            return; // Interrumpir si las contraseñas no coinciden
        }

        // Verificar que todos los campos estén completos
        if (datosInsert.getUsername() == null || datosInsert.getUsername().isEmpty() ||
            datosInsert.getNombre() == null || datosInsert.getNombre().isEmpty() ||
            datosInsert.getApellido() == null || datosInsert.getApellido().isEmpty() ||
            datosInsert.getEmail() == null || datosInsert.getEmail().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Todos los campos son obligatorios."));
            return; // Interrumpir si algún campo está vacío
        }

        // Si las contraseñas coinciden y todos los campos están completos, intenta insertar los datos
        insertaDatos();
    }

    // Método para insertar datos
    public void insertaDatos() {
        try (Connection conexion = con.conexionMysql()) {
            consulta.insertUsuario(conexion, datosInsert);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Usuario registrado exitosamente."));
            limpiarCampos(); // Limpiar los campos tras la inserción
        } catch (Exception ex) {
            System.out.println("Error al registrar el usuario: " + ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al registrar el usuario."));
        }
    }

    // Método para limpiar los campos del formulario
    public void limpiarCampos() {
        datosInsert = new DatosUsuarios(); // Reiniciar los datos del usuario
        confirmPassword = null; // Reiniciar la confirmación de contraseña
    }
}
