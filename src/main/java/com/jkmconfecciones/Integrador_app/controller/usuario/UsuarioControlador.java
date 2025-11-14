package com.jkmconfecciones.Integrador_app.controller.usuario;

import com.jkmconfecciones.Integrador_app.dto.CotizacionRequestDTO;
import com.jkmconfecciones.Integrador_app.entidades.Cotizacion;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio;
import com.jkmconfecciones.Integrador_app.service.Usuario.CotizacionService;
import com.jkmconfecciones.Integrador_app.service.Usuario.PerfilUsuarioService;
import com.jkmconfecciones.Integrador_app.service.Usuario.UsuarioCatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PerfilUsuarioService perfilUsuarioService;

    @Autowired
    private UsuarioCatalogoService usuarioCatalogoService;

    @Autowired
    private CotizacionService cotizacionService;

    @GetMapping("/usuario")
    public String paginaUsuario(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();
        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("inicial", usuario.getNombre().substring(0, 1).toUpperCase());
        return "usuario/usuario";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(Usuario datosActualizados, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        perfilUsuarioService.actualizarPerfil(
                correo,
                datosActualizados.getNombre(),
                datosActualizados.getTelefono(),
                datosActualizados.getDireccion()
        );

        return "redirect:/usuario/usuario";
    }

    @GetMapping("/catalogo")
    public String mostrarCatalogo(
            @RequestParam(required = false) Integer colegioId,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String query,
            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String correo = auth.getName();
            Usuario usuario = usuarioRepositorio.findByCorreo(correo).orElse(null);
            if (usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("inicial", usuario.getNombre().substring(0, 1).toUpperCase());
            }
        }

        var productos = usuarioCatalogoService.obtenerProductosFiltrados(colegioId, categoriaId, query);

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", usuarioCatalogoService.listarCategorias());
        model.addAttribute("colegios", usuarioCatalogoService.listarColegios());
        model.addAttribute("categoriaSeleccionada", categoriaId);
        model.addAttribute("colegioSeleccionado", colegioId);
        model.addAttribute("query", query);

        return "usuario/catalogo";
    }

    @GetMapping("/producto/{id}/tallas")
    @ResponseBody
    public Map<String, Object> obtenerTallas(@PathVariable Long id) {
        var tallas = usuarioCatalogoService.listarTallasPorProducto(id);
        Map<String, Object> response = new HashMap<>();
        response.put("tallas", tallas);
        return response;
    }

    @GetMapping("/producto/{id}")
    public String verDetalleProducto(@PathVariable Long id, Model model) {
        var producto = usuarioCatalogoService.obtenerProductoPorId(id);
        if (producto == null) {
            return "redirect:/usuario/catalogo";
        }

        // Cargar tallas
        var tallas = usuarioCatalogoService.listarTallasPorProducto(id);

        // Calcular el stock total sumando las cantidades de cada talla
        int stockTotal = producto.getTallas().stream()
                .mapToInt(t -> t.getCantidadStock() != null ? t.getCantidadStock() : 0)
                .sum();

        // Obtener usuario autenticado (igual que en el catálogo)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String correo = auth.getName();
            Usuario usuario = usuarioRepositorio.findByCorreo(correo).orElse(null);
            if (usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("inicial", usuario.getNombre().substring(0, 1).toUpperCase());
            }
        }

        model.addAttribute("producto", producto);
        model.addAttribute("tallas", tallas);
        model.addAttribute("stockTotal", stockTotal);

        return "usuario/detalle";
    }


    @PostMapping("/cotizar")
    @ResponseBody
    public Map<String, Object> hacerCotizacionJson(@RequestBody CotizacionRequestDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        var cotizacion = cotizacionService.crearCotizacion(dto, correo);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Cotización creada exitosamente");
        response.put("cotizacionId", cotizacion.getId());
        response.put("total", cotizacion.getTotal());

        return response;
    }

}
