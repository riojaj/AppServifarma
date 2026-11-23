package com.example.proyecto.app.service;

import com.example.proyecto.app.dto.request.ActualizarOrdenCompraRequest;
import com.example.proyecto.app.dto.request.ItemReposicionRequest;
import com.example.proyecto.app.dto.response.EnvioOrdenResponse;
import com.example.proyecto.app.dto.response.OrdenCompraResponse;

import java.util.List;

public interface OrdenCompraService {
    List<OrdenCompraResponse> crearDesdeReposicion(List<ItemReposicionRequest> items);
    List<OrdenCompraResponse> listar(String estado);
    OrdenCompraResponse obtener(Integer id);
    OrdenCompraResponse actualizar(Integer id, ActualizarOrdenCompraRequest request);
    byte[] generarPdf(Integer id);
    EnvioOrdenResponse enviar(Integer id, String canal);
    OrdenCompraResponse anular(Integer id);
}
