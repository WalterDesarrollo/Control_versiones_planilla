package principal;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.List;
import umg.edu.gt.DAO.ConexionDAO;
import umg.edu.gt.DAO.ConsultaEmpleadosDAO;
import umg.edu.gt.DTO.DatosEmpleadosDTO;
import umg.edu.gt.DTO.DatosEmpleadosDTO.Rol;

@ManagedBean(name="bkn_Empleados")
@ViewScoped
public class EmpleadoUI implements Serializable {

    private String mensaje; 
    private List<DatosEmpleadosDTO> lista;
    private DatosEmpleadosDTO datosInsert = new DatosEmpleadosDTO();
    private String codigoEmpleadoGenerado;
    private String barcodeImagePath;
    
    // Getters y Setters
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public List<DatosEmpleadosDTO> getLista() { return lista; }
    public void setLista(List<DatosEmpleadosDTO> lista) { this.lista = lista; }

    public DatosEmpleadosDTO getDatosInsert() { return datosInsert; }
    public void setDatosInsert(DatosEmpleadosDTO datosInsert) { this.datosInsert = datosInsert; }

    public String getCodigoEmpleadoGenerado() { return codigoEmpleadoGenerado; }
    public void setCodigoEmpleadoGenerado(String codigoEmpleadoGenerado) { this.codigoEmpleadoGenerado = codigoEmpleadoGenerado; }

    public String getBarcodeImagePath() { return barcodeImagePath; }

    @PostConstruct
    public void init() {
        ConexionDAO con = new ConexionDAO();
        ConsultaEmpleadosDAO consulta = new ConsultaEmpleadosDAO();
        try {
            lista = consulta.findAllEmpleados();
            System.out.println("La conexion es: " + con.conexionMysql());
            System.out.println("La lista de empleados es: " + lista.size());
        } catch (Exception ex) {
            System.out.println("Error en la conexion: " + ex);
        }
    }
    
    public Rol[] getRolItems() {
    return Rol.values();
}

    public void insertaDatos() {
         if (datosInsert != null && 
        (datosInsert.getCodigoEmpleado() > 0 ||
         datosInsert.getNombre() != null && !datosInsert.getNombre().isEmpty() ||
         datosInsert.getApellido() != null && !datosInsert.getApellido().isEmpty() ||
         datosInsert.getIdentificacion() != null && !datosInsert.getIdentificacion().isEmpty() ||
         datosInsert.getTelefono() != null && !datosInsert.getTelefono().isEmpty() ||
         datosInsert.getDireccion() != null && !datosInsert.getDireccion().isEmpty() ||       
         datosInsert.getRol() != null || // Cambiado para comprobar que el Rol no sea nulo
         datosInsert.getArea() != null && !datosInsert.getArea().isEmpty() ||          
         datosInsert.isActivo())) {

            ConexionDAO con = new ConexionDAO();
            try (Connection conexion = con.conexionMysql()) {
                ConsultaEmpleadosDAO consulta = new ConsultaEmpleadosDAO();
                consulta.insertEmpleado(conexion, datosInsert);
                lista = consulta.findAllEmpleados();

                codigoEmpleadoGenerado = String.valueOf(datosInsert.getCodigoEmpleado());
                generarCodigoBarras(datosInsert.getCodigoEmpleado());

                mensaje = "Empleado guardado exitosamente.";
                System.out.println("Código de empleado generado: " + codigoEmpleadoGenerado);
                System.out.println("Ruta de imagen del código de barras: " + barcodeImagePath);
        
            } catch (Exception ex) {
                System.out.println("ERROR AL ESTABLECER CONEXION " + ex);
                mensaje = "Error al guardar el empleado.";
            }
        } else {
            System.out.println("VALORES NO VALIDOS");
            mensaje = "Por favor, complete los campos requeridos.";
        }
    }
    
   //metodo para limpiar los campos despues de insertar 
    public void limpiarFormulario() {
        datosInsert = new DatosEmpleadosDTO();
        codigoEmpleadoGenerado = null;
        barcodeImagePath = null;
    }

    
    //metodo para la generacion del codigo de barras 
    public void generarCodigoBarras(int codigoEmpleado) {
      String relativeWebPath = "/resources/barcodes/";
      String absoluteDiskPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(relativeWebPath);
      String fileName = codigoEmpleado + ".png";
      String filePath = absoluteDiskPath + File.separator + fileName;

      // Asegurarse de que la carpeta exista
      File barcodeDir = new File(absoluteDiskPath);
      if (!barcodeDir.exists()) {
          barcodeDir.mkdirs();  // Crear el directorio si no existe
      }

      try {
          BarcodeGenerator.generateBarcode(String.valueOf(codigoEmpleado), filePath);
          System.out.println("Código de barras generado en: " + filePath);

          // Actualiza esta línea para que coincida con la ruta en el XHTML
          barcodeImagePath = relativeWebPath + fileName;
          System.out.println("Ruta de imagen guardada: " + barcodeImagePath);
      } catch (WriterException | IOException e) {
          System.out.println("Error al generar el código de barras: " + e.getMessage());
          mensaje = "Error al generar el código de barras.";
      }
  }

    // Método para generar el PDF con el código de barras
    public void generarPDF() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

        try {
            // Ruta de la imagen del código de barras
            String barcodePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(barcodeImagePath);

            // Creamos un OutputStream para almacenar el PDF en memoria
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Añadimos un título al PDF
            document.add(new Paragraph("Código de Barras del Empleado"));

            // Añadir la imagen del código de barras al PDF
            Image barcodeImage = Image.getInstance(barcodePath);
            document.add(barcodeImage);

            // Cerramos el documento
            document.close();

            // Preparamos la respuesta HTTP para descargar el archivo PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=codigo_barras_" + codigoEmpleadoGenerado + ".pdf");
            response.setContentLength(baos.size());

            OutputStream os = response.getOutputStream();
            baos.writeTo(os);
            os.flush();
            os.close();

            facesContext.responseComplete();
        } catch (DocumentException | IOException e) {
            System.out.println("Error al generar el PDF: " + e.getMessage());
        }
    }

    // Clase generadora de códigos de barras
    public static class BarcodeGenerator {
        public static void generateBarcode(String data, String filePath) throws WriterException, IOException {
            Writer writer = new Code128Writer();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.CODE_128, 300, 150);
            Path path = Paths.get(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "png", path);
        }
    }
}
