package com.jkmconfecciones.Integrador_app.service.ProductoService.Impl;

import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.repositorios.*;
import com.jkmconfecciones.Integrador_app.service.ProductoService.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepositorio productoRepositorio;
    private final TallaRepositorio tallaRepositorio;
    private final ColegioRepositorio colegioRepositorio;

    @Override
    public Producto crearProducto(Producto producto, List<ProductoTalla> listaTallas, MultipartFile imagen) {
        // Guardar imagen en static/productos y almacenar URL
        if (imagen != null && !imagen.isEmpty()) {
            try {
                String carpetaDestino = new ClassPathResource("static/productos/").getFile().getAbsolutePath();
                Files.createDirectories(Paths.get(carpetaDestino));

                String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                Path rutaDestino = Paths.get(carpetaDestino, nombreArchivo);

                Files.copy(imagen.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

                producto.setImagenUrl("/productos/" + nombreArchivo);

            } catch (Exception e) {
                throw new RuntimeException("Error al guardar la imagen del producto", e);
            }
        }

        // Asociar tallas con stock
        if (listaTallas != null && !listaTallas.isEmpty()) {
            for (ProductoTalla pt : listaTallas) {
                pt.setProducto(producto);
            }
            producto.setTallas(listaTallas);
        }

        // Asociar colegios
        if (producto.getColegios() != null && !producto.getColegios().isEmpty()) {
            producto.setColegios(producto.getColegios().stream()
                    .map(colegio -> colegioRepositorio.findById(colegio.getId())
                            .orElseThrow(() -> new RuntimeException("Colegio no encontrado: " + colegio.getId())))
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

        // Eliminar imagen de los archivos locales
        /*if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            try {
                String rutaImagen = "src/main/resources/static" + producto.getImagenUrl();
                Path path = Paths.get(rutaImagen);
                Files.deleteIfExists(path);
            } catch (Exception e) {
                System.err.println("Error al eliminar la imagen de producto: " + e.getMessage());
            }
        }*/
        productoRepositorio.deleteById(id.longValue());
    }

    
    @Override
    public Producto buscarPorId(Integer id) {
        return productoRepositorio.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    @Override
    public Producto actualizarProducto(Producto producto, List<ProductoTalla> listaTallas, MultipartFile imagen) {
        Producto existente = productoRepositorio.findById(producto.getId().longValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + producto.getId()));

        // actualizar campos
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

        // si sube una imagen nueva, elimina la anterior y conserva la nueva imagen
        if (imagen != null && !imagen.isEmpty()) {
            if (existente.getImagenUrl() != null && !existente.getImagenUrl().isEmpty()) {
                try {
                    String rutaImagen = "src/main/resources/static" + existente.getImagenUrl();
                    Path path = Paths.get(rutaImagen);
                    Files.deleteIfExists(path);
                } catch (Exception e) {
                    System.err.println("Error al eliminar imagen antigua: " + e.getMessage());
                }
            }

            try {
                String carpetaDestino = "src/main/resources/static/productos/";
                Files.createDirectories(Paths.get(carpetaDestino));

                String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                Path rutaDestino = Paths.get(carpetaDestino + nombreArchivo);

                Files.copy(imagen.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
                existente.setImagenUrl("/productos/" + nombreArchivo);
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar la imagen del producto", e);
            }
        }

        // actualizar y asociar colegios
        if (producto.getColegios() != null && !producto.getColegios().isEmpty()) {
            existente.setColegios(producto.getColegios().stream()
                    .map(colegio -> colegioRepositorio.findById(colegio.getId())
                            .orElseThrow(() -> new RuntimeException("Colegio no encontrado: " + colegio.getId())))
                    .collect(Collectors.toSet()));
        }

        // actualizar asociar tallas
        // para evitar duplicidad de datos: actualizamos, agregamos y eliminamos segun sea necesario
        if (listaTallas != null) {
            //creamos un mapa de tallas nuevas por id para una busqueda rapida
            java.util.Map<Integer, ProductoTalla> nuevasTallasMap = new java.util.HashMap<>();
            for (ProductoTalla pt : listaTallas) {
                nuevasTallasMap.put(pt.getTalla().getId(), pt);
            }
            
            // usamos iterar sobre las tallas existentes para eliminar de forma segura
            java.util.Iterator<ProductoTalla> iterator = existente.getTallas().iterator();
            while (iterator.hasNext()) {
                ProductoTalla tallaExistente = iterator.next();
                Integer tallaId = tallaExistente.getTalla().getId();
                
                if (nuevasTallasMap.containsKey(tallaId)) {
                    // actualiza la talla existente con los nuevos valores
                    ProductoTalla nuevaTalla = nuevasTallasMap.get(tallaId);
                    tallaExistente.setCantidadStock(nuevaTalla.getCantidadStock());
                    tallaExistente.setPrecioUnitarioFinal(nuevaTalla.getPrecioUnitarioFinal());
                    // remueve el mapa cuando ya fue procesado
                    nuevasTallasMap.remove(tallaId);
                } else {
                    // si no está en las nuevas tallas, se elimina
                    iterator.remove();
                }
            }
            
            // agregar las tallas nuevas que quedaron en el mapa
            for (ProductoTalla nuevaTalla : nuevasTallasMap.values()) {
                nuevaTalla.setProducto(existente);
                existente.getTallas().add(nuevaTalla);
            }
        } else {
            // Si no hay tallas, limpiar colección
            existente.getTallas().clear();
        }

        return productoRepositorio.save(existente);
    }
}
