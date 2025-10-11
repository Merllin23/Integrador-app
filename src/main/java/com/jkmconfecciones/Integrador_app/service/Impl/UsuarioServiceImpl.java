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

            // validar contraseña
            if (BCrypt.checkpw(contraseña, usuario.getContraseña())) {
                return usuario;
            }
        }

        return null;
    }

    @Override
    public void iniciarRecuperacionPassword(String correo) throws Exception {
        System.out.println("Intentando recuperar contraseña para: " + correo);
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);
        
        if (usuarioOpt.isEmpty()) {
            System.out.println("No se encontró el usuario con correo: " + correo);
            throw new Exception("Correo no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        System.out.println("Usuario encontrado: " + usuario.getNombre());

        // Enviar correo de recuperación
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correo);
        mensaje.setFrom("cuentadesmo1992@gmail.com"); //correo de prueba de envio de mensajes
        mensaje.setSubject("Prueba de Recuperación de Contraseña - JKM Confecciones");
        mensaje.setText("Hola " + usuario.getNombre() + ",\n\n" +
                "Este es un correo de prueba para la funcionalidad de recuperación de contraseña.\n\n" +
                "Tu solicitud ha sido procesada correctamente.\n\n" +
                "Saludos,\nEquipo JKM Confecciones");

        try {
            System.out.println("Intentando enviar correo a: " + correo);
            emailSender.send(mensaje);
            System.out.println("Correo enviado exitosamente");
        } catch (Exception e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
