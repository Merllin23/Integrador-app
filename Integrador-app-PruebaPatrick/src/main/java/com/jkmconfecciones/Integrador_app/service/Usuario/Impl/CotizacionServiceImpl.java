package com.jkmconfecciones.Integrador_app.service.Usuario.Impl;

import com.jkmconfecciones.Integrador_app.DTO.CotizacionRequestDTO;
import com.jkmconfecciones.Integrador_app.DTO.CotizacionConDetallesDTO;
import com.jkmconfecciones.Integrador_app.entidades.*;
import com.jkmconfecciones.Integrador_app.repositorios.*;
import com.jkmconfecciones.Integrador_app.service.Usuario.CotizacionService;
import com.jkmconfecciones.Integrador_app.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired(required = false)
    private PromocionService promocionService;

    @Override
    @Transactional
    public Cotizacion crearCotizacion(CotizacionRequestDTO dto, String correoUsuario) {

        // 1. Obtener usuario
        Usuario usuario = usuarioRepositorio.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Crear cotización base
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setUsuario(usuario);
        cotizacion.setFecha(LocalDateTime.now());
        cotizacion.setEstado("PENDIENTE");

        List<DetalleCotizacion> detalles = new ArrayList<>();
        double total = 0.0;
        double descuentoTotal = 0.0;

        // 3. Recorrer productos enviados por el usuario
        for (CotizacionRequestDTO.ProductoCotizacionDTO item : dto.getProductos()) {

            Long idPT = item.getProductoTallaId().longValue();

            ProductoTalla pt = productoTallaRepositorio.findById(idPT)
                    .orElseThrow(() -> new RuntimeException("Producto/Talla no encontrado"));

            int cantidad = item.getCantidad();

            double precio = pt.getPrecioUnitarioFinal() != null
                    ? pt.getPrecioUnitarioFinal()
                    : pt.getProducto().getPrecioBase();

            // Calcular descuento si existe promoción para el colegio del usuario
            double descuento = 0.0;
            // Nota: usuario.getColegio() no existe en la BD actual
            // if (promocionService != null && usuario.getColegio() != null) {
            //     descuento = promocionService.obtenerDescuentoProducto(
            //             pt.getProducto().getId(), 
            //             usuario.getColegio().getId()
            //     );
            // }

            // Aplicar descuento al precio
            double precioConDescuento = precio * (1 - descuento / 100.0);
            double subtotal = precioConDescuento * cantidad;
            double montoDescuento = (precio - precioConDescuento) * cantidad;

            total += subtotal;
            descuentoTotal += montoDescuento;

            DetalleCotizacion det = new DetalleCotizacion();
            det.setCotizacion(cotizacion);
            det.setProductoTalla(pt);
            det.setCantidad(cantidad);
            det.setPrecioUnitario(precioConDescuento);
            det.setSubtotal(subtotal);

            detalles.add(det);
        }

        cotizacion.setTotal(total);
        cotizacion.setDescuentoTotal(descuentoTotal);

        // 4. Guardar cotización para generar ID
        cotizacionRepositorio.save(cotizacion);

        // 5. Guardar detalles
        detalleCotizacionRepositorio.saveAll(detalles);

        return cotizacion;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotizacionConDetallesDTO> obtenerCotizacionesConDetalles(String correoUsuario) {
        Usuario usuario = usuarioRepositorio.findByCorreo(correoUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Cotizacion> cotizaciones = cotizacionRepositorio.findByUsuario(usuario);
        LocalDate hoy = LocalDate.now();

        return cotizaciones.stream().map(cot -> {
            CotizacionConDetallesDTO dto = new CotizacionConDetallesDTO();
            dto.setId(cot.getId());
            dto.setFecha(cot.getFecha());
            dto.setEstado(cot.getEstado());

            double subtotal = 0.0;
            double descuentoTotal = 0.0;
            int totalItems = 0;
            String imagenPrimer = "/images/placeholder.png";

            List<CotizacionConDetallesDTO.DetalleCotizacionDTO> detallesDTO = new ArrayList<>();

            if (cot.getDetalles() != null) {
                for (DetalleCotizacion det : cot.getDetalles()) {
                    CotizacionConDetallesDTO.DetalleCotizacionDTO detDTO = new CotizacionConDetallesDTO.DetalleCotizacionDTO();

                    ProductoTalla pt = det.getProductoTalla();
                    Producto prod = pt != null ? pt.getProducto() : null;

                    // Info básica del detalle
                    detDTO.setId(det.getId());
                    detDTO.setProductoNombre(prod != null ? prod.getNombre() : "Producto");
                    detDTO.setProductoimagen(prod != null && prod.getImagenUrl() != null ? prod.getImagenUrl() : "/images/placeholder.png");
                    detDTO.setTalla(pt != null && pt.getTalla() != null ? pt.getTalla().getNombreTalla() : "-");
                    detDTO.setCantidad(det.getCantidad() != null ? det.getCantidad() : 0);
                    detDTO.setPrecioUnitario(det.getPrecioUnitario() != null ? det.getPrecioUnitario() : 0.0);
                    detDTO.setSubtotal(det.getSubtotal() != null ? det.getSubtotal() : 0.0);

                    // Buscar si hay descuento activo para este producto
                    double descuentoMonto = 0.0;
                    double descuentoPorcentaje = 0.0;
                    String tipoDescuento = null;
                    String promocionNombre = null;

                    if (prod != null && promocionService != null) {
                        List<Promocion> promocionesActivas = promocionService.listarPromocionesActivas()
                                .stream()
                                .filter(p -> p.getEsValida() && 
                                           p.getFechaInicio().compareTo(hoy) <= 0 && 
                                           p.getFechaFin().compareTo(hoy) >= 0)
                                .collect(Collectors.toList());

                        for (Promocion promo : promocionesActivas) {
                            if (promo.getProductos() != null && promo.getProductos().stream()
                                    .anyMatch(p -> p.getId().equals(prod.getId()))) {
                                tipoDescuento = promo.getTipoDescuento().toString();
                                promocionNombre = promo.getNombre();

                                // Calcular descuento
                                if ("porcentaje".equals(tipoDescuento)) {
                                    descuentoPorcentaje = promo.getValor();
                                    descuentoMonto = (det.getPrecioUnitario() * promo.getValor() / 100.0) * det.getCantidad();
                                } else if ("fijo".equals(tipoDescuento)) {
                                    descuentoMonto = promo.getValor() * det.getCantidad();
                                    descuentoPorcentaje = (descuentoMonto / (det.getPrecioUnitario() * det.getCantidad())) * 100;
                                }
                                break; // Tomar el primer descuento encontrado
                            }
                        }
                    }

                    detDTO.setDescuentoMonto(descuentoMonto);
                    detDTO.setDescuentoPorcentaje(descuentoPorcentaje);
                    detDTO.setTipoDescuento(tipoDescuento);
                    detDTO.setPromocionNombre(promocionNombre);

                    detallesDTO.add(detDTO);
                    subtotal += det.getSubtotal() != null ? det.getSubtotal() : 0.0;
                    descuentoTotal += descuentoMonto;
                    totalItems += det.getCantidad() != null ? det.getCantidad() : 0;

                    // Imagen del primer producto
                    if (imagenPrimer.equals("/images/placeholder.png") && prod != null && prod.getImagenUrl() != null) {
                        imagenPrimer = prod.getImagenUrl();
                    }
                }
            }

            dto.setDetalles(detallesDTO);
            dto.setTotalItems(totalItems);
            dto.setSubtotal(subtotal);
            dto.setDescuentoTotal(descuentoTotal);
            dto.setTotal(subtotal - descuentoTotal);
            dto.setImagenPrimer(imagenPrimer);

            return dto;
        }).collect(Collectors.toList());
    }
}

