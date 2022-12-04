package com.LU5MA.radioClub.controladores;

import com.LU5MA.radioClub.entidades.Actividad;
import com.LU5MA.radioClub.entidades.Usuario;
import com.LU5MA.radioClub.servicios.ActividadServicio;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/inicio")
public class UsuarioControlador {
    
    @Autowired
    private ActividadServicio actividadServicio;
    
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("")
    public String inicio(@RequestParam Map<String, Object> params, Model model, HttpSession session, ModelMap modelo) {
        
        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        
        if(logueado.getRol().toString().equals("ADMIN")){
            return "redirect:/admin/dashboard";
        }
        
        List<Actividad> actividades = actividadServicio.listarActividades();
        modelo.addAttribute("actividades", actividades);
        
        
        
        
        
        return "index.html";
    }
    
}
