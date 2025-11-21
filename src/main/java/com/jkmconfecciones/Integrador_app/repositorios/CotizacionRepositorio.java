package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Cotizacion;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CotizacionRepositorio extends JpaRepository<Cotizacion, Integer> {
    List<Cotizacion> findByUsuario(Usuario usuario);
    
    @Query("SELECT c FROM Cotizacion c JOIN FETCH c.usuario ORDER BY c.fecha DESC")
    List<Cotizacion> findAllByOrderByFechaDesc();
    
    @Query("SELECT c FROM Cotizacion c JOIN FETCH c.usuario WHERE c.estado = :estado ORDER BY c.fecha DESC")
    List<Cotizacion> findByEstadoOrderByFechaDesc(@Param("estado") String estado);
    
    @Query("SELECT c FROM Cotizacion c JOIN FETCH c.usuario LEFT JOIN FETCH c.detalles d LEFT JOIN FETCH d.productoTalla pt LEFT JOIN FETCH pt.producto LEFT JOIN FETCH pt.talla WHERE c.id = :id")
    Optional<Cotizacion> findByIdWithDetails(@Param("id") Integer id);

    List<Cotizacion> findByUsuarioOrderByFechaDesc(Usuario usuario);

}
