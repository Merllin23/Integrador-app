package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;

public interface RegistroService {
    String registrarUsuario(Usuario usuario, String captchaToken, String ip);
}