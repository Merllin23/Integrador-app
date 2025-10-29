package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

    // filtra por colegio
    @Query("SELECT p FROM Producto p JOIN p.colegios c WHERE c.id = :colegioId")
    List<Producto> findByColegioId(@Param("colegioId") Integer colegioId);

    // filtra por colegio y categoría
    @Query("SELECT DISTINCT p FROM Producto p " +
            "LEFT JOIN p.colegios c " +
            "WHERE (:colegioId IS NULL OR c.id = :colegioId) " +
            "AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId)")

    List<Producto> filtrarProductos(@Param("colegioId") Integer colegioId,
                                    @Param("categoriaId") Integer categoriaId);
}
