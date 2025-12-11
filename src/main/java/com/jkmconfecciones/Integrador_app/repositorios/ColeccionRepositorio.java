package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColeccionRepositorio extends JpaRepository<Coleccion, Long> {
    Optional<Coleccion> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
