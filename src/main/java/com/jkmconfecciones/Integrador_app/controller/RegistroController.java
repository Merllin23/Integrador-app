package com.jkmconfecciones.Integrador_app.controller;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro"; // archivo registro.html
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {
        // Validar si ya existe un correo registrado
        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado");
            return "registro";
        }

        usuario.setEstado("activo");
        usuario.setFechaRegistro(java.time.LocalDateTime.now());
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);

        model.addAttribute("mensaje", "Registro exitoso. ¡Ya puedes iniciar sesión!");
        return "login"; // te redirige al login
    }
}
