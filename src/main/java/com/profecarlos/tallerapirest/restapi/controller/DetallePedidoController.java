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

import com.profecarlos.tallerapirest.restapi.dto.DetallePedidoDTO;
import com.profecarlos.tallerapirest.restapi.model.DetallePedido;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.repository.DetallePedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;

@RestController
@RequestMapping("/api/v1/detalles-pedido")
public class DetallePedidoController {

    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductRepository productRepository;

    public DetallePedidoController(DetallePedidoRepository detallePedidoRepository, PedidoRepository pedidoRepository,
            ProductRepository productRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<List<DetallePedido>> listarTodos() {
        return ResponseEntity.ok(detallePedidoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedido> buscarPorId(@PathVariable Integer id) {
        return detallePedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<DetallePedido>> listarPorPedido(@PathVariable Integer pedidoId) {
        return ResponseEntity.ok(detallePedidoRepository.findByPedidoIdPedido(pedidoId));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody DetallePedidoDTO dto) {
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId()).orElse(null);
        if (pedido == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pedido no encontrado");
        }

        Product producto = productRepository.findById(dto.getProductoId()).orElse(null);
        if (producto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Producto no encontrado");
        }

        DetallePedido detallePedido = new DetallePedido();
        detallePedido.setPedido(pedido);
        detallePedido.setProducto(producto);
        detallePedido.setCantidad(dto.getCantidad());
        detallePedido.setPrecioUnitario(dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio());
        detallePedido.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal()
                : detallePedido.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.getCantidad())));

        return new ResponseEntity<>(detallePedidoRepository.save(detallePedido), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody DetallePedidoDTO dto) {
        return detallePedidoRepository.findById(id).map(existing -> {
            Pedido pedido = pedidoRepository.findById(dto.getPedidoId()).orElse(null);
            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pedido no encontrado");
            }

            Product producto = productRepository.findById(dto.getProductoId()).orElse(null);
            if (producto == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Producto no encontrado");
            }

            existing.setPedido(pedido);
            existing.setProducto(producto);
            existing.setCantidad(dto.getCantidad());
            existing.setPrecioUnitario(dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio());
            existing.setSubtotal(dto.getSubtotal() != null ? dto.getSubtotal()
                    : existing.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.getCantidad())));

            return ResponseEntity.ok(detallePedidoRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!detallePedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        detallePedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}