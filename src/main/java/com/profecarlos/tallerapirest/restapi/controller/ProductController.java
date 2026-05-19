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
import com.profecarlos.tallerapirest.restapi.model.Proveedor;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.CategoriaRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProveedorRepository;
import com.profecarlos.tallerapirest.restapi.dto.ProductDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProveedorRepository proveedorRepository;

    public ProductController(ProductRepository productRepository, CategoriaRepository categoriaRepository, ProveedorRepository proveedorRepository) {
        this.productRepository = productRepository;
        this.categoriaRepository = categoriaRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            List<Product> productos = productRepository.findAll();
            if (productos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⚠️ No hay productos registrados");
            }
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al obtener productos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de producto inválido");
            }
            return productRepository.findById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("⚠️ Producto no encontrado con ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al buscar producto: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ProductDTO productDTO) {
        try {
            if (productDTO.getNombreProducto() == null || productDTO.getNombreProducto().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ El nombre del producto es requerido");
            }

            if (productDTO.getPrecio() == null || productDTO.getPrecio().signum() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ El precio debe ser mayor a 0");
            }

            Categoria categoria = null;
            if (productDTO.getCategoriaId() != null) {
                categoria = categoriaRepository.findById(productDTO.getCategoriaId()).orElse(null);
                if (categoria == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("⚠️ Categoría no encontrada con ID: " + productDTO.getCategoriaId());
                }
            }

            Proveedor proveedor = null;
            if (productDTO.getProveedorId() != null) {
                proveedor = proveedorRepository.findById(productDTO.getProveedorId()).orElse(null);
                if (proveedor == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("⚠️ Proveedor no encontrado con ID: " + productDTO.getProveedorId());
                }
            }

            Product product = new Product(null, productDTO.getNombreProducto(), productDTO.getMarca(), 
                    productDTO.getDescripcion(), productDTO.getPrecio(), productDTO.getUnidadMedida());
            product.setCodigoSku(productDTO.getCodigoSku());
            product.setCategoria(categoria);
            product.setProveedor(proveedor);
            
            Product guardado = productRepository.save(product);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("✓ Producto creado exitosamente con ID: " + guardado.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al crear producto: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody ProductDTO productDTO) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de producto inválido");
            }

            return productRepository.findById(id).map(existing -> {
                try {
                    if (productDTO.getNombreProducto() != null && !productDTO.getNombreProducto().trim().isEmpty()) {
                        existing.setNombreProducto(productDTO.getNombreProducto());
                    }
                    if (productDTO.getMarca() != null) {
                        existing.setMarca(productDTO.getMarca());
                    }
                    if (productDTO.getPrecio() != null && productDTO.getPrecio().signum() > 0) {
                        existing.setPrecio(productDTO.getPrecio());
                    } else if (productDTO.getPrecio() != null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("⚠️ El precio debe ser mayor a 0");
                    }
                    if (productDTO.getDescripcion() != null) {
                        existing.setDescripcion(productDTO.getDescripcion());
                    }
                    if (productDTO.getUnidadMedida() != null) {
                        existing.setUnidadMedida(productDTO.getUnidadMedida());
                    }
                    if (productDTO.getCodigoSku() != null) {
                        existing.setCodigoSku(productDTO.getCodigoSku());
                    }
                    if (productDTO.getCategoriaId() != null) {
                        Categoria categoria = categoriaRepository.findById(productDTO.getCategoriaId()).orElse(null);
                        if (categoria == null) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("⚠️ Categoría no encontrada con ID: " + productDTO.getCategoriaId());
                        }
                        existing.setCategoria(categoria);
                    }
                    if (productDTO.getProveedorId() != null) {
                        Proveedor proveedor = proveedorRepository.findById(productDTO.getProveedorId()).orElse(null);
                        if (proveedor == null) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("⚠️ Proveedor no encontrado con ID: " + productDTO.getProveedorId());
                        }
                        existing.setProveedor(proveedor);
                    }
                    
                    Product actualizado = productRepository.save(existing);
                    return ResponseEntity.ok("✓ Producto actualizado exitosamente | ID: " + actualizado.getId());
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("⚠️ Error al actualizar producto: " + e.getMessage());
                }
            }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("⚠️ Producto no encontrado con ID: " + id));
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
                        .body("⚠️ ID de producto inválido");
            }

            if (!productRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⚠️ Producto no encontrado con ID: " + id);
            }

            productRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("✓ Producto eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al eliminar producto: " + e.getMessage());
        }
    }
}