package com.jkmconfecciones.Integrador_app.service.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Rol;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.RolRepositorio;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio;
import com.jkmconfecciones.Integrador_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepositorio.findByCorreo(correo);
    }

    @Override
    public void reiniciarIntentos(Usuario usuario) {
        usuario.setIntentosFallidos(0);
        usuario.setEstado("activo");
        usuarioRepositorio.save(usuario);
    }

    @Override
    public void aumentarIntentoFallido(Usuario usuario) {
        usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
        if (usuario.getIntentosFallidos() >= 5) {
            usuario.setEstado("bloqueado");
            usuario.setFechaBloqueo(LocalDateTime.now());
        }
        usuarioRepositorio.save(usuario);
    }

    @Override
    public void guardar(Usuario usuario) {
        usuarioRepositorio.save(usuario);
    }


    @Autowired
    private RolRepositorio rolRepositorio;

    @Override
    public void cambiarRol(Long usuarioId, Long rolId) throws Exception {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        Rol rol = rolRepositorio.findById(rolId)
                .orElseThrow(() -> new Exception("Rol no encontrado"));

        usuario.setRol(rol);
        usuarioRepositorio.save(usuario);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepositorio.findAll();
    }

    @Override
    public List<Rol> listarRoles() {
        return rolRepositorio.findAll();
    }

    @Override
    public Page<Usuario> listarTodos(Pageable pageable) {
        return usuarioRepositorio.findAll(pageable);
    }

}