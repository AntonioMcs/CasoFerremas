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

import com.profecarlos.tallerapirest.restapi.dto.FacturacionClienteDTO;
import com.profecarlos.tallerapirest.restapi.model.Cliente;
import com.profecarlos.tallerapirest.restapi.model.FacturacionCliente;
import com.profecarlos.tallerapirest.restapi.repository.ClienteRepository;
import com.profecarlos.tallerapirest.restapi.repository.FacturacionClienteRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/facturacion-clientes")
public class FacturacionClienteController {

    private final FacturacionClienteRepository facturacionClienteRepository;
    private final ClienteRepository clienteRepository;

    public FacturacionClienteController(FacturacionClienteRepository facturacionClienteRepository,
            ClienteRepository clienteRepository) {
        this.facturacionClienteRepository = facturacionClienteRepository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public ResponseEntity<List<FacturacionCliente>> listarTodos() {
        return ResponseEntity.ok(facturacionClienteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturacionCliente> buscarPorId(@PathVariable Integer id) {
        return facturacionClienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody FacturacionClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId()).orElse(null);
        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente no encontrado");
        }

        FacturacionCliente facturacion = new FacturacionCliente(null, cliente, dto.getRut(), dto.getNombre(),
                dto.getApellidos(), dto.getTelefono(), dto.getDireccion(), dto.getComuna());
        return new ResponseEntity<>(facturacionClienteRepository.save(facturacion), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody FacturacionClienteDTO dto) {
        return facturacionClienteRepository.findById(id)
                .<ResponseEntity<?>>map(existing -> {
                    Cliente cliente = clienteRepository.findById(dto.getClienteId()).orElse(null);
                    if (cliente == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cliente no encontrado");
                    }
                    existing.setCliente(cliente);
                    existing.setRut(dto.getRut());
                    existing.setNombre(dto.getNombre());
                    existing.setApellidos(dto.getApellidos());
                    existing.setTelefono(dto.getTelefono());
                    existing.setDireccion(dto.getDireccion());
                    existing.setComuna(dto.getComuna());
                    return ResponseEntity.ok(facturacionClienteRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!facturacionClienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        facturacionClienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
