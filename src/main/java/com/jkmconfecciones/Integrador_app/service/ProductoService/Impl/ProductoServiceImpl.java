package com.jkmconfecciones.Integrador_app.service.ProductoService.Impl;

import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.repositorios.*;
import com.jkmconfecciones.Integrador_app.service.ProductoService.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepositorio productoRepositorio;
    private final TallaRepositorio tallaRepositorio;
    private final ColegioRepositorio colegioRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;

    private final String CARPETA_IMAGENES = "C:\\jkm\\productos\\";

    @Override
    public Producto crearProducto(Producto producto, List<ProductoTalla> listaTallas, MultipartFile imagen) {
        if (imagen != null && !imagen.isEmpty()) {
            producto.setImagenUrl(guardarImagen(imagen));
        }

        if (listaTallas != null && !listaTallas.isEmpty()) {
            for (ProductoTalla pt : listaTallas) {
                pt.setProducto(producto);
            }
            producto.setTallas(listaTallas);
        }

        if (producto.getColegios() != null && !producto.getColegios().isEmpty()) {
            producto.setColegios(producto.getColegios().stream()
                    .map(c -> colegioRepositorio.findById(c.getId())
                            .orElseThrow(() -> new RuntimeException("Colegio no encontrado: " + c.getId())))
                    .collect(Collectors.toSet()));
        }

        return productoRepositorio.save(producto);
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepositorio.findAll();
    }

    @Override
    public List<Producto> listarProductosPorColegio(Integer colegioId) {
        return productoRepositorio.findByColegioId(colegioId);
    }

    @Override
    public void eliminarProducto(Integer id) {
        Producto producto = productoRepositorio.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            try {
                Path path = Paths.get(CARPETA_IMAGENES + producto.getImagenUrl().substring("/productos/".length()));
                Files.deleteIfExists(path);
            } catch (Exception e) {
                System.err.println("Error al eliminar la imagen de producto: " + e.getMessage());
            }
        }

        productoRepositorio.deleteById(id.longValue());
    }

    @Override
    public Producto buscarPorId(Integer id) {
        Producto p = productoRepositorio.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        // Forzar carga de relaciones
        if (p.getCategoria() != null) p.getCategoria().getNombre();
        if (p.getColeccion() != null) p.getColeccion().getNombre();
        p.getColegios().size(); // fuerza carga de la colección

        return p;
    }

    @Override
    public Producto actualizarProducto(Producto producto, List<ProductoTalla> listaTallas, MultipartFile imagen) {
        Producto existente = productoRepositorio.findById(producto.getId().longValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + producto.getId()));

        existente.setNombre(producto.getNombre());
        existente.setDescripcion(producto.getDescripcion());
        existente.setPrecioBase(producto.getPrecioBase());

        if (producto.getCategoria() != null) {
            Categoria cat = new Categoria();
            cat.setId(producto.getCategoria().getId());
            existente.setCategoria(cat);
        }
        if (producto.getColeccion() != null) {
            Coleccion col = new Coleccion();
            col.setId(producto.getColeccion().getId());
            existente.setColeccion(col);
        }

        if (imagen != null && !imagen.isEmpty()) {
            if (existente.getImagenUrl() != null && !existente.getImagenUrl().isEmpty()) {
                try {
                    Path path = Paths.get(CARPETA_IMAGENES + existente.getImagenUrl().substring("/productos/".length()));
                    Files.deleteIfExists(path);
                } catch (Exception e) {
                    System.err.println("Error al eliminar imagen antigua: " + e.getMessage());
                }
            }
            existente.setImagenUrl(guardarImagen(imagen));
        }

        if (producto.getColegios() != null && !producto.getColegios().isEmpty()) {
            existente.setColegios(producto.getColegios().stream()
                    .map(c -> colegioRepositorio.findById(c.getId())
                            .orElseThrow(() -> new RuntimeException("Colegio no encontrado: " + c.getId())))
                    .collect(Collectors.toSet()));
        }

        if (listaTallas != null) {
            Map<Integer, ProductoTalla> nuevasTallasMap = new HashMap<>();
            for (ProductoTalla pt : listaTallas) {
                nuevasTallasMap.put(pt.getTalla().getId(), pt);
            }

            Iterator<ProductoTalla> iterator = existente.getTallas().iterator();
            while (iterator.hasNext()) {
                ProductoTalla tallaExistente = iterator.next();
                Integer tallaId = tallaExistente.getTalla().getId();
                if (nuevasTallasMap.containsKey(tallaId)) {
                    ProductoTalla nuevaTalla = nuevasTallasMap.get(tallaId);
                    tallaExistente.setCantidadStock(nuevaTalla.getCantidadStock());
                    tallaExistente.setPrecioUnitarioFinal(nuevaTalla.getPrecioUnitarioFinal());
                    nuevasTallasMap.remove(tallaId);
                } else {
                    iterator.remove();
                }
            }

            for (ProductoTalla nuevaTalla : nuevasTallasMap.values()) {
                nuevaTalla.setProducto(existente);
                existente.getTallas().add(nuevaTalla);
            }
        } else {
            existente.getTallas().clear();
        }

        return productoRepositorio.save(existente);
    }

    private String guardarImagen(MultipartFile imagen) {
        try {
            Files.createDirectories(Paths.get(CARPETA_IMAGENES));
            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path rutaDestino = Paths.get(CARPETA_IMAGENES, nombreArchivo);
            Files.copy(imagen.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
            return "/productos/" + nombreArchivo;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la imagen del producto", e);
        }
    }


    @Override
    public List<Producto> listarProductosFiltrados(Integer colegioId, Integer categoriaId) {
        return productoRepositorio.filtrarProductos(
                (colegioId != null && colegioId > 0) ? colegioId : null,
                (categoriaId != null && categoriaId > 0) ? categoriaId : null
        );
    }

    @Override
    public List<Categoria> listarCategorias() {
        return categoriaRepositorio.findAll();
    }
}
