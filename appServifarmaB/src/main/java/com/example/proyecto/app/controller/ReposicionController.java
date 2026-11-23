package com.example.proyecto.app.controller;

import com.example.proyecto.app.dto.request.ItemReposicionRequest;
import com.example.proyecto.app.dto.response.OrdenCompraResponse;
import com.example.proyecto.app.dto.response.ReposicionResumenResponse;
import com.example.proyecto.app.dto.response.ReposicionSugeridaResponse;
import com.example.proyecto.app.service.OrdenCompraService;
import com.example.proyecto.app.service.ReposicionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reposicion")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReposicionController {

    private final ReposicionService reposicionService;
    private final OrdenCompraService ordenCompraService;

    @GetMapping("/sugerencias")
    public ResponseEntity<List<ReposicionSugeridaResponse>> obtenerSugerencias() {
        return ResponseEntity.ok(reposicionService.generarReposicionSugerida());
    }

    /** Para el globito del menú: cuántos productos están pendientes de reponer. */
    @GetMapping("/resumen")
    public ResponseEntity<ReposicionResumenResponse> resumen() {
        return ResponseEntity.ok(reposicionService.obtenerResumen());
    }

    /** Crea una orden en borrador por cada proveedor. */
    @PostMapping("/convertir-orden")
    public ResponseEntity<List<OrdenCompraResponse>> convertirAOrden(@RequestBody List<ItemReposicionRequest> items) {
        return ResponseEntity.ok(ordenCompraService.crearDesdeReposicion(items));
    }
}
