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

import com.profecarlos.tallerapirest.restapi.model.Inventario;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.repository.InventarioRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.dto.InventarioDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/inventarios")
public class InventarioController {

    private final InventarioRepository inventarioRepository;
    private final ProductRepository productRepository;

    public InventarioController(InventarioRepository inventarioRepository, ProductRepository productRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<Inventario>> listarTodos() {
        return ResponseEntity.ok(inventarioRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> buscarPorId(@PathVariable Integer id) {
        return inventarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Inventario> crear(@Valid @RequestBody InventarioDTO inventarioDTO) {
        Product producto = productRepository.findById(inventarioDTO.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        Inventario inventario = new Inventario(null, producto, inventarioDTO.getStockActual(),
                inventarioDTO.getStockMinimo(), inventarioDTO.getUbicacionBodega());
        return new ResponseEntity<>(inventarioRepository.save(inventario), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizar(@PathVariable Integer id, @Valid @RequestBody InventarioDTO inventarioDTO) {
        return inventarioRepository.findById(id)
                .map(existing -> {
                    existing.setStockActual(inventarioDTO.getStockActual());
                    existing.setStockMinimo(inventarioDTO.getStockMinimo());
                    existing.setUbicacionBodega(inventarioDTO.getUbicacionBodega());
                    return ResponseEntity.ok(inventarioRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!inventarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        inventarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
