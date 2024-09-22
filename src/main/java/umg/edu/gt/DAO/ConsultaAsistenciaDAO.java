package umg.edu.gt.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import umg.edu.gt.DTO.AsistenciaDTO;
import umg.edu.gt.DTO.DatosEmpleadosDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class ConsultaAsistenciaDAO {

    private transient ConexionDAO con = new ConexionDAO();

    // Método para obtener todas las asistencias
    public List<AsistenciaDTO> findAllAsistencia() throws Exception {
        List<AsistenciaDTO> lista = new ArrayList<>();
        String sql = "SELECT * FROM asistencia";

        try (Connection conexion = con.conexionMysql();
             PreparedStatement pst = conexion.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                AsistenciaDTO asistencia = new AsistenciaDTO();
                asistencia.setAsistenciaId(rs.getInt("asistencia_id"));
                asistencia.setCodigoEmpleado(rs.getInt("Codigo_empleado"));
                asistencia.setEmpleadoId(rs.getInt("empleado_id"));
                asistencia.setNombre(rs.getString("nombre"));
                asistencia.setApellido(rs.getString("apellido"));       
                asistencia.setFecha(rs.getDate("fecha"));
                asistencia.setHoraEntrada(rs.getTime("hora_entrada"));
                asistencia.setHoraSalida(rs.getTime("hora_salida"));
                asistencia.setHorasTrabajadas(rs.getBigDecimal("horas_trabajadas"));
                lista.add(asistencia);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar asistencias: " + e.getMessage());
        }
        return lista;
    }

    //--------------------------------------------------------------------------------------------------
    
    // Método para registrar la entrada del empleado 
 public void registrarEntrada(AsistenciaDTO asistencia) throws SQLException, Exception {
    // Buscar los detalles del empleado antes de registrar la entrada
    DatosEmpleadosDTO empleado = buscarEmpleadoPorCodigo(asistencia.getCodigoEmpleado());
    
    if (empleado != null) {
        asistencia.setNombre(empleado.getNombre());      // Asigna el nombre
        asistencia.setApellido(empleado.getApellido());  // Asigna el apellido

        // Verificar si el empleado ya tiene una entrada sin salida
        if (!verificarUltimoRegistro(asistencia.getEmpleadoId())) {
            String sql = "INSERT INTO asistencia (Codigo_empleado, empleado_id, nombre, apellido, fecha, hora_entrada) "
                       + "VALUES (?, ?, ?, ?, CURDATE(), CURTIME())";

            try (Connection conexion = con.conexionMysql();
                 PreparedStatement pst = conexion.prepareStatement(sql)) {

                pst.setInt(1, asistencia.getCodigoEmpleado()); // Código del empleado
                pst.setInt(2, asistencia.getEmpleadoId());     // ID del empleado
                pst.setString(3, asistencia.getNombre());      // Nombre del empleado
                pst.setString(4, asistencia.getApellido());    // Apellido del empleado

                pst.executeUpdate();
                System.out.println("Entrada registrada correctamente con nombre y apellido.");
            } catch (SQLException e) {
                throw new SQLException("Error al registrar la entrada: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error al establecer la conexión: " + e.getMessage());
            }
        } else {
            System.out.println("Ya existe una entrada sin salida para el empleado con ID: " + asistencia.getEmpleadoId());
        }
    } else {
        System.out.println("Empleado no encontrado con el código: " + asistencia.getCodigoEmpleado());
    }
}
   
    
//-------------------------------------------------------------------------------------------------------
 
    //metodo para el registro de la hora de salida 
  public void registrarSalida(int empleadoId, java.sql.Time horaSalida) throws SQLException, Exception {
    String sql = "UPDATE asistencia SET hora_salida = ?, horas_trabajadas = ? WHERE empleado_id = ? AND hora_salida IS NULL";
    try (Connection conexion = con.conexionMysql()) {
        String consultaEntrada = "SELECT fecha, hora_entrada FROM asistencia WHERE empleado_id = ? AND hora_salida IS NULL";
        try (PreparedStatement pstEntrada = conexion.prepareStatement(consultaEntrada)) {
            pstEntrada.setInt(1, empleadoId);
            try (ResultSet rs = pstEntrada.executeQuery()) {
                if (rs.next()) {
                    LocalDateTime fechaEntrada = rs.getTimestamp("fecha").toLocalDateTime();
                    LocalTime horaEntrada = rs.getTime("hora_entrada").toLocalTime();
                    LocalDateTime entrada = fechaEntrada.with(horaEntrada);
                    
                    LocalDateTime salida = LocalDateTime.of(fechaEntrada.toLocalDate(), horaSalida.toLocalTime());
                    
                    // Calcular la duración entre la entrada y la salida
                    Duration duracion = Duration.between(entrada, salida);
                    long minutosTrabajados = duracion.toMinutes();
                    
                    // Calcular las horas trabajadas en el formato deseado
                    int horas = (int) (minutosTrabajados / 60);
                    int minutos = (int) (minutosTrabajados % 60);
                    String horasFormateadas = String.format("%d.%02d", horas, minutos);
                    
                    System.out.println("Horas trabajadas calculadas: " + horasFormateadas);
                    
                    try (PreparedStatement pst = conexion.prepareStatement(sql)) {
                        pst.setTime(1, horaSalida);
                        pst.setString(2, horasFormateadas); // Guardamos como String para mantener el formato
                        pst.setInt(3, empleadoId);
                        int filasActualizadas = pst.executeUpdate();
                        if (filasActualizadas > 0) {
                            System.out.println("Salida y horas trabajadas registradas correctamente.");
                        } else {
                            System.out.println("No se encontró un registro de entrada para actualizar la salida.");
                        }
                    }
                } else {
                    System.out.println("No se encontró una entrada para el empleado con ID: " + empleadoId);
                }
            }
        }
    } catch (SQLException e) {
        throw new SQLException("Error al registrar la salida: " + e.getMessage());
    }
}

  //--------------------------------------------------------------------------------------------------

  //metodo para verificar el ultimo registro, para determinar si es salida o entrada 
    public boolean verificarUltimoRegistro(int empleadoId) throws SQLException, Exception {
        String sql = "SELECT * FROM asistencia WHERE empleado_id = ? AND hora_salida IS NULL";

        try (Connection conexion = con.conexionMysql();
             PreparedStatement pst = conexion.prepareStatement(sql)) {

            pst.setInt(1, empleadoId);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next(); // Si hay registros sin salida, devuelve true
            }
        } catch (SQLException e) {
            throw new SQLException("Error al verificar el último registro: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception("Error al establecer la conexión: " + e.getMessage());
        }
    }
 
    //---------------------------------------------------------------------------------------
    
    //para buscar si el empleado existe en la base de datos 
    public DatosEmpleadosDTO buscarEmpleadoPorCodigo(int codigoEmpleado) throws SQLException {
        String sql = "SELECT * FROM empleados WHERE Codigo_empleado = ?";
        DatosEmpleadosDTO empleado = null;

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
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al buscar el empleado por código: " + e.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(ConsultaAsistenciaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return empleado;
    }
}
