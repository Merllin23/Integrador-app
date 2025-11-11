package com.jkmconfecciones.Integrador_app.service.Usuario.Impl;

import com.jkmconfecciones.Integrador_app.dto.CotizacionRequestDTO;
import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.repositorios.CotizacionRepositorio;
import com.jkmconfecciones.Integrador_app.repositorios.DetalleCotizacionRepositorio;
import com.jkmconfecciones.Integrador_app.repositorios.ProductoTallaRepositorio;
import com.jkmconfecciones.Integrador_app.service.Usuario.CotizacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepositorio cotizacionRepositorio;
    private final DetalleCotizacionRepositorio detalleCotizacionRepositorio;
    private final ProductoTallaRepositorio productoTallaRepositorio;

    @Override
    @Transactional
    public Cotizacion crearCotizacion(Usuario usuario, CotizacionRequestDTO dto) {

        // Crear cotización principal
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setUsuario(usuario);
        cotizacion.setFecha(LocalDateTime.now());
        cotizacion.setEstado("PENDIENTE");
        cotizacion.setTotal(0.0);
        cotizacion = cotizacionRepositorio.save(cotizacion);

        double total = 0.0;

        // Iterar sobre los productos del DTO
        for (CotizacionRequestDTO.ProductoCotizacionDTO p : dto.getProductos()) {
            ProductoTalla pt = productoTallaRepositorio.findById(p.getProductoTallaId().longValue())
                    .orElseThrow(() -> new RuntimeException("ProductoTalla no encontrado: " + p.getProductoTallaId()));

            DetalleCotizacion detalle = new DetalleCotizacion();
            detalle.setCotizacion(cotizacion);
            detalle.setProductoTalla(pt);
            detalle.setCantidad(p.getCantidad());
            detalle.setPrecioUnitario(pt.getPrecioUnitarioFinal());
            detalle.setSubtotal(pt.getPrecioUnitarioFinal() * p.getCantidad());

            detalleCotizacionRepositorio.save(detalle);
            total += detalle.getSubtotal();
        }

        cotizacion.setTotal(total);
        return cotizacionRepositorio.save(cotizacion);
    }
}
