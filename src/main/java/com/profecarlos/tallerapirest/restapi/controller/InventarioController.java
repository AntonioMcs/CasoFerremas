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
import com.profecarlos.tallerapirest.restapi.model.Proveedor;
import com.profecarlos.tallerapirest.restapi.repository.InventarioRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProveedorRepository;
import com.profecarlos.tallerapirest.restapi.dto.InventarioDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/inventarios")
public class InventarioController {

    private final InventarioRepository inventarioRepository;
    private final ProductRepository productRepository;
    private final ProveedorRepository proveedorRepository;

    public InventarioController(InventarioRepository inventarioRepository, ProductRepository productRepository, ProveedorRepository proveedorRepository) {
        this.inventarioRepository = inventarioRepository;
        this.productRepository = productRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            List<Inventario> inventarios = inventarioRepository.findAll();
            if (inventarios.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⚠️ No hay inventarios registrados");
            }
            return ResponseEntity.ok(inventarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al obtener inventarios: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de inventario inválido");
            }
            return inventarioRepository.findById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("⚠️ Inventario no encontrado con ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al buscar inventario: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody InventarioDTO inventarioDTO) {
        try {
            if (inventarioDTO.getProductoId() == null || inventarioDTO.getProductoId() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de producto inválido");
            }

            Product producto = productRepository.findById(inventarioDTO.getProductoId()).orElse(null);
            if (producto == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ Producto no encontrado con ID: " + inventarioDTO.getProductoId());
            }

            if (inventarioDTO.getStockActual() == null || inventarioDTO.getStockActual() < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ El stock actual no puede ser negativo");
            }

            Proveedor proveedor = null;
            if (inventarioDTO.getProveedorId() != null) {
                proveedor = proveedorRepository.findById(inventarioDTO.getProveedorId()).orElse(null);
                if (proveedor == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("⚠️ Proveedor no encontrado con ID: " + inventarioDTO.getProveedorId());
                }
            }

            Inventario inventario = new Inventario(null, producto, inventarioDTO.getStockActual(),
                    inventarioDTO.getStockMinimo(), inventarioDTO.getUbicacionBodega());
            inventario.setProveedor(proveedor);
            
            Inventario guardado = inventarioRepository.save(inventario);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("✓ Inventario creado exitosamente con ID: " + guardado.getIdInventario());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al crear inventario: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody InventarioDTO inventarioDTO) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de inventario inválido");
            }

            return inventarioRepository.findById(id).map(existing -> {
                try {
                    if (inventarioDTO.getStockActual() != null) {
                        if (inventarioDTO.getStockActual() < 0) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("⚠️ El stock actual no puede ser negativo");
                        }
                        existing.setStockActual(inventarioDTO.getStockActual());
                    }
                    if (inventarioDTO.getStockMinimo() != null) {
                        existing.setStockMinimo(inventarioDTO.getStockMinimo());
                    }
                    if (inventarioDTO.getUbicacionBodega() != null) {
                        existing.setUbicacionBodega(inventarioDTO.getUbicacionBodega());
                    }
                    if (inventarioDTO.getProveedorId() != null) {
                        Proveedor proveedor = proveedorRepository.findById(inventarioDTO.getProveedorId()).orElse(null);
                        if (proveedor == null) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("⚠️ Proveedor no encontrado con ID: " + inventarioDTO.getProveedorId());
                        }
                        existing.setProveedor(proveedor);
                    }

                    Inventario actualizado = inventarioRepository.save(existing);
                    return ResponseEntity.ok("✓ Inventario actualizado exitosamente | ID: " + actualizado.getIdInventario());
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("⚠️ Error al actualizar inventario: " + e.getMessage());
                }
            }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("⚠️ Inventario no encontrado con ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error del servidor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de inventario inválido");
            }

            if (!inventarioRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⚠️ Inventario no encontrado con ID: " + id);
            }

            inventarioRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("✓ Inventario eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al eliminar inventario: " + e.getMessage());
        }
    }
}
