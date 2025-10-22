package com.jkmconfecciones.Integrador_app.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminControlador {

    @GetMapping("/admin")
    public String paginaAdmin() {
        return "admin/admin";
    }

    @GetMapping("/pedidos")
    public String paginaPedidosAdmin(){
        return "admin/pedidos";
    }

    @GetMapping("/cargaMasivaDatos")
    public String paginaCargadedatos(){
        return "admin/cargaMasivaDatos";
    }

    @GetMapping("/edicionProducto")
    public String paginaedicioProducto(){
        return "admin/edicionProducto";
    }

    @GetMapping("/notificaciones")
    public String paginanotificaciones(){
        return "admin/notificaciones";
    }


}
