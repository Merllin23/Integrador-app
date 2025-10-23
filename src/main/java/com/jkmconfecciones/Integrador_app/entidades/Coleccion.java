package com.jkmconfecciones.Integrador_app.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "coleccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_coleccion")
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "coleccion")
    private List<Producto> productos;
}
