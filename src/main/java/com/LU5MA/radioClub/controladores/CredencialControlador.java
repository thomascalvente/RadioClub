package com.LU5MA.radioClub.controladores;

import com.LU5MA.radioClub.entidades.Actividad;
import com.LU5MA.radioClub.entidades.Credencial;
import com.LU5MA.radioClub.servicios.ActividadServicio;
import com.LU5MA.radioClub.servicios.CredencialServicio;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("credenciales")
public class CredencialControlador {
    
    @Autowired
    private CredencialServicio credencialServicio;
    
    @Autowired
    private ActividadServicio actividadServicio;
    
    @GetMapping("/tarjetasYCertificados/{id}")
    public String tarjetasYCertificados(@PathVariable Long id, ModelMap modelo){
        
        Optional<Actividad> actividades = actividadServicio.findById(id);
        modelo.addAttribute("actividad", actividades.get());
        
        List<Credencial> credenciales = credencialServicio.listarCredencialesPorActividad(id);
        modelo.addAttribute("credenciales", credenciales);
        
        return "buscador.html";
    }
    
    @PostMapping("/buscar/{id}")
    public String buscar(@PathVariable Long id, @RequestParam("licencia") String licencia, ModelMap modelo){
        
        modelo.addAttribute("admin", false);

        Optional<Actividad> actividades = actividadServicio.findById(id);
        modelo.addAttribute("actividad", actividades.get());
 
        List<Credencial> credenciales = credencialServicio.buscarPorLicencia(licencia.toUpperCase(), id);
        modelo.addAttribute("credenciales", credenciales);
       
        String nombre = credencialServicio.retornarNombreDeLaLicencia(licencia.toUpperCase());
        modelo.addAttribute("nombre", nombre.toUpperCase());
        
        if(nombre.equals(licencia.toUpperCase())){
            boolean band = true;
            modelo.addAttribute("band", band);
        }else{
            boolean band = false;
            modelo.addAttribute("band", band);
        }
        
        List<String> estacionesLlamantes = credencialServicio.traerTodasLasEstacionesLlamantes(licencia, id);

        HashSet<String> cantidadEstaciones = new HashSet<>(estacionesLlamantes);
        
        List<String> estacionesLlamantesFiltrada = new ArrayList<>(cantidadEstaciones);
        
        
        int contEstaciones = 0;
        
        List<String> estacionesObligatorias = new ArrayList<String>(Arrays.asList(actividades.get().getEstacionesObligatorias().replace(";", " ").replace("  ", " ").split(" ")));
        
        List<String> estacionesObligatoriasFiltrada = estacionesObligatorias.stream().filter(elem -> !elem.isEmpty()).collect(Collectors.toList());

        
        for (int i = 0; i < estacionesLlamantesFiltrada.size(); i++) {
            for (int j = 0; j < estacionesObligatoriasFiltrada.size(); j++) {
                if(estacionesLlamantesFiltrada.get(i).equals(estacionesObligatoriasFiltrada.get(j))){
                    contEstaciones++;
                }
            }    
        }   
        
        if(cantidadEstaciones.isEmpty()){
            modelo.put("fatal", "No hay ninguna licencia con ese nombre: "+nombre.toUpperCase());            
        }else if(cantidadEstaciones.size() >= actividades.get().getCantidadEstaciones() && contEstaciones == estacionesObligatoriasFiltrada.size() && actividades.get().getCantidadEstacionesTarjetas() == 0){
            modelo.put("exito", "Felicidades por conseguirlo "+nombre.toUpperCase()+", aqui tiene su tarjeta y su certificado");
        }else if(cantidadEstaciones.size() >= actividades.get().getCantidadEstacionesTarjetas() && actividades.get().getCantidadEstaciones() == 0 && actividades.get().getCantidadEstacionesObligatorias() == 0){
            modelo.put("bien", "Felicidades por conseguirlo, aqui tiene su tarjeta "+nombre.toUpperCase());
        }else if(cantidadEstaciones.size() >= actividades.get().getCantidadEstaciones() && contEstaciones != estacionesObligatoriasFiltrada.size()){ 
            modelo.put("bueno", "Estás cada vez mas cerca de llegar a la meta "+nombre.toUpperCase()+", seguí asi!!");
        }else if(cantidadEstaciones.size() < actividades.get().getCantidadEstaciones() && contEstaciones == estacionesObligatoriasFiltrada.size()){
            modelo.put("bueno", "Estás cada vez mas cerca de llegar a la meta "+nombre.toUpperCase()+", seguí asi!!");
        }else{
            modelo.put("error", "Lo siento "+nombre.toUpperCase()+". No tiene los requisitos suficientes");
        }

        
        return "certificados.html";
    }
    
    @GetMapping("/buscar/{id}/descarga-certificado/{idCredencial}")
    public String capturaCertificado(@PathVariable Long id, @PathVariable Long idCredencial, ModelMap modelo) throws InterruptedException, IOException{
        
        String licencia = credencialServicio.traerLicenciaPorId(id, idCredencial);
        
        String nombre = credencialServicio.retornarNombreDeLaLicencia(licencia.toUpperCase());
        
        credencialServicio.descargarCertificado(id, nombre.toUpperCase(), licencia.toUpperCase(), idCredencial);
        
        
        Optional<Actividad> actividades = actividadServicio.findById(id);
        modelo.addAttribute("actividad", actividades.get());
        
        modelo.addAttribute("licencia", licencia);
        
        if(licencia.contains("/")){
            modelo.put("exito", "Imagen descargada con éxito, porfavor vaya a la carpeta Descargas para verla con el nombre: \"Certificado"+idCredencial+".png\"");
        }else{
            modelo.put("exito", "Imagen descargada con éxito, porfavor vaya a la carpeta Descargas para verla con el nombre: \""+licencia+"(certificado)"+idCredencial+".png\"");
        }
        
        
        Thread.currentThread().sleep(1000);

        return "descarga_realizada.html";
    }
    
    @GetMapping("/buscar/{id}/descarga-tarjeta/{idCredencial}")
    public String capturaTarjeta(@PathVariable Long id, @PathVariable Long idCredencial, ModelMap modelo) throws InterruptedException, IOException{
        
        String licencia = credencialServicio.traerLicenciaPorId(id, idCredencial);

        String nombre = credencialServicio.retornarNombreDeLaLicencia(licencia.toUpperCase());
        
        credencialServicio.descargarTarjeta(id, nombre.toUpperCase(), licencia.toUpperCase(), idCredencial);
        
        Optional<Actividad> actividades = actividadServicio.findById(id);
        modelo.addAttribute("actividad", actividades.get());
        
        modelo.addAttribute("licencia", licencia);
        
        if(licencia.contains("/")){
            modelo.put("exito", "Imagen descargada con éxito, porfavor vaya a la carpeta Descargas para verla con el nombre: \"Tarjeta"+idCredencial+".png\"");
        }else{
            modelo.put("exito", "Imagen descargada con éxito, porfavor vaya a la carpeta Descargas para verla con el nombre: \""+licencia+"(tarjeta)"+idCredencial+".png\"");
        }
        
        
        Thread.currentThread().sleep(1000);
        
        return "descarga_realizada.html";
    }
}
