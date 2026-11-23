package com.example.proyecto.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReposicionResumenResponse {
    private LocalDateTime ultimaGeneracion;
    private int productosPendientes;
}
