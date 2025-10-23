package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Talla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TallaRepositorio extends JpaRepository<Talla, Integer> {

    Optional<Talla> findByNombreTalla(String nombreTalla);
}
