package com.jkmconfecciones.Integrador_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.jkmconfecciones.Integrador_app.service.ColegioService;
import com.jkmconfecciones.Integrador_app.entidades.Colegio;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// Controller principal para rutas generales, landing page y catálogo de colegios
@Controller
public class ControllerInicio {

    @Autowired
    private ColegioService colegioService;

    @GetMapping("/landing_page")
    public String landing_page() {
        System.out.println("[ControllerInicio] GET /landing_page -> rendering landing_page.html");
        return "landing_page";
    }

    // Alias para prueba rápida de enrutamiento
    @GetMapping("/landing")
    public String landingAlias() {
        System.out.println("[ControllerInicio] GET /landing -> rendering landing_page.html (alias)");
        return "landing_page";
    }

    @GetMapping("/catalogoColegios")
    public String catalogoColegios(@RequestParam(value = "q", required = false) String termino,
                                   Model model) {
        List<Colegio> colegios = (termino == null || termino.isBlank())
                ? colegioService.listarTodos()
                : colegioService.buscarPorNombre(termino);

        model.addAttribute("colegios", colegios);
        model.addAttribute("q", termino == null ? "" : termino);
        model.addAttribute("currentPage", "catalogoColegios");
        model.addAttribute("pageTitle", "Catálogo de Colegios - JKM Confecciones");
        return "catalogoColegios";
    }

    @GetMapping("/catalogoProductosColegios")
    public String catalogoProductosColegios(@RequestParam("colegioId") Long colegioId,
                                            Model model) {
        Colegio colegio = colegioService.obtenerPorId(colegioId);
        String nombreColegio = (colegio != null && colegio.getNombre() != null) ? colegio.getNombre() : "Seleccionado";
        model.addAttribute("colegio", colegio);
        model.addAttribute("nombreColegio", nombreColegio);
        model.addAttribute("pageTitle", "Catálogo de productos " + nombreColegio);
        return "productosColegio";
    }
}
