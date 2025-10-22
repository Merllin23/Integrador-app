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
    public void enviarCorreoRecuperacion(String correo) {
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByCorreo(correo);
        if (usuarioOpt.isEmpty()) {
            return; // No decimos si existe o no
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
                    true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean validarToken(String token) {
        return tokenManager.obtenerCorreoPorToken(token) != null;
    }

    @Override
    public boolean actualizarContraseña(String token, String nuevaContraseña) {
        String correo = tokenManager.obtenerCorreoPorToken(token);
        if (correo == null) return false;

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
