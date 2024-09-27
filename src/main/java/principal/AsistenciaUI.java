package principal;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import umg.edu.gt.DAO.ConexionDAO;
import umg.edu.gt.DAO.ConsultaAsistenciaDAO;
import umg.edu.gt.DAO.ConsultaEmpleadosDAO;
import umg.edu.gt.DTO.AsistenciaDTO;
import umg.edu.gt.DTO.DatosEmpleadosDTO;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFTableStyleInfo;

// Importaciones para Apache POI (Excel)
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font; // Esta importación es para Apache POI


// Importaciones para iText (PDF)
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import javax.faces.application.FacesMessage;
import org.primefaces.util.IOUtils;


@ManagedBean(name = "bkn_Asistencia")
@ViewScoped
public class AsistenciaUI implements Serializable {

    private String mensaje;
    private String filtro;  // Nuevo campo para capturar el texto de búsqueda
    private List<AsistenciaDTO> lista;
    private List<AsistenciaDTO> listaFiltrada;  // Nueva lista para los resultados filtrados
    private AsistenciaDTO datosInsert = new AsistenciaDTO();
    private Long idRegistro;
    private String codigoBarras;  // Cambiar a String si es necesario para códigos no numéricos
    private BigDecimal total_devengado = BigDecimal.ZERO; // Inicialización para evitar valores nulos
    // Variables para las fechas de filtro
  // Cambia las variables fechaInicio y fechaFin a java.util.Date
    private java.util.Date fechaInicio;
    private java.util.Date fechaFin;

