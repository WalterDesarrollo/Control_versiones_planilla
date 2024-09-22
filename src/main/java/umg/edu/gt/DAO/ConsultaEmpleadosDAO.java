package umg.edu.gt.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import umg.edu.gt.DTO.DatosEmpleadosDTO;

public class ConsultaEmpleadosDAO {

    // Instancia de la conexión
    private transient ConexionDAO con = new ConexionDAO();

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
                empleado.setEmail(rs.getString("email"));
                empleado.setTelefono(rs.getString("telefono"));
                empleado.setDireccion(rs.getString("direccion"));
                empleado.setFechaContratacion(rs.getDate("fecha_contratacion"));
                empleado.setSalarioBase(rs.getDouble("salario_base"));
                empleado.setRol(rs.getString("rol"));
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
        String sql = "INSERT INTO empleados (Codigo_empleado, nombre, apellido, email, telefono, direccion, fecha_contratacion, salario_base, rol, activo) "
                   + "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?)";

        try (PreparedStatement pst = conexion.prepareStatement(sql)) {
            pst.setInt(1, empleado.getCodigoEmpleado());
            pst.setString(2, empleado.getNombre());
            pst.setString(3, empleado.getApellido());
            pst.setString(4, empleado.getEmail());
            pst.setString(5, empleado.getTelefono());
            pst.setString(6, empleado.getDireccion());
            // Saltamos el índice 7 ya que la fecha será insertada con NOW()
            pst.setDouble(7, empleado.getSalarioBase());
            pst.setString(8, empleado.getRol());
            pst.setBoolean(9, empleado.isActivo());
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
                    empleado.setEmail(rs.getString("email"));
                    empleado.setTelefono(rs.getString("telefono"));
                    empleado.setDireccion(rs.getString("direccion"));
                    empleado.setFechaContratacion(rs.getDate("fecha_contratacion"));
                    empleado.setSalarioBase(rs.getDouble("salario_base"));
                    empleado.setRol(rs.getString("rol"));
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


