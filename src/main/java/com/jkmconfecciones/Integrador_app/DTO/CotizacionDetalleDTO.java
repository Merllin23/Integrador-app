package com.jkmconfecciones.Integrador_app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionDetalleDTO {
    private Integer id;
    private LocalDateTime fecha;
    private Double total;
    private String estado;
    private UsuarioSimpleDTO usuario;
    private List<DetalleItemDTO> detalles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioSimpleDTO {
        private Long id;
        private String nombre;
        private String apellido;
        private String correo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleItemDTO {
        private Integer id;
        private Integer cantidad;
        private Double precioUnitario;
        private Double subtotal;
        private ProductoTallaSimpleDTO productoTalla;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoTallaSimpleDTO {
        private Integer id;
        private ProductoSimpleDTO producto;
        private TallaSimpleDTO talla;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoSimpleDTO {
        private Integer id;
        private String nombre;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TallaSimpleDTO {
        private Integer id;
        private String nombreTalla;
    }
}
