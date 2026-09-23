package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.response.MovimientoStockResponse;
import com.example.proyecto.app.entity.Lote;
import com.example.proyecto.app.entity.MovimientoStock;
import com.example.proyecto.app.entity.Producto;
import com.example.proyecto.app.entity.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:52-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class MovimientoStockMapperImpl implements MovimientoStockMapper {

    @Override
    public MovimientoStockResponse toResponse(MovimientoStock entity) {
        if ( entity == null ) {
            return null;
        }

        MovimientoStockResponse.MovimientoStockResponseBuilder movimientoStockResponse = MovimientoStockResponse.builder();

        movimientoStockResponse.loteId( entityLoteId( entity ) );
        movimientoStockResponse.nombreProducto( entityLoteProductoNombre( entity ) );
        movimientoStockResponse.usuarioId( entityUsuarioId( entity ) );
        movimientoStockResponse.nombreUsuario( entityUsuarioNombreCompleto( entity ) );
        movimientoStockResponse.cantidad( entity.getCantidad() );
        movimientoStockResponse.costoUnitario( entity.getCostoUnitario() );
        movimientoStockResponse.createdAt( entity.getCreatedAt() );
        movimientoStockResponse.fecha( entity.getFecha() );
        movimientoStockResponse.id( entity.getId() );
        movimientoStockResponse.observacion( entity.getObservacion() );
        movimientoStockResponse.referenciaId( entity.getReferenciaId() );
        movimientoStockResponse.tipoMovimiento( entity.getTipoMovimiento() );

        return movimientoStockResponse.build();
    }

    private Integer entityLoteId(MovimientoStock movimientoStock) {
        Lote lote = movimientoStock.getLote();
        if ( lote == null ) {
            return null;
        }
        return lote.getId();
    }

    private String entityLoteProductoNombre(MovimientoStock movimientoStock) {
        Lote lote = movimientoStock.getLote();
        if ( lote == null ) {
            return null;
        }
        Producto producto = lote.getProducto();
        if ( producto == null ) {
            return null;
        }
        return producto.getNombre();
    }

    private Integer entityUsuarioId(MovimientoStock movimientoStock) {
        Usuario usuario = movimientoStock.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getId();
    }

    private String entityUsuarioNombreCompleto(MovimientoStock movimientoStock) {
        Usuario usuario = movimientoStock.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getNombreCompleto();
    }
}
