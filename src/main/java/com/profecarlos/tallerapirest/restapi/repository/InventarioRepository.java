package com.profecarlos.tallerapirest.restapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.profecarlos.tallerapirest.restapi.model.Inventario;
import com.profecarlos.tallerapirest.restapi.model.Product;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    List<Inventario> findByProducto(Product producto);

    List<Inventario> findByProductoId(Integer productoId);

    Optional<Inventario> findFirstByProductoIdAndSucursalIgnoreCase(Integer productoId, String sucursal);
}
