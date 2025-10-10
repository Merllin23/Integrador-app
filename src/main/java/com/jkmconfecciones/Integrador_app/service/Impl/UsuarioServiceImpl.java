package com.jkmconfecciones.Integrador_app.service.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepository;
import com.jkmconfecciones.Integrador_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public Usuario validarLogin(String correo, String contraseña) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // validar que esté activo
            if (!"activo".equalsIgnoreCase(usuario.getEstado())) {
                return null;
            }

            // validar contraseña
            if (BCrypt.checkpw(contraseña, usuario.getContraseña())) {
                return usuario;
            }
        }

        return null;
    }

    @Override
    public void iniciarRecuperacionPassword(String correo) throws Exception {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        
        if (usuarioOpt.isEmpty()) {
            throw new Exception("Correo no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        String tokenRecuperacion = UUID.randomUUID().toString();
        
        // Actualizar el token de recuperación del usuario
        usuario.setTokenRecuperacion(tokenRecuperacion);
        usuario.setFechaTokenRecuperacion(new java.util.Date());
        usuarioRepository.save(usuario);

        // Enviar correo de recuperación
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correo);
        mensaje.setSubject("Recuperación de Contraseña - JKM Confecciones");
        mensaje.setText("Hola,\n\nHas solicitado recuperar tu contraseña. " +
                "Para crear una nueva contraseña, haz clic en el siguiente enlace:\n\n" +
                "link-de-recuperacion" + tokenRecuperacion + "\n\n" +
                "Si no solicitaste recuperar tu contraseña, ignora este mensaje.\n\n" +
                "Saludos,\nEquipo JKM Confecciones");

        emailSender.send(mensaje);
    }
}
