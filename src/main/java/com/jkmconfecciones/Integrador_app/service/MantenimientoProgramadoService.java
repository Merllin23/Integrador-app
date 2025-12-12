package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.repositorios.ProductoRepositorio;
import com.jkmconfecciones.Integrador_app.service.ProductoService.ProductoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MantenimientoProgramadoService {

    private final ProductoRepositorio productoRepositorio;
    private final ProductoService productoService; // inyectar para exportar Excel
    private static final Logger logger = LoggerFactory.getLogger(MantenimientoProgramadoService.class);

    // Limpiar imágenes antiguas diariamente a las 2:00 am
    @Scheduled(cron = "0 0 2 * * ?")
    public void limpiarImagenesAntiguas() {
        logger.info("Iniciando limpieza de imágenes antiguas...");
        File carpeta = new File("C:\\jkm\\productos\\");
        if(carpeta.exists()) {
            for(File archivo : carpeta.listFiles()) {
                boolean usado = productoRepositorio.existsByImagenUrl("/productos/" + archivo.getName());
                if(!usado) {
                    archivo.delete();
                    logger.info("Eliminada imagen no usada: {}", archivo.getName());
                }
            }
        }
    }

    // Exportar inventario semanalmente
    @Scheduled(cron = "0 0 3 * * MON")
    public void exportarInventarioBackup() {
        logger.info("Exportando inventario como backup semanal...");
        try {
            byte[] excel = productoService.exportarInventarioExcel(null); // null = todo el inventario
            File backupDir = new File("C:\\jkm\\backups\\");
            if(!backupDir.exists()) backupDir.mkdirs();
            File file = new File(backupDir, "inventario_" + System.currentTimeMillis() + ".xlsx");
            java.nio.file.Files.write(file.toPath(), excel);
            logger.info("Inventario exportado correctamente: {}", file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Error al exportar inventario para backup", e);
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void ejecutarBackup() {
        try {
            logger.info("Iniciando backup automático...");
            Runtime.getRuntime().exec("C:\\ruta\\backup_integrador.bat");
            logger.info("Backup automático finalizado.");
        } catch (IOException e) {
            logger.error("Error al ejecutar backup automático", e);
        }
    }

    // Ejecutar limpieza de logs semanalmente Lunes 3:00 AM
    @Scheduled(cron = "0 0 3 * * MON")
    public void limpiarLogs() {
        try {
            logger.info("Iniciando limpieza automática de logs...");
            Runtime.getRuntime().exec("C:\\ruta\\limpiar_logs.bat");
            logger.info("Limpieza de logs finalizada.");
        } catch (IOException e) {
            logger.error("Error al limpiar logs automáticamente", e);
        }
    }
}
