package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.AuditoriaSeguridad;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepositorio extends JpaRepository<AuditoriaSeguridad, Long> {

    // Buscar todos los registros ordenados por fecha
    Page<AuditoriaSeguridad> findAllByOrderByFechaHoraDesc(Pageable pageable);

    // Buscar por usuario
    Page<AuditoriaSeguridad> findByUsuarioOrderByFechaHoraDesc(Usuario usuario, Pageable pageable);

    // Buscar por acción
    Page<AuditoriaSeguridad> findByAccionOrderByFechaHoraDesc(String accion, Pageable pageable);

    // Buscar por recurso
    Page<AuditoriaSeguridad> findByRecursoOrderByFechaHoraDesc(String recurso, Pageable pageable);

    // Buscar por rango de fechas
    Page<AuditoriaSeguridad> findByFechaHoraBetweenOrderByFechaHoraDesc(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin, 
            Pageable pageable
    );

    // Búsqueda con múltiples filtros
    @Query("SELECT a FROM AuditoriaSeguridad a WHERE " +
           "(:usuario IS NULL OR a.usuario.correo LIKE %:usuario%) AND " +
           "(:accion IS NULL OR a.accion = :accion) AND " +
           "(:fechaInicio IS NULL OR a.fechaHora >= :fechaInicio) AND " +
           "(:fechaFin IS NULL OR a.fechaHora <= :fechaFin) " +
           "ORDER BY a.fechaHora DESC")
    Page<AuditoriaSeguridad> buscarConFiltros(
            @Param("usuario") String usuario,
            @Param("accion") String accion,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    // Obtener últimos N registros
    List<AuditoriaSeguridad> findTop10ByOrderByFechaHoraDesc();
}
