package com.jkmconfecciones.Integrador_app.controller.admin;

import com.jkmconfecciones.Integrador_app.DTO.AuditoriaDTO;
import com.jkmconfecciones.Integrador_app.DTO.CotizacionDetalleDTO;
import com.jkmconfecciones.Integrador_app.DTO.ProductoDetalleDTO;
import com.jkmconfecciones.Integrador_app.DTO.ProductoCargaMasivaDTO;
import com.jkmconfecciones.Integrador_app.DTO.ResultadoCargaMasivaDTO;
import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.service.ControlClientes.ClienteService;
import com.jkmconfecciones.Integrador_app.service.CotizacionAdmin.AdminCotizacionService;
import com.jkmconfecciones.Integrador_app.service.ProductoService.*;
import com.jkmconfecciones.Integrador_app.service.Tallas.ProductoTallaService;
import com.jkmconfecciones.Integrador_app.service.UsuarioService;
import com.jkmconfecciones.Integrador_app.service.Notificacion.NotificacionService;
import com.jkmconfecciones.Integrador_app.service.Auditoria.AuditoriaService;
import com.jkmconfecciones.Integrador_app.service.CargaMasiva.CargaMasivaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminControlador {

    private final CategoriaService categoriaService;
    private final ColeccionService coleccionService;
    private final ColegioService colegioService;
    private final ProductoService productoService;
    private final TallaService tallaService;
    private final AdminCotizacionService adminCotizacionService;
    private final ClienteService clienteService;
    private final NotificacionService notificacionService;
    private final UsuarioService usuarioService;
    private final AuditoriaService auditoriaService;
    private final CargaMasivaService cargaMasivaService;
    private final ProductoTallaService productoTallaService;


    @GetMapping("/panel")
    public String mostrarPanelAdmin(Model model) {
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Panel de Administración - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        model.addAttribute("mainContent", "admin/panel :: mainContent");
        model.addAttribute("extraCss", "admin/panel :: extraCss");
        model.addAttribute("extraJs", "admin/panel :: extraJs");

        return "fragments/admin-layout";
    }

    @GetMapping("/cat-col")
    public String paginaCatCol(@RequestParam(defaultValue = "categorias") String tipo, Model model) {

        model.addAttribute("currentPage", "cat-col");
        model.addAttribute("pageTitle", "Categorías y Colecciones - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        // Enviar lo que el HTML espera: "items"
        if (tipo.equals("colecciones")) {
            model.addAttribute("items", coleccionService.listarColecciones());
        } else {
            model.addAttribute("items", categoriaService.listarCategorias());
        }

        model.addAttribute("tipo", tipo);

        // Fragmentos
        model.addAttribute("mainContent", "admin/cat-col :: mainContent");
        model.addAttribute("extraCss", "admin/cat-col :: extraCss");
        model.addAttribute("extraJs", "admin/cat-col :: extraJs");

        return "fragments/admin-layout";
    }

    @PostMapping("/categorias/crear")
    @ResponseBody
    public Map<String, Object> crearCategoria(@RequestBody Map<String, String> body) {
        try {
            Categoria categoria = new Categoria();
            categoria.setNombre(body.get("nombre"));
            categoriaService.guardarCategoria(categoria);

            return Map.of("success", true, "message", "Categoría creada correctamente");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error al crear categoría");
        }
    }

    @PutMapping("/categorias/editar/{id}")
    @ResponseBody
    public Map<String, Object> editarCategoria(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            categoriaService.editarCategoria(id, body.get("nombre"));
            return Map.of("success", true, "message", "Categoría actualizada");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error al actualizar categoría");
        }
    }

    @DeleteMapping("/categorias/eliminar/{id}")
    @ResponseBody
    public Map<String, Object> eliminarCategoria(@PathVariable Long id) {
        try {
            categoriaService.eliminarCategoria(id);
            return Map.of("success", true, "message", "Categoría eliminada");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error al eliminar categoría");
        }
    }

    @PostMapping("/colecciones/crear")
    @ResponseBody
    public Map<String, Object> crearColeccion(@RequestBody Map<String, String> body) {
        try {
            Coleccion coleccion = new Coleccion();
            coleccion.setNombre(body.get("nombre"));
            coleccionService.guardarColeccion(coleccion);

            return Map.of("success", true, "message", "Colección creada correctamente");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error al crear colección");
        }
    }

    @PutMapping("/colecciones/editar/{id}")
    @ResponseBody
    public Map<String, Object> editarColeccion(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            coleccionService.editarColeccion(id, body.get("nombre"));
            return Map.of("success", true, "message", "Colección actualizada");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error al actualizar colección");
        }
    }

    @DeleteMapping("/colecciones/eliminar/{id}")
    @ResponseBody
    public Map<String, Object> eliminarColeccion(@PathVariable Long id) {
        try {
            coleccionService.eliminarColeccion(id);
            return Map.of("success", true, "message", "Colección eliminada");
        } catch (Exception e) {
            return Map.of("success", false, "message", "Error al eliminar colección");
        }
    }

    @GetMapping("/pedidos")
    public String paginaPedidosAdmin(@RequestParam(value = "estado", required = false) String estado,
                                     Model model) {
        model.addAttribute("currentPage", "pedidos");
        model.addAttribute("pageTitle", "Gestión de Pedidos - JKM Confecciones");

        List<Cotizacion> cotizaciones;
        if (estado == null || estado.isEmpty()) {
            cotizaciones = adminCotizacionService.listarPedidos(); // Todas las cotizaciones
        } else {
            cotizaciones = adminCotizacionService.listarCotizacionesPorEstado(estado); // Filtrado por estado
        }

        model.addAttribute("pedidos", cotizaciones); // Renombramos para mantener consistencia con el HTML
        model.addAttribute("estadoSeleccionado", estado);
        model.addAttribute("mainContent", "admin/pedidos :: mainContent");
        model.addAttribute("extraCss", "admin/pedidos :: extraCss");
        model.addAttribute("extraJs", "admin/pedidos :: extraJs");

        return "fragments/admin-layout";
    }

    @PostMapping("/pedidos/{id}/avanzar")
    @ResponseBody
    public Map<String, Object> avanzarPedido(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            adminCotizacionService.avanzarEstado(id);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @GetMapping("/pedidos/{id}/detalle")
    @ResponseBody
    public Map<String, Object> detallePedido(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            CotizacionDetalleDTO detalleDTO = adminCotizacionService.obtenerDetalle(id);
            response.put("success", true);
            response.put("cotizacion", detalleDTO);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Pedido no encontrado");
        }
        return response;
    }

    @GetMapping("/cargaMasivaDatos")
    public String paginaCargadeDatos(Model model) {
        model.addAttribute("currentPage", "cargaMasivaDatos");
        model.addAttribute("pageTitle", "Carga Masiva de Datos - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        model.addAttribute("mainContent", "admin/cargaMasivaDatos :: mainContent");
        model.addAttribute("extraCss", "admin/cargaMasivaDatos :: extraCss");
        model.addAttribute("extraJs", "admin/cargaMasivaDatos :: extraJs");
        return "fragments/admin-layout";
    }

    @PostMapping("/cargaMasivaDatos/procesar")
    @ResponseBody
    public ResponseEntity<ResultadoCargaMasivaDTO> procesarArchivoCargaMasiva(
            @RequestParam("archivo") MultipartFile archivo) {
        try {
            if (archivo.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            ResultadoCargaMasivaDTO resultado = cargaMasivaService.procesarArchivo(archivo);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al procesar archivo de carga masiva", e);
            ResultadoCargaMasivaDTO error = ResultadoCargaMasivaDTO.builder()
                    .erroresGenerales(Arrays.asList("Error al procesar archivo: " + e.getMessage()))
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/cargaMasivaDatos/importar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> importarProductosCargaMasiva(
            @RequestBody List<ProductoCargaMasivaDTO> productos) {
        try {
            int importados = cargaMasivaService.importarProductos(productos);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("productosImportados", importados);
            response.put("message", importados + " productos importados exitosamente");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al importar productos", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al importar productos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/cargaMasivaDatos/descargarPlantilla")
    public ResponseEntity<byte[]> descargarPlantillaCSV() {
        try {
            byte[] plantilla = cargaMasivaService.generarPlantillaCSV();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDisposition(
                    ContentDisposition.builder("attachment")
                            .filename("plantilla_productos.csv", StandardCharsets.UTF_8)
                            .build()
            );
            
            return new ResponseEntity<>(plantilla, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al generar plantilla CSV", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/edicionProducto")
    public String paginaEdicionProducto(Model model) {
        model.addAttribute("currentPage", "edicionProducto");
        model.addAttribute("pageTitle", "Edición de Producto - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        model.addAttribute("mainContent", "admin/edicionProducto :: mainContent");
        return "fragments/admin-layout";
    }

    @GetMapping("/inventario")
    public String adminStock(Model model,
                             @RequestParam(value = "colegioId", required = false) Integer colegioId,
                             @RequestParam(value = "page", defaultValue = "1") Integer pagina) {

        int TAM_PAGINA = 10; // máximo registros de inventario por página

        model.addAttribute("currentPage", "stock");
        model.addAttribute("pageTitle", "Stock de Productos - JKM Confecciones");

        // Lista de colegios para el filtro
        model.addAttribute("colegios", colegioService.listarColegios());

        // Si se seleccionó un colegio, filtrar por ese colegio
        List<ProductoTalla> detallesStockCompleto;
        if (colegioId != null && colegioId > 0) {
            detallesStockCompleto = productoService.obtenerInventarioPorColegio(colegioId);
        } else {
            detallesStockCompleto = productoService.obtenerInventarioCompleto();
        }

        // Paginación
        int totalRegistros = detallesStockCompleto.size();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / TAM_PAGINA);
        totalPaginas = Math.max(1, totalPaginas); // al menos 1 página
        pagina = Math.max(1, Math.min(pagina, totalPaginas)); // asegurar que no salga de rango

        int desde = (pagina - 1) * TAM_PAGINA;
        int hasta = Math.min(desde + TAM_PAGINA, totalRegistros);
        List<ProductoTalla> detallesStock = totalRegistros > 0 ? detallesStockCompleto.subList(desde, hasta) : detallesStockCompleto;

        model.addAttribute("detallesStock", detallesStock);
        model.addAttribute("colegioIdSeleccionado", colegioId);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("totalRegistros", totalRegistros);

        model.addAttribute("mainContent", "admin/inventario :: mainContent");
        model.addAttribute("extraCss", "admin/inventario :: extraCss");
        model.addAttribute("extraJs", "admin/inventario :: extraJs");
        return "fragments/admin-layout";
    }

    @GetMapping("/registroAuditoriaSeguridad")
    public String paginaRegistroAuditoria(Model model) {
        model.addAttribute("currentPage", "registroAuditoriaSeguridad");
        model.addAttribute("pageTitle", "Auditoría de Seguridad - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        model.addAttribute("mainContent", "admin/registroAuditoriaSeguridad :: mainContent");
        model.addAttribute("extraCss", "admin/registroAuditoriaSeguridad :: extraCss");
        model.addAttribute("extraJs", "admin/registroAuditoriaSeguridad :: extraJs");
        return "fragments/admin-layout";
    }

    @GetMapping("/productos/nuevo")
    public String adminNuevoProducto(Model model) {
        model.addAttribute("currentPage", "edicionProducto");
        model.addAttribute("pageTitle", "Añadir Producto - JKM Confecciones");

        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("colecciones", coleccionService.listarColecciones());
        model.addAttribute("colegios", colegioService.listarColegios());
        model.addAttribute("tallas", tallaService.listarTallas());

        model.addAttribute("mainContent", "admin/productoForm :: mainContent");
        model.addAttribute("extraCss", "admin/productoForm :: extraCss");
        model.addAttribute("extraJs", "admin/productoForm :: extraJs");
        return "fragments/admin-layout";
    }

    @GetMapping("/productos/{id}/editar")
    public String adminEditarProducto(@PathVariable Integer id, Model model) {
        model.addAttribute("currentPage", "edicionProducto");
        model.addAttribute("pageTitle", "Editar Producto - JKM Confecciones");

        Producto producto = productoService.buscarPorId(id);

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("colecciones", coleccionService.listarColecciones());
        model.addAttribute("colegios", colegioService.listarColegios());
        model.addAttribute("tallas", tallaService.listarTallas());

        // IDs de colegios asociados
        Set<Integer> colegiosIds = producto.getColegios().stream()
                .map(Colegio::getId)
                .collect(Collectors.toSet());
        model.addAttribute("colegiosIds", colegiosIds);

        // Crear mapa de tallas por id para facilitar Thymeleaf
        Map<Integer, ProductoTalla> mapaTallas = new HashMap<>();
        if (producto.getTallas() != null) {
            for (ProductoTalla pt : producto.getTallas()) {
                mapaTallas.put(pt.getTalla().getId(), pt);
            }
        }
        model.addAttribute("mapaTallas", mapaTallas);

        model.addAttribute("formAction", "/admin/productos/" + id + "/editar");
        model.addAttribute("mainContent", "admin/productoForm :: mainContent");
        model.addAttribute("extraCss", "admin/productoForm :: extraCss");
        model.addAttribute("extraJs", "admin/productoForm :: extraJs");
        return "fragments/admin-layout";
    }


    @PostMapping("/productos/{id}/editar")
    public String guardarEdicionProducto(
            @PathVariable Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestParam("coleccionId") Long coleccionId,
            @RequestParam(value = "colegioId", required = false) Long colegioId,
            @RequestParam(value = "tallas", required = false) List<Integer> tallaIds,
            @RequestParam Map<String, String> allParams,
            @RequestParam(name = "imagen", required = false) MultipartFile imagen,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            Producto productoExistente = productoService.buscarPorId(id);

            productoExistente.setNombre(nombre);
            productoExistente.setDescripcion(descripcion);
            productoExistente.setPrecioBase(precio);

            Categoria cat = new Categoria();
            cat.setId(categoriaId.intValue());
            productoExistente.setCategoria(cat);

            Coleccion col = new Coleccion();
            col.setId(coleccionId.intValue());
            productoExistente.setColeccion(col);

            if (colegioId != null) {
                Colegio colegio = new Colegio();
                colegio.setId(colegioId.intValue());
                productoExistente.setColegios(Set.of(colegio));
            }

            // Crear lista de ProductoTalla con stock
            List<ProductoTalla> listaTallas = new ArrayList<>();
            if (tallaIds != null) {
                for (Integer idTalla : tallaIds) {
                    String stockStr = allParams.get("stock__" + idTalla);
                    int stock = 0;
                    if (stockStr != null && !stockStr.isEmpty()) {
                        try {
                            stock = Integer.parseInt(stockStr);
                        } catch (NumberFormatException e) {
                            log.warn("Stock inválido para talla {}: '{}'", idTalla, stockStr);
                        }
                    }

                    Talla talla = tallaService.buscarPorId(idTalla);
                    ProductoTalla pt = new ProductoTalla();
                    pt.setProducto(productoExistente);
                    pt.setTalla(talla);
                    pt.setCantidadStock(stock);
                    pt.setPrecioUnitarioFinal(productoExistente.getPrecioBase());
                    listaTallas.add(pt);
                }
            }

            // Guardar actualización
            productoService.actualizarProducto(productoExistente, listaTallas, imagen);

            redirectAttributes.addFlashAttribute("exito", "Producto actualizado correctamente.");
            log.info("Producto '{}' actualizado correctamente.", nombre);
            return "redirect:/admin/productos";

        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al editar producto: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado al editar producto: {}", e.getMessage(), e);
            model.addAttribute("error", "Error inesperado: " + e.getMessage());
        }

        // Si hubo error, recarga el formulario
        model.addAttribute("producto", productoService.buscarPorId(id));
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("colecciones", coleccionService.listarColecciones());
        model.addAttribute("colegios", colegioService.listarColegios());
        model.addAttribute("tallas", tallaService.listarTallas());

        model.addAttribute("mainContent", "admin/productoForm :: mainContent");
        model.addAttribute("extraCss", "admin/productoForm :: extraCss");
        model.addAttribute("extraJs", "admin/productoForm :: extraJs");
        return "fragments/admin-layout";
    }



    @GetMapping("/productos")
    public String adminProductos(Model model,
                                 @RequestParam(value = "colegioId", required = false) Integer colegioId,
                                 @RequestParam(value = "categoriaId", required = false) Integer categoriaId,
                                 @RequestParam(value = "page", defaultValue = "1") Integer pagina) {

        int TAM_PAGINA = 6; // máximo productos por página
        model.addAttribute("currentPage", "productos");
        model.addAttribute("pageTitle", "Gestión de Productos - JKM Confecciones");

        // Listado completo filtrado
        List<Producto> productosFiltrados = productoService.listarProductosFiltrados(colegioId, categoriaId);

        // Paginación
        int totalProductos = productosFiltrados.size();
        int totalPaginas = (int) Math.ceil((double) totalProductos / TAM_PAGINA);
        pagina = Math.max(1, Math.min(pagina, totalPaginas)); // asegurar que no salga de rango

        int desde = (pagina - 1) * TAM_PAGINA;
        int hasta = Math.min(desde + TAM_PAGINA, totalProductos);
        List<Producto> productosPagina = productosFiltrados.subList(desde, hasta);

        model.addAttribute("productos", productosPagina);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", totalPaginas);

        // Filtros
        model.addAttribute("colegios", colegioService.listarColegios());
        model.addAttribute("categorias", productoService.listarCategorias());
        model.addAttribute("colegioSeleccionado", colegioId);
        model.addAttribute("categoriaSeleccionada", categoriaId);

        // Fragments
        model.addAttribute("mainContent", "admin/productos :: mainContent");
        model.addAttribute("extraCss", "admin/productos :: extraCss");
        model.addAttribute("extraJs", "admin/productos :: extraJs");

        return "fragments/admin-layout";
    }


    @PostMapping("/productos/guardar")
    public String guardarProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("precio") Double precio,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestParam("coleccionId") Long coleccionId,
            @RequestParam(value = "colegioId", required = false) List<Long> colegioIds,
            @RequestParam(value = "tallas", required = false) List<Integer> tallaIds,
            @RequestParam Map<String, String> allParams,
            @RequestParam("imagen") MultipartFile imagen,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            Producto p = new Producto();
            p.setNombre(nombre);
            p.setDescripcion(descripcion);
            p.setPrecioBase(precio);

            Categoria cat = new Categoria();
            cat.setId(categoriaId.intValue());
            p.setCategoria(cat);

            Coleccion col = new Coleccion();
            col.setId(coleccionId.intValue());
            p.setColeccion(col);

            // Asociar colegios (si existen)
            if (colegioIds != null) {
                Set<Colegio> colegios = new HashSet<>();
                for (Long id : colegioIds) {
                    Colegio co = new Colegio();
                    co.setId(id.intValue());
                    colegios.add(co);
                }
                p.setColegios(colegios);
            }

            // Crear lista de tallas con stock
            List<ProductoTalla> listaTallas = new ArrayList<>();
            if (tallaIds != null) {
                for (Integer idTalla : tallaIds) {
                    String stockStr = allParams.get("stock__" + idTalla);
                    int stock = 0;
                    if (stockStr != null && !stockStr.isEmpty()) {
                        try {
                            stock = Integer.parseInt(stockStr);
                        } catch (NumberFormatException e) {
                            log.warn("Stock inválido para talla {}: '{}'", idTalla, stockStr);
                        }
                    }

                    Talla talla = tallaService.buscarPorId(idTalla);
                    ProductoTalla pt = new ProductoTalla();
                    pt.setProducto(p);
                    pt.setTalla(talla);
                    pt.setCantidadStock(stock);
                    pt.setPrecioUnitarioFinal(p.getPrecioBase());
                    listaTallas.add(pt);
                }
            }

            productoService.crearProducto(p, listaTallas, imagen);

            log.info("Producto '{}' registrado correctamente.", nombre);
            redirectAttributes.addFlashAttribute("exito", "Producto registrado correctamente.");
            return "redirect:/admin/productos";

        } catch (IllegalArgumentException e) {
            // Error de validación — recargar el formulario con layout
            log.warn("Error de validación: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());

        } catch (Exception e) {
            // Error inesperado — recargar también el formulario con layout
            log.error("Error inesperado en /productos/guardar: {}", e.getMessage(), e);
            model.addAttribute("error", "Error inesperado: " + e.getMessage());
        }

        // recargar datos y retornar layout
        model.addAttribute("currentPage", "edicionProducto");
        model.addAttribute("pageTitle", "Añadir Producto - JKM Confecciones");

        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("colecciones", coleccionService.listarColecciones());
        model.addAttribute("colegios", colegioService.listarColegios());
        model.addAttribute("tallas", tallaService.listarTallas());

        model.addAttribute("mainContent", "admin/productoForm :: mainContent");
        model.addAttribute("extraCss", "admin/productoForm :: extraCss");
        model.addAttribute("extraJs", "admin/productoForm :: extraJs");

        return "fragments/admin-layout";
    }


    @GetMapping("/productos/{id}/eliminar")
      public String eliminarProducto(@PathVariable Integer id) {
          productoService.eliminarProducto(id);
          return "redirect:/admin/productos";
      }

    @GetMapping("/productos/{id}/json")
    @ResponseBody
    public ProductoDetalleDTO obtenerProductoJson(@PathVariable Integer id) {
        Producto p = productoService.buscarPorId(id);

        ProductoDetalleDTO dto = new ProductoDetalleDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecioBase(p.getPrecioBase());
        dto.setImagenUrl(p.getImagenUrl());
        dto.setCategoria(p.getCategoria() != null ? p.getCategoria().getNombre() : null);
        dto.setColeccion(p.getColeccion() != null ? p.getColeccion().getNombre() : null);
        dto.setColegios(p.getColegios().stream().map(Colegio::getNombre).toList());

        return dto;
    }

    //Solo actualizar datos para inventario
    @PostMapping("/productos/actualizar-stock")
    public String actualizarStock(@RequestParam Integer productoId,
                                  @RequestParam Integer cantidadStock,
                                  RedirectAttributes redirectAttributes) {

        try {
            productoService.actualizarStock(productoId, cantidadStock);
            redirectAttributes.addFlashAttribute("mensajeExito", "Stock actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar stock");
        }
        return "redirect:/admin/inventario"; // Regresa a inventario
    }

    @GetMapping("/inventario/exportar")
    public ResponseEntity<byte[]> exportarInventario(@RequestParam(value = "colegioId", required = false) Integer colegioId) {
        byte[] excelData = productoService.exportarInventarioExcel(colegioId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("inventario.xlsx")
                .build());

        return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
    }


    @GetMapping("/cotizaciones")
    public String gestionCotizaciones(
            Model model,
            @RequestParam(value = "estado", required = false) String estadoFiltro,
            @RequestParam(value = "clienteId", required = false) Long clienteId, // nuevo parámetro
            @RequestParam(value = "page", defaultValue = "1") Integer pagina) {

        int TAM_PAGINA = 10;

        model.addAttribute("currentPage", "cotizaciones");
        model.addAttribute("pageTitle", "Gestión de Cotizaciones - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        List<Cotizacion> cotizacionesCompletas;

        if (clienteId != null) {
            cotizacionesCompletas = adminCotizacionService.listarPorCliente(clienteId);
            // opcional: obtener nombre del cliente para mostrarlo
            Usuario cliente = clienteService.obtenerPorId(clienteId);
            model.addAttribute("pageTitle", "Cotizaciones de " + cliente.getNombre());
        } else {
            cotizacionesCompletas = adminCotizacionService.listarPorEstado(estadoFiltro);
        }

        // Paginación
        int totalRegistros = cotizacionesCompletas.size();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / TAM_PAGINA);
        totalPaginas = Math.max(1, totalPaginas);
        pagina = Math.max(1, Math.min(pagina, totalPaginas));

        int desde = (pagina - 1) * TAM_PAGINA;
        int hasta = Math.min(desde + TAM_PAGINA, totalRegistros);
        List<Cotizacion> cotizaciones = cotizacionesCompletas.subList(desde, hasta);

        model.addAttribute("cotizaciones", cotizaciones);
        model.addAttribute("estadoSeleccionado", estadoFiltro);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", totalPaginas);

        model.addAttribute("mainContent", "admin/gestionCotizaciones :: mainContent");
        model.addAttribute("extraCss", "admin/gestionCotizaciones :: extraCss");
        model.addAttribute("extraJs", "admin/gestionCotizaciones :: extraJs");

        return "fragments/admin-layout";
    }

    @PostMapping("/cotizaciones/{id}/actualizar-estado")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarEstadoCotizacion(
            @PathVariable Integer id,
            @RequestBody Map<String, String> request) {

        Map<String, Object> response = new HashMap<>();

        try {
            String nuevoEstado = request.get("estado");
            adminCotizacionService.actualizarEstado(id, nuevoEstado);

            response.put("success", true);
            response.put("message", "Estado actualizado correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/cotizaciones/{id}/eliminar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> eliminarCotizacion(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        try {
            adminCotizacionService.eliminar(id);
            response.put("success", true);
            response.put("message", "Cotización eliminada correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }


    @GetMapping("/cotizaciones/{id}/detalle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerDetalleCotizacion(@PathVariable Integer id) {

        Map<String, Object> response = new HashMap<>();

        try {
            CotizacionDetalleDTO dto = adminCotizacionService.obtenerDetalle(id);

            response.put("success", true);
            response.put("cotizacion", dto);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/clientes")
    public String listarClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<Usuario> clientesPage = clienteService.listarClientes(page, size, keyword);

        List<Map<String, Object>> clientesView = clientesPage.getContent().stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("nombre", c.getNombre());
            map.put("correo", c.getCorreo());
            map.put("telefono", c.getTelefono());
            map.put("estado", c.getEstado());

            // Calculamos días inactivos desde el último login
            LocalDateTime ultimaActividad = c.getFechaUltimoLogin() != null ? c.getFechaUltimoLogin() : c.getFechaRegistro();
            long diasInactivo = java.time.Duration.between(ultimaActividad, LocalDateTime.now()).toDays();
            map.put("diasInactivo", diasInactivo);

            return map;
        }).toList();

        model.addAttribute("clientes", clientesView);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", clientesPage.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Gestión de Clientes - JKM Confecciones");

        model.addAttribute("mainContent", "admin/panelControlCliente :: mainContent");
        model.addAttribute("extraCss", "admin/panelControlCliente :: extraCss");
        model.addAttribute("extraJs", "admin/panelControlCliente :: extraJs");

        return "fragments/admin-layout";
    }

    @GetMapping("/clientes/{id}/toggle")
    public String toggleEstadoCliente(@PathVariable Long id) {
        Usuario usuario = clienteService.obtenerPorId(id);

        // Solo activar si no está inactivo > 1 año
        if ("bloqueado".equalsIgnoreCase(usuario.getEstado())) {
            if (usuario.getFechaUltimoLogin() != null &&
                    usuario.getFechaUltimoLogin().isBefore(LocalDateTime.now().minusYears(1))) {

                return "redirect:/admin/clientes?error=CuentaInactiva";
            }
            usuario.setEstado("activo");
        } else {
            usuario.setEstado("bloqueado");
        }

        clienteService.guardar(usuario);
        return "redirect:/admin/clientes";
    }

    // ========== NOTIFICACIONES ==========

    @GetMapping("/notificaciones")
    public String paginaNotificaciones(Model model) {

        model.addAttribute("currentPage", "notificaciones");
        model.addAttribute("pageTitle", "Notificaciones - JKM Confecciones");

        // Fragmentos del HTML
        model.addAttribute("mainContent", "admin/notificaciones :: mainContent");
        model.addAttribute("extraCss", "admin/notificaciones :: extraCss");
        model.addAttribute("extraJs", "admin/notificaciones :: extraJs");

        return "fragments/admin-layout";
    }

    @GetMapping("/api/notificaciones")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerNotificaciones(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesUsuario(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notificaciones", notificaciones);
            response.put("total", notificaciones.size());
            response.put("noLeidas", notificacionService.contarNotificacionesNoLeidas(usuario));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener notificaciones", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al cargar notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/api/notificaciones/no-leidas")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerNotificacionesNoLeidas(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesNoLeidas(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notificaciones", notificaciones);
            response.put("total", notificaciones.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener notificaciones no leídas", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al cargar notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/api/notificaciones/archivadas")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerNotificacionesArchivadas(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesArchivadas(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("notificaciones", notificaciones);
            response.put("total", notificaciones.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener notificaciones archivadas", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al cargar notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/api/notificaciones/{id}/marcar-leida")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> marcarComoLeida(@PathVariable Long id) {
        try {
            notificacionService.marcarComoLeida(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación marcada como leída");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al marcar notificación como leída", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al actualizar notificación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/api/notificaciones/{id}/archivar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> archivarNotificacion(@PathVariable Long id) {
        try {
            notificacionService.marcarComoArchivada(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notificación archivada");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al archivar notificación", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al archivar notificación");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/api/notificaciones/marcar-todas-leidas")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> marcarTodasComoLeidas(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            notificacionService.marcarTodasComoLeidas(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Todas las notificaciones marcadas como leídas");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al marcar todas las notificaciones como leídas", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al actualizar notificaciones");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ========== API AUDITORÍA ==========

    @GetMapping("/api/auditoria")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerAuditoria(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AuditoriaSeguridad> auditoriaPage;

            if (usuario != null || accion != null || fechaInicio != null || fechaFin != null) {
                auditoriaPage = auditoriaService.buscarConFiltros(usuario, accion, fechaInicio, fechaFin, pageable);
            } else {
                auditoriaPage = auditoriaService.obtenerTodos(pageable);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            
            List<AuditoriaDTO> registrosDTO = auditoriaPage.getContent()
                    .stream()
                    .map(a -> new AuditoriaDTO(
                            a.getUsuario() != null ? a.getUsuario().getCorreo() : "Desconocido",
                            a.getAccion(),
                            a.getRecurso(),
                            a.getRecursoId(),
                            a.getIpAddress(),
                            a.getFechaHora() != null ? a.getFechaHora().format(formatter) : null,
                            a.getEstado(),
                            a.getDetalles(),
                            a.getUserAgent()
                    ))
                    .toList();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("registros", registrosDTO);
            response.put("totalElementos", auditoriaPage.getTotalElements());
            response.put("totalPaginas", auditoriaPage.getTotalPages());
            response.put("paginaActual", auditoriaPage.getNumber());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener registros de auditoría", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al cargar registros de auditoría");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/api/auditoria/recientes")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerAuditoriaReciente() {
        try {
            List<AuditoriaSeguridad> registros = auditoriaService.obtenerUltimosRegistros();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("registros", registros);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al obtener registros recientes de auditoría", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error al cargar registros");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/tallas")
    public String mostrarTallas(Model model) {
        List<ProductoTalla> productosTallas = productoTallaService.listarTodos();
        model.addAttribute("productosTallas", productosTallas);
        model.addAttribute("currentPage", "tallas");
        model.addAttribute("pageTitle", "Gestión de Tallas - JKM Confecciones");
        model.addAttribute("mainContent", "admin/tallas :: mainContent");
        model.addAttribute("extraCss", "admin/tallas :: extraCss");
        model.addAttribute("extraJs", "admin/tallas :: extraJs");
        return "fragments/admin-layout";
    }

    @GetMapping("/tallas/api/list")
    @ResponseBody
    public List<Map<String, Object>> listarTallas() {
        return productoTallaService.listarTallasComoMap();
    }

    @PutMapping("/tallas/api/{id}/toggle-estado")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleEstado(@PathVariable Long id,
                                                            @RequestBody Map<String, Boolean> request) {
        Boolean nuevoEstado = request.get("activo");
        return ResponseEntity.ok(productoTallaService.toggleEstado(id, nuevoEstado));
    }

    @GetMapping("/tallas/api/{id}")
    @ResponseBody
    public Map<String, Object> obtenerDetalle(@PathVariable Long id) {
        return productoTallaService.obtenerDetalle(id);
    }

    @GetMapping("/cambiarrol")
    public String mostrarCambiarRol(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Usuario> usuariosPage = usuarioService.listarTodos(PageRequest.of(page, size));
        List<Rol> roles = usuarioService.listarRoles();

        model.addAttribute("usuariosPage", usuariosPage);
        model.addAttribute("usuarios", usuariosPage.getContent());
        model.addAttribute("roles", roles);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usuariosPage.getTotalPages());

        // Datos para el layout
        model.addAttribute("pageTitle", "Cambiar Rol de Usuario - JKM Confecciones");
        model.addAttribute("mainContent", "admin/cambiarRol :: mainContent");
        model.addAttribute("extraCss", "admin/cambiarRol :: extraCss");
        model.addAttribute("extraJs", "admin/cambiarRol :: extraJs");

        return "fragments/admin-layout";
    }

    @PostMapping("/{id}/rol")
    public String actualizarRol(
            @PathVariable Long id,
            @RequestParam Long rolId,
            Model model
    ) {
        try {
            usuarioService.cambiarRol(id, rolId);
            model.addAttribute("success", "Rol actualizado correctamente");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/cambiarrol";
    }


}
