package com.jkmconfecciones.Integrador_app.service.Usuario;

import com.jkmconfecciones.Integrador_app.DTO.CotizacionRequestDTO;
import com.jkmconfecciones.Integrador_app.entidades.Cotizacion;
import com.jkmconfecciones.Integrador_app.entidades.Usuario;

public interface CotizacionService {
    Cotizacion crearCotizacion(CotizacionRequestDTO dto, String correoUsuario);
}
