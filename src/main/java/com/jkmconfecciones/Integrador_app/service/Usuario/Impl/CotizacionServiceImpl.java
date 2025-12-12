package com.jkmconfecciones.Integrador_app.service.Usuario.Impl;

import com.jkmconfecciones.Integrador_app.DTO.CotizacionHistorialDTO;
import com.jkmconfecciones.Integrador_app.DTO.CotizacionRequestDTO;
import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.repositorios.*;
import com.jkmconfecciones.Integrador_app.service.Usuario.CotizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class CotizacionServiceImpl implements CotizacionService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ProductoTallaRepositorio productoTallaRepositorio;

    @Autowired
    private CotizacionRepositorio cotizacionRepositorio;

    @Autowired
    private DetalleCotizacionRepositorio detalleCotizacionRepositorio;

    @Override
    @Transactional
    public Cotizacion crearCotizacion(CotizacionRequestDTO dto, String correoUsuario) {

        // 1. Obtener usuario
        Usuario usuario = usuarioRepositorio.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Crear cotización base
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setUsuario(usuario);
        cotizacion.setFecha(LocalDateTime.now(ZoneId.of("America/Lima")));
        cotizacion.setEstado("PENDIENTE");

        List<DetalleCotizacion> detalles = new ArrayList<>();
        double total = 0.0;

        // 3. Recorrer productos enviados por el usuario
        for (CotizacionRequestDTO.ProductoCotizacionDTO item : dto.getProductos()) {

            Long idPT = item.getProductoTallaId().longValue();

            ProductoTalla pt = productoTallaRepositorio.findById(idPT)
                    .orElseThrow(() -> new RuntimeException("Producto/Talla no encontrado"));

            int cantidad = item.getCantidad();

            double precio = pt.getPrecioUnitarioFinal() != null
                    ? pt.getPrecioUnitarioFinal()
                    : pt.getProducto().getPrecioBase();

            double subtotal = precio * cantidad;
            total += subtotal;

            DetalleCotizacion det = new DetalleCotizacion();
            det.setCotizacion(cotizacion);
            det.setProductoTalla(pt);
            det.setCantidad(cantidad);
            det.setPrecioUnitario(precio);
            det.setSubtotal(subtotal);

            detalles.add(det);
        }

        cotizacion.setTotal(total);

        // 4. Guardar cotización para generar ID
        cotizacionRepositorio.save(cotizacion);

        // 5. Guardar detalles
        detalleCotizacionRepositorio.saveAll(detalles);

        return cotizacion;
    }

    @Override
    public List<CotizacionHistorialDTO> listarHistorialDTO(String correo) {

        Usuario usuario = usuarioRepositorio.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Cotizacion> cotizaciones = cotizacionRepositorio.findByUsuarioOrderByFechaDesc(usuario);

        return cotizaciones.stream()
                .map(c -> {
                    // Calcular total de productos sumando las cantidades de los detalles
                    int totalProductos = c.getDetalles().stream()
                            .mapToInt(DetalleCotizacion::getCantidad)
                            .sum();

                    return new CotizacionHistorialDTO(
                            c.getId(),
                            c.getFecha(),
                            totalProductos,
                            c.getEstado()
                    );
                })
                .toList();
    }

}
