package com.jkmconfecciones.Integrador_app.controller.admin;

import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.service.ProductoService.*;
import com.jkmconfecciones.Integrador_app.service.ProductoService.CategoriaService;
import com.jkmconfecciones.Integrador_app.service.ProductoService.ColeccionService;
import com.jkmconfecciones.Integrador_app.service.ProductoService.ColegioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

    @GetMapping("/productos")
    public String adminProductos(Model model,
                                 @RequestParam(value = "colegioId", required = false) Integer colegioId) {
        model.addAttribute("currentPage", "productos");
        model.addAttribute("pageTitle", "Gestión de Productos - JKM Confecciones");

        List<Producto> productos;
        if (colegioId != null && colegioId > 0) {
            productos = productoService.listarProductosPorColegio(colegioId);
        } else {
            productos = productoService.listarProductos();
        }
        model.addAttribute("productos", productos);

        model.addAttribute("colegios", colegioService.listarColegios());
        model.addAttribute("colegioSeleccionado", colegioId);

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
            @RequestParam("imagen") MultipartFile imagen
    ) {
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

        if (colegioIds != null) {
            Set<Colegio> colegios = new HashSet<>();
            for (Long id : colegioIds) {
                Colegio co = new Colegio();
                co.setId(id.intValue());
                colegios.add(co);
            }
            p.setColegios(colegios);
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
                        stock = 0;
                    }
                }

                // Usando el metodo buscarporID
                Talla talla = tallaService.buscarPorId(idTalla);
                ProductoTalla pt = new ProductoTalla();
                pt.setProducto(p);
                pt.setTalla(talla);
                pt.setCantidadStock(stock);
                pt.setPrecioUnitarioFinal(p.getPrecioBase());
                listaTallas.add(pt);
            }
        }

        p.setTallas(listaTallas);
        productoService.crearProducto(p, listaTallas, imagen);

        return "redirect:/admin/productos";
    }
      @GetMapping("/productos/{id}/eliminar")
      public String eliminarProducto(@PathVariable Integer id) {
          productoService.eliminarProducto(id);
          return "redirect:/admin/productos";
      }
}
