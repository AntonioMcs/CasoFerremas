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

import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.model.Categoria;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.CategoriaRepository;
import com.profecarlos.tallerapirest.restapi.dto.ProductDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductController(ProductRepository productRepository, CategoriaRepository categoriaRepository) {
        this.productRepository = productRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Product>> listarTodos() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> buscarPorId(@PathVariable Integer id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> crear(@Valid @RequestBody ProductDTO productDTO) {
        Categoria categoria = null;
        if (productDTO.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(productDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        }
        Product product = new Product(null, productDTO.getNombreProducto(), productDTO.getMarca(), 
                productDTO.getDescripcion(), productDTO.getPrecio(), productDTO.getUnidadMedida());
        product.setStock(productDTO.getStock());
        product.setCodigoSku(productDTO.getCodigoSku());
        product.setCategoria(categoria);
        return new ResponseEntity<>(productRepository.save(product), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> actualizar(@PathVariable Integer id, @Valid @RequestBody ProductDTO productDTO) {
        return productRepository.findById(id)
                .map(existing -> {
                    existing.setNombreProducto(productDTO.getNombreProducto());
                    existing.setMarca(productDTO.getMarca());
                    existing.setPrecio(productDTO.getPrecio());
                    existing.setDescripcion(productDTO.getDescripcion());
                    existing.setStock(productDTO.getStock());
                    existing.setUnidadMedida(productDTO.getUnidadMedida());
                    existing.setCodigoSku(productDTO.getCodigoSku());
                    if (productDTO.getCategoriaId() != null) {
                        Categoria categoria = categoriaRepository.findById(productDTO.getCategoriaId())
                                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
                        existing.setCategoria(categoria);
                    }
                    return ResponseEntity.ok(productRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}