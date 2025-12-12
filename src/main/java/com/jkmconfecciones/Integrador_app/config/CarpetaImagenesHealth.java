package com.jkmconfecciones.Integrador_app.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import java.io.File;

@Component
public class CarpetaImagenesHealth implements HealthIndicator {

    private static final String RUTA_IMAGENES = "C:/jkm/productos/";

    @Override
    public Health health() {
        File carpeta = new File(RUTA_IMAGENES);
        if (carpeta.exists() && carpeta.canRead()) {
            return Health.up().withDetail("ruta", RUTA_IMAGENES).build();
        } else {
            return Health.down().withDetail("ruta", RUTA_IMAGENES).build();
        }
    }
}

