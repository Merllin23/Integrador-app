package com.jkmconfecciones.Integrador_app.service.Impl;

import com.jkmconfecciones.Integrador_app.dto.UsuarioDTO;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepository;
import com.jkmconfecciones.Integrador_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UsuarioDTO validarLogin(String correo, String contraseña) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // valida que este activo
            if (!"activo".equalsIgnoreCase(usuario.getEstado())) return null;

            // validar contraseña
            if (BCrypt.checkpw(contraseña, usuario.getContraseña())) {
                // crear DTO
                return new UsuarioDTO(
                        usuario.getNombre(),
                        usuario.getCorreo(),
                        usuario.getRol().getNombreRol()
                );
            }
        }

        return null;
    }
}
