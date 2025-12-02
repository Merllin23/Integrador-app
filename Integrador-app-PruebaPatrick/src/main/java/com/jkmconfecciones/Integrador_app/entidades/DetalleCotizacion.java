package com.jkmconfecciones.Integrador_app.entidades;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detallecotizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cotizacion_id")
    private Cotizacion cotizacion;

    @ManyToOne
    @JoinColumn(name = "producto_talla_id")
    private ProductoTalla productoTalla;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private Double precioUnitario;

    private Double subtotal;

    // Campos para descuentos (transitorios, no en BD)
    @Transient
    private Double descuentoMonto = 0.0;

    @Transient
    private Double descuentoPorcentaje = 0.0;

    @Transient
    private String tipoDescuento;

    @Transient
    private String promocionNombre;
}

