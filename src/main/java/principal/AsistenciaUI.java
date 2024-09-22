package principal;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import umg.edu.gt.DAO.ConexionDAO;
import umg.edu.gt.DAO.ConsultaAsistenciaDAO;
import umg.edu.gt.DAO.ConsultaEmpleadosDAO;
import umg.edu.gt.DTO.AsistenciaDTO;
import umg.edu.gt.DTO.DatosEmpleadosDTO;

@ManagedBean(name = "bkn_Asistencia")
@ViewScoped
public class AsistenciaUI implements Serializable {

    private String mensaje;
    private List<AsistenciaDTO> lista;
    private AsistenciaDTO datosInsert = new AsistenciaDTO();
    private Long idRegistro;
    private int codigoBarras;  // Puedes cambiar a String si el código de barras no es numérico

    // Getters y Setters
    public int getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(int codigoBarras) { this.codigoBarras = codigoBarras; }
    public Long getIdRegistro() { return idRegistro; }
    public void setIdRegistro(Long idRegistro) { this.idRegistro = idRegistro; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public List<AsistenciaDTO> getLista() { return lista; }
    public void setLista(List<AsistenciaDTO> lista) { this.lista = lista; }
    public AsistenciaDTO getDatosInsert() { return datosInsert; }
    public void setDatosInsert(AsistenciaDTO datosInsert) { this.datosInsert = datosInsert; }

    @PostConstruct
    public void init() {
        setMensaje("******* ASISTENCIA ******");
        loadAsistenciaList(); // Cargar la lista al iniciar
    }

    // Método para cargar la lista de asistencia
    private void loadAsistenciaList() {
        ConexionDAO con = new ConexionDAO();
        ConsultaAsistenciaDAO consulta = new ConsultaAsistenciaDAO();
        try {
            lista = consulta.findAllAsistencia();
            System.out.println("La conexión es: " + con.conexionMysql());
            System.out.println("La lista es: " + lista.size());
            if (!lista.isEmpty()) {
                System.out.println("Primer registro: " + lista.get(0).getCodigoEmpleado());
            }
        } catch (Exception ex) {
            System.out.println("Error en la conexión: " + ex);
        }
    }

    // Método para procesar el código de barras
    public void procesarCodigoBarras() {
        if (codigoBarras != 0) {
            DatosEmpleadosDTO empleado = buscarEmpleadoPorCodigo(codigoBarras);
            if (empleado != null) {
                if (esEntrada(empleado.getEmpleadoId())) {
                    // Es una entrada
                    registrarEntrada(empleado.getEmpleadoId(), empleado.getCodigoEmpleado());
                    mensaje = "Entrada registrada para: " + empleado.getNombre();
                } else {
                    // Es una salida
                    registrarSalida(empleado.getEmpleadoId());
                    mensaje = "Salida registrada para: " + empleado.getNombre();
                }
                // Actualizar la lista después de procesar
                loadAsistenciaList(); // Recargar la lista actualizada
            } else {
                mensaje = "Empleado no encontrado para el código: " + codigoBarras;
            }
        } else {
            mensaje = "Por favor, escanee un código de barras válido.";
        }
    }

    // Método para buscar empleado en la base de datos basado en el código de barras
    private DatosEmpleadosDTO buscarEmpleadoPorCodigo(int codigoBarras) {
        ConsultaEmpleadosDAO consulta = new ConsultaEmpleadosDAO();
        try {
            DatosEmpleadosDTO empleado = consulta.findEmpleadoByCodigo(codigoBarras);
            if (empleado != null) {
                System.out.println("Empleado encontrado con código: " + codigoBarras);
            }
            return empleado;
        } catch (Exception e) {
            System.out.println("Error al buscar el empleado: " + e.getMessage());
            return null;
        }
    }

    // Método para verificar si es entrada o salida
    private boolean esEntrada(int empleadoId) {
        ConsultaAsistenciaDAO consultaAsistencia = new ConsultaAsistenciaDAO();
        try {
            // Cambia la lógica: si no existe un registro de entrada sin salida, debe registrar una entrada
            return !consultaAsistencia.verificarUltimoRegistro(empleadoId);
        } catch (Exception e) {
            System.out.println("Error al verificar el registro: " + e.getMessage());
            return true; // Si hay algún error en la verificación, asumir que es una entrada
        }
    }

    // Método para registrar la entrada
    private void registrarEntrada(int empleadoId, int codigoEmpleado) {
        ConsultaAsistenciaDAO consultaAsistencia = new ConsultaAsistenciaDAO();
        AsistenciaDTO asistencia = new AsistenciaDTO();
        asistencia.setEmpleadoId(empleadoId);
        asistencia.setCodigoEmpleado(codigoEmpleado);
        asistencia.setFecha(new java.sql.Date(System.currentTimeMillis()));
        asistencia.setHoraEntrada(new java.sql.Time(System.currentTimeMillis()));
        try {
            consultaAsistencia.registrarEntrada(asistencia);
            System.out.println("Entrada registrada para el empleado con ID: " + empleadoId);
        } catch (Exception e) {
            System.out.println("Error al registrar la entrada: " + e.getMessage());
        }
    }

    // Método para registrar la salida
    private void registrarSalida(int empleadoId) {
        ConsultaAsistenciaDAO consultaAsistencia = new ConsultaAsistenciaDAO();
        try {
            consultaAsistencia.registrarSalida(empleadoId, new java.sql.Time(System.currentTimeMillis()));
            System.out.println("Salida registrada para el empleado con ID: " + empleadoId);
        } catch (Exception e) {
            System.out.println("Error al registrar la salida: " + e.getMessage());
        }
    }
}
