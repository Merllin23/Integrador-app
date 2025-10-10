package com.jkmconfecciones.Integrador_app.controller;

import com.jkmconfecciones.Integrador_app.dto.UsuarioDTO;
import com.jkmconfecciones.Integrador_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String contraseña,
                                Model model) {

        UsuarioDTO usuario = usuarioService.validarLogin(correo, contraseña);

        if (usuario != null) {
            model.addAttribute("nombre", usuario.getNombre());
            model.addAttribute("rol", usuario.getRol());

            String rol = usuario.getRol();
            if ("Administrador".equalsIgnoreCase(rol)) {
                return "redirect:/admin";
            } else {
                return "redirect:/usuario";
            }
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "index";
        }
    }

}
