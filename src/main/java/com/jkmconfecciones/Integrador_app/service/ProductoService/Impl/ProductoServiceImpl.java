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
                String carpetaDestino = "src/main/resources/static/productos/";
                Files.createDirectories(Paths.get(carpetaDestino));

                String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                Path rutaDestino = Paths.get(carpetaDestino + nombreArchivo);

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
}
