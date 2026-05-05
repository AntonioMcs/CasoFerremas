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

import com.profecarlos.tallerapirest.restapi.dto.DespachoDTO;
import com.profecarlos.tallerapirest.restapi.model.Despacho;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.repository.DespachoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;

@RestController
@RequestMapping("/api/v1/despachos")
public class DespachoController {

    private final DespachoRepository despachoRepository;
    private final PedidoRepository pedidoRepository;

    public DespachoController(DespachoRepository despachoRepository, PedidoRepository pedidoRepository) {
        this.despachoRepository = despachoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Despacho>> listarTodos() {
        return ResponseEntity.ok(despachoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Despacho> buscarPorId(@PathVariable Integer id) {
        return despachoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> buscarPorPedido(@PathVariable Integer pedidoId) {
        return despachoRepository.findByPedidoIdPedido(pedidoId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody DespachoDTO dto) {
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId()).orElse(null);
        if (pedido == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pedido no encontrado");
        }

        Despacho despacho = new Despacho();
        despacho.setPedido(pedido);
        despacho.setDireccionEntrega(dto.getDireccionEntrega());
        despacho.setFechaEnvio(dto.getFechaEnvio());
        despacho.setFechaEntrega(dto.getFechaEntrega());
        despacho.setEstadoDespacho(dto.getEstadoDespacho());

        return new ResponseEntity<>(despachoRepository.save(despacho), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody DespachoDTO dto) {
        return despachoRepository.findById(id).map(existing -> {
            Pedido pedido = pedidoRepository.findById(dto.getPedidoId()).orElse(null);
            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Pedido no encontrado");
            }

            existing.setPedido(pedido);
            existing.setDireccionEntrega(dto.getDireccionEntrega());
            existing.setFechaEnvio(dto.getFechaEnvio());
            existing.setFechaEntrega(dto.getFechaEntrega());
            existing.setEstadoDespacho(dto.getEstadoDespacho());

            return ResponseEntity.ok(despachoRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!despachoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        despachoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}