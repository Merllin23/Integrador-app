package com.jkmconfecciones.Integrador_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Controller
public class ControllerInicio {
    // If needed in the future, inject services here


    @GetMapping("/landing_page")
    public String landing_page() {
        return "landing_page";
    }

    // Admin: Crear nuevo producto (form)
    @GetMapping("/admin/productos/nuevo")
    public String adminNuevoProducto(Model model) {
        // Datos base para el layout
        model.addAttribute("currentPage", "edicionProducto");
        model.addAttribute("pageTitle", "Añadir Producto - JKM Confecciones");

        // Optionally provide colegios list from controller if available in your project

        // Indicar al layout qué fragmentos renderizar
        model.addAttribute("mainContent", "admin/productoForm :: mainContent");
        model.addAttribute("extraCss", "admin/productoForm :: extraCss");
        model.addAttribute("extraJs", "admin/productoForm :: extraJs");
        return "fragments/admin-layout";
    }

    // Admin: Gestión de productos (lista)
    @GetMapping("/admin/productos")
    public String adminProductos(Model model, @RequestParam(value = "colegioId", required = false) String colegioId) {
        model.addAttribute("currentPage", "productos");
        model.addAttribute("pageTitle", "Product Management - JKM Confecciones");
        // Si en el futuro se filtra por colegio, usar colegioId
        model.addAttribute("mainContent", "admin/productos :: mainContent");
        model.addAttribute("extraCss", "admin/productos :: extraCss");
        model.addAttribute("extraJs", "admin/productos :: extraJs");
        return "fragments/admin-layout";
    }
}