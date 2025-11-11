package com.jkmconfecciones.Integrador_app.controller.usuario;

import com.jkmconfecciones.Integrador_app.entidades.Producto;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio;
import com.jkmconfecciones.Integrador_app.service.Usuario.PerfilUsuarioService;
import com.jkmconfecciones.Integrador_app.service.Usuario.UsuarioCatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PerfilUsuarioService perfilUsuarioService;

    @Autowired
    private UsuarioCatalogoService usuarioCatalogoService;

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
    public String actualizarPerfil(Usuario datosActualizados, RedirectAttributes redirectAttrs) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName();

        perfilUsuarioService.actualizarPerfil(
                correo,
                datosActualizados.getNombre(),
                datosActualizados.getTelefono(),
                datosActualizados.getDireccion()
        );

        redirectAttrs.addFlashAttribute("mensaje", "✅ Perfil actualizado correctamente.");
        return "redirect:/usuario/usuario";
    }

    @GetMapping("/catalogo")
    public String mostrarCatalogo(
            @RequestParam(required = false) Integer colegioId,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String query,  // <-- aca es parámetro de búsqueda
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model) {

        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            String correo = auth.getName();
            Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                    .orElse(null);
            if (usuario != null) {
                model.addAttribute("nombreUsuario", usuario.getNombre());
                model.addAttribute("inicial", usuario.getNombre().substring(0,1).toUpperCase());
            }
        }

        // Listar productos filtrados por colegio, categoría y query (nombre)
        List<Producto> productos = usuarioCatalogoService.obtenerProductosFiltrados(colegioId, categoriaId, query);

        // Calcular paginación
        int totalProductos = productos.size();
        int totalPaginas = (int) Math.ceil((double) totalProductos / size);
        int desde = (page - 1) * size;
        int hasta = Math.min(desde + size, totalProductos);
        List<Producto> productosPagina = productos.subList(desde, hasta);

        // Añadir atributos al modelo
        model.addAttribute("productos", productosPagina);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("paginaActual", page);

        model.addAttribute("categorias", usuarioCatalogoService.listarCategorias());
        model.addAttribute("colegios", usuarioCatalogoService.listarColegios());
        model.addAttribute("categoriaSeleccionada", categoriaId);
        model.addAttribute("colegioSeleccionado", colegioId);
        model.addAttribute("query", query); // <-- para que el input mantenga el valor

        return "usuario/catalogo";
    }


}
