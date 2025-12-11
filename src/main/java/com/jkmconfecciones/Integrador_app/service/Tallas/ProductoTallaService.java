package com.jkmconfecciones.Integrador_app.service.Tallas;

import com.jkmconfecciones.Integrador_app.entidades.ProductoTalla;
import java.util.List;
import java.util.Map;

public interface ProductoTallaService {

    // Devuelve la lista de ProductoTalla para el view
    List<ProductoTalla> listarTodos();

    // Devuelve la lista de ProductoTalla como Map para el API
    List<Map<String, Object>> listarTallasComoMap();

    // Obtener detalle de una talla
    Map<String, Object> obtenerDetalle(Long id);

    // Cambiar estado activo/inactivo
    Map<String, Object> toggleEstado(Long id, Boolean nuevoEstado);
}
