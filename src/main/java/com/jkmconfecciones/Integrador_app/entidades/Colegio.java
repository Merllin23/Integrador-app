package com.jkmconfecciones.Integrador_app.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "colegio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Colegio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_colegio")
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "logo_url")
    private String logoUrl;

    private String direccion;

    private String telefono;

    @Column(name = "RUC")
    private String ruc;
}
