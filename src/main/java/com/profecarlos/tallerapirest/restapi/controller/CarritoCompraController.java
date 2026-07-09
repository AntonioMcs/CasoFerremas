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
import com.profecarlos.tallerapirest.restapi.model.Cliente;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.repository.CarritoCompraRepository;
import com.profecarlos.tallerapirest.restapi.repository.ClienteRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;

@RestController
@RequestMapping("/api/v1/carritos-compras")
public class CarritoCompraController {

    private final CarritoCompraRepository carritoCompraRepository;
    private final ClienteRepository clienteRepository;
    private final ProductRepository productRepository;

    public CarritoCompraController(CarritoCompraRepository carritoCompraRepository, ClienteRepository clienteRepository,
            ProductRepository productRepository) {
        this.carritoCompraRepository = carritoCompraRepository;
        this.clienteRepository = clienteRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<CarritoCompra>> listarTodos() {
        return ResponseEntity.ok(carritoCompraRepository.findAll());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CarritoCompra>> listarPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(carritoCompraRepository.findByClienteId(clienteId));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CarritoCompraDTO dto) {
        CarritoCompra carritoCompra = new CarritoCompra();
        ResponseEntity<?> resultado = aplicarDatos(carritoCompra, dto);
        if (resultado != null) {
            return resultado;
        }
        return new ResponseEntity<>(carritoCompraRepository.save(carritoCompra), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody CarritoCompraDTO dto) {
        return carritoCompraRepository.findById(id).map(existing -> {
            ResponseEntity<?> resultado = aplicarDatos(existing, dto);
            if (resultado != null) {
                return resultado;
            }
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

    private ResponseEntity<?> aplicarDatos(CarritoCompra carritoCompra, CarritoCompraDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId()).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente no encontrado");
        }

        Product producto = productRepository.findById(dto.getProductoId()).orElse(null);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Producto no encontrado");
        }

        BigDecimal descuento = dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO;
        BigDecimal precioUnitario = dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio();

        carritoCompra.setCliente(cliente);
        carritoCompra.setProducto(producto);
        carritoCompra.setCantidad(dto.getCantidad());
        carritoCompra.setPrecioUnitario(precioUnitario);
        carritoCompra.setDescuento(descuento);
        carritoCompra.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal()
                : precioUnitario.multiply(BigDecimal.valueOf(dto.getCantidad())).subtract(descuento));
        carritoCompra.setFechaAgregado(dto.getFechaAgregado());
        return null;
    }
}
