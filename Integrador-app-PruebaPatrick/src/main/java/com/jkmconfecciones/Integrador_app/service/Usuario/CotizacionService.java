package com.jkmconfecciones.Integrador_app.service.Usuario;

import com.jkmconfecciones.Integrador_app.DTO.CotizacionRequestDTO;
import com.jkmconfecciones.Integrador_app.DTO.CotizacionConDetallesDTO;
import com.jkmconfecciones.Integrador_app.entidades.Cotizacion;
import java.util.List;

public interface CotizacionService {
    Cotizacion crearCotizacion(CotizacionRequestDTO dto, String correoUsuario);
    
    List<CotizacionConDetallesDTO> obtenerCotizacionesConDetalles(String correoUsuario);
}
