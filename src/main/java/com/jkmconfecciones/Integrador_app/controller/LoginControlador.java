package com.jkmconfecciones.Integrador_app.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginControlador {

    @GetMapping("/login")
    public String mostrarLogin(Authentication auth, HttpServletResponse response) {
        // Evita que el navegador guarde esta página en caché
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Si ya está autenticado, redirige según rol
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/redireccion";
        }

        return "index"; // tu vista de login
    }

    @GetMapping("/redireccion")
    public String redirigirSegunRol(Authentication auth) {
        String rol = auth.getAuthorities().iterator().next().getAuthority();

        if (rol.equals("ROLE_ADMINISTRADOR")) {
            return "redirect:/admin/panel";
        } else if (rol.equals("ROLE_USUARIO")) {
            return "redirect:/usuario/usuario";
        } else {
            return "redirect:/login?error=true";
        }
    }
}
