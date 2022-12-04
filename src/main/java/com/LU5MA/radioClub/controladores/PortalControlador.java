package com.LU5MA.radioClub.controladores;

import com.LU5MA.radioClub.entidades.Actividad;
import com.LU5MA.radioClub.excepciones.MiException;
import com.LU5MA.radioClub.servicios.ActividadServicio;
import com.LU5MA.radioClub.servicios.UsuarioServicio;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/")
public class PortalControlador { 

    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Autowired
    private ActividadServicio actividadServicio;
    
    @GetMapping("/")
    public String index(@RequestParam Map<String, Object> params, Model model, ModelMap modelo) { 
        
        List<Actividad> actividades = actividadServicio.listarActividades();
        modelo.addAttribute("actividades", actividades);

        
        return "index.html";
        

        
    }

    @PostMapping("/")
    public String Index(@RequestParam("titulo") String titulo,
            @RequestParam("cuerpo") String cuerpo, @RequestParam("file") MultipartFile imagen,
            @RequestParam("cantidadEstaciones") Integer cantidadEstaciones, @RequestParam("cantidadEstacionesObligatorias") Integer cantidadEstacionesObligatorias,
            @RequestParam("bases") MultipartFile bases, @RequestParam("certificado") MultipartFile certificado, 
            @RequestParam("tarjeta") MultipartFile tarjeta, @RequestParam("estacionesObligatorias") String estacionesObligatorias,
            @RequestParam("cantidadEstacionesTarjetas") Integer cantidadEstacionesTarjetas, ModelMap modelo) throws IOException {
        try {
            actividadServicio.crearActividad(titulo, cuerpo, imagen, cantidadEstaciones, cantidadEstacionesObligatorias, bases, certificado, tarjeta, estacionesObligatorias, cantidadEstacionesTarjetas);

            modelo.put("exito", "La actividad se cargó correctamente, porfavor espere un momento mientras redireccionamos al inicio...");

            return "mensaje_modificacion_creacion_eliminacion_actividades.html";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "actividades_form.html";
        }
    }
    
    @GetMapping("/registrar")
    public String registrar() {
        return "registro.html";
    }
    
    @PostMapping("/registro")
    public String registro(@RequestParam String licencia, @RequestParam String nombre, @RequestParam String email,
            @RequestParam String password, @RequestParam String password2, ModelMap modelo) {

        try {
            usuarioServicio.registrar(licencia.toUpperCase(), nombre, email, password, password2);

            modelo.put("exito", "Usuario registrado correctamente, porfavor espere un momento mientras redireccionamos al inicio...");


            return "mensaje_registro_usuarios.html";
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("licencia", licencia);
            modelo.put("nombre", nombre);
            modelo.put("email", email);
            return "registro.html";
        }
    }
    
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        if(error != null){
            modelo.put("error", "Usuario o Contraseña inválidos");
        }
        return "login.html";
    }
    
    

    @GetMapping("/zona")
    public String zona() {
        return "zona.html";
    }
    
    @GetMapping("/repetidora-147.345")
    public String repetidora() {
        return "repetidora.html";
    }

}
