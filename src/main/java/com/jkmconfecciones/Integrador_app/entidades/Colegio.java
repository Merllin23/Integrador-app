package com.jkmconfecciones.Integrador_app.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "colegio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Colegio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_colegio")
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    private String logoUrl;
    private String direccion;
    private String telefono;
    private String ruc;

    @ManyToMany(mappedBy = "colegios")
    private Set<Producto> productos;
}
