package com.jkmconfecciones.Integrador_app.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long id;

    @Column(nullable = false)
    private String tipo; // 'COTIZACION', 'PEDIDO', 'SISTEMA', 'ALERTA'

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "leida", nullable = false)
    @Builder.Default
    private boolean leida = false;

    @Column(name = "archivada", nullable = false)
    @Builder.Default
    private boolean archivada = false;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "referencia_id")
    private Long referenciaId; // ID de la cotización, pedido, etc.

    @Column(name = "referencia_tipo")
    private String referenciaTipo; // 'COTIZACION', 'PEDIDO', etc.

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
