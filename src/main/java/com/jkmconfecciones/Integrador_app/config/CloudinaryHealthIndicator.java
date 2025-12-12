package com.jkmconfecciones.Integrador_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health Check personalizado para verificar la configuración de Cloudinary.
 * Valida que las credenciales estén configuradas correctamente.
 * Se expone en /actuator/health (solo para ADMINISTRADOR).
 */
@Component
public class CloudinaryHealthIndicator implements HealthIndicator {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;
    
    @Value("${cloudinary.api-key}")
    private String apiKey;
    
    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Override
    public Health health() {
        try {
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

            if (apiSecret == null || apiSecret.equals("default") || apiSecret.isEmpty()) {
                return Health.down()
                        .withDetail("error", "API Secret no configurado")
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
