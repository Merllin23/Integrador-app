package com.jkmconfecciones.Integrador_app.controller;

import com.jkmconfecciones.Integrador_app.service.RecuperarCuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class RecuperarCuentaController {

    private final RecuperarCuentaService recuperarCuentaService;

    @GetMapping("/recuperar")
    public String mostrarFormularioRecuperar() {
        return "recuperar-contrasena";
    }

    @PostMapping("/recuperar")
    public String procesarRecuperar(@RequestParam("correo") String correo, Model model) {
        recuperarCuentaService.enviarCorreoRecuperacion(correo);
        model.addAttribute("mensaje", "Si el correo está registrado, se enviará un enlace para restablecer la contraseña.");
        return "recuperar-contrasena";
    }

    @GetMapping("/restablecer")
    public String mostrarFormularioRestablecer(@RequestParam("token") String token, Model model) {
        if (!recuperarCuentaService.validarToken(token)) {
            model.addAttribute("error", "El enlace es inválido o ha expirado.");
            return "error_token";
        }
        model.addAttribute("token", token);
        return "restablecer";
    }

    @PostMapping("/restablecer")
    public String procesarRestablecer(@RequestParam("token") String token,
                                      @RequestParam("contraseña") String contraseña,
                                      @RequestParam("confirmar") String confirmar,
                                      Model model) {
        if (!contraseña.equals(confirmar)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            model.addAttribute("token", token);
            return "restablecer";
        }

        boolean exito = recuperarCuentaService.actualizarContraseña(token, contraseña);
        if (!exito) {
            model.addAttribute("error", "El enlace es inválido o ha expirado.");
            return "error_token";
        }

        model.addAttribute("mensaje", "Tu contraseña se actualizó correctamente.");
        return "index";
    }
}
