package com.jkmconfecciones.Integrador_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControllerInicio {

    @GetMapping("/")
    public String home(@RequestParam(value = "registro", required = false) String registro, Model model) {
        return "index";
    }
    @GetMapping("/admin")
    public String adminPage(Model model) {
        // Si acceden directamente sin autenticación, redirigir al login
        model.addAttribute("mensaje", "Accede desde el panel de administración");
        return "admin";
    }

    @GetMapping("/pedidos")
    public String pedidosPage(Model model) {
        // Si acceden directamente sin autenticación, redirigir al login
        model.addAttribute("mensaje", "Accede desde el panel de pedidos");
        return "pedidos";
    }

    @GetMapping("/usuario")
    public String usuarioPage(Model model) {
        // Si acceden directamente sin autenticación, redirigir al login
        model.addAttribute("mensaje", "Accede desde el panel de usuario");
        return "usuario";
    }

    @GetMapping("/recuperar")
    public String recuperarPage(Model model) {
        return "recuperar-contrasena";
    }
    
    @GetMapping("/landing_page")
    public String landing_page() {
        return "landing_page";
    }

}

