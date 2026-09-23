package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.response.DetalleVentaResponse;
import com.example.proyecto.app.entity.DetalleVenta;
import com.example.proyecto.app.entity.Lote;
import com.example.proyecto.app.entity.Producto;
import com.example.proyecto.app.entity.Venta;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:52-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class DetalleVentaMapperImpl implements DetalleVentaMapper {

    @Override
    public DetalleVentaResponse toResponse(DetalleVenta entity) {
        if ( entity == null ) {
            return null;
        }

        DetalleVentaResponse.DetalleVentaResponseBuilder detalleVentaResponse = DetalleVentaResponse.builder();

        detalleVentaResponse.ventaId( entityVentaId( entity ) );
        detalleVentaResponse.loteId( entityLoteId( entity ) );
        detalleVentaResponse.productoNombre( entityLoteProductoNombre( entity ) );
        detalleVentaResponse.cantidad( entity.getCantidad() );
        detalleVentaResponse.createdAt( entity.getCreatedAt() );
        detalleVentaResponse.id( entity.getId() );
        detalleVentaResponse.precioCompraUnitario( entity.getPrecioCompraUnitario() );
        detalleVentaResponse.precioUnitarioVenta( entity.getPrecioUnitarioVenta() );
        detalleVentaResponse.subtotal( entity.getSubtotal() );

        return detalleVentaResponse.build();
    }

    private Integer entityVentaId(DetalleVenta detalleVenta) {
        Venta venta = detalleVenta.getVenta();
        if ( venta == null ) {
            return null;
        }
        return venta.getId();
    }

    private Integer entityLoteId(DetalleVenta detalleVenta) {
        Lote lote = detalleVenta.getLote();
        if ( lote == null ) {
            return null;
        }
        return lote.getId();
    }

    private String entityLoteProductoNombre(DetalleVenta detalleVenta) {
        Lote lote = detalleVenta.getLote();
        if ( lote == null ) {
            return null;
        }
        Producto producto = lote.getProducto();
        if ( producto == null ) {
            return null;
        }
        return producto.getNombre();
    }
}
