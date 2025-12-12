package com.jkmconfecciones.Integrador_app.service.ProductoService;

import com.jkmconfecciones.Integrador_app.entidades.Categoria;
import java.util.List;

public interface CategoriaService {
    List<Categoria> listarCategorias();

    Categoria guardarCategoria(Categoria categoria);

    void editarCategoria(Long id, String nombre);
    void eliminarCategoria(Long id);
}
