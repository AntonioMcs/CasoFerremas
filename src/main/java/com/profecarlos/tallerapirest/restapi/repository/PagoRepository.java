package com.profecarlos.tallerapirest.restapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profecarlos.tallerapirest.restapi.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    Optional<Pago> findByPedidoIdPedido(Integer pedidoId);

    boolean existsByPedidoIdPedido(Integer pedidoId);
}