package com.example.proyecto.app.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdenCompraResponse {
    private Integer id;
    private String numero;
    private Integer proveedorId;
    private String proveedorNombre;
    private String proveedorEmail;
    private String proveedorTelefono;
    private Integer usuarioId;
    private LocalDateTime fechaCreacion;
    private String estado;
    private BigDecimal total;
    private LocalDateTime fechaEnvio;
    private String canalEnvio;
    private String notas;
    private List<Detalle> detalles;

    @Data
    public static class Detalle {
        private Integer id;
        private Integer productoId;
        private String productoNombre;
        private Integer cantidad;
        private BigDecimal precio;
        private BigDecimal subtotal;
    }
}
