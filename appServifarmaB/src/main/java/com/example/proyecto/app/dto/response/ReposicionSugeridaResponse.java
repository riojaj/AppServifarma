package com.example.proyecto.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ReposicionSugeridaResponse {
    private Integer productoId;
    private String nombreProducto;
    private Integer stockActual;
    private Integer stockMinimo;
    private Long ventas90Dias;
    private Double promedioDiario;
    private Integer cantidadSugerida;

    /** Proveedor preseleccionado (null si el producto nunca se compró). */
    private Integer proveedorId;
    private String proveedorSugerido;
    private BigDecimal precioEstimado;

    /** Proveedores entre los que el administrador puede elegir, con su precio estimado. */
    private List<ProveedorOpcion> opciones;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProveedorOpcion {
        private Integer id;
        private String razonSocial;
        private BigDecimal precio;
        private boolean conHistorial;
    }
}
