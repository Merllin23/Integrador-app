package com.jkmconfecciones.Integrador_app.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControllerInicio {



    @GetMapping("/recuperar")
    public String recuperarPage() {
        return "recuperar-contrasena";
    }

    @GetMapping("/landing_page")
    public String landing_page() {
        return "landing_page";
    }
}
