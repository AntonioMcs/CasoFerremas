package com.profecarlos.tallerapirest.restapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profecarlos.tallerapirest.restapi.model.CarritoCompra;

public interface CarritoCompraRepository extends JpaRepository<CarritoCompra, Integer> {

    List<CarritoCompra> findByClienteId(Integer clienteId);
}
