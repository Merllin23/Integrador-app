package com.jkmconfecciones.Integrador_app.service.CotizacionAdmin.Impl;

import com.jkmconfecciones.Integrador_app.DTO.CotizacionDetalleDTO;
import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.repositorios.CotizacionRepositorio;
import com.jkmconfecciones.Integrador_app.repositorios.ProductoTallaRepositorio;
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
    private final com.jkmconfecciones.Integrador_app.service.NotificacionAutomaticaService notificacionAutomaticaService;
    private final com.jkmconfecciones.Integrador_app.service.Auditoria.AuditoriaService auditoriaService;
    private final com.jkmconfecciones.Integrador_app.repositorios.UsuarioRepositorio usuarioRepositorio;
    private final ProductoTallaRepositorio productoTallaRepositorio;

    private String obtenerUsuarioActual() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    @Transactional
    public List<Cotizacion> listarPorEstado(String estadoFiltro) {
        // Cambiar COMPLETADA → PEDIDO
        List<Cotizacion> completadas = cotizacionRepositorio.findByEstadoOrderByFechaDesc("COMPLETADA");
        if (!completadas.isEmpty()) {
            for (Cotizacion c : completadas) {
                c.setEstado("PEDIDO");
            }
            cotizacionRepositorio.saveAll(completadas);
        }

        // Devolver según filtro
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

        // Cambio automático de COMPLETADA → PEDIDO
        if ("COMPLETADA".equals(nuevoEstado)) {
            nuevoEstado = "PEDIDO";
        }

        cotizacion.setEstado(nuevoEstado);
        cotizacionRepositorio.save(cotizacion);

        log.info("ADMIN '{}' cambió el estado de la Cotización ID {} de '{}' a '{}'",
                admin, id, estadoAnterior, nuevoEstado);

        try { notificacionAutomaticaService.notificarCambioEstadoCotizacion(cotizacion, estadoAnterior, nuevoEstado); }
        catch (Exception e) { log.error("Error al generar notificación", e); }

        try {
            Usuario usuario = usuarioRepositorio.findByCorreo(admin).orElse(null);
            if (usuario != null) {
                auditoriaService.registrarAccionSimple(
                        usuario,
                        "ACTUALIZAR_ESTADO_COTIZACION",
                        "COTIZACION",
                        "EXITOSO",
                        String.format("Cotización #%d: %s → %s", id, estadoAnterior, nuevoEstado)
                );
            }
        } catch (Exception e) {
            log.error("Error al registrar auditoría", e);
        }
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

    @Override
    @Transactional
    public List<Cotizacion> listarPedidos() {
        // Buscar todas las cotizaciones COMPLETADA
        List<Cotizacion> completadas = cotizacionRepositorio.findByEstadoOrderByFechaDesc("COMPLETADA");

        // Cambiar su estado a PEDIDO en memoria
        for (Cotizacion c : completadas) {
            c.setEstado("PEDIDO");
        }

        // Guardar todos los cambios de golpe
        if (!completadas.isEmpty()) {
            cotizacionRepositorio.saveAll(completadas);
        }

        // Retornar todos los pedidos relevantes
        return cotizacionRepositorio.findByEstadoInOrderByFechaDesc(
                List.of("PEDIDO", "EN_PROCESO", "FABRICACION", "PREPARADO", "ENTREGADO")
        );
    }


    @Override
    @Transactional
    public void avanzarEstado(Integer id) {
        Cotizacion c = cotizacionRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        switch (c.getEstado()) {
            case "PEDIDO" -> c.setEstado("EN_PROCESO");
            case "EN_PROCESO" -> c.setEstado("FABRICACION");
            case "FABRICACION" -> c.setEstado("PREPARADO");
            case "PREPARADO" -> {
                c.setEstado("ENTREGADO");

                // Reducir stock de los productos por talla
                if (c.getDetalles() != null) {
                    for (DetalleCotizacion detalle : c.getDetalles()) {
                        ProductoTalla pt = detalle.getProductoTalla();
                        pt.setCantidadStock(pt.getCantidadStock() - detalle.getCantidad());
                        // Guardar productoTalla actualizado
                        productoTallaRepositorio.save(pt);
                    }
                }
            }
            default -> throw new IllegalStateException("No hay más pasos");
        }

        cotizacionRepositorio.save(c);
    }


    @Override
    @Transactional
    public List<Cotizacion> listarCotizacionesPorEstado(String estado) {
        // Cambiar COMPLETADA → PEDIDO
        List<Cotizacion> completadas = cotizacionRepositorio.findByEstadoOrderByFechaDesc("COMPLETADA");
        if (!completadas.isEmpty()) {
            for (Cotizacion c : completadas) {
                c.setEstado("PEDIDO");
            }
            cotizacionRepositorio.saveAll(completadas);
        }

        if (estado == null || estado.isEmpty() || estado.equals("TODOS")) {
            return listarPedidos();
        }

        List<String> estadosValidos = List.of("PEDIDO","EN_PROCESO","FABRICACION","PREPARADO","ENTREGADO");
        if (!estadosValidos.contains(estado)) return new ArrayList<>();

        return cotizacionRepositorio.findByEstadoOrderByFechaDesc(estado);
    }


}
