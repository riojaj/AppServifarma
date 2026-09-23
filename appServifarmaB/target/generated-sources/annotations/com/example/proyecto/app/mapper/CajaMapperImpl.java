package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.response.CajaResponse;
import com.example.proyecto.app.entity.Caja;
import com.example.proyecto.app.entity.Usuario;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:51-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class CajaMapperImpl implements CajaMapper {

    @Override
    public CajaResponse toResponse(Caja entity) {
        if ( entity == null ) {
            return null;
        }

        CajaResponse.CajaResponseBuilder cajaResponse = CajaResponse.builder();

        cajaResponse.usuarioAperturaId( entityUsuarioAperturaId( entity ) );
        cajaResponse.usuarioAperturaNombre( entityUsuarioAperturaNombreCompleto( entity ) );
        cajaResponse.usuarioCierreId( entityUsuarioCierreId( entity ) );
        cajaResponse.usuarioCierreNombre( entityUsuarioCierreNombreCompleto( entity ) );
        cajaResponse.createdAt( entity.getCreatedAt() );
        cajaResponse.estado( entity.getEstado() );
        cajaResponse.fechaApertura( entity.getFechaApertura() );
        cajaResponse.fechaCierre( entity.getFechaCierre() );
        cajaResponse.id( entity.getId() );
        cajaResponse.montoApertura( entity.getMontoApertura() );
        cajaResponse.montoCierreDeclarado( entity.getMontoCierreDeclarado() );

        return cajaResponse.build();
    }

    private Integer entityUsuarioAperturaId(Caja caja) {
        Usuario usuarioApertura = caja.getUsuarioApertura();
        if ( usuarioApertura == null ) {
            return null;
        }
        return usuarioApertura.getId();
    }

    private String entityUsuarioAperturaNombreCompleto(Caja caja) {
        Usuario usuarioApertura = caja.getUsuarioApertura();
        if ( usuarioApertura == null ) {
            return null;
        }
        return usuarioApertura.getNombreCompleto();
    }

    private Integer entityUsuarioCierreId(Caja caja) {
        Usuario usuarioCierre = caja.getUsuarioCierre();
        if ( usuarioCierre == null ) {
            return null;
        }
        return usuarioCierre.getId();
    }

    private String entityUsuarioCierreNombreCompleto(Caja caja) {
        Usuario usuarioCierre = caja.getUsuarioCierre();
        if ( usuarioCierre == null ) {
            return null;
        }
        return usuarioCierre.getNombreCompleto();
    }
}
