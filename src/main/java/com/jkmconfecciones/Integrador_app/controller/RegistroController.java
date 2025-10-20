package com.jkmconfecciones.Integrador_app.controller;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.service.RegistroService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistroController {

    @Autowired
    private RegistroService registroService;

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario,
                            @RequestParam("g-recaptcha-response") String captcha,
                            HttpServletRequest request,
                            Model model) {

        String ip = request.getRemoteAddr();
        String resultado = registroService.registrarUsuario(usuario, captcha, ip);

        if (!resultado.equals("OK")) {
            model.addAttribute("error", resultado);
            return "registro";
        }

        model.addAttribute("exito", "Cuenta creada correctamente. ¡Ya puedes iniciar sesión!");
        return "index";
    }
}
