package com.jkmconfecciones.Integrador_app.config;

import com.jkmconfecciones.Integrador_app.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class ManejadorExitoAutenticacion implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(ManejadorExitoAutenticacion.class);

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String correo = authentication.getName();

        usuarioService.buscarPorCorreo(correo).ifPresent(usuario -> {
            usuarioService.reiniciarIntentos(usuario);
            log.info("Usuario '{}' inició sesión correctamente. Intentos reiniciados.", correo);
        });

        // Redirigir según el rol directamente
        String rol = authentication.getAuthorities().iterator().next().getAuthority();

        if ("ROLE_ADMINISTRADOR".equals(rol)) {
            response.sendRedirect("/admin/panel");
        } else if ("ROLE_USUARIO".equals(rol)) {
            response.sendRedirect("/");
        } else {
            response.sendRedirect("/login?error=true");
        }
    }
}
