package com.profecarlos.tallerapirest.restapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.model.Categoria;

public interface ProductRepository extends JpaRepository<Product, Integer>{
    
    // Buscar productos por categoría
    // SELECT * FROM productos WHERE id_categoria = :categoria
    List<Product> findByCategoria(Categoria categoria);

    // Buscar productos por nombre (búsqueda parcial)
    List<Product> findByNombreProductoContainingIgnoreCase(String nombreProducto);
}

