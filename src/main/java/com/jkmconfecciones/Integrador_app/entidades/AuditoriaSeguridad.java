package com.jkmconfecciones.Integrador_app.entidades;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_seguridad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaSeguridad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private String accion; // 'LOGIN', 'LOGOUT', 'CREAR', 'EDITAR', 'ELIMINAR', 'VER'

    @Column
    private String recurso; // Nombre del recurso afectado ('Producto', 'Usuario', 'Cotización')

    @Column(name = "recurso_id")
    private Long recursoId; // ID del recurso afectado

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private String estado; // 'EXITOSO', 'FALLIDO'

    @Column(columnDefinition = "TEXT")
    private String detalles; // Información adicional sobre la acción

    @Column(name = "user_agent")
    private String userAgent;

    @PrePersist
    protected void onCreate() {
        // Solo establecer fechaHora si no fue asignada previamente
        if (this.fechaHora == null) {
            this.fechaHora = java.time.ZonedDateTime.now(java.time.ZoneId.of("America/Lima")).toLocalDateTime();
        }
    }
}
