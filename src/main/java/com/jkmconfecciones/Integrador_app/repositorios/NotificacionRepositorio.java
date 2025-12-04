package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Notificacion;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepositorio extends JpaRepository<Notificacion, Long> {

    // Buscar todas las notificaciones de un usuario ordenadas por fecha
    List<Notificacion> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    // Buscar notificaciones no leídas
    List<Notificacion> findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(Usuario usuario);

    // Buscar notificaciones archivadas
    List<Notificacion> findByUsuarioAndArchivadaTrueOrderByFechaCreacionDesc(Usuario usuario);

    // Buscar notificaciones no archivadas 
    List<Notificacion> findByUsuarioAndArchivadaFalseOrderByFechaCreacionDesc(Usuario usuario);

    // Contar notificaciones no leídas 
    long countByUsuarioAndLeidaFalse(Usuario usuario);

    // Paginación de notificaciones
    Page<Notificacion> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario, Pageable pageable);

    // Buscar por tipo y usuario
    List<Notificacion> findByUsuarioAndTipoOrderByFechaCreacionDesc(Usuario usuario, String tipo);
}
