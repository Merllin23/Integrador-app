package com.jkmconfecciones.Integrador_app.config;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.service.Auditoria.AuditoriaService;
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
import java.time.LocalDateTime;

@Component
public class ManejadorExitoAutenticacion implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(ManejadorExitoAutenticacion.class);

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AuditoriaService auditoriaService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String correo = authentication.getName();

        // Buscar el usuario en BD (SEGURO hacer esto)
        Usuario usuario = usuarioService.buscarPorCorreo(correo).orElse(null);

        if (usuario != null) {
            // Registrar auditoría de login exitoso
            auditoriaService.registrarLogin(usuario, request);

            // Reiniciar intentos fallidos
            usuarioService.reiniciarIntentos(usuario);

            // Actualizar fecha de último login
            usuario.setFechaUltimoLogin(LocalDateTime.now());
            usuarioService.guardar(usuario);

            log.info("Usuario '{}' inició sesión correctamente. Intentos reiniciados y fecha de último login actualizada.", correo);
        } else {
            log.error("No se encontró el usuario '{}' después del login. ¡Revisar!", correo);
        }

        // Redirigir según rol
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
