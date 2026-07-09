package com.profecarlos.tallerapirest.restapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.profecarlos.tallerapirest.restapi.model.ProductoImagen;

@Repository
public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Integer> {

    List<ProductoImagen> findByProductoId(Integer productoId);
}
