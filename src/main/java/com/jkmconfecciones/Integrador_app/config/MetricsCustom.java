package com.jkmconfecciones.Integrador_app.config;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.jkmconfecciones.Integrador_app.repositorios.ProductoRepositorio;

/**
 * Métricas personalizadas para monitoreo de la aplicación.
 * Expone contadores y gauges sobre productos, categorías y colegios.
 * Se puede consultar en /actuator/metrics (solo para ADMINISTRADOR).
 */
@Component
@RequiredArgsConstructor
public class MetricsCustom {

    private final ProductoRepositorio productoRepositorio;
    private final MeterRegistry meterRegistry;
    private static final Logger logger = LoggerFactory.getLogger(MetricsCustom.class);

    @PostConstruct
    public void initMetrics() {
        try {
            logger.info("Inicializando métricas personalizadas...");
            
            // Métrica: cantidad de productos totales en el sistema
            meterRegistry.gauge("productos.totales", productoRepositorio, repo -> {
                try {
                    return repo.count();
                } catch (Exception e) {
                    logger.error("Error al obtener productos totales", e);
                    return 0;
                }
            });

            // Métrica: productos por categoría (ejemplos para las primeras categorías)
            for (int i = 1; i <= 5; i++) {
                final int categoriaId = i;
                meterRegistry.gauge("productos.categoria." + categoriaId, productoRepositorio,
                        repo -> {
                            try {
                                return repo.findByCategoriaId(categoriaId).size();
                            } catch (Exception e) {
                                logger.error("Error al obtener productos de categoría " + categoriaId, e);
                                return 0;
                            }
                        });
            }

            // Métrica: productos por colegio (ejemplos para los primeros colegios)
            for (int i = 1; i <= 5; i++) {
                final int colegioId = i;
                meterRegistry.gauge("productos.colegio." + colegioId, productoRepositorio,
                        repo -> {
                            try {
                                return repo.findByColegioId(colegioId).size();
                            } catch (Exception e) {
                                logger.error("Error al obtener productos de colegio " + colegioId, e);
                                return 0;
                            }
                        });
            }
            
            logger.info("Métricas personalizadas inicializadas correctamente");
            
        } catch (Exception e) {
            logger.error("Error al inicializar métricas personalizadas", e);
        }
    }
}
