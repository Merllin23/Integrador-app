package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;

public interface UsuarioService {
    Usuario validarLogin(String correo, String contraseña);
}
