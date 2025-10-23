package com.jkmconfecciones.Integrador_app.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "talla")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Talla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_talla")
    private Integer id;

    @Column(name = "nombre_talla", unique = true, nullable = false)
    private String nombreTalla;

    @OneToMany(mappedBy = "talla")
    private List<ProductoTalla> productoTallas;
}
