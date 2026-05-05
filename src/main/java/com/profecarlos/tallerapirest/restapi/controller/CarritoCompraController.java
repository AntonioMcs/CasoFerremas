package com.profecarlos.tallerapirest.restapi.controller;

import java.math.BigDecimal;
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

import com.profecarlos.tallerapirest.restapi.dto.CarritoCompraDTO;
import com.profecarlos.tallerapirest.restapi.model.CarritoCompra;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.model.Usuario;
import com.profecarlos.tallerapirest.restapi.repository.CarritoCompraRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/carritos-compras")
public class CarritoCompraController {

    private final CarritoCompraRepository carritoCompraRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CarritoCompraController(CarritoCompraRepository carritoCompraRepository, UserRepository userRepository,
            ProductRepository productRepository) {
        this.carritoCompraRepository = carritoCompraRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<CarritoCompra>> listarTodos() {
        return ResponseEntity.ok(carritoCompraRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoCompra> buscarPorId(@PathVariable Integer id) {
        return carritoCompraRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CarritoCompraDTO dto) {
        Usuario usuario = userRepository.findById(dto.getUsuarioId()).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }

        Product producto = productRepository.findById(dto.getProductoId()).orElse(null);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Producto no encontrado");
        }

        CarritoCompra carritoCompra = new CarritoCompra();
        carritoCompra.setUsuario(usuario);
        carritoCompra.setProducto(producto);
        carritoCompra.setCantidad(dto.getCantidad());
        carritoCompra.setPrecioUnitario(dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio());
        carritoCompra.setDescuento(dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO);
        carritoCompra.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal()
                : carritoCompra.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.getCantidad()))
                        .subtract(carritoCompra.getDescuento()));
        carritoCompra.setFechaAgregado(dto.getFechaAgregado());

        return new ResponseEntity<>(carritoCompraRepository.save(carritoCompra), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody CarritoCompraDTO dto) {
        return carritoCompraRepository.findById(id).map(existing -> {
            Usuario usuario = userRepository.findById(dto.getUsuarioId()).orElse(null);
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
            }

            Product producto = productRepository.findById(dto.getProductoId()).orElse(null);
            if (producto == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Producto no encontrado");
            }

            existing.setUsuario(usuario);
            existing.setProducto(producto);
            existing.setCantidad(dto.getCantidad());
            existing.setPrecioUnitario(dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio());
            existing.setDescuento(dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO);
            existing.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal()
                    : existing.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.getCantidad()))
                            .subtract(existing.getDescuento()));
            existing.setFechaAgregado(dto.getFechaAgregado() != null ? dto.getFechaAgregado() : existing.getFechaAgregado());

            return ResponseEntity.ok(carritoCompraRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!carritoCompraRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        carritoCompraRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}