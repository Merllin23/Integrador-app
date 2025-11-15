package com.jkmconfecciones.Integrador_app.DTO;

import lombok.Data;
import java.util.List;

@Data
public class CotizacionRequestDTO {
    private List<ProductoCotizacionDTO> productos;

    @Data
    public static class ProductoCotizacionDTO {
        private Integer productoTallaId;
        private Integer cantidad;
    }
}
