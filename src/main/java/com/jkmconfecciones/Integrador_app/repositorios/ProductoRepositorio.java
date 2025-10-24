package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Producto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> { 
    @Query( "Select p FROM Producto p JOIN p.colegios c WHERE c.id = :colegioId" )
    List<Producto> findByColegioId(@Param("colegioId") Integer colegioId);
}
