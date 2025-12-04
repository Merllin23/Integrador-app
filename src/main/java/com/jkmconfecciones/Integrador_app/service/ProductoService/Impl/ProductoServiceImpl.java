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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
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
    private final ProductoTallaRepositorio productoTallaRepositorio;
    private final com.jkmconfecciones.Integrador_app.service.NotificacionAutomaticaService notificacionAutomaticaService;
    private final com.jkmconfecciones.Integrador_app.service.Auditoria.AuditoriaService auditoriaService;
    private final com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio usuarioRepositorio;

    private static final Logger logger = LoggerFactory.getLogger(ProductoServiceImpl.class);
    private static final String CARPETA_IMAGENES = "C:\\jkm\\productos\\";

    private String obtenerUsuarioLogueado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
    }

    @Override
    public Producto crearProducto(Producto producto, List<ProductoTalla> listaTallas, MultipartFile imagen) {
        Preconditions.checkNotNull(producto, "El producto no puede ser nulo");
        String username = obtenerUsuarioLogueado();

        String nombreOriginal = producto.getNombre();
        String nombreLimpio = (nombreOriginal != null) ? nombreOriginal.trim().replaceAll("\\s{2,}", " ") : "";
        Preconditions.checkArgument(!nombreLimpio.isEmpty() && nombreLimpio.length() >= 2,
                "El nombre del producto no puede estar vacío ni contener solo espacios.");
        producto.setNombre(nombreLimpio);

        logger.info("Usuario '{}' está creando producto: {}", username, producto.getNombre());

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

        if (listaTallas != null && !listaTallas.isEmpty()) {
            listaTallas.forEach(pt -> pt.setProducto(producto));
            producto.setTallas(ImmutableList.copyOf(listaTallas));
            logger.info("Asignadas {} tallas al producto '{}'", listaTallas.size(), producto.getNombre());
        } else {
            logger.warn("No se asignaron tallas al producto '{}'", producto.getNombre());
        }

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
        logger.info("Usuario '{}' creó el producto '{}' exitosamente con ID {}", username, guardado.getNombre(), guardado.getId());

        return guardado;
    }

    @Override
    public void eliminarProducto(Integer id) {
        String username = obtenerUsuarioLogueado();
        logger.info("Usuario '{}' intenta eliminar producto con ID {}", username, id);

        Producto producto = productoRepositorio.findById(id.longValue())
                .orElseThrow(() -> {
                    logger.error("Producto con ID {} no encontrado, no se puede eliminar", id);
                    return new RuntimeException("Producto no encontrado con ID: " + id);
                });

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
        logger.info("Usuario '{}' eliminó el producto '{}' con ID {}", username, producto.getNombre(), id);
    }

    @Override
    public Producto actualizarProducto(Producto producto, List<ProductoTalla> listaTallas, MultipartFile imagen) {
        String username = obtenerUsuarioLogueado();
        Producto existente = productoRepositorio.findById(producto.getId().longValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + producto.getId()));

        // Guardar precio anterior para detectar cambios
        Double precioAnterior = existente.getPrecioBase();

        String nombreOriginal = producto.getNombre();
        String nombreLimpio = (nombreOriginal != null) ? nombreOriginal.trim().replaceAll("\\s{2,}", " ") : "";
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

        if (imagen != null && !imagen.isEmpty()) {
            if (!Strings.isNullOrEmpty(existente.getImagenUrl())) {
                File antigua = new File(CARPETA_IMAGENES + existente.getImagenUrl().substring("/productos/".length()));
                FileUtils.deleteQuietly(antigua);
            }
            existente.setImagenUrl(guardarImagen(imagen));
        }

        if (producto.getColegios() != null && !producto.getColegios().isEmpty()) {
            existente.setColegios(
                    producto.getColegios().stream()
                            .map(c -> colegioRepositorio.findById(c.getId())
                                    .orElseThrow(() -> new RuntimeException("Colegio no encontrado: " + c.getId())))
                            .collect(Collectors.toSet())
            );
        }

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

        Producto actualizado = productoRepositorio.save(existente);
        logger.info("Usuario '{}' actualizó el producto '{}' con ID {}", username, actualizado.getNombre(), actualizado.getId());

        // Registrar auditoría
        try {
            Usuario usuario = usuarioRepositorio.findByCorreo(username).orElse(null);
            if (usuario != null) {
                auditoriaService.registrarAccionSimple(
                    usuario,
                    "ACTUALIZAR_PRODUCTO",
                    "PRODUCTO",
                    "EXITOSO",
                    "Producto actualizado: " + actualizado.getNombre()
                );
            }
        } catch (Exception e) {
            logger.error("Error al registrar auditoría", e);
        }

        // Notificar si hubo cambio de precio
        if (precioAnterior != null && !precioAnterior.equals(actualizado.getPrecioBase())) {
            try {
                notificacionAutomaticaService.notificarCambioPrecio(actualizado, precioAnterior, actualizado.getPrecioBase());
            } catch (Exception e) {
                logger.error("Error al generar notificación de cambio de precio", e);
            }
        }

        return actualizado;
    }

    @Transactional
    public void actualizarStock(Integer productoId, Integer cantidad) {
        ProductoTalla detalle = productoTallaRepositorio.findById(productoId.longValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        int stockAnterior = detalle.getCantidadStock();
        detalle.setCantidadStock(cantidad);
        productoTallaRepositorio.save(detalle);

        String username = obtenerUsuarioLogueado();
        logger.info("Usuario '{}' actualizó stock: Producto ID {}, nueva cantidad {}", username, productoId, cantidad);
        
        // Registrar auditoría
        try {
            Usuario usuario = usuarioRepositorio.findByCorreo(username).orElse(null);
            if (usuario != null) {
                auditoriaService.registrarAccionSimple(
                    usuario,
                    "ACTUALIZAR_STOCK",
                    "PRODUCTO_TALLA",
                    "EXITOSO",
                    String.format("Stock actualizado: %s - %s (De %d a %d unidades)",
                        detalle.getProducto().getNombre(),
                        detalle.getTalla().getNombreTalla(),
                        stockAnterior,
                        cantidad)
                );
            }
        } catch (Exception e) {
            logger.error("Error al registrar auditoría", e);
        }
    }

    @Override
    public byte[] exportarInventarioExcel(Integer colegioId) {
        String username = obtenerUsuarioLogueado();
        List<ProductoTalla> inventario = (colegioId != null && colegioId > 0)
                ? obtenerInventarioPorColegio(colegioId)
                : obtenerInventarioCompleto();

        logger.info("Usuario '{}' exportó inventario a Excel para colegio ID {}", username, colegioId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");
            Row header = sheet.createRow(0);
            String[] columnas = {"Producto", "Talla", "Stock", "Precio"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columnas[i]);
                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowNum = 1;
            for (ProductoTalla pt : inventario) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(pt.getProducto().getNombre());
                row.createCell(1).setCellValue(pt.getTalla().getNombreTalla());
                row.createCell(2).setCellValue(pt.getCantidadStock());
                row.createCell(3).setCellValue(pt.getPrecioUnitarioFinal().doubleValue());
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel del inventario", e);
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

    @Override
    public List<ProductoTalla> obtenerInventarioCompleto() {
        return productoTallaRepositorio.findAllWithDetails();
    }

    @Override
    public List<ProductoTalla> obtenerInventarioPorColegio(Integer colegioId) {
        return productoTallaRepositorio.findByColegioId(colegioId);
    }

    @Override
    public Producto buscarPorId(Integer id) {
        Producto p = productoRepositorio.findById(id.longValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        if (p.getCategoria() != null) p.getCategoria().getNombre();
        if (p.getColeccion() != null) p.getColeccion().getNombre();
        p.getColegios().size();
        return p;
    }

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
}
