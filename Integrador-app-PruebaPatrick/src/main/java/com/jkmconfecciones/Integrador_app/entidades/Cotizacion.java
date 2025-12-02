package com.jkmconfecciones.Integrador_app.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cotizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cotizacion")
    private Integer id;

    private LocalDateTime fecha;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @ToString.Exclude
    private Usuario usuario;

    private Double total;
    private Double descuentoTotal = 0.0;
    private String estado;

    @OneToMany(mappedBy = "cotizacion")
    @ToString.Exclude
    private List<DetalleCotizacion> detalles;
}
