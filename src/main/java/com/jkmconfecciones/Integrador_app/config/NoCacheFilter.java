package com.jkmconfecciones.Integrador_app.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NoCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Evitar almacenamiento en caché de cualquier página autenticada
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setHeader("Expires", "0");

        // Si el usuario intenta volver atrás tras cerrar sesión, forzar redirección al login
        if (httpRequest.getRequestURI().contains("/admin") || httpRequest.getRequestURI().contains("/usuario")) {
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        }

        chain.doFilter(request, response);
    }
}
