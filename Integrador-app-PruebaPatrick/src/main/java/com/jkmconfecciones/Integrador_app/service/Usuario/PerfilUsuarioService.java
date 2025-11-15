package com.jkmconfecciones.Integrador_app.service.Usuario;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import java.util.Optional;

public interface PerfilUsuarioService {

    Optional<Usuario> buscarPorCorreo(String correo);

    void actualizarPerfil(String correo, String nombre, String telefono, String direccion);
}
