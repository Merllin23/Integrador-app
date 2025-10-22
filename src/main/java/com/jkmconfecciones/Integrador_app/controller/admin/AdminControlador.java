package com.jkmconfecciones.Integrador_app.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminControlador {

    @GetMapping("/admin")
    public String paginaAdmin(Model model) {
        model.addAttribute("currentPage", "admin");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");
        return "admin/admin";
    }

    @GetMapping("/pedidos")
    public String paginaPedidosAdmin(Model model){
        model.addAttribute("currentPage", "pedidos");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");
        return "admin/pedidos";
    }

    @GetMapping("/cargaMasivaDatos")
    public String paginaCargadedatos(Model model){
        model.addAttribute("currentPage", "cargaMasivaDatos");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");
        return "admin/cargaMasivaDatos";
    }

    @GetMapping("/edicionProducto")
    public String paginaedicioProducto(Model model){
        model.addAttribute("currentPage", "edicionProducto");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");
        return "admin/edicionProducto";
    }

    @GetMapping("/notificaciones")
    public String paginanotificaciones(Model model){
        model.addAttribute("currentPage", "notificaciones");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");
        return "admin/notificaciones";
    }

    @GetMapping("/panelControlCliente")
    public String paginaPanelControlCliente(Model model){
        model.addAttribute("currentPage", "panelControlCliente");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");
        return "admin/panelControlCliente";
    }

    @GetMapping("/registroAuditoriaSeguridad")
    public String paginaRegistroAuditoria(Model model){
        model.addAttribute("currentPage", "registroAuditoriaSeguridad");
        model.addAttribute("nombre", "Administrador");
        model.addAttribute("rol", "Administrador");
        return "admin/registroAuditoriaSeguridad";
    }
}