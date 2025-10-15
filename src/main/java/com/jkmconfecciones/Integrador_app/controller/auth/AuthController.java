package com.jkmconfecciones.Integrador_app.controller.auth;

import com.jkmconfecciones.Integrador_app.dto.LoginResponseDTO;
import com.jkmconfecciones.Integrador_app.dto.UsuarioDTO;
import com.jkmconfecciones.Integrador_app.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String contraseña,
                                Model model,
                                HttpSession session) {

        LoginResponseDTO response = usuarioService.validarLogin(correo, contraseña);

        if (response.getUsuario() != null) {
            UsuarioDTO usuario = response.getUsuario();
            session.setAttribute("usuario", usuario);

            // Redirección según el rol
            if ("Administrador".equalsIgnoreCase(usuario.getRol())) {
                return "redirect:/admin";
            } else {
                return "redirect:/usuario";
            }

        } else {
            model.addAttribute("error", response.getMensaje());
            return "index"; // retorna la vista del login
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Cierra la sesión
        return "redirect:/";  //vuelve al login principal (index.html)
    }
}
