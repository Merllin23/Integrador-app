package com.jkmconfecciones.Integrador_app.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "producto_talla")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoTalla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto_talla")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "talla_id")
    private Talla talla;

    @Column(name = "cantidad_stock")
    private Integer cantidadStock;

    @Column(name = "precio_unitario_final")
    private Double precioUnitarioFinal;

    @OneToMany(mappedBy = "productoTalla")
    private List<DetalleCotizacion> detalleCotizaciones;

    @OneToMany(mappedBy = "productoTalla")
    private List<InventarioMovimiento> movimientos;
}
