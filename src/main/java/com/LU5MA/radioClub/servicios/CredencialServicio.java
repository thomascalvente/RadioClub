package com.LU5MA.radioClub.servicios;

import com.LU5MA.radioClub.entidades.Actividad;
import com.LU5MA.radioClub.entidades.Credencial;
import com.LU5MA.radioClub.excepciones.MiException;
import com.LU5MA.radioClub.repositorios.ActividadRepositorio;
import com.LU5MA.radioClub.repositorios.CredencialRepositorio;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CredencialServicio {

    @Autowired
    private CredencialRepositorio credencialRepositorio;

    @Autowired
    private ActividadRepositorio actividadRepositorio;

    @Transactional
    public void crearCredencial(Long id_actividad, MultipartFile archivo) throws MiException, IOException {

        validar(archivo);

        if (!archivo.isEmpty()) {
            byte[] bytes = archivo.getBytes();
            Path path = Paths.get(".//src//main//resources//static//files//" + archivo.getOriginalFilename());
            Files.write(path, bytes);
        }

        String path = ".//src//main//resources//static//files//" + archivo.getOriginalFilename();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String linea;
            List<String> removerElementos = new ArrayList();
            for (int i = 0; i < 4; i++) {

                removerElementos.remove(linea = br.readLine());
            }

            while ((linea = br.readLine()) != null) {

                List<String> string = Arrays.asList(linea.replaceAll("<", ";").replaceAll(">", ";").split(";"));
                List<String> listaFiltrada = string.stream().filter(elem -> !elem.startsWith("PROGRAMID")
                        && !elem.equals("")
                        && !elem.equals(" ")
                        && !elem.startsWith("STATION_CALLSIGN")
                        && !elem.startsWith("CALL")
                        && !elem.startsWith("MODE")
                        && !elem.startsWith("MODE")
                        && !elem.startsWith("BAND")
                        && !elem.startsWith("FREQ")
                        && !elem.startsWith("QSO_DATE")
                        && !elem.startsWith("TIME_ON")
                        && !elem.startsWith("RST_SENT")
                        && !elem.startsWith("RST_RCVD")
                        && !elem.startsWith("OPERATOR")
                        && !elem.startsWith("COMMENT")
                        && !elem.startsWith("QSLMSG")
                        && !elem.startsWith("MY_GRIDSQUARE")
                        && !elem.startsWith("GRIDSQUARE")
                        && !elem.startsWith("EOR")
                        && !elem.startsWith("EOH")
                        && !elem.startsWith("EOF"))
                        .collect(Collectors.toList());

                if (!listaFiltrada.isEmpty()) {

                    String anio = listaFiltrada.get(5).substring(0, 4);
                    String mes = listaFiltrada.get(5).substring(4, 6);
                    String dia = listaFiltrada.get(5).substring(6, 8);

                    String hora = listaFiltrada.get(6).substring(0, 2);
                    String minutos = listaFiltrada.get(6).substring(2, 4);

                    Integer hr = Integer.parseInt(hora);
                    String rango;

                    if (hr <= 11) {
                        rango = "am";
                    } else {
                        rango = "pm";
                    }

                    credencialRepositorio.save(new Credencial(new Date(), id_actividad, listaFiltrada.get(0), listaFiltrada.get(1),
                            listaFiltrada.get(2), listaFiltrada.get(3),
                            listaFiltrada.get(4), dia + "/" + mes + "/" + anio,
                            hora + ":" + minutos + " " + rango, listaFiltrada.get(7),
                            listaFiltrada.get(8), listaFiltrada.get(9)));
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

    }

    public Optional<Credencial> findById(Long id) {
        return credencialRepositorio.findById(id);
    }

    public List<Credencial> listarCredenciales() {

        return credencialRepositorio.findAll();
    }

    public List<Credencial> listarCredencialesPorActividad(Long id) {

        return credencialRepositorio.buscarPorId(id);
    }

    public List<Credencial> buscarPorLicencia(String licencia, Long id) {

        return credencialRepositorio.buscarPorLicencia(licencia.toUpperCase(), id);
    }

    public List<String> traerTodasLasEstacionesLlamantes(String licencia, Long id) {

        return credencialRepositorio.buscarPorEstacionLlamante(licencia.toUpperCase(), id);
    }

    public Optional<Credencial> encontrarCredenciales(Long id) {

        return credencialRepositorio.findById(id);
    }

    @Transactional
    public void eliminarCredenciales(Long id) {

        credencialRepositorio.borrarCredenciales(id);
    }

    public Page<Credencial> traerCredencialesPorId(Long id, PageRequest pageable) {

        return credencialRepositorio.traerCredencialesPorId(id, pageable);
    }
    
    public String traerLicenciaPorId(Long id, Long idCredencial){
        System.out.println(credencialRepositorio.traerLicenciaPorId(id, idCredencial));
        return credencialRepositorio.traerLicenciaPorId(id, idCredencial);
    }

    /*public static void tomarCapturaCertificado(String fileName){
        
        try {
            ImageIO.write(redimensionarCertificado(), "png", new File(System.getProperty("user.home") +"//Downloads//"+fileName+"(certificado).png"));
            System.out.println("Screenshot saved");
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    public static BufferedImage redimensionarCertificado(){
        BufferedImage Image =null;
        try {
            
            System.setProperty("java.awt.headless", "false");
            Robot r = new Robot();

            Rectangle capture = new Rectangle(294,103,779,536);
            Image = r.createScreenCapture(capture);
        }catch (AWTException ex) {
            System.out.println(ex);
        }
            int ancho = Image.getWidth();
            int alto = Image.getHeight();
            int escalaAncho = (int)(ancho*1.643);
            int escalaAlto = (int)(alto*1.64);
            System.out.println("ancho: "+ancho+", alto: "+alto+", escalaAncho: "+escalaAncho+", escalaAlto: "+escalaAlto);
            BufferedImage bufim = new BufferedImage(escalaAncho, escalaAlto, Image.getType());
            Graphics2D g =bufim.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(Image, 0,0, escalaAncho, escalaAlto, 0,0,ancho,alto, null);
            g.dispose();
            return bufim;           
        
    }
    
    public static void tomarCapturaTarjeta(String fileName){

        try {
            ImageIO.write(redimensionarTarjeta(), "png", new File(System.getProperty("user.home") +"//Downloads//"+fileName+"(tarjeta).png"));
            System.out.println("Screenshot saved");
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }
    
    public static BufferedImage redimensionarTarjeta(){
        BufferedImage Image =null;
        try {
            
            System.setProperty("java.awt.headless", "false");
            Robot r = new Robot();

            Rectangle capture = new Rectangle(266,103,834,535); 
            Image = r.createScreenCapture(capture);
        }catch (AWTException ex) {
            System.out.println(ex);
        }
            int ancho = Image.getWidth();
            int alto = Image.getHeight();
            int escalaAncho = (int)(ancho*0.68);
            int escalaAlto = (int)(alto*0.64);
            System.out.println("ancho: "+ancho+", alto: "+alto+", escalaAncho: "+escalaAncho+", escalaAlto: "+escalaAlto);
            BufferedImage bufim = new BufferedImage(escalaAncho, escalaAlto, Image.getType());
            Graphics2D g =bufim.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(Image, 0,0, escalaAncho, escalaAlto, 0,0,ancho,alto, null);
            g.dispose();
            return bufim;           
        
    }*/
    public void descargarCertificado(Long id, String nombre, String licencia, long idCredencial) throws IOException {

        Optional<Actividad> actividades = actividadRepositorio.findById(id);

        final BufferedImage image = ImageIO.read(new File(".//src//main//resources//static//certificadosAndTarjetas//" + actividades.get().getCertificado()));

        Font licenciaFont = new Font("Serif", Font.ITALIC, 40);
        Font nombreFont = new Font("SansSerif", Font.BOLD, 20);

        Graphics g = image.getGraphics();
        if (licencia.contains("/")) {
            int textWidth = g.getFontMetrics().stringWidth(licencia);

            int x = ((image.getWidth()) / 2) - (textWidth / 2);
            int y = (image.getHeight() / 2)+10;

            g.setColor(Color.decode("#000000"));
            g.setFont(licenciaFont);
            g.drawString(licencia, x - (textWidth), y);

            g.dispose();

            ImageIO.write(image, "png", new File(System.getProperty("user.home") + "//Downloads//Certificado"+idCredencial+".png"));
        } else if(nombre.equals(licencia)){
            int textWidth = g.getFontMetrics().stringWidth(licencia);

            int x = ((image.getWidth()) / 2) - (textWidth / 2);
            int y = (image.getHeight() / 2)+10;

            g.setColor(Color.decode("#000000"));
            g.setFont(licenciaFont);
            g.drawString(licencia, x - (textWidth), y);

            g.dispose();

            ImageIO.write(image, "png", new File(System.getProperty("user.home") + "//Downloads//"+licencia+"(certificado)"+idCredencial+".png"));
        }else{
            int textWidth = g.getFontMetrics().stringWidth(licencia);
            int textWidth2 = g.getFontMetrics().stringWidth(nombre);

            int x = ((image.getWidth()) / 2) - (textWidth / 2);
            double x1 = (image.getWidth() / 2)- (textWidth2 / 1.225);

            g.setColor(Color.decode("#000000"));
            g.setFont(licenciaFont);
            g.drawString(licencia, x - (textWidth), 420);
            g.setFont(nombreFont);
            g.drawString(nombre, (int)x1, 480);
            g.dispose();

            ImageIO.write(image, "png", new File(System.getProperty("user.home") + "//Downloads//"+licencia+"(certificado)"+idCredencial+".png"));
        }

    }

    public void descargarTarjeta(Long id, String nombre, String licencia, long idCredencial) throws IOException {

        Optional<Actividad> actividades = actividadRepositorio.findById(id);
        List<Credencial> credenciales = credencialRepositorio.buscarPorLicenciaYId(licencia, id, idCredencial);

        final BufferedImage image = ImageIO.read(new File(".//src//main//resources//static//certificadosAndTarjetas//" + actividades.get().getTarjeta()));

        Font stringFont = new Font("Serif", Font.BOLD, 20);

        Graphics g = image.getGraphics();
        if (licencia.contains("/")) {
            

            int textLicencia = g.getFontMetrics().stringWidth(licencia);
            double xLicencia = ((image.getWidth()) / 6) - (textLicencia / 2);
            
            int textFreq = g.getFontMetrics().stringWidth(credenciales.get(0).getFreq());
            double xFreq = ((image.getWidth()) / 1.455) - (textFreq / 10);

            g.setColor(Color.decode("#000000"));
            g.setFont(stringFont);
            g.drawString(licencia, (int)xLicencia, 790);
            g.drawString(credenciales.get(0).getDate(), 515, 790);
            g.drawString(credenciales.get(0).getBanda(), 707, 790);
            g.drawString(credenciales.get(0).getTime(), 797, 790);
            g.drawString(credenciales.get(0).getFreq(), (int)xFreq, 790); //946
            g.drawString(credenciales.get(0).getRstSent(), 1068, 790);
            g.drawString(credenciales.get(0).getModo(), 1155, 790);

            g.dispose();

            ImageIO.write(image, "png", new File(System.getProperty("user.home") + "//Downloads//Tarjeta"+idCredencial+".png"));
        } else if(nombre.equals(licencia)){
            int textLicencia = g.getFontMetrics().stringWidth(licencia);
            double xLicencia = ((image.getWidth()) / 6) - (textLicencia / 2);
            
            int textFreq = g.getFontMetrics().stringWidth(credenciales.get(0).getFreq());
            double xFreq = ((image.getWidth()) / 1.455) - (textFreq / 10);

            g.setColor(Color.decode("#000000"));
            g.setFont(stringFont);
            g.drawString(licencia, (int)xLicencia, 790);
            g.drawString(credenciales.get(0).getDate(), 515, 790);
            g.drawString(credenciales.get(0).getBanda(), 707, 790);
            g.drawString(credenciales.get(0).getTime(), 797, 790);
            g.drawString(credenciales.get(0).getFreq(), (int)xFreq, 790); //946
            g.drawString(credenciales.get(0).getRstSent(), 1068, 790);
            g.drawString(credenciales.get(0).getModo(), 1155, 790);

            g.dispose();

            ImageIO.write(image, "png", new File(System.getProperty("user.home") + "//Downloads//"+licencia+"(tarjeta)"+idCredencial+".png"));
            
        }else{
            int textLicencia = g.getFontMetrics().stringWidth(licencia);
            double xLicencia = ((image.getWidth()) / 6) - (textLicencia / 2);
            
            int textNombre = g.getFontMetrics().stringWidth(nombre);
            double xNombre = ((image.getWidth()) / 7.6) - (textNombre / 2);
            
            int textFreq = g.getFontMetrics().stringWidth(credenciales.get(0).getFreq());
            double xFreq = ((image.getWidth()) / 1.455) - (textFreq / 10);
            

            g.setColor(Color.decode("#000000"));
            g.setFont(stringFont);
            g.drawString(licencia, (int)xLicencia, 770);
            g.drawString(nombre, (int)xNombre, 805);
            g.drawString(credenciales.get(0).getDate(), 515, 790);
            g.drawString(credenciales.get(0).getBanda(), 707, 790);
            g.drawString(credenciales.get(0).getTime(), 797, 790);
            g.drawString(credenciales.get(0).getFreq(), (int)xFreq, 790);
            g.drawString(credenciales.get(0).getRstSent(), 1068, 790);
            g.drawString(credenciales.get(0).getModo(), 1155, 790);
            
            g.dispose();

            ImageIO.write(image, "png", new File(System.getProperty("user.home") + "//Downloads//"+licencia+"(tarjeta)"+idCredencial+".png"));
        }

    }

    public String retornarNombreDeLaLicencia(String licencia) {

        List<String> listaRadioaficionados = new ArrayList();

        try {
            String nombre = "";
            FileInputStream archivo = new FileInputStream("C:\\Users\\PC\\Documents\\NetBeansProjects\\radioClub\\src\\main\\resources\\static\\listadoRadioaficionados\\Listado_Radioaficionados.xls");

            XSSFWorkbook libro = new XSSFWorkbook(archivo);

            XSSFSheet hoja = libro.getSheetAt(0);

            int numeroFilas = hoja.getLastRowNum();

            for (int i = 1; i <= numeroFilas; i++) {
                Row fila = hoja.getRow(i);
                int numeroColumnas = 2;

                for (int j = 0; j < numeroColumnas; j++) {

                    Cell celda = fila.getCell(j);

                    listaRadioaficionados.add(celda.getStringCellValue());
                }

            }

            for (int i = 0; i < listaRadioaficionados.size(); i++) {
                if (licencia.equals(listaRadioaficionados.get(i))) {
                    nombre = listaRadioaficionados.get(i - 1);
                }
            }
            if (nombre.isEmpty()) {
                nombre = licencia;
            }
            return nombre;

        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            return "";
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CredencialServicio.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        } catch (IOException ex) {
            Logger.getLogger(CredencialServicio.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }

    }

    private void validar(MultipartFile archivo) throws MiException {

        if (archivo.isEmpty() || archivo == null) {
            throw new MiException("Seleccione un archivo");
        }

    }
}
