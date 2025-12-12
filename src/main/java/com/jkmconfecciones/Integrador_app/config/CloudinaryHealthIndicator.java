package com.jkmconfecciones.Integrador_app.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Health Check personalizado para verificar la conexión con Cloudinary.
 * Valida que las credenciales estén configuradas y la API sea accesible.
 * Se expone en /actuator/health (solo para ADMINISTRADOR).
 */
@Component
public class CloudinaryHealthIndicator implements HealthIndicator {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    public Health health() {
        try {
            // Obtener configuración de Cloudinary
            Map<String, Object> config = cloudinary.config.asMap();
            String cloudName = (String) config.get("cloud_name");
            String apiKey = (String) config.get("api_key");
            
            // Validar que las credenciales estén configuradas
            if (cloudName == null || cloudName.equals("default") || cloudName.isEmpty()) {
                return Health.down()
                        .withDetail("error", "Cloudinary no configurado correctamente")
                        .withDetail("cloud_name", "no definido")
                        .build();
            }

            if (apiKey == null || apiKey.equals("default") || apiKey.isEmpty()) {
                return Health.down()
                        .withDetail("error", "API Key no configurada")
                        .withDetail("cloud_name", cloudName)
                        .build();
            }
            
            // Configuración válida
            return Health.up()
                    .withDetail("cloud_name", cloudName)
                    .withDetail("status", "configurado")
                    .withDetail("api_key", apiKey.substring(0, Math.min(4, apiKey.length())) + "****")
                    .build();
                    
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "error")
                    .build();
        }
    }
}
