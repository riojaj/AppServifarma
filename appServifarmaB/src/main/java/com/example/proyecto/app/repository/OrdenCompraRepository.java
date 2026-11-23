package com.example.proyecto.app.repository;

import com.example.proyecto.app.entity.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Integer> {

    List<OrdenCompra> findAllByOrderByFechaCreacionDesc();

    List<OrdenCompra> findByEstadoOrderByFechaCreacionDesc(OrdenCompra.EstadoOrden estado);
}
