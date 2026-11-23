package com.example.proyecto.app.service;

import com.example.proyecto.app.dto.response.ReposicionResumenResponse;
import com.example.proyecto.app.dto.response.ReposicionSugeridaResponse;
import com.example.proyecto.app.entity.Producto;
import com.example.proyecto.app.entity.Proveedor;

import java.math.BigDecimal;
import java.util.List;

public interface ReposicionService {

    List<ReposicionSugeridaResponse> generarReposicionSugerida();

    /** Lo llama el job de las 06:00. Calcula y guarda la fecha/cantidad de la última generación. */
    void ejecutarGeneracionDiaria();

    ReposicionResumenResponse obtenerResumen();

    /** Precio estimado de un producto con un proveedor (último lote de ese proveedor o referencia). */
    BigDecimal precioEstimado(Producto producto, Proveedor proveedor);
}
