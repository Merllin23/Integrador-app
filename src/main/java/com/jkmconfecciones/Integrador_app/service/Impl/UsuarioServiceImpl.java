package com.jkmconfecciones.Integrador_app.service.Impl;

import com.jkmconfecciones.Integrador_app.dto.LoginResponseDTO;
import com.jkmconfecciones.Integrador_app.dto.UsuarioDTO;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepository;
import com.jkmconfecciones.Integrador_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final int MAX_INTENTOS = 5;
    private static final int MINUTOS_BLOQUEO = 5;

    public LoginResponseDTO validarLogin(String correo, String contraseña) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isEmpty())
            return new LoginResponseDTO(null, "Correo o contraseña incorrectos");

        Usuario usuario = usuarioOpt.get();

        // Revisar si está bloqueado
        if ("bloqueado".equalsIgnoreCase(usuario.getEstado())) {
            if (usuario.getFechaBloqueo() != null &&
                    usuario.getFechaBloqueo().plusMinutes(MINUTOS_BLOQUEO).isBefore(LocalDateTime.now())) {
                // Desbloquear usuario
                usuario.setEstado("activo");
                usuario.setIntentosFallidos(0);
                usuario.setFechaBloqueo(null);
                usuarioRepository.save(usuario);
            } else {
                return new LoginResponseDTO(null, "Usuario bloqueado temporalmente. Intenta más tarde.");
            }
        }

        // Validar contraseña
        if (BCrypt.checkpw(contraseña, usuario.getContraseña())) {
            usuario.setIntentosFallidos(0);
            usuarioRepository.save(usuario);

            UsuarioDTO dto = new UsuarioDTO(usuario.getNombre(), usuario.getCorreo(), usuario.getRol().getNombreRol());
            return new LoginResponseDTO(dto, null);
        } else {
            // Incrementar intentos fallidos
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            if (usuario.getIntentosFallidos() >= MAX_INTENTOS) {
                usuario.setEstado("bloqueado");
                usuario.setFechaBloqueo(LocalDateTime.now());
            }
            usuarioRepository.save(usuario);
            return new LoginResponseDTO(null, "Correo o contraseña incorrectos");
        }
    }
}
