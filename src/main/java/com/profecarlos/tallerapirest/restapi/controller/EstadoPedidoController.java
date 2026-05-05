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

import com.profecarlos.tallerapirest.restapi.dto.EstadoPedidoDTO;
import com.profecarlos.tallerapirest.restapi.model.EstadoPedido;
import com.profecarlos.tallerapirest.restapi.repository.EstadoPedidoRepository;

@RestController
@RequestMapping("/api/v1/estados-pedido")
public class EstadoPedidoController {

    private final EstadoPedidoRepository estadoPedidoRepository;

    public EstadoPedidoController(EstadoPedidoRepository estadoPedidoRepository) {
        this.estadoPedidoRepository = estadoPedidoRepository;
    }

    @GetMapping
    public ResponseEntity<List<EstadoPedido>> listarTodos() {
        return ResponseEntity.ok(estadoPedidoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadoPedido> buscarPorId(@PathVariable Integer id) {
        return estadoPedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EstadoPedido> crear(@RequestBody EstadoPedidoDTO dto) {
        EstadoPedido estadoPedido = new EstadoPedido();
        estadoPedido.setNombreEstado(dto.getNombreEstado());
        return new ResponseEntity<>(estadoPedidoRepository.save(estadoPedido), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadoPedido> actualizar(@PathVariable Integer id, @RequestBody EstadoPedidoDTO dto) {
        return estadoPedidoRepository.findById(id)
                .map(existing -> {
                    existing.setNombreEstado(dto.getNombreEstado());
                    return ResponseEntity.ok(estadoPedidoRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!estadoPedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        estadoPedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}