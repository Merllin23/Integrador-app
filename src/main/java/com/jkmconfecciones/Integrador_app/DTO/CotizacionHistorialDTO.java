package com.jkmconfecciones.Integrador_app.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionHistorialDTO {

    private Integer id;
    private LocalDateTime fecha;
    private Double total;
    private String estado;
}
