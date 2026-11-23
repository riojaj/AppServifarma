package com.example.proyecto.app.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ActualizarOrdenCompraRequest {
    private String notas;
    private List<Linea> lineas;

    @Data
    public static class Linea {
        private Integer detalleId;
        /** 0 elimina la línea de la orden. */
        private Integer cantidad;
        private BigDecimal precio;
    }
}
