package com.jkmconfecciones.Integrador_app.service;

public interface RecuperarCuentaService {
    void enviarCorreoRecuperacion(String correo);
    boolean validarToken(String token);
    boolean actualizarContraseña(String token, String nuevaContraseña);
}