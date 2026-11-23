package com.example.proyecto.app.service.impl;

import com.example.proyecto.app.dto.response.ReposicionResumenResponse;
import com.example.proyecto.app.dto.response.ReposicionSugeridaResponse;
import com.example.proyecto.app.dto.response.ReposicionSugeridaResponse.ProveedorOpcion;
import com.example.proyecto.app.entity.Lote;
import com.example.proyecto.app.entity.Producto;
import com.example.proyecto.app.entity.Proveedor;
import com.example.proyecto.app.repository.*;
import com.example.proyecto.app.service.ReposicionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReposicionServiceImpl implements ReposicionService {

    private static final BigDecimal FACTOR_SIN_HISTORIAL = new BigDecimal("0.6");

    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final DemandaInsatisfechaRepository demandaRepository;
    private final ProveedorRepository proveedorRepository;
    private final ParametroSistemaRepository parametroRepository;

    private volatile LocalDateTime ultimaGeneracion;
    private volatile int ultimaCantidad;

    @Override
    @Transactional(readOnly = true)
    public List<ReposicionSugeridaResponse> generarReposicionSugerida() {
        log.info("Generando lista de reposición sugerida (RF18)");
        LocalDateTime desde = LocalDateTime.now().minusDays(90);

        // Parámetros configurables (tabla parametros_sistema)
        int diasEntrega = (int) parametro("dias_entrega_proveedor", 7);
        int diasCobertura = (int) parametro("dias_cobertura_reposicion", 30);
        double factorSeguridad = 1.0 + parametro("margen_seguridad_reposicion", 20) / 100.0;

        List<Proveedor> proveedores = proveedorRepository.findAllByOrderByRazonSocialAsc();
        List<ReposicionSugeridaResponse> resultado = new ArrayList<>();

        for (Producto producto : productoRepository.findAll()) {
            Integer sumaStock = loteRepository.sumCantidadByProductoIdAndEstado(producto.getId(), Lote.EstadoLote.activo);
            int stockActual = sumaStock == null ? 0 : sumaStock;
            int stockMinimo = producto.getStockMinimo();

            // RF18: el 100 % de los productos en el mínimo o por debajo
            if (stockActual > stockMinimo) continue;

            Long v = productoRepository.sumVentasDesde(producto.getId(), desde);
            long ventas90Dias = v == null ? 0 : v;
            double promedioDiario = ventas90Dias / 90.0;
            long demandaInsatisfecha = demandaRepository
                    .findByProductoSolicitadoContainingIgnoreCase(producto.getNombre()).size();

            // Stock objetivo = mínimo (colchón) + demanda de (entrega + cobertura) con margen + demanda insatisfecha
            double objetivo = stockMinimo
                    + promedioDiario * (diasEntrega + diasCobertura) * factorSeguridad
                    + demandaInsatisfecha;
            int cantidadSugerida = (int) Math.ceil(Math.max(objetivo - stockActual, 0));

            ReposicionSugeridaResponse dto = new ReposicionSugeridaResponse();
            dto.setProductoId(producto.getId());
            dto.setNombreProducto(producto.getNombre());
            dto.setStockActual(stockActual);
            dto.setStockMinimo(stockMinimo);
            dto.setVentas90Dias(ventas90Dias);
            dto.setPromedioDiario(Math.round(promedioDiario * 100.0) / 100.0);
            dto.setCantidadSugerida(cantidadSugerida);

            List<Lote> lotes = loteRepository.findByProductoId(producto.getId());
            Lote ultimo = ultimoLote(lotes, null);
            BigDecimal referencia = precioReferencia(producto, ultimo);

            List<ProveedorOpcion> opciones = new ArrayList<>();
            for (Proveedor prov : proveedores) {
                Lote delProveedor = ultimoLote(lotes, prov.getId());
                opciones.add(new ProveedorOpcion(
                        prov.getId(),
                        prov.getRazonSocial(),
                        delProveedor != null ? delProveedor.getPrecioCompra() : referencia,
                        delProveedor != null));
            }
            dto.setOpciones(opciones);

            if (ultimo != null && ultimo.getProveedor() != null) {
                Proveedor sugerido = ultimo.getProveedor();
                dto.setProveedorId(sugerido.getId());
                dto.setProveedorSugerido(sugerido.getRazonSocial());
                dto.setPrecioEstimado(opciones.stream()
                        .filter(o -> o.getId().equals(sugerido.getId()))
                        .map(ProveedorOpcion::getPrecio).findFirst().orElse(referencia));
            } else {
                dto.setProveedorId(null);
                dto.setProveedorSugerido("Sin proveedor previo");
                dto.setPrecioEstimado(referencia);
            }
            resultado.add(dto);
        }

        // Los más urgentes (menos stock) primero
        resultado.sort(Comparator.comparing(ReposicionSugeridaResponse::getStockActual));
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public void ejecutarGeneracionDiaria() {
        List<ReposicionSugeridaResponse> lista = generarReposicionSugerida();
        ultimaGeneracion = LocalDateTime.now();
        ultimaCantidad = lista.size();
        log.info("Reposición diaria generada: {} productos pendientes", ultimaCantidad);
    }

    @Override
    @Transactional(readOnly = true)
    public ReposicionResumenResponse obtenerResumen() {
        if (ultimaGeneracion == null) ejecutarGeneracionDiaria();
        return new ReposicionResumenResponse(ultimaGeneracion, ultimaCantidad);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal precioEstimado(Producto producto, Proveedor proveedor) {
        List<Lote> lotes = loteRepository.findByProductoId(producto.getId());
        Lote delProveedor = ultimoLote(lotes, proveedor.getId());
        if (delProveedor != null) return delProveedor.getPrecioCompra();
        return precioReferencia(producto, ultimoLote(lotes, null));
    }

    // ---------- helpers ----------

    /** Último lote (mayor id) del producto; si proveedorId != null, solo de ese proveedor. */
    private Lote ultimoLote(List<Lote> lotes, Integer proveedorId) {
        return lotes.stream()
                .filter(l -> proveedorId == null
                        || (l.getProveedor() != null && proveedorId.equals(l.getProveedor().getId())))
                .max(Comparator.comparing(Lote::getId))
                .orElse(null);
    }

    private BigDecimal precioReferencia(Producto producto, Lote ultimo) {
        if (ultimo != null && ultimo.getPrecioCompra() != null) return ultimo.getPrecioCompra();
        return producto.getPrecioVentaActual().multiply(FACTOR_SIN_HISTORIAL).setScale(2, RoundingMode.HALF_UP);
    }

    private double parametro(String clave, double porDefecto) {
        return parametroRepository.findValorByClave(clave).map(valor -> {
            try {
                return Double.parseDouble(valor.trim());
            } catch (NumberFormatException e) {
                return porDefecto;
            }
        }).orElse(porDefecto);
    }
}
