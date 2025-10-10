package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.dto.UsuarioDTO;

public interface UsuarioService {
    UsuarioDTO validarLogin(String correo, String contraseña);
}
