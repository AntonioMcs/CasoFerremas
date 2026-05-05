package com.profecarlos.tallerapirest.restapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profecarlos.tallerapirest.restapi.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuarioId(Integer usuarioId);

    List<Pedido> findByEstadoPedidoIdEstado(Integer estadoId);
}


