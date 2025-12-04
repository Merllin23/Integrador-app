package com.jkmconfecciones.Integrador_app.service;

import com.jkmconfecciones.Integrador_app.entidades.Cotizacion;
import com.jkmconfecciones.Integrador_app.entidades.Producto;
import com.jkmconfecciones.Integrador_app.entidades.ProductoTalla;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;
import com.jkmconfecciones.Integrador_app.repositorios.ProductoTallaRepositorio;
import com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio;
import com.jkmconfecciones.Integrador_app.service.Notificacion.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionAutomaticaService {

    private final NotificacionService notificacionService;
    private final ProductoTallaRepositorio productoTallaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    
    @Scheduled(fixedRate = 3600000) // Cada hora
    @Transactional
    public void verificarStockCritico() {
        log.info("Verificando stock crítico de productos...");
        
        List<ProductoTalla> productosStockBajo = productoTallaRepositorio.findAll()
                .stream()
                .filter(pt -> pt.getCantidadStock() != null && pt.getCantidadStock() < 20)
                .toList();

        if (!productosStockBajo.isEmpty()) {
            List<Usuario> administradores = usuarioRepositorio.findByRolNombreRol("ADMINISTRADOR");
            
            for (Usuario admin : administradores) {
                for (ProductoTalla productoTalla : productosStockBajo) {
                    String nivelCriticidad = productoTalla.getCantidadStock() < 10 ? "CRÍTICO" : "BAJO";
                    String titulo = String.format("Stock %s: %s - %s", 
                            nivelCriticidad,
                            productoTalla.getProducto().getNombre(),
                            productoTalla.getTalla().getNombreTalla());
                    
                    String mensaje = String.format("El producto '%s' (talla %s) tiene solo %d unidades en stock. " +
                            "Se recomienda reabastecer pronto.",
                            productoTalla.getProducto().getNombre(),
                            productoTalla.getTalla().getNombreTalla(),
                            productoTalla.getCantidadStock());

                    notificacionService.crearNotificacionConReferencia(
                            "ALERTA",
                            titulo,
                            mensaje,
                            admin,
                            productoTalla.getId().longValue(),
                            "PRODUCTO_TALLA"
                    );
                }
            }
            
            log.info("Se generaron notificaciones de stock crítico para {} productos", productosStockBajo.size());
        }
    }

    //Notifica a los administradores sobre una nueva cotización
    
    @Transactional
    public void notificarNuevaCotizacion(Cotizacion cotizacion) {
        log.info("Generando notificación para nueva cotización ID: {}", cotizacion.getId());
        
        List<Usuario> administradores = usuarioRepositorio.findByRolNombreRol("ADMINISTRADOR");
        
        String titulo = String.format("Nueva Cotización #%d", cotizacion.getId());
        String mensaje = String.format("El cliente %s %s ha creado una nueva cotización por un monto de S/ %.2f",
                cotizacion.getUsuario().getNombre(),
                cotizacion.getUsuario().getApellido(),
                cotizacion.getTotal());

        for (Usuario admin : administradores) {
            notificacionService.crearNotificacionConReferencia(
                    "COTIZACION",
                    titulo,
                    mensaje,
                    admin,
                    cotizacion.getId().longValue(),
                    "COTIZACION"
            );
        }
    }

    
    // Notifica a los administradores sobre un cambio de precio
    
    @Transactional
    public void notificarCambioPrecio(Producto producto, Double precioAnterior, Double precioNuevo) {
        log.info("Generando notificación para cambio de precio en producto ID: {}", producto.getId());
        
        List<Usuario> administradores = usuarioRepositorio.findByRolNombreRol("ADMINISTRADOR");
        
        double diferencia = precioNuevo - precioAnterior;
        String cambio = diferencia > 0 ? "aumentó" : "disminuyó";
        double porcentaje = Math.abs((diferencia / precioAnterior) * 100);

        String titulo = String.format("Cambio de Precio: %s", producto.getNombre());
        String mensaje = String.format("El precio del producto '%s' %s de S/ %.2f a S/ %.2f (%.1f%%)",
                producto.getNombre(),
                cambio,
                precioAnterior,
                precioNuevo,
                porcentaje);

        for (Usuario admin : administradores) {
            notificacionService.crearNotificacionConReferencia(
                    "SISTEMA",
                    titulo,
                    mensaje,
                    admin,
                    producto.getId().longValue(),
                    "PRODUCTO"
            );
        }
    }

    
    // Notifica cuando se actualiza el estado de una cotización
    
    @Transactional
    public void notificarCambioEstadoCotizacion(Cotizacion cotizacion, String estadoAnterior, String estadoNuevo) {
        log.info("Generando notificación para cambio de estado en cotización ID: {}", cotizacion.getId());
        
        String titulo = String.format("Cotización #%d - Estado Actualizado", cotizacion.getId());
        String mensaje = String.format("El estado de su cotización cambió de '%s' a '%s'",
                estadoAnterior,
                estadoNuevo);

    
        notificacionService.crearNotificacionConReferencia(
                "COTIZACION",
                titulo,
                mensaje,
                cotizacion.getUsuario(),
                cotizacion.getId().longValue(),
                "COTIZACION"
        );
    }

    //Genera notificaciones de bienvenida para nuevos usuarios
    
    @Transactional
    public void notificarNuevoUsuario(Usuario usuario) {
        log.info("Generando notificación de bienvenida para usuario: {}", usuario.getCorreo());
        
        String titulo = "¡Bienvenido a JKM Confecciones!";
        String mensaje = String.format("Hola %s, gracias por registrarte. " +
                "Explora nuestro catálogo y realiza tus cotizaciones fácilmente.",
                usuario.getNombre());

        notificacionService.crearNotificacion(
                "SISTEMA",
                titulo,
                mensaje,
                usuario
        );
    }
}