    // Getters y Setters
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    public Long getIdRegistro() { return idRegistro; }
    public void setIdRegistro(Long idRegistro) { this.idRegistro = idRegistro; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getFiltro() { return filtro; }  // Getter del filtro
    public void setFiltro(String filtro) { this.filtro = filtro; }  // Setter del filtro
    public List<AsistenciaDTO> getLista() { return lista; }
    public void setLista(List<AsistenciaDTO> lista) { this.lista = lista; }
    
    public List<AsistenciaDTO> getListaFiltrada() { 
    return listaFiltrada == null || listaFiltrada.isEmpty() ? lista : listaFiltrada; }

    public AsistenciaDTO getDatosInsert() { return datosInsert; }
    public void setDatosInsert(AsistenciaDTO datosInsert) { this.datosInsert = datosInsert; }

        // Getters y Setters
      public java.util.Date getFechaInicio() {
          return fechaInicio;
      }

      public void setFechaInicio(java.util.Date fechaInicio) {
          this.fechaInicio = fechaInicio;
      }

      public java.util.Date getFechaFin() {
          return fechaFin;
      }

      public void setFechaFin(java.util.Date fechaFin) {
          this.fechaFin = fechaFin;
      }

@PostConstruct
   public void init() {    
    loadAsistenciaList(); // Cargar la lista al iniciar
    listaFiltrada = new ArrayList<>(lista); // Inicializar listaFiltrada con todos los registros
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

    // Método para buscar en la lista según el filtro ingresado
    public void buscar() {
    listaFiltrada = new ArrayList<>(); // Inicializar la lista filtrada

    if (filtro != null && !filtro.isEmpty()) {
        for (AsistenciaDTO asistencia : lista) {
            if (asistencia.getNombre().toLowerCase().contains(filtro.toLowerCase())) {
                listaFiltrada.add(asistencia); // Agregar los registros que coincidan con el filtro
            }
        }
    } else {
        listaFiltrada = lista; // Restablecer la lista a la original si no hay filtro
    }
    }
    
  // Método para ordenar la lista de asistencia según el campo seleccionado
    public void ordenar(String campo) {
    if (campo.equals("Codigo_empleado")) {
        // Ordenar por código de empleado
        Collections.sort(listaFiltrada, new Comparator<AsistenciaDTO>() {
            @Override
            public int compare(AsistenciaDTO a1, AsistenciaDTO a2) {
                return Integer.compare(a1.getCodigoEmpleado(), a2.getCodigoEmpleado());
            }
        });
    } else if (campo.equals("nombre")) {
        // Ordenar por nombre
        Collections.sort(listaFiltrada, new Comparator<AsistenciaDTO>() {
            @Override
            public int compare(AsistenciaDTO a1, AsistenciaDTO a2) {
                return a1.getNombre().compareTo(a2.getNombre());
            }
        });
    } else if (campo.equals("fecha")) {
        // Ordenar por fecha
        Collections.sort(listaFiltrada, new Comparator<AsistenciaDTO>() {
            @Override
            public int compare(AsistenciaDTO a1, AsistenciaDTO a2) {
                return a1.getFecha().compareTo(a2.getFecha());
            }
        });
    }
    }

    // Método para procesar el código de barras
    public void procesarCodigoBarras() {
        if (codigoBarras != null && !codigoBarras.isEmpty()) {
            try {
                int codigo = Integer.parseInt(codigoBarras); // Convertir a entero si es necesario
                DatosEmpleadosDTO empleado = buscarEmpleadoPorCodigo(codigo);

                if (empleado != null) {
                    if (esEntrada(empleado.getEmpleadoId())) {
                        registrarEntrada(empleado.getEmpleadoId(), empleado.getCodigoEmpleado());
                        mensaje = "Entrada registrada para: " + empleado.getNombre();
                    } else {
                        registrarSalida(empleado.getEmpleadoId());
                        mensaje = "Salida registrada para: " + empleado.getNombre();
                    }
                    loadAsistenciaList(); // Recargar la lista actualizada
                } else {
                    mensaje = "Empleado no encontrado para el código: " + codigoBarras;
                }
            } catch (NumberFormatException e) {
                mensaje = "El código de barras debe ser numérico.";
            }
        } else {
            mensaje = "Por favor, escanee un código de barras válido.";
        }
        // Limpiar la casilla del código de barras
        codigoBarras = ""; 
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
    
    
    // Método para calcular el total devengado por empleado
    public void calcularTotalDevengado() {
        if (listaFiltrada == null || listaFiltrada.isEmpty()) {
            return; // No hacer nada si la lista está vacía
        }

    // Ordenar la lista filtrada por código de empleado
    Collections.sort(listaFiltrada, new Comparator<AsistenciaDTO>() {
        @Override
        public int compare(AsistenciaDTO a1, AsistenciaDTO a2) {
            return Integer.compare(a1.getCodigoEmpleado(), a2.getCodigoEmpleado());
        }
    });

    // Inicializar un acumulador y un código de empleado actual
    BigDecimal total = BigDecimal.ZERO; // Inicializar BigDecimal correctamente
    int codigoActual = -1;

    // Iterar sobre la lista filtrada para calcular los totales
    for (int i = 0; i < listaFiltrada.size(); i++) {
        AsistenciaDTO asistencia = listaFiltrada.get(i);
        if (asistencia.getCodigoEmpleado() != codigoActual) {
            // Nuevo empleado: actualizar el total devengado del anterior
            if (codigoActual != -1) {
                listaFiltrada.get(i - 1).setTotal_devengado(total);
            }
            // Reiniciar acumulador para el nuevo empleado
            total = asistencia.getMonto();
            codigoActual = asistencia.getCodigoEmpleado();
        } else {
            // Acumular el monto para el empleado actual usando BigDecimal.add()
            total = total.add(asistencia.getMonto());
        }
    }
    // Actualizar el total devengado del último empleado en la lista
    listaFiltrada.get(listaFiltrada.size() - 1).setTotal_devengado(total);
    }

    
        // Generación de reportes en Excel
  public void generarReporte() {
    // Verificar que las fechas no sean nulas antes de filtrar
    if (fechaInicio == null || fechaFin == null) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un rango de fechas válido."));
        return;
    }

    // Convertir java.util.Date a java.sql.Date para la comparación
    java.sql.Date sqlFechaInicio = convertirUtilADateSql(fechaInicio);
    java.sql.Date sqlFechaFin = convertirUtilADateSql(fechaFin);

    // Filtrar la lista según el rango de fechas seleccionado
    List<AsistenciaDTO> datosFiltrados = new ArrayList<>();
    for (AsistenciaDTO asistencia : listaFiltrada) {
        if (!asistencia.getFecha().before(sqlFechaInicio) && !asistencia.getFecha().after(sqlFechaFin)) {
            datosFiltrados.add(asistencia);
        }
    }

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Reporte de Asistencia");

    // Crear el título del reporte y la fecha, iniciando en la columna B
    Row titleRow = sheet.createRow(0);
    Cell titleCell = titleRow.createCell(1); // Ajustar para que el título comience desde la columna B
    titleCell.setCellValue("Plantaciones Nahualate--Reporte de Asistencia");
    CellStyle titleStyle = workbook.createCellStyle();
    Font titleFont = workbook.createFont();
    titleFont.setBold(true);
    titleFont.setFontHeightInPoints((short) 16);
    titleStyle.setFont(titleFont);
    titleCell.setCellStyle(titleStyle);

    // Crear la fila de la fecha, iniciando en la columna B
    Row dateRow = sheet.createRow(1);
    Cell dateCell = dateRow.createCell(1);
    dateCell.setCellValue("Generado el: " + new java.util.Date().toString());
    CellStyle dateStyle = workbook.createCellStyle();
    Font dateFont = workbook.createFont();
    dateFont.setFontHeightInPoints((short) 12);
    dateStyle.setFont(dateFont);
    dateCell.setCellStyle(dateStyle);

    // Crear el encabezado de la tabla en la fila 4 (índice 3) y en la columna C (índice 2)
    String[] headers = {"Codigo Empleado", "Nombre", "Apellido", "Fecha", "Hora Entrada", "Hora Salida", "Tiempo Trabajado", "Paga Diaria", "Total Devengado"};
    Row headerRow = sheet.createRow(3); // Cambiar de 0 a 3 para que los datos comiencen en la fila 4
    for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i + 2); // Ajustar para que los encabezados comiencen en la columna C
        cell.setCellValue(headers[i]);
        cell.setCellStyle(crearEstiloEncabezado(workbook));
    }

    // Llenar los datos de la tabla a partir de la fila 5 y la columna C
    int rowNum = 4;
    for (AsistenciaDTO asistencia : datosFiltrados) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(2).setCellValue(asistencia.getCodigoEmpleado());
        row.createCell(3).setCellValue(asistencia.getNombre());
        row.createCell(4).setCellValue(asistencia.getApellido());
        row.createCell(5).setCellValue(asistencia.getFecha().toString());
        row.createCell(6).setCellValue(asistencia.getHoraEntrada().toString());
        row.createCell(7).setCellValue(asistencia.getHoraSalida().toString());
        row.createCell(8).setCellValue(asistencia.getHorasTrabajadas().toString());
        row.createCell(9).setCellValue(asistencia.getMonto().doubleValue());
        row.createCell(10).setCellValue(
                asistencia.getTotal_devengado() != null ? asistencia.getTotal_devengado().doubleValue() : 0.0
        );
    }

