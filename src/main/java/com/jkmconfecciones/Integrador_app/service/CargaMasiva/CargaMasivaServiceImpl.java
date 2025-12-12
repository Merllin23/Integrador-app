package com.jkmconfecciones.Integrador_app.service.CargaMasiva;

import com.jkmconfecciones.Integrador_app.DTO.ProductoCargaMasivaDTO;
import com.jkmconfecciones.Integrador_app.DTO.ProductoCargaMasivaDTO.TallaStockDTO;
import com.jkmconfecciones.Integrador_app.DTO.ResultadoCargaMasivaDTO;
import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.repositorios.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CargaMasivaServiceImpl implements CargaMasivaService {

    private final ProductoRepositorio productoRepositorio;
    private final CategoriaRepositorio categoriaRepositorio;
    private final ColeccionRepositorio coleccionRepositorio;
    private final ColegioRepositorio colegioRepositorio;
    private final TallaRepositorio tallaRepositorio;
    private final ProductoTallaRepositorio productoTallaRepositorio;

    @Override
    public ResultadoCargaMasivaDTO procesarArchivo(MultipartFile archivo) throws IOException {
        String nombreArchivo = archivo.getOriginalFilename();
        
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }

        if (nombreArchivo.endsWith(".csv")) {
            return procesarCSV(archivo);
        } else if (nombreArchivo.endsWith(".xlsx") || nombreArchivo.endsWith(".xls")) {
            return procesarExcel(archivo);
        } else {
            throw new IllegalArgumentException("Formato de archivo no soportado. Use CSV o Excel (.xlsx, .xls)");
        }
    }

    private ResultadoCargaMasivaDTO procesarCSV(MultipartFile archivo) throws IOException {
        List<ProductoCargaMasivaDTO> productos = new ArrayList<>();
        List<String> erroresGenerales = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            
            String linea;
            int numeroFila = 0;
            String[] encabezados = null;
            
            while ((linea = reader.readLine()) != null) {
                numeroFila++;
                
                if (numeroFila == 1) {
                    // Validar encabezados
                    encabezados = linea.split(",");
                    if (!validarEncabezados(encabezados)) {
                        erroresGenerales.add("Los encabezados del CSV no coinciden con el formato esperado");
                        break;
                    }
                    continue;
                }
                
                try {
                    ProductoCargaMasivaDTO producto = parsearLineaCSV(linea, numeroFila);
                    validarProducto(producto);
                    productos.add(producto);
                } catch (Exception e) {
                    log.error("Error al procesar fila {}: {}", numeroFila, e.getMessage());
                    ProductoCargaMasivaDTO productoError = ProductoCargaMasivaDTO.builder()
                            .fila(numeroFila)
                            .estado("FORMATO_INVALIDO")
                            .build();
                    productoError.getErrores().add("Error al procesar: " + e.getMessage());
                    productos.add(productoError);
                }
            }
        }

        return construirResultado(productos, erroresGenerales);
    }

    private ResultadoCargaMasivaDTO procesarExcel(MultipartFile archivo) throws IOException {
        List<ProductoCargaMasivaDTO> productos = new ArrayList<>();
        List<String> erroresGenerales = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Validar encabezados (primera fila)
            Row headerRow = sheet.getRow(0);
            if (headerRow == null || !validarEncabezadosExcel(headerRow)) {
                erroresGenerales.add("Los encabezados del Excel no coinciden con el formato esperado");
                return ResultadoCargaMasivaDTO.builder()
                        .erroresGenerales(erroresGenerales)
                        .build();
            }

            // Procesar filas de datos
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    ProductoCargaMasivaDTO producto = parsearFilaExcel(row, i + 1);
                    validarProducto(producto);
                    productos.add(producto);
                } catch (Exception e) {
                    log.error("Error al procesar fila {}: {}", i + 1, e.getMessage());
                    ProductoCargaMasivaDTO productoError = ProductoCargaMasivaDTO.builder()
                            .fila(i + 1)
                            .estado("FORMATO_INVALIDO")
                            .build();
                    productoError.getErrores().add("Error al procesar: " + e.getMessage());
                    productos.add(productoError);
                }
            }
        }

        return construirResultado(productos, erroresGenerales);
    }

    private boolean validarEncabezados(String[] encabezados) {
        if (encabezados.length < 8) return false;
        
        String[] esperados = {"nombre", "descripcion", "precio", "categoria", "coleccion", 
                             "colegio", "talla", "stock"};
        
        for (int i = 0; i < esperados.length; i++) {
            if (!encabezados[i].trim().equalsIgnoreCase(esperados[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean validarEncabezadosExcel(Row headerRow) {
        String[] esperados = {"nombre", "descripcion", "precio", "categoria", "coleccion", 
                             "colegio", "talla", "stock"};
        
        if (headerRow.getLastCellNum() < esperados.length) return false;
        
        for (int i = 0; i < esperados.length; i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null || !cell.getStringCellValue().trim().equalsIgnoreCase(esperados[i])) {
                return false;
            }
        }
        return true;
    }

    private ProductoCargaMasivaDTO parsearLineaCSV(String linea, int numeroFila) {
        String[] valores = linea.split(",", -1); // -1 para incluir campos vacíos
        
        if (valores.length < 8) {
            throw new IllegalArgumentException("Número insuficiente de columnas");
        }

        return ProductoCargaMasivaDTO.builder()
                .fila(numeroFila)
                .nombre(valores[0].trim())
                .descripcion(valores[1].trim())
                .precio(parseDouble(valores[2].trim()))
                .categoria(valores[3].trim())
                .coleccion(valores[4].trim())
                .colegioAsociado(valores[5].trim())
                .tallas(Arrays.asList(TallaStockDTO.builder()
                        .nombreTalla(valores[6].trim())
                        .stock(parseInt(valores[7].trim()))
                        .build()))
                .build();
    }

    private ProductoCargaMasivaDTO parsearFilaExcel(Row row, int numeroFila) {
        List<TallaStockDTO> tallas = new ArrayList<>();
        
        // talla y stock (puede haber multiples columnas de talla-stock)
        for (int i = 6; i < row.getLastCellNum(); i += 2) {
            Cell cellTalla = row.getCell(i);
            Cell cellStock = row.getCell(i + 1);
            
            if (cellTalla != null && cellStock != null) {
                String nombreTalla = getCellValueAsString(cellTalla).trim();
                Integer stock = (int) getCellValueAsDouble(cellStock);
                
                if (!nombreTalla.isEmpty()) {
                    tallas.add(TallaStockDTO.builder()
                            .nombreTalla(nombreTalla)
                            .stock(stock)
                            .build());
                }
            }
        }

        return ProductoCargaMasivaDTO.builder()
                .fila(numeroFila)
                .nombre(getCellValueAsString(row.getCell(0)).trim())
                .descripcion(getCellValueAsString(row.getCell(1)).trim())
                .precio(getCellValueAsDouble(row.getCell(2)))
                .categoria(getCellValueAsString(row.getCell(3)).trim())
                .coleccion(getCellValueAsString(row.getCell(4)).trim())
                .colegioAsociado(getCellValueAsString(row.getCell(5)).trim())
                .tallas(tallas)
                .build();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private double getCellValueAsDouble(Cell cell) {
        if (cell == null) return 0.0;
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            default:
                return 0.0;
        }
    }

    private void validarProducto(ProductoCargaMasivaDTO producto) {
        List<String> errores = new ArrayList<>();

        // Validar nombre
        if (producto.getNombre() == null || producto.getNombre().isEmpty()) {
            errores.add("El nombre es obligatorio");
        }

        // Validar precio
        if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
            errores.add("El precio debe ser mayor a 0");
        }

        // Validar categoría existe
        if (producto.getCategoria() == null || producto.getCategoria().isEmpty()) {
            errores.add("La categoría es obligatoria");
        } else if (!categoriaRepositorio.existsByNombre(producto.getCategoria())) {
            errores.add("La categoría '" + producto.getCategoria() + "' no existe en el sistema");
        }

        // Validar colección existe
        if (producto.getColeccion() != null && !producto.getColeccion().isEmpty()) {
            if (!coleccionRepositorio.existsByNombre(producto.getColeccion())) {
                errores.add("La colección '" + producto.getColeccion() + "' no existe en el sistema");
            }
        }

        // Validar colegio existe
        if (producto.getColegioAsociado() == null || producto.getColegioAsociado().isEmpty()) {
            errores.add("El colegio asociado es obligatorio");
        } else if (!colegioRepositorio.existsByNombre(producto.getColegioAsociado())) {
            errores.add("El colegio '" + producto.getColegioAsociado() + "' no existe en el sistema");
        }

        // Validar tallas
        if (producto.getTallas() == null || producto.getTallas().isEmpty()) {
            errores.add("Debe especificar al menos una talla con stock");
        } else {
            for (TallaStockDTO talla : producto.getTallas()) {
                if (talla.getNombreTalla() == null || talla.getNombreTalla().isEmpty()) {
                    errores.add("Nombre de talla vacío");
                } else if (!tallaRepositorio.existsByNombreTalla(talla.getNombreTalla())) {
                    errores.add("La talla '" + talla.getNombreTalla() + "' no existe en el sistema");
                }
                
                if (talla.getStock() == null || talla.getStock() < 0) {
                    errores.add("El stock debe ser mayor o igual a 0");
                }
            }
        }

        // Establecer estado según errores
        if (errores.isEmpty()) {
            producto.setEstado("VALIDO");
        } else {
            producto.setEstado("FORMATO_INVALIDO");
            producto.setErrores(errores);
        }
    }

    private ResultadoCargaMasivaDTO construirResultado(List<ProductoCargaMasivaDTO> productos, 
                                                       List<String> erroresGenerales) {
        int validos = (int) productos.stream()
                .filter(p -> "VALIDO".equals(p.getEstado()))
                .count();
        
        return ResultadoCargaMasivaDTO.builder()
                .productos(productos)
                .totalProductos(productos.size())
                .productosValidos(validos)
                .productosInvalidos(productos.size() - validos)
                .erroresGenerales(erroresGenerales)
                .build();
    }

    @Override
    @Transactional
    public int importarProductos(List<ProductoCargaMasivaDTO> productos) {
        int importados = 0;

        for (ProductoCargaMasivaDTO dto : productos) {
            if (!"VALIDO".equals(dto.getEstado())) {
                continue; // Saltar productos inválidos
            }

            try {
                // Buscar entidades relacionadas
                Categoria categoria = categoriaRepositorio.findByNombre(dto.getCategoria())
                        .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
                
                Coleccion coleccion = null;
                if (dto.getColeccion() != null && !dto.getColeccion().isEmpty()) {
                    coleccion = coleccionRepositorio.findByNombre(dto.getColeccion()).orElse(null);
                }
                
                Colegio colegio = colegioRepositorio.findByNombre(dto.getColegioAsociado())
                        .orElseThrow(() -> new RuntimeException("Colegio no encontrado"));

                // Crear producto
                Producto producto = new Producto();
                producto.setNombre(dto.getNombre());
                producto.setDescripcion(dto.getDescripcion());
                producto.setPrecioBase(dto.getPrecio());
                producto.setCategoria(categoria);
                producto.setColeccion(coleccion);
                producto.setColegios(new HashSet<>(Arrays.asList(colegio)));

                // Guardar producto
                producto = productoRepositorio.save(producto);

                // Crear ProductoTalla para cada talla
                for (TallaStockDTO tallaDTO : dto.getTallas()) {
                    Talla talla = tallaRepositorio.findByNombreTalla(tallaDTO.getNombreTalla())
                            .orElseThrow(() -> new RuntimeException("Talla no encontrada"));

                    ProductoTalla productoTalla = new ProductoTalla();
                    productoTalla.setProducto(producto);
                    productoTalla.setTalla(talla);
                    productoTalla.setCantidadStock(tallaDTO.getStock());
                    productoTalla.setPrecioUnitarioFinal(dto.getPrecio()); // Usar precio base

                    productoTallaRepositorio.save(productoTalla);
                }

                importados++;
                log.info("Producto '{}' importado exitosamente", dto.getNombre());
            } catch (Exception e) {
                log.error("Error al importar producto '{}': {}", dto.getNombre(), e.getMessage());
            }
        }

        return importados;
    }

    @Override
    public byte[] generarPlantillaCSV() {
        StringBuilder csv = new StringBuilder();
        
        // Encabezados
        csv.append("nombre,descripcion,precio,categoria,coleccion,colegio,talla,stock\n");
        
        // Obtener datos reales del sistema para los ejemplos
        List<String> categorias = categoriaRepositorio.findAll().stream()
                .map(Categoria::getNombre)
                .toList();
        
        List<String> colecciones = coleccionRepositorio.findAll().stream()
                .map(Coleccion::getNombre)
                .toList();
        
        List<String> colegios = colegioRepositorio.findAll().stream()
                .map(Colegio::getNombre)
                .toList();
        
        List<String> tallas = tallaRepositorio.findAll().stream()
                .map(Talla::getNombreTalla)
                .toList();

        // Datos de ejemplo
        String categoriaEjemplo = categorias.isEmpty() ? "Uniformes" : categorias.get(0);
        String coleccionEjemplo = colecciones.isEmpty() ? "Verano 2024" : colecciones.get(0);
        String colegioEjemplo = colegios.isEmpty() ? "Colegio San Juan" : colegios.get(0);
        String tallaEjemplo = tallas.isEmpty() ? "M" : tallas.get(0);

        csv.append(String.format("Camisa Escolar Blanca,Camisa de algodon para uniforme,45.50,%s,%s,%s,%s,50\n",
                categoriaEjemplo, coleccionEjemplo, colegioEjemplo, tallaEjemplo));
        
        csv.append(String.format("Pantalon Azul Marino,Pantalon de drill para uniforme,55.00,%s,%s,%s,%s,30\n",
                categoriaEjemplo, coleccionEjemplo, colegioEjemplo, tallaEjemplo));

        // Agregar comentario con opciones disponibles
        csv.append("\n# Categorias disponibles: ").append(String.join(", ", categorias)).append("\n");
        csv.append("# Colecciones disponibles: ").append(String.join(", ", colecciones)).append("\n");
        csv.append("# Colegios disponibles: ").append(String.join(", ", colegios)).append("\n");
        csv.append("# Tallas disponibles: ").append(String.join(", ", tallas)).append("\n");

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private Double parseDouble(String valor) {
        try {
            return valor.isEmpty() ? null : Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInt(String valor) {
        try {
            return valor.isEmpty() ? null : Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
