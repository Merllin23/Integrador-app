package com.jkmconfecciones.Integrador_app.controller;

import com.jkmconfecciones.Integrador_app.entidades.Rol;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.RolRepository;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute Usuario usuario, Model model) {

        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            model.addAttribute("error", "El correo ya está registrado");
            model.addAttribute("usuario", new Usuario()); // limpia los campos
            return "registro";
        }

    // rol por defecto (según BD actual: 'Usuario')
    Rol rolUsuario = rolRepository.findByNombreRol("Usuario")
        .orElseThrow(() -> new RuntimeException("No se encontró el rol 'Usuario'"));

        String hash = BCrypt.hashpw(usuario.getContraseña(), BCrypt.gensalt());
        usuario.setContraseña(hash);

        usuario.setRol(rolUsuario);
        usuario.setEstado("activo");
        usuario.setFechaRegistro(java.time.LocalDateTime.now());
        usuario.setIntentosFallidos(0);

        usuarioRepository.save(usuario);

        return "redirect:/";
    }
}
