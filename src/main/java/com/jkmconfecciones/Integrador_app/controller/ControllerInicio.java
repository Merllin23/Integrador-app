package com.jkmconfecciones.Integrador_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControllerInicio {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        return "admin";
    }

    @GetMapping("/usuario")
    public String usuarioPage(Model model) {
        return "usuario";
    }

    @GetMapping("/recuperar")
    public String recuperarPage(Model model) {
        return "recuperar-contrasena";
    }

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        return "registro";
    }

}

