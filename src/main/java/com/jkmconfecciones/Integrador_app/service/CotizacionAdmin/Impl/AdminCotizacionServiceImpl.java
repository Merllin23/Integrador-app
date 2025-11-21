package com.jkmconfecciones.Integrador_app.service.CotizacionAdmin.Impl;

import com.jkmconfecciones.Integrador_app.DTO.CotizacionDetalleDTO;
import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.repositorios.CotizacionRepositorio;
import com.jkmconfecciones.Integrador_app.service.CotizacionAdmin.AdminCotizacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCotizacionServiceImpl implements AdminCotizacionService {

    private final CotizacionRepositorio cotizacionRepositorio;


    private String obtenerUsuarioActual() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public List<Cotizacion> listarPorEstado(String estadoFiltro) {
        if (estadoFiltro != null && !estadoFiltro.isEmpty() && !estadoFiltro.equals("TODOS")) {
            return cotizacionRepositorio.findByEstadoOrderByFechaDesc(estadoFiltro);
        }
        return cotizacionRepositorio.findAllByOrderByFechaDesc();
    }

    @Override
    @Transactional
    public void actualizarEstado(Integer id, String nuevoEstado) {

        Cotizacion cotizacion = cotizacionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        String estadoAnterior = cotizacion.getEstado();
        String admin = obtenerUsuarioActual();

        cotizacion.setEstado(nuevoEstado);
        cotizacionRepositorio.save(cotizacion);

        // Logger
        log.info("ADMIN '{}' cambió el estado de la Cotización ID {} de '{}' a '{}'",
                admin, id, estadoAnterior, nuevoEstado);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {

        Cotizacion cotizacion = cotizacionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        String admin = obtenerUsuarioActual();

        cotizacionRepositorio.delete(cotizacion);

        // Logger
        log.info("ADMIN '{}' eliminó la Cotización ID {}", admin, id);
    }

    @Override
    public CotizacionDetalleDTO obtenerDetalle(Integer id) {
        Cotizacion cotizacion = cotizacionRepositorio.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        CotizacionDetalleDTO dto = new CotizacionDetalleDTO();
        dto.setId(cotizacion.getId());
        dto.setFecha(cotizacion.getFecha());
        dto.setTotal(cotizacion.getTotal());
        dto.setEstado(cotizacion.getEstado());

        // Usuario
        CotizacionDetalleDTO.UsuarioSimpleDTO usuarioDTO = new CotizacionDetalleDTO.UsuarioSimpleDTO();
        usuarioDTO.setId(cotizacion.getUsuario().getId());
        usuarioDTO.setNombre(cotizacion.getUsuario().getNombre());
        usuarioDTO.setApellido(cotizacion.getUsuario().getApellido());
        usuarioDTO.setCorreo(cotizacion.getUsuario().getCorreo());
        dto.setUsuario(usuarioDTO);

        // Detalles
        List<CotizacionDetalleDTO.DetalleItemDTO> detallesDTO = new ArrayList<>();
        if (cotizacion.getDetalles() != null) {
            for (DetalleCotizacion detalle : cotizacion.getDetalles()) {
                CotizacionDetalleDTO.DetalleItemDTO detDTO = new CotizacionDetalleDTO.DetalleItemDTO();
                detDTO.setId(detalle.getId());
                detDTO.setCantidad(detalle.getCantidad());
                detDTO.setPrecioUnitario(detalle.getPrecioUnitario());
                detDTO.setSubtotal(detalle.getSubtotal());

                CotizacionDetalleDTO.ProductoTallaSimpleDTO ptDTO = new CotizacionDetalleDTO.ProductoTallaSimpleDTO();
                ptDTO.setId(detalle.getProductoTalla().getId());

                CotizacionDetalleDTO.ProductoSimpleDTO prodDTO = new CotizacionDetalleDTO.ProductoSimpleDTO();
                prodDTO.setId(detalle.getProductoTalla().getProducto().getId());
                prodDTO.setNombre(detalle.getProductoTalla().getProducto().getNombre());
                ptDTO.setProducto(prodDTO);

                CotizacionDetalleDTO.TallaSimpleDTO tallaDTO = new CotizacionDetalleDTO.TallaSimpleDTO();
                tallaDTO.setId(detalle.getProductoTalla().getTalla().getId());
                tallaDTO.setNombreTalla(detalle.getProductoTalla().getTalla().getNombreTalla());
                ptDTO.setTalla(tallaDTO);

                detDTO.setProductoTalla(ptDTO);
                detallesDTO.add(detDTO);
            }
        }

        dto.setDetalles(detallesDTO);
        return dto;
    }

    @Override
    public List<Cotizacion> listarPorCliente(Long clienteId) {
        return cotizacionRepositorio.findByUsuarioId(clienteId);
    }
}
