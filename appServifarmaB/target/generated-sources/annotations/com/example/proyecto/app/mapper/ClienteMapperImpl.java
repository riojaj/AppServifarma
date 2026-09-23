package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.request.ClienteRequest;
import com.example.proyecto.app.dto.response.ClienteResponse;
import com.example.proyecto.app.entity.Cliente;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:51-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class ClienteMapperImpl implements ClienteMapper {

    @Override
    public Cliente toEntity(ClienteRequest request) {
        if ( request == null ) {
            return null;
        }

        Cliente.ClienteBuilder cliente = Cliente.builder();

        cliente.direccion( request.getDireccion() );
        cliente.documentoNumero( request.getDocumentoNumero() );
        cliente.documentoTipo( request.getDocumentoTipo() );
        cliente.email( request.getEmail() );
        cliente.nombre( request.getNombre() );
        cliente.telefono( request.getTelefono() );

        return cliente.build();
    }

    @Override
    public ClienteResponse toResponse(Cliente entity) {
        if ( entity == null ) {
            return null;
        }

        ClienteResponse.ClienteResponseBuilder clienteResponse = ClienteResponse.builder();

        clienteResponse.createdAt( entity.getCreatedAt() );
        clienteResponse.direccion( entity.getDireccion() );
        clienteResponse.documentoNumero( entity.getDocumentoNumero() );
        clienteResponse.documentoTipo( entity.getDocumentoTipo() );
        clienteResponse.email( entity.getEmail() );
        clienteResponse.id( entity.getId() );
        clienteResponse.nombre( entity.getNombre() );
        clienteResponse.telefono( entity.getTelefono() );

        return clienteResponse.build();
    }

    @Override
    public void updateEntity(Cliente entity, ClienteRequest request) {
        if ( request == null ) {
            return;
        }

        entity.setDireccion( request.getDireccion() );
        entity.setDocumentoNumero( request.getDocumentoNumero() );
        entity.setDocumentoTipo( request.getDocumentoTipo() );
        entity.setEmail( request.getEmail() );
        entity.setNombre( request.getNombre() );
        entity.setTelefono( request.getTelefono() );
    }
}
