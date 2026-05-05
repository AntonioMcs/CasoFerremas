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

import com.profecarlos.tallerapirest.restapi.dto.PagoDTO;
import com.profecarlos.tallerapirest.restapi.model.Pago;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.repository.PagoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;

    public PagoController(PagoRepository pagoRepository, PedidoRepository pedidoRepository) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Pago>> listarTodos() {
        return ResponseEntity.ok(pagoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> buscarPorId(@PathVariable Integer id) {
        return pagoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> buscarPorPedido(@PathVariable Integer pedidoId) {
        return pagoRepository.findByPedidoIdPedido(pedidoId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody PagoDTO dto) {
        if (pagoRepository.existsByPedidoIdPedido(dto.getPedidoId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ya existe un pago para este pedido");
        }

        Pedido pedido = pedidoRepository.findById(dto.getPedidoId()).orElse(null);
        if (pedido == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pedido no encontrado");
        }

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstadoPago(dto.getEstadoPago());
        pago.setFechaPago(dto.getFechaPago());

        return new ResponseEntity<>(pagoRepository.save(pago), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody PagoDTO dto) {
        return pagoRepository.findById(id).map(existing -> {
            Pedido pedido = pedidoRepository.findById(dto.getPedidoId()).orElse(null);
            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pedido no encontrado");
            }

            existing.setPedido(pedido);
            existing.setMonto(dto.getMonto());
            existing.setMetodoPago(dto.getMetodoPago());
            existing.setEstadoPago(dto.getEstadoPago());
            existing.setFechaPago(dto.getFechaPago() != null ? dto.getFechaPago() : existing.getFechaPago());

            return ResponseEntity.ok(pagoRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!pagoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pagoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}