    // Autoajustar las columnas, comenzando desde la columna B para el título y desde C para los datos
    for (int i = 1; i <= headers.length + 1; i++) {
        sheet.autoSizeColumn(i);
    }

    // Enviar el archivo al navegador para descarga
    FacesContext facesContext = FacesContext.getCurrentInstance();
    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
    response.reset();
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=Reporte_Asistencia.xlsx");

    try (OutputStream output = response.getOutputStream()) {
        workbook.write(output);
        facesContext.responseComplete();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    // Método para crear estilo de encabezado en Excel
    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }



   // Método generarReportePDF
    public void generarReportePDF() {
    // Verificar que las fechas no sean nulas antes de filtrar
    if (fechaInicio == null || fechaFin == null) {
        // Mostrar un mensaje de error si las fechas no están definidas
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionar un rango de fechas válido."));
        return;
    }

    // Convertir fechas para la comparación
    java.sql.Date sqlFechaInicio = convertirUtilADateSql(fechaInicio);
    java.sql.Date sqlFechaFin = convertirUtilADateSql(fechaFin);

    // Filtrar la lista según el rango de fechas seleccionado
    List<AsistenciaDTO> datosFiltrados = new ArrayList<>();
    for (AsistenciaDTO asistencia : listaFiltrada) {
        if (!asistencia.getFecha().before(sqlFechaInicio) && !asistencia.getFecha().after(sqlFechaFin)) {
            datosFiltrados.add(asistencia);
        }
    }

    Document document = new Document(PageSize.A4.rotate());
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Añadir encabezado con logo, título y fecha
        PdfPTable headerTable = new PdfPTable(2); // Tabla de 2 columnas: una para el logo y otra para el título
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 4}); // Asignar proporciones de ancho a las columnas

        // Cargar el logo desde el directorio de recursos
        String logoPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images/logo_empresa.jpg");
        Image logo = Image.getInstance(logoPath);
        logo.scaleToFit(150, 150); // Ajustar el tamaño del logo
        logo.setAlignment(Image.ALIGN_LEFT);

        // Añadir la celda del logo
        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setBorder(PdfPCell.NO_BORDER);
        headerTable.addCell(logoCell);

        // Crear el título y la fecha
        com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font fontSubTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);

        // Formatear la fecha
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());

        Paragraph title = new Paragraph("Plantaciones Nahualate\nReporte de Asistencia", fontTitle);
        title.setAlignment(Element.ALIGN_LEFT);

        Paragraph generatedDate = new Paragraph("Generado el: " + formattedDate, fontSubTitle);
        generatedDate.setAlignment(Element.ALIGN_LEFT);

        // Añadir el título y la fecha a la celda de texto
        PdfPCell textCell = new PdfPCell();
        textCell.setBorder(PdfPCell.NO_BORDER);
        textCell.addElement(title);
        textCell.addElement(generatedDate);
        headerTable.addCell(textCell);

        // Añadir la tabla de encabezado al documento
        document.add(headerTable);
        document.add(new Paragraph(" ")); // Añadir espacio

        // Crear tabla de datos
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Añadir encabezados
        String[] headers = {"Codigo Empleado", "Nombre", "Apellido", "Fecha", "Hora Entrada", "Hora Salida", "Tiempo Trabajado", "Paga Diaria", "Total Devengado"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell);
        }

        // Llenar la tabla con los datos filtrados
        for (AsistenciaDTO asistencia : datosFiltrados) {
            table.addCell(String.valueOf(asistencia.getCodigoEmpleado()));
            table.addCell(asistencia.getNombre());
            table.addCell(asistencia.getApellido());
            table.addCell(asistencia.getFecha().toString());
            table.addCell(asistencia.getHoraEntrada().toString());
            table.addCell(asistencia.getHoraSalida().toString());
            table.addCell(asistencia.getHorasTrabajadas().toString());
            table.addCell(String.valueOf(asistencia.getMonto().doubleValue()));
            table.addCell(String.valueOf(asistencia.getTotal_devengado() != null ? asistencia.getTotal_devengado().doubleValue() : 0.0));
        }

        document.add(table);
    } catch (DocumentException | IOException e) {
        e.printStackTrace();
    } finally {
        document.close();
    }

    // Configurar la respuesta para descargar el PDF
    FacesContext facesContext = FacesContext.getCurrentInstance();
    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
    response.reset();
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=Reporte_Asistencia.pdf");

    try (OutputStream output = response.getOutputStream()) {
        baos.writeTo(output);
        facesContext.responseComplete();
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

   // Método para filtrar los datos por nombre y fechas
    public void filtrarPorFechas() {
    List<AsistenciaDTO> resultados = new ArrayList<>();
    
    // Verificar que las fechas no sean nulas
    if (fechaInicio != null && fechaFin != null) {
        for (AsistenciaDTO asistencia : lista) {
            // Verificar si el nombre coincide y si la fecha está dentro del rango
            boolean coincideNombre = (filtro == null || filtro.isEmpty()) || asistencia.getNombre().toLowerCase().contains(filtro.toLowerCase());
            boolean dentroDeFechas = !asistencia.getFecha().before(fechaInicio) && !asistencia.getFecha().after(fechaFin);
            
            if (coincideNombre && dentroDeFechas) {
                resultados.add(asistencia);
            }
        }
    } else {
        // Si las fechas no están definidas, filtrar solo por nombre
        for (AsistenciaDTO asistencia : lista) {
            if (filtro == null || filtro.isEmpty() || asistencia.getNombre().toLowerCase().contains(filtro.toLowerCase())) {
                resultados.add(asistencia);
            }
        }
    }
    
    // Actualizar la lista filtrada con los resultados
    listaFiltrada = resultados;
    }
 
        // Métodos que convierten java.util.Date a java.sql.Date cuando es necesario
    private java.sql.Date convertirUtilADateSql(java.util.Date fecha) {
        return fecha != null ? new java.sql.Date(fecha.getTime()) : null;
    }

   
    }
 