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

import java.io.IOException;

@Component
public class ManejadorFalloAutenticacion implements AuthenticationFailureHandler {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String correo = request.getParameter("username"); // campo del form

        // Si la excepción es bloqueo temporal
        if (exception instanceof LockedException) {
            response.sendRedirect("/login?bloqueo=true");
            return;
        }

        // Incrementar intentos fallidos
        usuarioService.buscarPorCorreo(correo).ifPresent(usuarioService::aumentarIntentoFallido);

        response.sendRedirect("/login?error=true");
    }
}
