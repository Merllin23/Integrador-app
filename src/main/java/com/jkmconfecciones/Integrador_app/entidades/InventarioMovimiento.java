package com.jkmconfecciones.Integrador_app.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_movimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventarioMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "producto_talla_id")
    private ProductoTalla productoTalla;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento")
    private TipoMovimiento tipoMovimiento;

    private Integer cantidad;
    private LocalDateTime fecha;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public enum TipoMovimiento {
        entrada, salida, ajuste
    }
}
