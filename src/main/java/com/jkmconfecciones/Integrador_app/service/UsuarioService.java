package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.dto.LoginResponseDTO;

public interface UsuarioService {
    LoginResponseDTO validarLogin(String correo, String contraseña);
}
