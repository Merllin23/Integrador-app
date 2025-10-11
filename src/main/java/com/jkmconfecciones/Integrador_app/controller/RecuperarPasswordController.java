package com.jkmconfecciones.Integrador_app.controller;

import com.jkmconfecciones.Integrador_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RecuperarPasswordController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/recuperar-password")
    public String mostrarRecuperarPassword() {
        return "recuperar-password";
    }

    @PostMapping("/recuperar-password")
    public String procesarRecuperarPassword(@RequestParam("correo") String correo, Model model) {
        try {
            usuarioService.iniciarRecuperacionPassword(correo);
            model.addAttribute("mensaje", "Se han enviado las instrucciones a tu correo electrónico.");
            return "recuperar-password";
        } catch (Exception e) {
            model.addAttribute("error", true);
            model.addAttribute("mensaje", "Error: " + e.getMessage());
            e.printStackTrace(); // Para ver el error completo en la consola
            return "recuperar-password";
        }
    }
}