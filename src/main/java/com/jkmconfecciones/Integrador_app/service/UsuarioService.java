package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import java.util.Optional;

public interface UsuarioService {
    Optional<Usuario> buscarPorCorreo(String correo);
    void reiniciarIntentos(Usuario usuario);
    void aumentarIntentoFallido(Usuario usuario);

    void guardar(Usuario usuario);
}