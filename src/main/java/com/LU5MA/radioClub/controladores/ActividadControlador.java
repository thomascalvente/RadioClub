package com.LU5MA.radioClub.controladores;

import com.LU5MA.radioClub.entidades.Actividad;
import com.LU5MA.radioClub.servicios.ActividadServicio;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("actividades")
public class ActividadControlador {

    @Autowired
    private ActividadServicio actividadServicio;
    
    
    
    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, ModelMap modelo) {

        Optional<Actividad> actividades = actividadServicio.findById(id);
        modelo.addAttribute("actividad", actividades.get());
        return "detail.html";
    }  
    
}
