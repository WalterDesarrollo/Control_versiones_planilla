package umg.edu.gt.DAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import umg.edu.gt.DTO.DatosEmpleadosDTO;
import umg.edu.gt.DTO.DatosEmpleadosDTO.Rol; // Importación del enum Rol

public class ConsultaEmpleadosDAO {

    // Instancia de la conexión
    private transient ConexionDAO con = new ConexionDAO();

    // Método seguro para convertir un valor String al Enum Rol
    private Rol obtenerRolSeguro(String valor) {
        if (valor == null) {
            System.out.println("El valor del rol es nulo.");
            return null; // Maneja nulos según tu lógica de negocio
        }
        try {
            return Rol.valueOf(valor.trim()); // Convierte el valor eliminando espacios
        } catch (IllegalArgumentException e) {
            System.out.println("Error al asignar el rol: Valor no válido - '" + valor + "'");
            return null; // O asigna un valor por defecto si es necesario
        }
    }

    // Método para obtener todos los empleados
    public List<DatosEmpleadosDTO> findAllEmpleados() throws Exception {
        List<DatosEmpleadosDTO> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleados";

        try (Connection conexion = con.conexionMysql();
             PreparedStatement pst = conexion.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                DatosEmpleadosDTO empleado = new DatosEmpleadosDTO();
                empleado.setEmpleadoId(rs.getInt("empleado_id"));
                empleado.setCodigoEmpleado(rs.getInt("Codigo_empleado"));
                empleado.setNombre(rs.getString("nombre"));
                empleado.setApellido(rs.getString("apellido"));
                empleado.setIdentificacion(rs.getString("identificacion"));
                empleado.setTelefono(rs.getString("telefono"));
                empleado.setDireccion(rs.getString("direccion"));
                empleado.setFechaContratacion(rs.getDate("fecha_contratacion"));
                // Asignación del rol con validación del enum usando método seguro
                String rolValue = rs.getString("rol");
                empleado.setRol(obtenerRolSeguro(rolValue));
                empleado.setArea(rs.getString("area"));
                empleado.setMonto_diario(rs.getBigDecimal("monto_diario"));
                empleado.setActivo(rs.getBoolean("activo"));
                lista.add(empleado);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar empleados: " + e.getMessage());
        }
        return lista;
    }

    // Método para insertar un nuevo empleado
    public void insertEmpleado(Connection conexion, DatosEmpleadosDTO empleado) {
        if (empleado != null) {
            String sql = "INSERT INTO empleados (Codigo_empleado, nombre, apellido, identificacion, telefono, direccion, fecha_contratacion, rol, area, monto_diario, activo) "
                       + "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?)";

            try (PreparedStatement pst = conexion.prepareStatement(sql)) {
                pst.setInt(1, empleado.getCodigoEmpleado());
                pst.setString(2, empleado.getNombre());
                pst.setString(3, empleado.getApellido());
                pst.setString(4, empleado.getIdentificacion());
                pst.setString(5, empleado.getTelefono());
                pst.setString(6, empleado.getDireccion());
                pst.setString(7, empleado.getRol() != null ? empleado.getRol().name() : ""); // Maneja nulos si es posible
                pst.setString(8, empleado.getArea());
                pst.setBigDecimal(9,empleado.getMonto_diario());
                pst.setBoolean(10, empleado.isActivo());
                pst.executeUpdate();
                System.out.println("Empleado ingresado correctamente");
            } catch (SQLException e) {
                System.out.println("Error al insertar el empleado: " + e.getMessage());
            }
        } else {
            System.out.println("ERROR EN DATOS PROPORCIONADOS");
        }
    }

    // Método para buscar un empleado por el código de barras (Codigo_empleado)
    public DatosEmpleadosDTO findEmpleadoByCodigo(int codigoEmpleado) throws SQLException, Exception {
        DatosEmpleadosDTO empleado = null;
        String sql = "SELECT * FROM empleados WHERE Codigo_empleado = ?";

        try (Connection conexion = con.conexionMysql();
             PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, codigoEmpleado);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    empleado = new DatosEmpleadosDTO();
                    empleado.setEmpleadoId(rs.getInt("empleado_id"));
                    empleado.setCodigoEmpleado(rs.getInt("Codigo_empleado"));
                    empleado.setNombre(rs.getString("nombre"));
                    empleado.setApellido(rs.getString("apellido"));
                    empleado.setIdentificacion(rs.getString("identificacion"));
                    empleado.setTelefono(rs.getString("telefono"));
                    empleado.setDireccion(rs.getString("direccion"));
                    empleado.setFechaContratacion(rs.getDate("fecha_contratacion"));
                    // Asignación del rol con validación del enum usando método seguro
                    String rolValue = rs.getString("rol");
                    empleado.setRol(obtenerRolSeguro(rolValue));
                    empleado.setArea(rs.getString("area"));
                    empleado.setMonto_diario(rs.getBigDecimal("monto_diario"));
                    empleado.setActivo(rs.getBoolean("activo"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar el empleado por código: " + e.getMessage());
            throw new SQLException("Error al buscar el empleado: " + e.getMessage());
        }
        return empleado;
    }
}


