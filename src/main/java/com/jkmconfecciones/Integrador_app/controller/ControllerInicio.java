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

}

