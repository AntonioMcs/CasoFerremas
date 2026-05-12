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

import com.profecarlos.tallerapirest.restapi.model.FacturacionCliente;
import com.profecarlos.tallerapirest.restapi.model.Usuario;
import com.profecarlos.tallerapirest.restapi.repository.FacturacionClienteRepository;
import com.profecarlos.tallerapirest.restapi.repository.UserRepository;
import com.profecarlos.tallerapirest.restapi.dto.FacturacionClienteDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/facturacion-clientes")
public class FacturacionClienteController {

    private final FacturacionClienteRepository facturacionClienteRepository;
    private final UserRepository userRepository;

    public FacturacionClienteController(FacturacionClienteRepository facturacionClienteRepository, UserRepository userRepository) {
        this.facturacionClienteRepository = facturacionClienteRepository;
        this.userRepository = userRepository;
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
    public ResponseEntity<FacturacionCliente> crear(@Valid @RequestBody FacturacionClienteDTO facturacionDTO) {
        Usuario usuario = userRepository.findById(facturacionDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        FacturacionCliente facturacion = new FacturacionCliente(null, usuario, facturacionDTO.getRut(),
                facturacionDTO.getNombre(), facturacionDTO.getApellidos(), facturacionDTO.getTelefono(),
                facturacionDTO.getDireccion(), facturacionDTO.getComuna());
        return new ResponseEntity<>(facturacionClienteRepository.save(facturacion), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacturacionCliente> actualizar(@PathVariable Integer id, @Valid @RequestBody FacturacionClienteDTO facturacionDTO) {
        return facturacionClienteRepository.findById(id)
                .map(existing -> {
                    existing.setRut(facturacionDTO.getRut());
                    existing.setNombre(facturacionDTO.getNombre());
                    existing.setApellidos(facturacionDTO.getApellidos());
                    existing.setTelefono(facturacionDTO.getTelefono());
                    existing.setDireccion(facturacionDTO.getDireccion());
                    existing.setComuna(facturacionDTO.getComuna());
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
