package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Colegio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColegioRepository extends JpaRepository<Colegio, Long> {
    List<Colegio> findByNombreContainingIgnoreCase(String nombre);
}
