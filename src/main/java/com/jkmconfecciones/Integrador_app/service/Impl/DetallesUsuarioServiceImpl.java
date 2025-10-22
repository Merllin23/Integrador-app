package com.jkmconfecciones.Integrador_app.service.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class DetallesUsuarioServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        // Buscar usuario
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Verificar si está bloqueado temporalmente
        boolean estaBloqueado = false;
        if ("bloqueado".equalsIgnoreCase(usuario.getEstado()) && usuario.getFechaBloqueo() != null) {
            long minutosBloqueo = Duration.between(usuario.getFechaBloqueo(), LocalDateTime.now()).toMinutes();
            if (minutosBloqueo < 5) {
                estaBloqueado = true; // sigue bloqueado
            } else {
                // Desbloquear automáticamente después de 5 minutos
                usuario.setEstado("activo");
                usuario.setIntentosFallidos(0);
                usuario.setFechaBloqueo(null);
                usuarioRepositorio.save(usuario);
            }
        }

        // rol
        String rol = "ROLE_" + usuario.getRol().getNombreRol().toUpperCase();

        // Crear usuario para que reconozca el Spring Security  según el estado
        return new User(
                usuario.getCorreo(),
                usuario.getContraseña(),
                true,
                true,
                true,
                !estaBloqueado,
                Collections.singletonList(new SimpleGrantedAuthority(rol))
        );
    }
}