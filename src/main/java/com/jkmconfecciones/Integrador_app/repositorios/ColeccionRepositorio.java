package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColeccionRepositorio extends JpaRepository<Coleccion, Long> {
}
