package com.jkmconfecciones.Integrador_app.controller;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControllerInicio {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String landing_page(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            String correo = userDetails.getUsername();
            usuarioService.buscarPorCorreo(correo).ifPresent(usuario -> {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("inicial", usuario.getNombre().substring(0, 1).toUpperCase());
            });
        }
        return "landing_page";
    }
}
