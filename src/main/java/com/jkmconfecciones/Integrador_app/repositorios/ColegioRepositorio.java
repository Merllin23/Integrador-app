package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Colegio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColegioRepositorio extends JpaRepository<Colegio, Integer> {
    boolean existsByNombre(String nombre);
    Optional<Colegio> findByNombre(String nombre);
}
