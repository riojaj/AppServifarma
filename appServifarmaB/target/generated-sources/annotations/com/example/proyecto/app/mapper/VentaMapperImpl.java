package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.response.DetalleVentaResponse;
import com.example.proyecto.app.dto.response.VentaResponse;
import com.example.proyecto.app.entity.Caja;
import com.example.proyecto.app.entity.Cliente;
import com.example.proyecto.app.entity.DetalleVenta;
import com.example.proyecto.app.entity.Usuario;
import com.example.proyecto.app.entity.Venta;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:51-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class VentaMapperImpl implements VentaMapper {

    @Autowired
    private DetalleVentaMapper detalleVentaMapper;

    @Override
    public VentaResponse toResponse(Venta entity) {
        if ( entity == null ) {
            return null;
        }

        VentaResponse.VentaResponseBuilder ventaResponse = VentaResponse.builder();

        ventaResponse.usuarioId( entityUsuarioId( entity ) );
        ventaResponse.usuarioNombre( entityUsuarioNombreCompleto( entity ) );
        ventaResponse.clienteId( entityClienteId( entity ) );
        ventaResponse.clienteNombre( entityClienteNombre( entity ) );
        ventaResponse.cajaId( entityCajaId( entity ) );
        ventaResponse.codigoAutorizacion( entity.getCodigoAutorizacion() );
        ventaResponse.createdAt( entity.getCreatedAt() );
        ventaResponse.detalles( detalleVentaListToDetalleVentaResponseList( entity.getDetalles() ) );
        ventaResponse.estado( entity.getEstado() );
        ventaResponse.fecha( entity.getFecha() );
        ventaResponse.id( entity.getId() );
        ventaResponse.medioPago( entity.getMedioPago() );
        ventaResponse.total( entity.getTotal() );

        return ventaResponse.build();
    }

    private Integer entityUsuarioId(Venta venta) {
        Usuario usuario = venta.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getId();
    }

    private String entityUsuarioNombreCompleto(Venta venta) {
        Usuario usuario = venta.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getNombreCompleto();
    }

    private Integer entityClienteId(Venta venta) {
        Cliente cliente = venta.getCliente();
        if ( cliente == null ) {
            return null;
        }
        return cliente.getId();
    }

    private String entityClienteNombre(Venta venta) {
        Cliente cliente = venta.getCliente();
        if ( cliente == null ) {
            return null;
        }
        return cliente.getNombre();
    }

    private Integer entityCajaId(Venta venta) {
        Caja caja = venta.getCaja();
        if ( caja == null ) {
            return null;
        }
        return caja.getId();
    }

    protected List<DetalleVentaResponse> detalleVentaListToDetalleVentaResponseList(List<DetalleVenta> list) {
        if ( list == null ) {
            return null;
        }

        List<DetalleVentaResponse> list1 = new ArrayList<DetalleVentaResponse>( list.size() );
        for ( DetalleVenta detalleVenta : list ) {
            list1.add( detalleVentaMapper.toResponse( detalleVenta ) );
        }

        return list1;
    }
}
