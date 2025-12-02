package com.jkmconfecciones.Integrador_app.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionConDetallesDTO {

    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fecha;

    private String estado;
    
    private Integer totalItems;
    
    private Double subtotal = 0.0;
    
    private Double descuentoTotal = 0.0;
    
    private Double total;

    private String imagenPrimer;

    private List<DetalleCotizacionDTO> detalles;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleCotizacionDTO {
        private Integer id;
        private String productoNombre;
        private String productoimagen;
        private String talla;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
        private Double descuentoMonto;
        private Double descuentoPorcentaje;
        private String tipoDescuento;
        private String promocionNombre;
    }
}
