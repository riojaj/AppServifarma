package com.example.proyecto.app.controller;

import com.example.proyecto.app.dto.request.ActualizarOrdenCompraRequest;
import com.example.proyecto.app.dto.request.EnviarOrdenCompraRequest;
import com.example.proyecto.app.dto.response.EnvioOrdenResponse;
import com.example.proyecto.app.dto.response.OrdenCompraResponse;
import com.example.proyecto.app.service.OrdenCompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes-compra")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    @GetMapping
    public ResponseEntity<List<OrdenCompraResponse>> listar(@RequestParam(required = false) String estado) {
        return ResponseEntity.ok(ordenCompraService.listar(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompraResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenCompraService.obtener(id));
    }

    /** Botón "Guardar": cantidades, precios y notas (solo en borrador). */
    @PutMapping("/{id}")
    public ResponseEntity<OrdenCompraResponse> actualizar(@PathVariable Integer id,
                                                          @RequestBody ActualizarOrdenCompraRequest request) {
        return ResponseEntity.ok(ordenCompraService.actualizar(id, request));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Integer id) {
        byte[] pdf = ordenCompraService.generarPdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"orden-compra-" + id + ".pdf\"")
                .body(pdf);
    }

    /** Botón "Enviar orden": canal EMAIL o WHATSAPP. */
    @PostMapping("/{id}/enviar")
    public ResponseEntity<EnvioOrdenResponse> enviar(@PathVariable Integer id,
                                                     @RequestBody EnviarOrdenCompraRequest request) {
        return ResponseEntity.ok(ordenCompraService.enviar(id, request.getCanal()));
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<OrdenCompraResponse> anular(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenCompraService.anular(id));
    }
}
