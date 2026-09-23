package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.request.ProveedorRequest;
import com.example.proyecto.app.dto.response.ProveedorResponse;
import com.example.proyecto.app.entity.Proveedor;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:52-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class ProveedorMapperImpl implements ProveedorMapper {

    @Override
    public Proveedor toEntity(ProveedorRequest request) {
        if ( request == null ) {
            return null;
        }

        Proveedor.ProveedorBuilder proveedor = Proveedor.builder();

        proveedor.contacto( request.getContacto() );
        proveedor.direccion( request.getDireccion() );
        proveedor.email( request.getEmail() );
        proveedor.razonSocial( request.getRazonSocial() );
        proveedor.region( request.getRegion() );
        proveedor.ruc( request.getRuc() );
        proveedor.telefono( request.getTelefono() );

        return proveedor.build();
    }

    @Override
    public ProveedorResponse toResponse(Proveedor entity) {
        if ( entity == null ) {
            return null;
        }

        ProveedorResponse.ProveedorResponseBuilder proveedorResponse = ProveedorResponse.builder();

        proveedorResponse.contacto( entity.getContacto() );
        proveedorResponse.createdAt( entity.getCreatedAt() );
        proveedorResponse.direccion( entity.getDireccion() );
        proveedorResponse.email( entity.getEmail() );
        proveedorResponse.id( entity.getId() );
        proveedorResponse.razonSocial( entity.getRazonSocial() );
        proveedorResponse.region( entity.getRegion() );
        proveedorResponse.ruc( entity.getRuc() );
        proveedorResponse.telefono( entity.getTelefono() );

        return proveedorResponse.build();
    }

    @Override
    public void updateEntity(Proveedor entity, ProveedorRequest request) {
        if ( request == null ) {
            return;
        }

        entity.setContacto( request.getContacto() );
        entity.setDireccion( request.getDireccion() );
        entity.setEmail( request.getEmail() );
        entity.setRazonSocial( request.getRazonSocial() );
        entity.setRegion( request.getRegion() );
        entity.setRuc( request.getRuc() );
        entity.setTelefono( request.getTelefono() );
    }
}
