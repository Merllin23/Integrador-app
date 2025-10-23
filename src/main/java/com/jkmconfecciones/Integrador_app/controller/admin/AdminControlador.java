package com.jkmconfecciones.Integrador_app.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminControlador {

    @GetMapping("/panel")
    public String mostrarPanelAdmin(Model model) {
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Panel de Administración - JKM Confecciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");

        // Fragmentos de Thymeleaf
        model.addAttribute("mainContent", "admin/panel :: mainContent");
        model.addAttribute("extraCss", "admin/panel :: extraCss");
        model.addAttribute("extraJs", "admin/panel :: extraJs");

        // Retorna el layout base
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

        model.addAttribute("mainContent", "admin/productoForm :: mainContent");
        model.addAttribute("extraCss", "admin/productoForm :: extraCss");
        model.addAttribute("extraJs", "admin/productoForm :: extraJs");
        return "fragments/admin-layout";
    }

    @GetMapping("/productos")
    public String adminProductos(Model model, @RequestParam(value = "colegioId", required = false) String colegioId) {
        model.addAttribute("currentPage", "productos");
        model.addAttribute("pageTitle", "Gestión de Productos - JKM Confecciones");

        // En el futuro: usar colegioId si se requiere filtrar
        model.addAttribute("mainContent", "admin/productos :: mainContent");
        model.addAttribute("extraCss", "admin/productos :: extraCss");
        model.addAttribute("extraJs", "admin/productos :: extraJs");
        return "fragments/admin-layout";
    }
}
