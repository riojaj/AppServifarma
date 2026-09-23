package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.request.CategoriaRequest;
import com.example.proyecto.app.dto.response.CategoriaResponse;
import com.example.proyecto.app.entity.Categoria;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:51-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class CategoriaMapperImpl implements CategoriaMapper {

    @Override
    public Categoria toEntity(CategoriaRequest request) {
        if ( request == null ) {
            return null;
        }

        Categoria.CategoriaBuilder categoria = Categoria.builder();

        categoria.descripcion( request.getDescripcion() );
        categoria.nombre( request.getNombre() );

        return categoria.build();
    }

    @Override
    public CategoriaResponse toResponse(Categoria entity) {
        if ( entity == null ) {
            return null;
        }

        CategoriaResponse.CategoriaResponseBuilder categoriaResponse = CategoriaResponse.builder();

        categoriaResponse.createdAt( entity.getCreatedAt() );
        categoriaResponse.descripcion( entity.getDescripcion() );
        categoriaResponse.id( entity.getId() );
        categoriaResponse.nombre( entity.getNombre() );

        return categoriaResponse.build();
    }

    @Override
    public void updateEntity(Categoria entity, CategoriaRequest request) {
        if ( request == null ) {
            return;
        }

        entity.setDescripcion( request.getDescripcion() );
        entity.setNombre( request.getNombre() );
    }
}
