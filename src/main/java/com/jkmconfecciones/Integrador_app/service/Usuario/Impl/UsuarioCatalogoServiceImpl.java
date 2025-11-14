package com.jkmconfecciones.Integrador_app.service.Usuario.Impl;

import com.jkmconfecciones.Integrador_app.entidades.Producto;
import com.jkmconfecciones.Integrador_app.entidades.Categoria;
import com.jkmconfecciones.Integrador_app.entidades.Colegio;
import com.jkmconfecciones.Integrador_app.repositorios.ProductoRepositorio;
import com.jkmconfecciones.Integrador_app.repositorios.CategoriaRepositorio;
import com.jkmconfecciones.Integrador_app.repositorios.ColegioRepositorio;
import com.jkmconfecciones.Integrador_app.repositorios.ProductoTallaRepositorio;
import com.jkmconfecciones.Integrador_app.service.Usuario.UsuarioCatalogoService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioCatalogoServiceImpl implements UsuarioCatalogoService {

    private final ProductoTallaRepositorio productoTallaRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;
    private final ColegioRepositorio colegioRepositorio;

    @Override
    public List<Producto> obtenerProductosFiltrados(Integer colegioId, Integer categoriaId, String query) {
        List<Producto> productos;

        // Si no se elige filtro, listar todos
        if ((colegioId == null || colegioId == 0)
                && (categoriaId == null || categoriaId == 0)
                && (query == null || query.isEmpty())) {
            productos = productoRepositorio.findAll();
        } else {
            productos = productoRepositorio.filtrarProductos(
                    (colegioId != null && colegioId > 0) ? colegioId : null,
                    (categoriaId != null && categoriaId > 0) ? categoriaId : null
            );
        }

        // Filtrar por nombre si hay query
        if (query != null && !query.isEmpty()) {
            String q = query.toLowerCase();
            productos = productos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(q))
                    .toList();
        }

        return productos;
    }


    @Override
    public List<Categoria> listarCategorias() {
        return categoriaRepositorio.findAll();
    }

    @Override
    public List<Colegio> listarColegios() {
        return colegioRepositorio.findAll();
    }

    @Override
    public List<String> listarTallasPorProducto(Long productoId) {
        return productoTallaRepositorio.findByProductoId(productoId)
                .stream()
                .map(pt -> pt.getTalla().getNombreTalla())
                .toList();
    }

    @Override
    public Producto obtenerProductoPorId(Long id) {
        return productoRepositorio.findById(id).orElse(null);
    }

}
