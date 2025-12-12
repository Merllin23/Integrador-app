package com.jkmconfecciones.Integrador_app.service.CotizacionAdmin;

import com.jkmconfecciones.Integrador_app.DTO.CotizacionDetalleDTO;
import com.jkmconfecciones.Integrador_app.entidades.Cotizacion;

import java.util.List;
import java.util.Map;

public interface AdminCotizacionService {

    List<Cotizacion> listarPorEstado(String estadoFiltro);

    void actualizarEstado(Integer id, String nuevoEstado);

    void eliminar(Integer id);

    CotizacionDetalleDTO obtenerDetalle(Integer id);

    List<Cotizacion> listarPorCliente(Long clienteId);
    List<Cotizacion> listarPedidos();
    void avanzarEstado(Integer id);

    List<Cotizacion> listarCotizacionesPorEstado(String estado);


}
