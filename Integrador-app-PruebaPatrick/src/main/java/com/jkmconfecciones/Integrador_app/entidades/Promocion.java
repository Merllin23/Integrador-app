package com.jkmconfecciones.Integrador_app.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "promocion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promocion")
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false)
    private TipoDescuento tipoDescuento;

    private Double valor;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "es_valida")
    private Boolean esValida;

    // Campos que NO existen en la BD pero se mantienen para compatibilidad
    @Transient
    private Colegio colegio;

    @Transient
    private DiaSemana diaSemana;

    @Transient
    private Double descuentoPorcentaje;

    @ManyToMany
    @JoinTable(
        name = "producto_promocion",
        joinColumns = @JoinColumn(name = "promocion_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private Set<Producto> productos;

    public enum TipoDescuento {
        porcentaje, fijo
    }

    public enum DiaSemana {
        LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO
    }
}
