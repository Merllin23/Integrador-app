package com.jkmconfecciones.Integrador_app.service.Usuario.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio;
import com.jkmconfecciones.Integrador_app.service.Usuario.PerfilUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PerfilUsuarioServiceImpl implements PerfilUsuarioService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepositorio.findByCorreo(correo);
    }

    @Override
    public void actualizarPerfil(String correo, String nombre, String telefono, String direccion) {
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(nombre);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);

        usuarioRepositorio.save(usuario);
    }
}
