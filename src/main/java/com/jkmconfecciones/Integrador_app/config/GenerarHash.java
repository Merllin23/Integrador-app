package com.jkmconfecciones.Integrador_app.config;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class GenerarHash {
    public static void main(String[] args) {
        String passwordAdmin = "admin123";
        String passwordUsuario = "user123";

        String hashAdmin = BCrypt.hashpw(passwordAdmin, BCrypt.gensalt());
        String hashUsuario = BCrypt.hashpw(passwordUsuario, BCrypt.gensalt());

        System.out.println("Admin hash: " + hashAdmin);
        System.out.println("Usuario hash: " + hashUsuario);
    }
}
