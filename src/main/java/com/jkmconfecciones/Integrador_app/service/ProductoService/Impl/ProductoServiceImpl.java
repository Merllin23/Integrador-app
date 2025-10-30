package com.jkmconfecciones.Integrador_app.service.ProductoService.Impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.repositorios.*;
import com.jkmconfecciones.Integrador_app.service.ProductoService.ProductoService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepositorio productoRepositorio;
    private final TallaRepositorio tallaRepositorio;
    private final ColegioRepositorio colegioRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;
    private static final Logger logger = LoggerFactory.getLogger(ProductoServiceImpl.class);


    private static final String CARPETA_IMAGENES = "C:\\jkm\\productos\\";

    @Override
    public Producto crearProducto(Producto producto, List<ProductoTalla> listaTallas, MultipartFile imagen) {
        Preconditions.checkNotNull(producto, "El producto no puede ser nulo");

        // Validación y limpieza de nombre
        String nombreOriginal = producto.getNombre();
        String nombreLimpio = (nombreOriginal != null) ? nombreOriginal.trim().replaceAll("\\s{2,}", " ") : "";

        // No permitir vacío ni solo espacios ni longitud menor a 2
        Preconditions.checkArgument(!nombreLimpio.isEmpty() && nombreLimpio.length() >= 2,
                "El nombre del producto no puede estar vacío ni contener solo espacios.");
        producto.setNombre(nombreLimpio);

        logger.info("Creando producto: {}", producto.getNombre());

        // Guardar imagen si existe
        if (imagen != null && !imagen.isEmpty()) {
            try {
                String rutaImagen = guardarImagen(imagen);
                producto.setImagenUrl(rutaImagen);
                logger.info("Imagen guardada correctamente en {}", rutaImagen);
            } catch (RuntimeException e) {
                logger.error("Error al guardar la imagen del producto {}", producto.getNombre(), e);
                throw e;
            }
        } else {
            logger.warn("El producto '{}' no tiene imagen asociada", producto.getNombre());
        }

        // Asignar tallas
        if (listaTallas != null && !listaTallas.isEmpty()) {
            listaTallas.forEach(pt -> pt.setProducto(producto));
            producto.setTallas(ImmutableList.copyOf(listaTallas));
            logger.info("Asignadas {} tallas al producto '{}'", listaTallas.size(), producto.getNombre());
        } else {
            logger.warn("No se asignaron tallas al producto '{}'", producto.getNombre());
        }

        // Asociar colegios
        if (producto.getColegios() != null && !producto.getColegios().isEmpty()) {
            producto.setColegios(
                    producto.getColegios().stream()
                            .map(c -> colegioRepositorio.findById(c.getId())
                                    .orElseThrow(() -> new RuntimeException("Colegio no encontrado: " + c.getId())))
                            .collect(Collectors.toSet())
            );
            logger.info("Asociados {} colegios al producto '{}'", producto.getColegios().size(), producto.getNombre());
        } else {
            logger.warn("No se asociaron colegios al producto '{}'", producto.getNombre());
        }

        Producto guardado = productoRepositorio.save(producto);
        logger.info("Producto '{}' guardado exitosamente con ID {}", guardado.getNombre(), guardado.getId());

        return guardado;
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
        logger.info("Intentando eliminar producto con ID {}", id);

        Producto producto = productoRepositorio.findById(id.longValue())
                .orElseThrow(() -> {
                    logger.error("Producto con ID {} no encontrado, no se puede eliminar", id);
                    return new RuntimeException("Producto no encontrado con ID: " + id);
                });

        // Eliminar imagen si existe
        if (!Strings.isNullOrEmpty(producto.getImagenUrl())) {
            File imagenFile = new File(CARPETA_IMAGENES + producto.getImagenUrl().substring("/productos/".length()));
            boolean eliminado = FileUtils.deleteQuietly(imagenFile);

            if (eliminado) {
                logger.info("Imagen '{}' eliminada correctamente", imagenFile.getAbsolutePath());
            } else {
                logger.warn("No se encontró o no se pudo eliminar la imagen '{}'", imagenFile.getAbsolutePath());
            }
        } else {
            logger.warn("El producto '{}' no tenía imagen asociada", producto.getNombre());
        }

        productoRepositorio.deleteById(id.longValue());
        logger.info("Producto '{}' con ID {} eliminado correctamente", producto.getNombre(), id);
    }

    @Override
    public Producto buscarPorId(Integer id) {
        Producto p = productoRepositorio.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));

        // Forzar carga de relaciones (lazy)
        if (p.getCategoria() != null) p.getCategoria().getNombre();
        if (p.getColeccion() != null) p.getColeccion().getNombre();
        p.getColegios().size();

        return p;
    }

    @Override
    public Producto actualizarProducto(Producto producto, List<ProductoTalla> listaTallas, MultipartFile imagen) {
        Producto existente = productoRepositorio.findById(producto.getId().longValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + producto.getId()));

        // Validación y limpieza de nombre
        String nombreOriginal = producto.getNombre();
        String nombreLimpio = (nombreOriginal != null) ? nombreOriginal.trim().replaceAll("\\s{2,}", " ") : "";

        // No permitir vacío ni solo espacios ni longitud menor a 2
        Preconditions.checkArgument(!nombreLimpio.isEmpty() && nombreLimpio.length() >= 2,
                "El nombre del producto no puede estar vacío ni contener solo espacios.");
        existente.setNombre(nombreLimpio);

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

        // Reemplazar imagen si se sube una nueva
        if (imagen != null && !imagen.isEmpty()) {
            if (!Strings.isNullOrEmpty(existente.getImagenUrl())) {
                File antigua = new File(CARPETA_IMAGENES + existente.getImagenUrl().substring("/productos/".length()));
                FileUtils.deleteQuietly(antigua);
            }
            existente.setImagenUrl(guardarImagen(imagen));
        }

        // Actualizar colegios
        if (producto.getColegios() != null && !producto.getColegios().isEmpty()) {
            existente.setColegios(
                    producto.getColegios().stream()
                            .map(c -> colegioRepositorio.findById(c.getId())
                                    .orElseThrow(() -> new RuntimeException("Colegio no encontrado: " + c.getId())))
                            .collect(Collectors.toSet())
            );
        }

        // Actualizar tallas
        if (listaTallas != null) {
            Map<Integer, ProductoTalla> nuevasTallasMap = new HashMap<>();
            listaTallas.forEach(pt -> nuevasTallasMap.put(pt.getTalla().getId(), pt));

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

            nuevasTallasMap.values().forEach(nuevaTalla -> {
                nuevaTalla.setProducto(existente);
                existente.getTallas().add(nuevaTalla);
            });
        } else {
            existente.getTallas().clear();
        }

        return productoRepositorio.save(existente);
    }

    // Manejo de archivos con Commons IO
    private String guardarImagen(MultipartFile imagen) {
        try {
            Files.createDirectories(Paths.get(CARPETA_IMAGENES));
            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            File destino = new File(CARPETA_IMAGENES + nombreArchivo);

            FileUtils.copyInputStreamToFile(imagen.getInputStream(), destino);
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
