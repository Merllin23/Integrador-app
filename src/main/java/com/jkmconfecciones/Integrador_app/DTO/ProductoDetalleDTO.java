package com.jkmconfecciones.Integrador_app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDetalleDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Double precioBase;
    private String imagenUrl;
    private String categoria;
    private String coleccion;
    private List<String> colegios;
}
