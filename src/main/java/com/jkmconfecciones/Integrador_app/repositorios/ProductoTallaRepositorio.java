package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.ProductoTalla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoTallaRepositorio extends JpaRepository<ProductoTalla, Long> {
    
    @Query("SELECT pt FROM ProductoTalla pt " +
           "JOIN FETCH pt.producto p " +
           "JOIN FETCH pt.talla t " +
           "JOIN p.colegios c " +
           "WHERE c.id = :colegioId")
    List<ProductoTalla> findByColegioId(@Param("colegioId") Integer colegioId);
    
    @Query("SELECT pt FROM ProductoTalla pt " +
           "JOIN FETCH pt.producto p " +
           "JOIN FETCH pt.talla t")
    List<ProductoTalla> findAllWithDetails();
}
