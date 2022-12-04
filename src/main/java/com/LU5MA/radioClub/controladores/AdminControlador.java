package com.LU5MA.radioClub.controladores;

import com.LU5MA.radioClub.entidades.Actividad;
import com.LU5MA.radioClub.entidades.Credencial;
import com.LU5MA.radioClub.entidades.Usuario;
import com.LU5MA.radioClub.enumeraciones.Rol;
import com.LU5MA.radioClub.excepciones.MiException;
import com.LU5MA.radioClub.servicios.ActividadServicio;
import com.LU5MA.radioClub.servicios.CredencialServicio;
import com.LU5MA.radioClub.servicios.UsuarioServicio;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/admin")
public class AdminControlador {

    @Autowired
    private ActividadServicio actividadServicio;

    @Autowired
    private CredencialServicio credencialServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/dashboard")
    public String panelAdministrativo(@RequestParam Map<String, Object> params, Model model, ModelMap modelo) {

        List<Actividad> actividades = actividadServicio.listarActividades();
        modelo.addAttribute("actividades", actividades);
        
        
        

        return "index.html";
    }
    

    @GetMapping("/listar")
    public String listar(@RequestParam Map<String, Object> params, Model model, ModelMap modelo) {
        
        int page = params.get("page") != null ? (Integer.valueOf(params.get("page").toString()) -1 ) : 0;
        
        PageRequest pageRequest = PageRequest.of(page, 20);
        
        Page<Usuario> pageUsuarios = usuarioServicio.getAll(pageRequest);
        
        
        int totalPage = pageUsuarios.getTotalPages();
        
        
        if(totalPage > 0){
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }

        modelo.addAttribute("usuarios", pageUsuarios.getContent());
        
        model.addAttribute("current", page+1);
        
        model.addAttribute("next", page+2);
        
        model.addAttribute("prev", page);
        
        model.addAttribute("last", totalPage);
        
        return "listarUsuarios.html";
    }

    @GetMapping("/modificarRolUsuario/{id}/{rol}")
    public String modificar(@PathVariable String id, @PathVariable Rol rol, ModelMap modelo) {

        usuarioServicio.cambiarRol(id, rol);
        modelo.put("exito", "El rol del usuario se modificó correctamente, porfavor espere un momento para ser redireccionado...");

        return "mensaje_modificacion_y_eliminacion_usuarios.html";
    }

    @GetMapping("/eliminarUsuario/{id}")
    public String eliminar(@PathVariable String id, ModelMap modelo) {

        usuarioServicio.eliminar(id);
        modelo.put("exito", "El usuario se eliminó correctamente, porfavor espere un momento para ser redireccionado...");

        return "mensaje_modificacion_y_eliminacion_usuarios.html";
    }

    @PostMapping("/buscarUsuarios")
    public String buscar(@RequestParam("frase") String frase, ModelMap modelo) {

        List<Usuario> usuarios = usuarioServicio.buscarUsuarios(frase);
        modelo.addAttribute("usuarios", usuarios);
        modelo.addAttribute("buscarUsuarios", true);

        return "listarUsuarios.html";
    }
    
    

    @GetMapping("/publicar")
    public String actividades() {
        return "actividades_form.html";
    }
    
