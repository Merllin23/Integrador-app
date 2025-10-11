package com.jkmconfecciones.Integrador_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private UsuarioDTO usuario;
    private String mensaje;
}
