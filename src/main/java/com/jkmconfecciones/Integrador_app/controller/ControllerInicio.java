package com.jkmconfecciones.Integrador_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControllerInicio {

    @GetMapping("/")
    public String home() {
        return "index";
    }
    
}

