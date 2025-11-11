package com.jkmconfecciones.Integrador_app.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class RedireccionSiAutenticadoFilter extends OncePerRequestFilter {

    // Rutas públicas donde no deberían entrar usuarios logueados
    private static final Set<String> RUTAS_PUBLICAS = Set.of(
            "/login",
            "/registro",
            "/recuperar",
            "/recuperar-contrasena",
            "/restablecer"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // Ignorar recursos estáticos
        if (path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/images") || path.startsWith("/api")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean estaAutenticado = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);

        if (estaAutenticado) {
            // Si intenta entrar a una página pública estando autenticado
            for (String ruta : RUTAS_PUBLICAS) {
                if (path.equals(ruta) || path.startsWith(ruta + "/")) {
                    if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"))) {
                        response.sendRedirect("/admin/panel");
                        return;
                    } else if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USUARIO"))) {
                        response.sendRedirect("/usuario/");
                        return;
                    }
                }
            }

            // Evita volver atrás con caché del navegador
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        }

        filterChain.doFilter(request, response);
    }
}
