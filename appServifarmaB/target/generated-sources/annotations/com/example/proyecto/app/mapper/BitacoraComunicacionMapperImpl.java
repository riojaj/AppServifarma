package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.response.BitacoraComunicacionResponse;
import com.example.proyecto.app.entity.BitacoraComunicacion;
import com.example.proyecto.app.entity.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:51-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class BitacoraComunicacionMapperImpl implements BitacoraComunicacionMapper {

    @Override
    public BitacoraComunicacionResponse toResponse(BitacoraComunicacion entity) {
        if ( entity == null ) {
            return null;
        }

        BitacoraComunicacionResponse.BitacoraComunicacionResponseBuilder bitacoraComunicacionResponse = BitacoraComunicacionResponse.builder();

        bitacoraComunicacionResponse.usuarioId( entityUsuarioId( entity ) );
        bitacoraComunicacionResponse.usuarioNombre( entityUsuarioNombreCompleto( entity ) );
        bitacoraComunicacionResponse.createdAt( entity.getCreatedAt() );
        bitacoraComunicacionResponse.fechaHora( entity.getFechaHora() );
        bitacoraComunicacionResponse.id( entity.getId() );
        bitacoraComunicacionResponse.leido( entity.getLeido() );
        bitacoraComunicacionResponse.mensaje( entity.getMensaje() );
        bitacoraComunicacionResponse.tipo( entity.getTipo() );

        return bitacoraComunicacionResponse.build();
    }

    private Integer entityUsuarioId(BitacoraComunicacion bitacoraComunicacion) {
        Usuario usuario = bitacoraComunicacion.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getId();
    }

    private String entityUsuarioNombreCompleto(BitacoraComunicacion bitacoraComunicacion) {
        Usuario usuario = bitacoraComunicacion.getUsuario();
        if ( usuario == null ) {
            return null;
        }
        return usuario.getNombreCompleto();
    }
}
