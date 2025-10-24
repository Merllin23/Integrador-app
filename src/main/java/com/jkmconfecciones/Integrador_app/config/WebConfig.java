package com.jkmconfecciones.Integrador_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String rutaImagenesExternas = "C:/jkm/productos/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/productos/**")
                .addResourceLocations("file:" + rutaImagenesExternas);
    }
}

