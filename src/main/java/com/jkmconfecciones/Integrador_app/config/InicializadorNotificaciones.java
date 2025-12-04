package com.jkmconfecciones.Integrador_app.config;

import com.jkmconfecciones.Integrador_app.service.NotificacionAutomaticaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InicializadorNotificaciones implements ApplicationRunner {

    private final NotificacionAutomaticaService notificacionAutomaticaService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Inicializando notificaciones automáticas...");
        
        // Verificar stock crítico al iniciar
        try {
            notificacionAutomaticaService.verificarStockCritico();
            log.info("Verificación inicial de stock crítico completada");
        } catch (Exception e) {
            log.error("Error al verificar stock crítico inicial", e);
        }
    }
}
