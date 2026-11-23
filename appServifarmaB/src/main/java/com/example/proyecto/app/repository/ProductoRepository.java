package com.example.proyecto.app.repository;

import com.example.proyecto.app.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

       // ==============================
       // BÚSQUEDAS POR CAMPOS BÁSICOS
       // ==============================

       Optional<Producto> findByCodigoBarras(String codigoBarras);

       List<Producto> findByNombreIgnoreCase(String nombre);

       List<Producto> findByNombreContainingIgnoreCase(String nombre);

       List<Producto> findByPrincipioActivoContainingIgnoreCase(String principioActivo);

       List<Producto> findByEsGenerico(boolean esGenerico);

       // ==============================
       // BÚSQUEDAS POR RELACIONES
       // ==============================

       List<Producto> findByCategoriaId(Integer categoriaId);

       List<Producto> findByFabricanteId(Integer fabricanteId);

       List<Producto> findByProductoGenericoId(Integer productoGenericoId);

       // ==============================
       // BÚSQUEDAS COMBINADAS
       // ==============================

       List<Producto> findByNombreContainingIgnoreCaseOrCodigoBarrasContaining(String nombre, String codigoBarras);

       @Query("SELECT DISTINCT p FROM Producto p JOIN p.lotes l WHERE l.estado = 'activo' AND l.cantidad > 0")
       List<Producto> findProductosConStockDisponible();

       @Query("SELECT p FROM Producto p WHERE p.id IN (" +
                     "   SELECT l.producto.id FROM Lote l WHERE l.estado = 'activo' " +
                     "   GROUP BY l.producto.id HAVING COALESCE(SUM(l.cantidad), 0) < p.stockMinimo" +
                     ")")
       List<Producto> findProductosConStockBajo();

       @Query("SELECT p FROM Producto p WHERE NOT EXISTS (" +
                     "   SELECT l FROM Lote l WHERE l.producto.id = p.id AND l.estado = 'activo' AND l.cantidad > 0" +
                     ")")
       List<Producto> findProductosSinStock();

       // ==============================
       // CONSULTAS DE AGREGACIÓN
       // ==============================

       long countByCategoriaId(Integer categoriaId);

       long countByFabricanteId(Integer fabricanteId);

       long countByEsGenerico(boolean esGenerico);

       // ==============================
       // CONSULTAS PARA REPORTES
       // ==============================

       List<Producto> findAllByOrderByNombreAsc();

       @Query("SELECT p, SUM(dv.cantidad) as totalVendido FROM Producto p " +
                     "JOIN p.lotes l JOIN l.detalleVentas dv " +
                     "GROUP BY p.id ORDER BY totalVendido DESC")
       List<Object[]> findProductosMasVendidos();

       @Query("SELECT COALESCE(AVG(l.precioVenta), 0) FROM Lote l WHERE l.producto.id = :productoId AND l.estado = 'activo'")
       BigDecimal avgPrecioVentaByProductoId(@Param("productoId") Integer productoId);

       boolean existsByCodigoBarras(String codigoBarras);

       // ==============================
       // CONSULTA PARA REPOSICIÓN SUGERIDA (RF18)
       // ==============================

       /**
        * Suma las unidades vendidas de un producto desde una fecha (solo ventas
        * completadas).
        * Se usa en la reposición sugerida para calcular el promedio diario de ventas.
        */
       @Query("SELECT COALESCE(SUM(dv.cantidad), 0) FROM DetalleVenta dv " +
                     "WHERE dv.lote.producto.id = :productoId " +
                     "AND dv.venta.fecha >= :desde AND dv.venta.estado = 'completada'")
       Long sumVentasDesde(@Param("productoId") Integer productoId,
                     @Param("desde") LocalDateTime desde);

}