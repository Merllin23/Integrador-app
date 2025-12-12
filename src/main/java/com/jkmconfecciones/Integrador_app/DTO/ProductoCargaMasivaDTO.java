package com.jkmconfecciones.Integrador_app.DTO;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para representar un producto en la carga masiva
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoCargaMasivaDTO {
    
    private Integer fila; // número de fila en el CSV/Excel
    private String nombre;
    private String descripcion;
    private Double precio;
    private String categoria;
    private String coleccion;
    private String colegioAsociado;
    private String imagenUrl;
    
    // Tallas con su stock
    @Builder.Default
    private List<TallaStockDTO> tallas = new ArrayList<>();
    
    // Estado de validación
    private String estado; // "VALIDO" o "FORMATO_INVALIDO"
    
    @Builder.Default
    private List<String> errores = new ArrayList<>();
    
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TallaStockDTO {
        private String nombreTalla;
        private Integer stock;
    }
}
