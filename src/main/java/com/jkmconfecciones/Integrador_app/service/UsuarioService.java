package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.entidades.Rol;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Optional<Usuario> buscarPorCorreo(String correo);
    void reiniciarIntentos(Usuario usuario);
    void aumentarIntentoFallido(Usuario usuario);

    void guardar(Usuario usuario);

    void cambiarRol(Long usuarioId, Long rolId) throws Exception;

    List<Usuario> listarTodos();
    List<Rol> listarRoles();

    Page<Usuario> listarTodos(Pageable pageable);
}