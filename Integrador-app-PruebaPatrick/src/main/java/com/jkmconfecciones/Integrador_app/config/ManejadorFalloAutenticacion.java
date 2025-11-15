package com.jkmconfecciones.Integrador_app.config;

import com.jkmconfecciones.Integrador_app.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class ManejadorFalloAutenticacion implements AuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(ManejadorFalloAutenticacion.class); // 👈 Logger

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String correo = request.getParameter("username");

        if (exception instanceof LockedException) {
            log.warn("Usuario '{}' bloqueado temporalmente al intentar iniciar sesión.", correo);
            response.sendRedirect("/login?bloqueo=true");
            return;
        }

        usuarioService.buscarPorCorreo(correo).ifPresent(usuario -> {
            usuarioService.aumentarIntentoFallido(usuario);
            log.warn("Intento fallido de inicio de sesión para '{}'.", correo);
        });

        log.error("Error de autenticación: {}", exception.getMessage());
        response.sendRedirect("/login?error=true");
    }
}
