package com.profecarlos.tallerapirest.restapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.profecarlos.tallerapirest.restapi.dto.ProductoImagenDTO;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.model.ProductoImagen;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductoImagenRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/productos-imagenes")
public class ProductoImagenController {

    private final ProductoImagenRepository productoImagenRepository;
    private final ProductRepository productRepository;

    public ProductoImagenController(ProductoImagenRepository productoImagenRepository, ProductRepository productRepository) {
        this.productoImagenRepository = productoImagenRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<ProductoImagen>> listarTodos() {
        return ResponseEntity.ok(productoImagenRepository.findAll());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ProductoImagen>> listarPorProducto(@PathVariable Integer productoId) {
        return ResponseEntity.ok(productoImagenRepository.findByProductoId(productoId));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ProductoImagenDTO dto) {
        Product producto = productRepository.findById(dto.getProductoId()).orElse(null);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Producto no encontrado");
        }
        ProductoImagen imagen = new ProductoImagen();
        imagen.setProducto(producto);
        aplicarDatos(imagen, dto);
        return new ResponseEntity<>(productoImagenRepository.save(imagen), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody ProductoImagenDTO dto) {
        return productoImagenRepository.findById(id)
                .<ResponseEntity<?>>map(existing -> {
                    Product producto = productRepository.findById(dto.getProductoId()).orElse(null);
                    if (producto == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Producto no encontrado");
                    }
                    existing.setProducto(producto);
                    aplicarDatos(existing, dto);
                    return ResponseEntity.ok(productoImagenRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!productoImagenRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productoImagenRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void aplicarDatos(ProductoImagen imagen, ProductoImagenDTO dto) {
        imagen.setUrlImagen(dto.getUrlImagen());
        imagen.setTextoAlternativo(dto.getTextoAlternativo());
        imagen.setPrincipal(dto.getPrincipal() == null ? false : dto.getPrincipal());
    }
}
