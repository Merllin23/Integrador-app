package com.jkmconfecciones.Integrador_app.repositorios;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);

    Page<Usuario> findByRolNombreRol(String nombreRol, Pageable pageable);

    Page<Usuario> findByNombreContainingIgnoreCaseAndRolNombreRol(String nombre, String rol, Pageable pageable);

    List<Usuario> findByEstadoAndFechaUltimoLoginBefore(String estado, LocalDateTime fecha);

}