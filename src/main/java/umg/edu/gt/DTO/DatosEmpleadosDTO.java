package umg.edu.gt.DTO;

import java.io.Serializable;
import java.util.Date;

public class DatosEmpleadosDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int empleado_id;
    private int Codigo_empleado;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private Date fecha_contratacion;
    private double salario_base;
    private String rol;
    private boolean activo;

    // Constructor por defecto
    public DatosEmpleadosDTO() {
    }

    // Constructor con parámetros
    public DatosEmpleadosDTO(int empleadoId, int codigoEmpleado, String nombre, String apellido, String email, 
                             String telefono, String direccion, Date fechaContratacion, double salarioBase, 
                             String rol, boolean activo) {
        this.empleado_id = empleadoId;
        this.Codigo_empleado = codigoEmpleado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.fecha_contratacion = fechaContratacion;
        this.salario_base = salarioBase;
        this.rol = rol;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public double getSalarioBase() {
        return salario_base;
    }

    public void setSalarioBase(double salarioBase) {
        this.salario_base = salarioBase;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // Método toString() para facilitar la depuración
    @Override
    public String toString() {
        return "DatosEmpleadosDTO{" +
               "empleadoId=" + empleado_id +
               ", codigoEmpleado=" + Codigo_empleado +
               ", nombre='" + nombre + '\'' +
               ", apellido='" + apellido + '\'' +
               ", email='" + email + '\'' +
               ", telefono='" + telefono + '\'' +
               ", direccion='" + direccion + '\'' +
               ", fechaContratacion=" + fecha_contratacion +
               ", salarioBase=" + salario_base +
               ", rol='" + rol + '\'' +
               ", activo=" + activo +
               '}';
    }
}
