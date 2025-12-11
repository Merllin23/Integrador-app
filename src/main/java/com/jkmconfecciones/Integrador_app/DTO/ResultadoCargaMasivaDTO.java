package com.jkmconfecciones.Integrador_app.DTO;

import lombok.*;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoCargaMasivaDTO {
    
    @Builder.Default
    private List<ProductoCargaMasivaDTO> productos = new ArrayList<>();
    
    private int totalProductos;
    private int productosValidos;
    private int productosInvalidos;
    
    @Builder.Default
    private List<String> erroresGenerales = new ArrayList<>();
}
