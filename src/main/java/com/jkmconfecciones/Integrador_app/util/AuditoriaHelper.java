package com.jkmconfecciones.Integrador_app.util;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.service.Auditoria.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuditoriaHelper {

    private final AuditoriaService auditoriaService;

    //Registrar acción exitosa
     
    public void registrarExito(Usuario usuario, String accion, String recurso, Long recursoId, 
                               String detalles, HttpServletRequest request) {
        try {
            auditoriaService.registrarAccion(usuario, accion, recurso, recursoId, "EXITOSO", detalles, request);
        } catch (Exception e) {
            log.error("Error al registrar auditoría de éxito", e);
        }
    }

    
    // Registrar acción fallida
    
    public void registrarFallo(Usuario usuario, String accion, String recurso, Long recursoId, 
                               String detalles, HttpServletRequest request) {
        try {
            auditoriaService.registrarAccion(usuario, accion, recurso, recursoId, "FALLIDO", detalles, request);
        } catch (Exception e) {
            log.error("Error al registrar auditoría de fallo", e);
        }
    }

    
    // Registrar creación de recurso
    
    public void registrarCreacion(Usuario usuario, String recurso, Long recursoId, HttpServletRequest request) {
        registrarExito(usuario, "CREAR", recurso, recursoId, 
                      "Creación de " + recurso + " (ID: " + recursoId + ")", request);
    }

    // Registrar edición de recurso
    
    public void registrarEdicion(Usuario usuario, String recurso, Long recursoId, HttpServletRequest request) {
        registrarExito(usuario, "EDITAR", recurso, recursoId, 
                      "Edición de " + recurso + " (ID: " + recursoId + ")", request);
    }

    // Registrar eliminación de recurso
    
    public void registrarEliminacion(Usuario usuario, String recurso, Long recursoId, HttpServletRequest request) {
        registrarExito(usuario, "ELIMINAR", recurso, recursoId, 
                      "Eliminación de " + recurso + " (ID: " + recursoId + ")", request);
    }

    // Registrar visualización de recurso
    
    public void registrarVisualizacion(Usuario usuario, String recurso, Long recursoId, HttpServletRequest request) {
        registrarExito(usuario, "VER", recurso, recursoId, 
                      "Visualización de " + recurso + " (ID: " + recursoId + ")", request);
    }
}
