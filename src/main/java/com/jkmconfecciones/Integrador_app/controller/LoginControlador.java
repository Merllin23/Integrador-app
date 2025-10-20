package com.jkmconfecciones.Integrador_app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginControlador {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "index";
    }

    @GetMapping("/redireccion")
    public String redirigirSegunRol(Authentication auth) {
        String rol = auth.getAuthorities().iterator().next().getAuthority();

        if (rol.equals("ROLE_ADMINISTRADOR")) {
            return "redirect:/admin/admin";
        } else if (rol.equals("ROLE_USUARIO")) {
            return "redirect:/usuario/usuario";
        } else {
            return "redirect:/login?error=true";
        }
    }
}