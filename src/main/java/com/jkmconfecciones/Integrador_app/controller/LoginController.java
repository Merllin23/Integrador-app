package com.jkmconfecciones.Integrador_app.controller;

import com.jkmconfecciones.Integrador_app.dto.LoginResponseDTO;
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

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String contraseña,
                                Model model) {

        LoginResponseDTO response = usuarioService.validarLogin(correo, contraseña);

        if (response.getUsuario() != null) {
            UsuarioDTO usuario = response.getUsuario();
            model.addAttribute("nombre", usuario.getNombre());
            model.addAttribute("rol", usuario.getRol());

            if ("Administrador".equalsIgnoreCase(usuario.getRol())) {
                return "redirect:/admin";
            } else {
                return "redirect:/usuario";
            }
        } else {
            model.addAttribute("error", response.getMensaje());
            return "index";
        }
    }
}
