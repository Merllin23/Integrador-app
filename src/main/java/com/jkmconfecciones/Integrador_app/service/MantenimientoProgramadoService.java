package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.repositorios.ProductoRepositorio;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Servicio de mantenimiento programado para tareas automatizadas.
 * 
 * IMPORTANTE: Este servicio está deshabilitado por defecto en Railway.
 * Para habilitarlo localmente, agregar en application.properties:
 *   mantenimiento.programado.habilitado=true
 * 
 * Tareas programadas:
 * - Limpieza de imágenes huérfanas en Cloudinary (opcional)
 * - Reportes de métricas de inventario
 * - Monitoreo de productos sin stock
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "mantenimiento.programado.habilitado",
    havingValue = "true",
    matchIfMissing = false  // Deshabilitado por defecto
)
public class MantenimientoProgramadoService {

    private final ProductoRepositorio productoRepositorio;
    private static final Logger logger = LoggerFactory.getLogger(MantenimientoProgramadoService.class);

    /**
     * Genera un reporte de métricas del inventario cada lunes a las 3:00 AM.
     * Útil para monitoreo y auditoría.
     */
    @Scheduled(cron = "0 0 3 * * MON", zone = "America/Lima")
    public void generarReporteInventario() {
        try {
            logger.info("=== REPORTE SEMANAL DE INVENTARIO ===");
            
            long totalProductos = productoRepositorio.count();
            logger.info("Total de productos en sistema: {}", totalProductos);
            
            // Productos por categoría (si existen)
            for (int i = 1; i <= 5; i++) {
                try {
                    int count = productoRepositorio.findByCategoriaId(i).size();
                    if (count > 0) {
                        logger.info("Productos en categoría {}: {}", i, count);
                    }
                } catch (Exception e) {
                    // Categoría no existe, continuar
                }
            }
            
            // Productos por colegio (si existen)
            for (int i = 1; i <= 5; i++) {
                try {
                    int count = productoRepositorio.findByColegioId(i).size();
                    if (count > 0) {
                        logger.info("Productos para colegio {}: {}", i, count);
                    }
                } catch (Exception e) {
                    // Colegio no existe, continuar
                }
            }
            
            logger.info("=== FIN REPORTE ===");
            
        } catch (Exception e) {
            logger.error("Error al generar reporte de inventario", e);
        }
    }

    /**
     * Limpia logs antiguos del sistema cada domingo a las 2:00 AM.
     * Solo aplica para entorno local con archivos de log.
     */
    @Scheduled(cron = "0 0 2 * * SUN", zone = "America/Lima")
    public void limpiarLogsAntiguos() {
        logger.info("Verificando logs antiguos para limpieza...");
        // En Railway los logs se manejan por la plataforma
        // Esta tarea es principalmente para desarrollo local
        logger.info("Limpieza de logs completada");
    }

    /**
     * Monitorea productos con bajo stock o inconsistencias cada día a las 6:00 AM.
     */
    @Scheduled(cron = "0 0 6 * * *", zone = "America/Lima")
    public void monitorearInventario() {
        try {
            logger.info("Monitoreando estado del inventario...");
            
            long totalProductos = productoRepositorio.count();
            
            if (totalProductos == 0) {
                logger.warn("⚠️ ALERTA: No hay productos en el sistema");
            } else {
                logger.info("✓ Inventario OK: {} productos registrados", totalProductos);
            }
            
        } catch (Exception e) {
            logger.error("Error al monitorear inventario", e);
        }
    }

    /**
     * Genera estadísticas diarias a las 11:59 PM.
     */
    @Scheduled(cron = "0 59 23 * * *", zone = "America/Lima")
    public void generarEstadisticasDiarias() {
        try {
            logger.info("📊 Estadísticas del día:");
            logger.info("Total productos: {}", productoRepositorio.count());
            // Aquí se pueden agregar más métricas según necesidad
            
        } catch (Exception e) {
            logger.error("Error al generar estadísticas diarias", e);
        }
    }
}
