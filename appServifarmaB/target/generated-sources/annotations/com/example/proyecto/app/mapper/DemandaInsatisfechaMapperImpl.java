package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.response.DemandaInsatisfechaResponse;
import com.example.proyecto.app.entity.DemandaInsatisfecha;
import com.example.proyecto.app.entity.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:52-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class DemandaInsatisfechaMapperImpl implements DemandaInsatisfechaMapper {

    @Override
    public DemandaInsatisfechaResponse toResponse(DemandaInsatisfecha entity) {
        if ( entity == null ) {
            return null;
        }

        DemandaInsatisfechaResponse.DemandaInsatisfechaResponseBuilder demandaInsatisfechaResponse = DemandaInsatisfechaResponse.builder();

        demandaInsatisfechaResponse.usuarioId( entityUsuarioId( entity ) );
        demandaInsatisfechaResponse.usuarioNombre( entityUsuarioNombreCompleto( entity ) );
        demandaInsatisfechaResponse.clienteDocumento( entity.getClienteDocumento() );
        demandaInsatisfechaResponse.createdAt( entity.getCreatedAt() );
        demandaInsatisfechaResponse.fecha( entity.getFecha() );
        demandaInsatisfechaResponse.id( entity.getId() );
        demandaInsatisfechaResponse.productoSolicitado( entity.getProductoSolicitado() );

        return demandaInsatisfechaResponse.build();
    }

    private Integer entityUsuarioId(DemandaInsatisfecha demandaInsatisfecha) {
        Usuario usuario = demandaInsatisfecha.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getId();
    }

    private String entityUsuarioNombreCompleto(DemandaInsatisfecha demandaInsatisfecha) {
        Usuario usuario = demandaInsatisfecha.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getNombreCompleto();
    }
}
