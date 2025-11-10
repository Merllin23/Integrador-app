package com.jkmconfecciones.Integrador_app.service.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio;
import com.jkmconfecciones.Integrador_app.service.RecuperarCuentaService;
import com.jkmconfecciones.Integrador_app.util.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecuperarCuentaServiceImpl implements RecuperarCuentaService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final JavaMailSender mailSender;
    private final TokenManager tokenManager;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public boolean enviarCorreoRecuperacion(String correo) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return false;
        }

        String token = tokenManager.generarToken(correo);
        String enlace = "http://localhost:8080/restablecer?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(correo);
            helper.setSubject("Recuperación de contraseña - JKM Confecciones");
            helper.setText(
                    "<h3>Hola,</h3>"
                            + "<p>Has solicitado recuperar tu contraseña.</p>"
                            + "<p>Haz clic en el siguiente enlace para restablecerla:</p>"
                            + "<a href='" + enlace + "'>Restablecer contraseña</a>"
                            + "<br><p>Este enlace expirará en 15 minutos.</p>",
                    true
            );
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean validarToken(String token) {
        return tokenManager.obtenerCorreoPorToken(token) != null;
    }

    // Devuelve null si la contraseña es válida, o un mensaje de error si no
    private String validarContraseña(String contraseña) {
        if (contraseña == null || contraseña.isEmpty()) {
            return "La contraseña no puede estar vacía.";
        }
        if (contraseña.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres.";
        }
        if (!contraseña.matches(".*[A-Z].*")) {
            return "La contraseña debe contener al menos una letra mayúscula.";
        }
        if (!contraseña.matches(".*[a-z].*")) {
            return "La contraseña debe contener al menos una letra minúscula.";
        }
        if (!contraseña.matches(".*\\d.*")) {
            return "La contraseña debe contener al menos un número.";
        }
        if (!contraseña.matches(".*[^A-Za-z0-9].*")) {
            return "La contraseña debe contener al menos un carácter especial.";
        }
        return null; // contraseña válida
    }

    @Override
    public boolean actualizarContraseña(String token, String nuevaContraseña) {
        String correo = tokenManager.obtenerCorreoPorToken(token);
        if (correo == null) return false;

        String error = validarContraseña(nuevaContraseña);
        if (error != null) {
            throw new IllegalArgumentException(error); // el controlador puede capturar este mensaje
        }

        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) return false;

        Usuario usuario = usuarioOpt.get();
        usuario.setContraseña(passwordEncoder.encode(nuevaContraseña));
        usuarioRepositorio.save(usuario);
        tokenManager.eliminarToken(token);

        // Enviar correo de confirmación
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(correo);
            helper.setSubject("Contraseña actualizada - JKM Confecciones");
            helper.setText("<p>Tu contraseña ha sido actualizada correctamente.</p>", true);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Error enviando correo de confirmación: " + e.getMessage());
        }

        return true;
    }
}
