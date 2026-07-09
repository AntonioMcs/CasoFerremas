package com.profecarlos.tallerapirest.restapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.profecarlos.tallerapirest.restapi.model.Proveedor;

import java.util.Optional;
import java.util.List;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    Optional<Proveedor> findByNombreProveedor(String nombreProveedor);
    List<Proveedor> findByEmailContaining(String email);
    List<Proveedor> findByTelefonoContaining(String telefono);
}
