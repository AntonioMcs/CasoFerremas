package com.profecarlos.tallerapirest.restapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profecarlos.tallerapirest.restapi.model.Despacho;

public interface DespachoRepository extends JpaRepository<Despacho, Integer> {

    Optional<Despacho> findByPedidoIdPedido(Integer pedidoId);
}