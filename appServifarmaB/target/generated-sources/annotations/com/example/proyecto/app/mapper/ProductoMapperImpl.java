package com.example.proyecto.app.mapper;

import com.example.proyecto.app.dto.request.ProductoRequest;
import com.example.proyecto.app.dto.response.ProductoResponse;
import com.example.proyecto.app.entity.Categoria;
import com.example.proyecto.app.entity.Fabricante;
import com.example.proyecto.app.entity.Producto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-16T08:02:52-0500",
    comments = "version: 1.6.0, compiler: Eclipse JDT (IDE) 3.46.100.v20260826-1225, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class ProductoMapperImpl implements ProductoMapper {

    @Override
    public Producto toEntity(ProductoRequest request) {
        if ( request == null ) {
            return null;
        }

        Producto.ProductoBuilder producto = Producto.builder();

        producto.codigoBarras( request.getCodigoBarras() );
        producto.esGenerico( request.getEsGenerico() );
        producto.imagen( request.getImagen() );
        producto.nombre( request.getNombre() );
        producto.precioVentaActual( request.getPrecioVentaActual() );
        producto.principioActivo( request.getPrincipioActivo() );
        producto.stockMinimo( request.getStockMinimo() );

        return producto.build();
    }

    @Override
    public ProductoResponse toResponse(Producto entity) {
        if ( entity == null ) {
            return null;
        }

        ProductoResponse.ProductoResponseBuilder productoResponse = ProductoResponse.builder();

        productoResponse.categoriaId( entityCategoriaId( entity ) );
        productoResponse.categoriaNombre( entityCategoriaNombre( entity ) );
        productoResponse.fabricanteId( entityFabricanteId( entity ) );
        productoResponse.fabricanteNombre( entityFabricanteNombre( entity ) );
        productoResponse.productoGenericoId( entityProductoGenericoId( entity ) );
        productoResponse.productoGenericoNombre( entityProductoGenericoNombre( entity ) );
        productoResponse.codigoBarras( entity.getCodigoBarras() );
        productoResponse.createdAt( entity.getCreatedAt() );
        productoResponse.esGenerico( entity.getEsGenerico() );
        productoResponse.id( entity.getId() );
        productoResponse.imagen( entity.getImagen() );
        productoResponse.nombre( entity.getNombre() );
        productoResponse.precioVentaActual( entity.getPrecioVentaActual() );
        productoResponse.principioActivo( entity.getPrincipioActivo() );
        productoResponse.stockMinimo( entity.getStockMinimo() );
        productoResponse.updatedAt( entity.getUpdatedAt() );

        return productoResponse.build();
    }

    @Override
    public void updateEntity(Producto entity, ProductoRequest request) {
        if ( request == null ) {
            return;
        }

        entity.setCodigoBarras( request.getCodigoBarras() );
        entity.setEsGenerico( request.getEsGenerico() );
        entity.setImagen( request.getImagen() );
        entity.setNombre( request.getNombre() );
        entity.setPrecioVentaActual( request.getPrecioVentaActual() );
        entity.setPrincipioActivo( request.getPrincipioActivo() );
        entity.setStockMinimo( request.getStockMinimo() );
    }

    private Integer entityCategoriaId(Producto producto) {
        Categoria categoria = producto.getCategoria();
        if ( categoria == null ) {
            return null;
        }
        return categoria.getId();
    }

    private String entityCategoriaNombre(Producto producto) {
        Categoria categoria = producto.getCategoria();
        if ( categoria == null ) {
            return null;
        }
        return categoria.getNombre();
    }

    private Integer entityFabricanteId(Producto producto) {
        Fabricante fabricante = producto.getFabricante();
        if ( fabricante == null ) {
            return null;
        }
        return fabricante.getId();
    }

    private String entityFabricanteNombre(Producto producto) {
        Fabricante fabricante = producto.getFabricante();
        if ( fabricante == null ) {
            return null;
        }
        return fabricante.getNombre();
    }

    private Integer entityProductoGenericoId(Producto producto) {
        Producto productoGenerico = producto.getProductoGenerico();
        if ( productoGenerico == null ) {
            return null;
        }
        return productoGenerico.getId();
    }

    private String entityProductoGenericoNombre(Producto producto) {
        Producto productoGenerico = producto.getProductoGenerico();
        if ( productoGenerico == null ) {
            return null;
        }
        return productoGenerico.getNombre();
    }
}
