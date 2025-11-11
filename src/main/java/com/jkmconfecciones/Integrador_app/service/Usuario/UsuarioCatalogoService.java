package com.jkmconfecciones.Integrador_app.service.Usuario;

import com.jkmconfecciones.Integrador_app.entidades.Producto;
import com.jkmconfecciones.Integrador_app.entidades.Categoria;
import com.jkmconfecciones.Integrador_app.entidades.Colegio;
import java.util.List;

public interface UsuarioCatalogoService {
    List<Producto> obtenerProductosFiltrados(Integer colegioId, Integer categoriaId, String query);
    List<Categoria> listarCategorias();
    List<Colegio> listarColegios();
}
