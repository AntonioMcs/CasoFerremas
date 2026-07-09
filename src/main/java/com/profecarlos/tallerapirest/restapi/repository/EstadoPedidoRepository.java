package com.profecarlos.tallerapirest.restapi.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profecarlos.tallerapirest.restapi.model.EstadoPedido;

public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Integer> {

    Optional<EstadoPedido> findByNombreEstado(String nombreEstado);

    Optional<EstadoPedido> findByNombreEstadoIgnoreCase(String nombreEstado);

    List<EstadoPedido> findAllByNombreEstadoIgnoreCaseOrderByIdEstadoAsc(String nombreEstado);
}
