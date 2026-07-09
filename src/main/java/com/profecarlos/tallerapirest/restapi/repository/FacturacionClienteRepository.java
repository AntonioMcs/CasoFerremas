package com.profecarlos.tallerapirest.restapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.profecarlos.tallerapirest.restapi.model.FacturacionCliente;
import com.profecarlos.tallerapirest.restapi.model.Cliente;

@Repository
public interface FacturacionClienteRepository extends JpaRepository<FacturacionCliente, Integer> {

    Optional<FacturacionCliente> findByCliente(Cliente cliente);
}
