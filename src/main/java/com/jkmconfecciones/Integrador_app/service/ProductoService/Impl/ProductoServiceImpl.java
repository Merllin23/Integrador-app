package com.jkmconfecciones.Integrador_app.service.ProductoService.Impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.repositorios.*;
import com.jkmconfecciones.Integrador_app.service.CloudinaryService;
import com.jkmconfecciones.Integrador_app.service.ProductoService.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
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
    private final CloudinaryService cloudinaryService;

    private static final Logger logger = LoggerFactory.getLogger(ProductoServiceImpl.class);

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
                String urlImagen = cloudinaryService.subirImagen(imagen, "productos");
                producto.setImagenUrl(urlImagen);
                logger.info("Imagen guardada correctamente en Cloudinary: {}", urlImagen);
            } catch (Exception e) {
                logger.error("Error al guardar la imagen del producto {} en Cloudinary", producto.getNombre(), e);
                throw new RuntimeException("Error al guardar la imagen", e);
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
            try {
                cloudinaryService.eliminarImagen(producto.getImagenUrl());
                logger.info("Imagen eliminada correctamente de Cloudinary: {}", producto.getImagenUrl());
            } catch (Exception e) {
                logger.warn("No se pudo eliminar la imagen de Cloudinary: {}", producto.getImagenUrl(), e);
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
                try {
                    cloudinaryService.eliminarImagen(existente.getImagenUrl());
                    logger.info("Imagen anterior eliminada de Cloudinary");
                } catch (Exception e) {
                    logger.warn("No se pudo eliminar la imagen anterior de Cloudinary", e);
                }
            }
            try {
                String nuevaUrl = cloudinaryService.subirImagen(imagen, "productos");
                existente.setImagenUrl(nuevaUrl);
                logger.info("Nueva imagen guardada en Cloudinary: {}", nuevaUrl);
            } catch (Exception e) {
                logger.error("Error al subir nueva imagen a Cloudinary", e);
                throw new RuntimeException("Error al actualizar la imagen", e);
            }
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

        return actualizado;
    }

    @Transactional
    public void actualizarStock(Integer productoId, Integer cantidad) {
        ProductoTalla detalle = productoTallaRepositorio.findById(productoId.longValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        detalle.setCantidadStock(cantidad);
        productoTallaRepositorio.save(detalle);

        String username = obtenerUsuarioLogueado();
        logger.info("Usuario '{}' actualizó stock: Producto ID {}, nueva cantidad {}", username, productoId, cantidad);
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
            
            // Estilos profesionales
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Estilo para filas alternas
            CellStyle evenRowStyle = workbook.createCellStyle();
            evenRowStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            evenRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            evenRowStyle.setBorderBottom(BorderStyle.THIN);
            evenRowStyle.setBorderLeft(BorderStyle.THIN);
            evenRowStyle.setBorderRight(BorderStyle.THIN);

            CellStyle oddRowStyle = workbook.createCellStyle();
            oddRowStyle.setBorderBottom(BorderStyle.THIN);
            oddRowStyle.setBorderLeft(BorderStyle.THIN);
            oddRowStyle.setBorderRight(BorderStyle.THIN);

            // Estilo para precios (formato moneda)
            CellStyle priceStyle = workbook.createCellStyle();
            priceStyle.setDataFormat(workbook.createDataFormat().getFormat("S/ #,##0.00"));
            priceStyle.setBorderBottom(BorderStyle.THIN);
            priceStyle.setBorderLeft(BorderStyle.THIN);
            priceStyle.setBorderRight(BorderStyle.THIN);

            CellStyle pricestyleEven = workbook.createCellStyle();
            pricestyleEven.cloneStyleFrom(priceStyle);
            pricestyleEven.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            pricestyleEven.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Estilo para números centrados
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setAlignment(HorizontalAlignment.CENTER);
            numberStyle.setBorderBottom(BorderStyle.THIN);
            numberStyle.setBorderLeft(BorderStyle.THIN);
            numberStyle.setBorderRight(BorderStyle.THIN);

            CellStyle numberStyleEven = workbook.createCellStyle();
            numberStyleEven.cloneStyleFrom(numberStyle);
            numberStyleEven.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            numberStyleEven.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Crear encabezado
            Row header = sheet.createRow(0);
            header.setHeightInPoints(25);
            String[] columnas = {"Producto", "Talla", "Stock", "Precio Unitario"};
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos con estilos alternados
            int rowNum = 1;
            for (ProductoTalla pt : inventario) {
                Row row = sheet.createRow(rowNum);
                boolean isEven = (rowNum % 2 == 0);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(pt.getProducto().getNombre());
                cell0.setCellStyle(isEven ? evenRowStyle : oddRowStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(pt.getTalla().getNombreTalla());
                cell1.setCellStyle(isEven ? numberStyleEven : numberStyle);
                
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(pt.getCantidadStock());
                cell2.setCellStyle(isEven ? numberStyleEven : numberStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(pt.getPrecioUnitarioFinal().doubleValue());
                cell3.setCellStyle(isEven ? pricestyleEven : priceStyle);
                
                rowNum++;
            }

            // Ajustar ancho de columnas
            sheet.setColumnWidth(0, 8000); // Producto
            sheet.setColumnWidth(1, 2500); // Talla
            sheet.setColumnWidth(2, 2500); // Stock
            sheet.setColumnWidth(3, 4000); // Precio

            // Congelar primera fila
            sheet.createFreezePane(0, 1);

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

}
