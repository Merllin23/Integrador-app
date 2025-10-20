package com.jkmconfecciones.Integrador_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import com.jkmconfecciones.Integrador_app.service.ColegioService;
import com.jkmconfecciones.Integrador_app.entidades.Colegio;
import java.util.List;

//Se ha modificado el enrutamiento, los fragments, son los componentes estaticos que se reutilizan en varias páginas,
// como la barra lateral (sidebar) y el botón de soporte de WhatsApp y el boton logout no modificar!

@Controller
public class ControllerInicio {

    @Autowired
    private ColegioService colegioService;

    @GetMapping("/")
    public String home(Model model) {
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "registro", required = false) String registro, Model model) {
        return "login";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        // Si acceden directamente sin autenticación, redirigir al login
        model.addAttribute("mensaje", "Accede desde el panel de administración");
        model.addAttribute("currentPage", "admin");
        model.addAttribute("pageTitle", "Panel de Control - JKM Confecciones");
        return "admin";
    }

    @GetMapping("/pedidos")
    public String pedidosPage(Model model) {
        // Si acceden directamente sin autenticación, redirigir al login
        model.addAttribute("mensaje", "Accede desde el panel de pedidos");
        model.addAttribute("currentPage", "pedidos");
        model.addAttribute("pageTitle", "Pedidos - JKM Confecciones");
        return "pedidos";
    }
//debe acceder a panelControlCliente.html aqui!
    @GetMapping("/usuario")
    public String usuarioPage(Model model) {
        // Si acceden directamente sin autenticación, redirigir al login
        model.addAttribute("mensaje", "Accede desde el panel de usuario");
        return "usuario";
    }

    @GetMapping("/recuperar")
    public String recuperarPage(Model model) {
        return "recuperar-contrasena";
    }
    
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

    @GetMapping("/cargaMasivaDatos")
    public String cargaMasivaDatos(Model model) {
        model.addAttribute("currentPage", "cargaMasivaDatos");
        model.addAttribute("pageTitle", "Carga Masiva de Datos - JKM Confecciones");
        return "cargaMasivaDatos";
    }

    @GetMapping("/edicionProducto")
    public String edicionProducto(Model model) {
        model.addAttribute("currentPage", "edicionProducto");
        model.addAttribute("pageTitle", "Edición de Productos - JKM Confecciones");
        return "edicionProducto";
    }

    @GetMapping("/notificaciones")
    public String notificaciones(Model model) {
        model.addAttribute("currentPage", "notificaciones");
        model.addAttribute("pageTitle", "Notificaciones - JKM Confecciones");
        return "notificaciones";
    }

    @GetMapping("/panelControlCliente")
    public String panelControlCliente(Model model) {
        return "panelControlCliente";
    }

    @GetMapping("/registroAuditoriaSeguridad")
    public String registroAuditoriaSeguridad(Model model) {
        model.addAttribute("currentPage", "registroAuditoriaSeguridad");
        model.addAttribute("pageTitle", "Registro de Auditoría de Seguridad - JKM Confecciones");
        return "registroAuditoriaSeguridad";
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
        com.jkmconfecciones.Integrador_app.entidades.Colegio colegio = colegioService.obtenerPorId(colegioId);
        String nombreColegio = (colegio != null && colegio.getNombre() != null) ? colegio.getNombre() : "Seleccionado";
        model.addAttribute("colegio", colegio);
        model.addAttribute("nombreColegio", nombreColegio);
        model.addAttribute("pageTitle", "Catálogo de productos " + nombreColegio);
        return "productosColegio";
    }

}

