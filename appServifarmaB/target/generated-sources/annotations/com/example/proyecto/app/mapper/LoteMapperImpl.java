package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.request.LoteRequest;
import com.example.proyecto.app.dto.response.LoteResponse;
import com.example.proyecto.app.entity.Lote;
import com.example.proyecto.app.entity.Producto;
import com.example.proyecto.app.entity.Proveedor;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:52-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class LoteMapperImpl implements LoteMapper {

    @Override
    public Lote toEntity(LoteRequest request) {
        if ( request == null ) {
            return null;
        }

        Lote.LoteBuilder lote = Lote.builder();

        lote.cantidad( request.getCantidad() );
        lote.fechaIngreso( request.getFechaIngreso() );
        lote.fechaVencimiento( request.getFechaVencimiento() );
        lote.lote( request.getLote() );
        lote.precioCompra( request.getPrecioCompra() );
        lote.precioVenta( request.getPrecioVenta() );

        return lote.build();
    }

    @Override
    public LoteResponse toResponse(Lote entity) {
        if ( entity == null ) {
            return null;
        }

        LoteResponse.LoteResponseBuilder loteResponse = LoteResponse.builder();

        loteResponse.productoId( entityProductoId( entity ) );
        loteResponse.productoNombre( entityProductoNombre( entity ) );
        loteResponse.productoImagen( entityProductoImagen( entity ) );
        loteResponse.proveedorId( entityProveedorId( entity ) );
        loteResponse.proveedorRazonSocial( entityProveedorRazonSocial( entity ) );
        loteResponse.cantidad( entity.getCantidad() );
        loteResponse.createdAt( entity.getCreatedAt() );
        loteResponse.estado( entity.getEstado() );
        loteResponse.fechaIngreso( entity.getFechaIngreso() );
        loteResponse.fechaVencimiento( entity.getFechaVencimiento() );
        loteResponse.id( entity.getId() );
        loteResponse.lote( entity.getLote() );
        loteResponse.precioCompra( entity.getPrecioCompra() );
        loteResponse.precioVenta( entity.getPrecioVenta() );
        loteResponse.updatedAt( entity.getUpdatedAt() );

        return loteResponse.build();
    }

    @Override
    public void updateEntity(Lote entity, LoteRequest request) {
        if ( request == null ) {
            return;
        }

        entity.setCantidad( request.getCantidad() );
        entity.setFechaIngreso( request.getFechaIngreso() );
        entity.setFechaVencimiento( request.getFechaVencimiento() );
        entity.setLote( request.getLote() );
        entity.setPrecioCompra( request.getPrecioCompra() );
        entity.setPrecioVenta( request.getPrecioVenta() );
    }

    private Integer entityProductoId(Lote lote) {
        Producto producto = lote.getProducto();
        if ( producto == null ) {
            return null;
        }
        return producto.getId();
    }

    private String entityProductoNombre(Lote lote) {
        Producto producto = lote.getProducto();
        if ( producto == null ) {
            return null;
        }
        return producto.getNombre();
    }

    private String entityProductoImagen(Lote lote) {
        Producto producto = lote.getProducto();
        if ( producto == null ) {
            return null;
        }
        return producto.getImagen();
    }

    private Integer entityProveedorId(Lote lote) {
        Proveedor proveedor = lote.getProveedor();
        if ( proveedor == null ) {
            return null;
        }
        return proveedor.getId();
    }

    private String entityProveedorRazonSocial(Lote lote) {
        Proveedor proveedor = lote.getProveedor();
        if ( proveedor == null ) {
            return null;
        }
        return proveedor.getRazonSocial();
    }
}
