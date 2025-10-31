package com.jkmconfecciones.Integrador_app.controller.admin;

import com.jkmconfecciones.Integrador_app.DTO.ProductoDetalleDTO;
import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.service.ProductoService.*;
import org.springframework.http.*;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/pedidos")
    public String paginaPedidosAdmin(Model model) {
        model.addAttribute("currentPage", "pedidos");
        model.addAttribute("pageTitle", "Gestión de Pedidos - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        model.addAttribute("mainContent", "admin/pedidos :: mainContent");
        return "fragments/admin-layout";
    }

    @GetMapping("/cargaMasivaDatos")
    public String paginaCargadeDatos(Model model) {
        model.addAttribute("currentPage", "cargaMasivaDatos");
        model.addAttribute("pageTitle", "Carga Masiva de Datos - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        model.addAttribute("mainContent", "admin/cargaMasivaDatos :: mainContent");
        return "fragments/admin-layout";
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

    @GetMapping("/notificaciones")
    public String paginaNotificaciones(Model model) {
        model.addAttribute("currentPage", "notificaciones");
        model.addAttribute("pageTitle", "Notificaciones - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        model.addAttribute("mainContent", "admin/notificaciones :: mainContent");
        return "fragments/admin-layout";
    }

    @GetMapping("/panelControlCliente")
    public String paginaPanelControlCliente(Model model) {
        model.addAttribute("currentPage", "panelControlCliente");
        model.addAttribute("pageTitle", "Panel de Control de Clientes - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        model.addAttribute("mainContent", "admin/panelControlCliente :: mainContent");
        return "fragments/admin-layout";
    }

    @GetMapping("/registroAuditoriaSeguridad")
    public String paginaRegistroAuditoria(Model model) {
        model.addAttribute("currentPage", "registroAuditoriaSeguridad");
        model.addAttribute("pageTitle", "Auditoría de Seguridad - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        model.addAttribute("mainContent", "admin/registroAuditoriaSeguridad :: mainContent");
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



}
