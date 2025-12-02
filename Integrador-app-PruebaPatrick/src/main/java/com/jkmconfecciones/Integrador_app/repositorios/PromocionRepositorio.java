package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromocionRepositorio extends JpaRepository<Promocion, Integer> {

    // Obtener promociones activas por colegio
    @Query("SELECT p FROM Promocion p WHERE p.colegio.id = :colegioId AND p.esValida = true AND p.fechaInicio <= :fecha AND p.fechaFin >= :fecha")
    List<Promocion> findPromocionesActivasPorColegio(@Param("colegioId") Integer colegioId, @Param("fecha") LocalDate fecha);

    // Obtener promociones por día de semana y colegio
    @Query("SELECT p FROM Promocion p WHERE p.colegio.id = :colegioId AND p.diaSemana = :diaSemana AND p.esValida = true")
    List<Promocion> findPromocionesporDiaYColegio(@Param("colegioId") Integer colegioId, @Param("diaSemana") Promocion.DiaSemana diaSemana);

    // Obtener todas las promociones activas
    @Query("SELECT p FROM Promocion p WHERE p.esValida = true AND p.fechaInicio <= :fecha AND p.fechaFin >= :fecha")
    List<Promocion> findPromocionesActivas(@Param("fecha") LocalDate fecha);

    // Obtener promociones por colegio
    List<Promocion> findByColegioId(Integer colegioId);

    // Obtener promociones de un producto
    @Query("SELECT p FROM Promocion p JOIN p.productos prod WHERE prod.id = :productoId AND p.esValida = true")
    List<Promocion> findPromocionesDelProducto(@Param("productoId") Integer productoId);
}
