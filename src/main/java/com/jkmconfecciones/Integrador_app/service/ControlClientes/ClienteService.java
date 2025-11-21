package com.jkmconfecciones.Integrador_app.service.ControlClientes;

import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import org.springframework.data.domain.Page;


public interface ClienteService {
    Page<Usuario> listarClientes(int pagina, int tamaño, String keyword);
    Usuario obtenerPorId(Long id);
    public void guardar(Usuario usuario);
    public void toggleEstado(Long id);
}