    @GetMapping("/credenciales/{id}")
    public String certificados(@RequestParam Map<String, Object> params, Model model, @PathVariable Long id, ModelMap modelo){

        int page = params.get("page") != null ? (Integer.valueOf(params.get("page").toString()) -1 ) : 0;
        
        
        PageRequest pageRequest = PageRequest.of(page, 20);
               
        
        Page<Credencial> pageCredencial = credencialServicio.traerCredencialesPorId(id, pageRequest);

        
        List<Credencial> credenciales = credencialServicio.listarCredencialesPorActividad(id);

        
        double totalPage = 0;
        
        if(credenciales.size() == 0){
            totalPage = 1;
        }else{
            totalPage = Math.ceil(credenciales.size()/20.0);
        }

        if(totalPage > 0){
            List<Integer> pages = IntStream.rangeClosed(1, (int)totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        
        Optional<Actividad> actividades = actividadServicio.findById(id);
        modelo.addAttribute("actividad", actividades.get());
        
        modelo.addAttribute("credenciales", pageCredencial.getContent());
        
        model.addAttribute("current", page+1);
        
        model.addAttribute("next", page+2);
        
        model.addAttribute("prev", page);
        
        model.addAttribute("last", (int)totalPage);
        
        model.addAttribute("admin", true);
        
        
        
        return "certificados.html";
    }

    @GetMapping("/cargar/{id}")
    public String cargar(@PathVariable Long id, ModelMap modelo) {

        Optional<Actividad> actividades = actividadServicio.findById(id);
        modelo.addAttribute("actividad", actividades.get());

        List<Credencial> credenciales = credencialServicio.listarCredencialesPorActividad(id);
        modelo.addAttribute("credenciales", credenciales);

        return "certificados_form.html";
    }

    @PostMapping("/guardado")
    public String Cargar(@RequestParam("id") Long id_actividad, @RequestParam("file") MultipartFile archivo, ModelMap modelo) throws IOException {

        Optional<Actividad> actividades = actividadServicio.findById(id_actividad);
        modelo.addAttribute("actividad", actividades.get());

        try {
            credencialServicio.crearCredencial(id_actividad, archivo);

            List<Credencial> credenciales = credencialServicio.listarCredencialesPorActividad(id_actividad);
            modelo.addAttribute("credenciales", credenciales);
            return "redirect:/admin/credenciales/"+id_actividad;
        } catch (MiException ex) {
            modelo.put("error", ex.getMessage());
            return "certificados_form.html";
        }

    }
    
    @GetMapping("/eliminarCredenciales/{id}")
    public String eliminarCredenciales(@PathVariable Long id, ModelMap modelo) {
        
        credencialServicio.eliminarCredenciales(id);
        return "redirect:/admin/credenciales/"+id;
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        Optional<Actividad> actividad = actividadServicio.findById(id);
        modelo.addAttribute("actividad", actividad.get());
        modelo.addAttribute("titulo", actividad.get().getTitulo());
        modelo.addAttribute("Cuerpo", actividad.get().getCuerpo());
        modelo.addAttribute("cantidadEstaciones", actividad.get().getCantidadEstaciones());
        modelo.addAttribute("cantidadEstacionesObligatorias", actividad.get().getCantidadEstacionesObligatorias());
        modelo.addAttribute("EstacionesObligatorias", actividad.get().getEstacionesObligatorias());
        modelo.addAttribute("cantidadEstacionesTarjetas", actividad.get().getCantidadEstacionesTarjetas());
        //modelo.addAttribute("file", posteos.get().getImagen());

        return "modificar_form.html";
    }

    @PostMapping("/modificado")
    public String modificado(@RequestParam("id") Long id, @RequestParam("titulo") String titulo,
            @RequestParam("cuerpo") String cuerpo, @RequestParam("file") MultipartFile imagen,
            @RequestParam("cantidadEstaciones") Integer cantidadEstaciones, @RequestParam("cantidadEstacionesObligatorias") Integer cantidadEstacionesObligatorias,
            @RequestParam("bases") MultipartFile bases, @RequestParam("certificado") MultipartFile certificado,
            @RequestParam("tarjeta") MultipartFile tarjeta, @RequestParam("estacionesObligatorias") String estacionesObligatorias,
            @RequestParam("cantidadEstacionesTarjetas") Integer cantidadEstacionesTarjetas, LocalDate fecha, ModelMap modelo) throws MiException, IOException {

        try {
            actividadServicio.actualizar(id, titulo, cuerpo, imagen, cantidadEstaciones, cantidadEstacionesObligatorias, bases, certificado, tarjeta, estacionesObligatorias, cantidadEstacionesTarjetas, fecha);
            modelo.put("exito", "La actividad se modificó correctamente, porfavor espere un momento mientras redireccionamos al inicio...");

            return "mensaje_modificacion_creacion_eliminacion_actividades.html";
        } catch (MiException e) {
            
            modelo.put("error", e.getMessage());
            modelo.put("titulo", titulo);
            modelo.addAttribute("cuerpo", cuerpo);
            return "modificar_form.html";
        }

    }

    @GetMapping("/eliminar/{id}")
    public String eliminarActividad(@PathVariable Long id, ModelMap modelo) {
        //actividadServicio.eliminar(id, fecha);
        actividadServicio.borrar(id);
        credencialServicio.eliminarCredenciales(id);
        modelo.put("exito", "El posteo se eliminó correctamente, porfavor espere un momento mientras redireccionamos al inicio...");
        
        
        
        //Thread.currentThread().sleep(5*1000); timer, esperar 5 segundos y redireccionar
        
        
        
        return "mensaje_modificacion_creacion_eliminacion_actividades.html";
    }

}
