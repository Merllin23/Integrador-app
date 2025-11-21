package com.jkmconfecciones.Integrador_app.service.ControlClientes.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio;
import com.jkmconfecciones.Integrador_app.service.ControlClientes.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final UsuarioRepositorio usuarioRepositorio;

    @Override
    public Page<Usuario> listarClientes(int pagina, int tamaño, String keyword) {
        PageRequest pageRequest = PageRequest.of(
                pagina,
                tamaño,
                Sort.by(Sort.Direction.ASC, "fechaUltimoLogin")
        );

        if (keyword == null || keyword.isEmpty()) {
            return usuarioRepositorio.findByRolNombreRol("USUARIO", pageRequest);
        } else {
            return usuarioRepositorio.findByNombreContainingIgnoreCaseAndRolNombreRol(keyword, "USUARIO", pageRequest);
        }
    }


    @Override
    public Usuario obtenerPorId(Long id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    @Override
    public void toggleEstado(Long id) {
        Usuario usuario = obtenerPorId(id);
        if ("activo".equalsIgnoreCase(usuario.getEstado())) {
            usuario.setEstado("bloqueado");
        } else {
            usuario.setEstado("activo");
        }
        usuarioRepositorio.save(usuario);
    }

    @Override
    public void guardar(Usuario usuario) {
        usuarioRepositorio.save(usuario);
    }
}
