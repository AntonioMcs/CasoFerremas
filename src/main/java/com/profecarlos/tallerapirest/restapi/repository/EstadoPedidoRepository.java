package com.profecarlos.tallerapirest.restapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profecarlos.tallerapirest.restapi.model.EstadoPedido;

public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Integer> {

    Optional<EstadoPedido> findByNombreEstado(String nombreEstado);

    Optional<EstadoPedido> findByNombreEstadoIgnoreCase(String nombreEstado);
}