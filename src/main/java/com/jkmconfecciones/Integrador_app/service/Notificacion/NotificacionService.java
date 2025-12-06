package com.jkmconfecciones.Integrador_app.service.Notificacion;

import com.jkmconfecciones.Integrador_app.entidades.Notificacion;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.NotificacionRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepositorio notificacionRepositorio;

    //Crear una nueva notificación
    
    @Transactional
    public Notificacion crearNotificacion(String tipo, String titulo, String mensaje, Usuario usuario) {
        Notificacion notificacion = Notificacion.builder()
                .tipo(tipo)
                .titulo(titulo)
                .mensaje(mensaje)
                .usuario(usuario)
                .fechaCreacion(LocalDateTime.now())
                .leida(false)
                .archivada(false)
                .build();

        return notificacionRepositorio.save(notificacion);
    }

    //Crear notificación con referencia
    
    @Transactional
    public Notificacion crearNotificacionConReferencia(String tipo, String titulo, String mensaje, 
                                                       Usuario usuario, Long referenciaId, String referenciaTipo) {
        Notificacion notificacion = Notificacion.builder()
                .tipo(tipo)
                .titulo(titulo)
                .mensaje(mensaje)
                .usuario(usuario)
                .referenciaId(referenciaId)
                .referenciaTipo(referenciaTipo)
                .fechaCreacion(LocalDateTime.now())
                .leida(false)
                .archivada(false)
                .build();

        return notificacionRepositorio.save(notificacion);
    }

    //Obtener todas las notificaciones de un usuario
    
    public List<Notificacion> obtenerNotificacionesUsuario(Usuario usuario) {
        return notificacionRepositorio.findByUsuarioOrderByFechaCreacionDesc(usuario);
    }

    //Obtener notificaciones no leídas
    
    public List<Notificacion> obtenerNotificacionesNoLeidas(Usuario usuario) {
        return notificacionRepositorio.findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(usuario);
    }

    //Obtener notificaciones archivadas
    
    public List<Notificacion> obtenerNotificacionesArchivadas(Usuario usuario) {
        return notificacionRepositorio.findByUsuarioAndArchivadaTrueOrderByFechaCreacionDesc(usuario);
    }

    //Obtener notificaciones no archivadas
    
    public List<Notificacion> obtenerNotificacionesNoArchivadas(Usuario usuario) {
        return notificacionRepositorio.findByUsuarioAndArchivadaFalseOrderByFechaCreacionDesc(usuario);
    }

    //Marcar notificación como leída
    
    @Transactional
    public void marcarComoLeida(Long notificacionId) {
        Optional<Notificacion> notificacionOpt = notificacionRepositorio.findById(notificacionId);
        if (notificacionOpt.isPresent()) {
            Notificacion notificacion = notificacionOpt.get();
            notificacion.setLeida(true);
            notificacionRepositorio.save(notificacion);
            log.info("Notificación {} marcada como leída", notificacionId);
        }
    }

    //Marcar notificación como archivada

    @Transactional
    public void marcarComoArchivada(Long notificacionId) {
        Optional<Notificacion> notificacionOpt = notificacionRepositorio.findById(notificacionId);
        if (notificacionOpt.isPresent()) {
            Notificacion notificacion = notificacionOpt.get();
            notificacion.setArchivada(true);
            notificacionRepositorio.save(notificacion);
            log.info("Notificación {} marcada como archivada", notificacionId);
        }
    }

    //Contar notificaciones no leídas
    
    public long contarNotificacionesNoLeidas(Usuario usuario) {
        return notificacionRepositorio.countByUsuarioAndLeidaFalse(usuario);
    }

    //Obtener notificaciones con paginación
    
    public Page<Notificacion> obtenerNotificacionesPaginadas(Usuario usuario, Pageable pageable) {
        return notificacionRepositorio.findByUsuarioOrderByFechaCreacionDesc(usuario, pageable);
    }

    //Eliminar notificación
    
    @Transactional
    public void eliminarNotificacion(Long notificacionId) {
        notificacionRepositorio.deleteById(notificacionId);
        log.info("Notificación {} eliminada", notificacionId);
    }

    //Marcar todas como leídas
    @Transactional
    public void marcarTodasComoLeidas(Usuario usuario) {
        List<Notificacion> notificaciones = notificacionRepositorio.findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(usuario);
        notificaciones.forEach(n -> n.setLeida(true));
        notificacionRepositorio.saveAll(notificaciones);
        log.info("Todas las notificaciones del usuario {} marcadas como leídas", usuario.getId());
    }

    public boolean existeNotificacion(Long usuarioId, Long referenciaId, String referenciaTipo) {
        List<Notificacion> notificaciones = notificacionRepositorio
                .findAllByUsuarioIdAndReferenciaIdAndReferenciaTipo(usuarioId, referenciaId, referenciaTipo);
        return notificaciones != null && !notificaciones.isEmpty();
    }

}
