package com.jkmconfecciones.Integrador_app.controller.usuario;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio;
import com.jkmconfecciones.Integrador_app.service.Usuario.PerfilUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PerfilUsuarioService perfilUsuarioService;

    @GetMapping("/usuario")
    public String paginaUsuario(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("inicial", usuario.getNombre().substring(0, 1).toUpperCase());

        return "usuario/usuario";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(Usuario datosActualizados, RedirectAttributes redirectAttrs) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        perfilUsuarioService.actualizarPerfil(correo,
                datosActualizados.getNombre(),
                datosActualizados.getTelefono(),
                datosActualizados.getDireccion()
        );

        redirectAttrs.addFlashAttribute("mensaje", "✅ Perfil actualizado correctamente.");
        return "redirect:/usuario/usuario";
    }
}
