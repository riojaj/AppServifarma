package com.example.proyecto.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- NUEVO IMPORT
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    @JsonIgnore // <-- AGREGADO: Evita que traiga el objeto Rol entero
    private Rol rol;

    @Column(name = "nombre_completo", length = 100, nullable = false)
    private String nombreCompleto;

    @Column(length = 50, nullable = false, unique = true)
    private String usuario;

    @JsonIgnore // <-- AGREGADO: Por seguridad, NUNCA enviar la contraseña al frontend
    @Column(length = 255, nullable = false)
    private String contrasena;

    @Builder.Default
    private Boolean activo = true;

    // ========== NUEVOS CAMPOS PARA SEGURIDAD ==========
    @Builder.Default
    private Integer intentosFallidos = 0;

    private LocalDateTime bloqueadoHasta;

    private LocalDateTime ultimoAcceso;

    // ========== AUDITORÍA ==========
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== RELACIONES ==========
    // Agregamos @JsonIgnore a TODAS las listas para evitar bucles y sobrecarga de datos
    
    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<Venta> ventas;

    @OneToMany(mappedBy = "usuarioApertura")
    @JsonIgnore
    private List<Caja> cajasApertura;

    @OneToMany(mappedBy = "usuarioCierre")
    @JsonIgnore
    private List<Caja> cajasCierre;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<MovimientoStock> movimientosStock;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<DemandaInsatisfecha> demandasInsatisfechas;

    @OneToMany(mappedBy = "usuario")
    @JsonIgnore
    private List<BitacoraComunicacion> bitacoraComunicaciones;

    // ========== MÉTODOS AUXILIARES ==========
    public boolean isActivo() {
        return activo != null && activo;
    }

    public boolean isBloqueado() {
        return bloqueadoHasta != null && bloqueadoHasta.isAfter(LocalDateTime.now());
    }    
}