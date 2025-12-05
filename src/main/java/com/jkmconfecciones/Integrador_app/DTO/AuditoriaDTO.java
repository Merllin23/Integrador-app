package com.jkmconfecciones.Integrador_app.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuditoriaDTO {
    private String usuario;
    private String accion;
    private String recurso;
    private Long recursoId;
    private String ip;
    private String fecha;
    private String estado;
    private String detalles;
    private String userAgent;
}

