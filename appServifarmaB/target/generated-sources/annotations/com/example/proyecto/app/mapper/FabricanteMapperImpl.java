package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.request.FabricanteRequest;
import com.example.proyecto.app.dto.response.FabricanteResponse;
import com.example.proyecto.app.entity.Fabricante;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:51-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class FabricanteMapperImpl implements FabricanteMapper {

    @Override
    public Fabricante toEntity(FabricanteRequest request) {
        if ( request == null ) {
            return null;
        }

        Fabricante.FabricanteBuilder fabricante = Fabricante.builder();

        fabricante.contacto( request.getContacto() );
        fabricante.email( request.getEmail() );
        fabricante.nombre( request.getNombre() );
        fabricante.telefono( request.getTelefono() );

        return fabricante.build();
    }

    @Override
    public FabricanteResponse toResponse(Fabricante entity) {
        if ( entity == null ) {
            return null;
        }

        FabricanteResponse.FabricanteResponseBuilder fabricanteResponse = FabricanteResponse.builder();

        fabricanteResponse.contacto( entity.getContacto() );
        fabricanteResponse.createdAt( entity.getCreatedAt() );
        fabricanteResponse.email( entity.getEmail() );
        fabricanteResponse.id( entity.getId() );
        fabricanteResponse.nombre( entity.getNombre() );
        fabricanteResponse.telefono( entity.getTelefono() );

        return fabricanteResponse.build();
    }

    @Override
    public void updateEntity(Fabricante entity, FabricanteRequest request) {
        if ( request == null ) {
            return;
        }

        entity.setContacto( request.getContacto() );
        entity.setEmail( request.getEmail() );
        entity.setNombre( request.getNombre() );
        entity.setTelefono( request.getTelefono() );
    }
}
