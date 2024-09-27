package umg.edu.gt.DTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DatosEmpleadosDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int empleado_id;
    private int Codigo_empleado;
    private String nombre;
    private String apellido;
    private String identificacion;
    private String telefono;
    private String direccion;
    private Date fecha_contratacion;
    private Rol rol; // Cambio a tipo Rol
    private String area;
    private BigDecimal monto_diario;

  
    private boolean activo;

    // Enum para definir los roles permitidos
    public enum Rol {
        Jornal, Planilla
    }

    // Constructor por defecto
    public DatosEmpleadosDTO() {
    }

    // Constructor con parámetros
    public DatosEmpleadosDTO(int empleadoId, int codigoEmpleado, String nombre, String apellido, String identificacion,
                             String telefono, String direccion, Date fechaContratacion, Rol rol, String area, BigDecimal monto_diario, boolean activo) {
        this.empleado_id = empleadoId;
        this.Codigo_empleado = codigoEmpleado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.identificacion = identificacion;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fecha_contratacion = fechaContratacion;
        this.rol = rol;
        this.area = area;
        this.monto_diario = monto_diario;
        this.activo = activo;
    }

    // Getters y setters
    public int getEmpleadoId() {
        return empleado_id;
    }

    public void setEmpleadoId(int empleadoId) {
        this.empleado_id = empleadoId;
    }

    public int getCodigoEmpleado() {
        return Codigo_empleado;
    }

    public void setCodigoEmpleado(int codigoEmpleado) {
        this.Codigo_empleado = codigoEmpleado;
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

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Date getFechaContratacion() {
        return fecha_contratacion;
    }

    public void setFechaContratacion(Date fechaContratacion) {
        this.fecha_contratacion = fechaContratacion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

      public BigDecimal getMonto_diario() {
        return monto_diario;
    }

    public void setMonto_diario(BigDecimal monto_diario) {
        this.monto_diario = monto_diario;
    }
    // Método toString() para facilitar la depuración
    @Override
    public String toString() {
        return "DatosEmpleadosDTO{" +
                "empleadoId=" + empleado_id +
                ", codigoEmpleado=" + Codigo_empleado +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", identificacion='" + identificacion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                ", fechaContratacion=" + fecha_contratacion +
                ", rol=" + rol +
                ", area=" + area +
                ", monto_diario=" + monto_diario +
                ", activo=" + activo +
                '}';
    }
}
