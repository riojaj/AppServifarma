package com.example.proyecto.app.dto.request;

import lombok.Data;

@Data
public class ItemReposicionRequest {
    private Integer productoId;
    private Integer proveedorId;
    private Integer cantidad;
}
