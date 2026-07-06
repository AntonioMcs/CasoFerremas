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

import com.profecarlos.tallerapirest.restapi.dto.ClienteDTO;
import com.profecarlos.tallerapirest.restapi.model.Cliente;
import com.profecarlos.tallerapirest.restapi.repository.ClienteRepository;
import com.profecarlos.tallerapirest.restapi.service.AuditLogService;
import com.profecarlos.tallerapirest.restapi.service.PasswordPolicyService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final PasswordPolicyService passwordPolicyService;
    private final AuditLogService auditLogService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ClienteController(ClienteRepository clienteRepository, PasswordPolicyService passwordPolicyService,
            AuditLogService auditLogService) {
        this.clienteRepository = clienteRepository;
        this.passwordPolicyService = passwordPolicyService;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Integer id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ClienteDTO dto) {
        if (clienteRepository.findByEmail(dto.getEmail().trim()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya está registrado");
        }
        passwordPolicyService.validate(dto.getContrasena());
        Cliente cliente = new Cliente(null, dto.getNombre(), dto.getEmail().trim(), passwordEncoder.encode(dto.getContrasena()));
        aplicarDatos(cliente, dto);
        Cliente guardado = clienteRepository.save(cliente);
        auditLogService.registrar("CLIENTE", guardado.getId(), "CREAR", "Cliente creado", guardado.getId(), "cliente", guardado.getNombre());
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody ClienteDTO dto) {
        return clienteRepository.findById(id)
                .map(existing -> {
                    if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
                        passwordPolicyService.validate(dto.getContrasena());
                        existing.setContrasena(passwordEncoder.encode(dto.getContrasena()));
                    }
                    existing.setNombre(dto.getNombre());
                    existing.setEmail(dto.getEmail().trim());
                    aplicarDatos(existing, dto);
                    Cliente actualizado = clienteRepository.save(existing);
                    auditLogService.registrar("CLIENTE", actualizado.getId(), "ACTUALIZAR", "Cliente actualizado", actualizado.getId(), "cliente", actualizado.getNombre());
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        clienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void aplicarDatos(Cliente cliente, ClienteDTO dto) {
        cliente.setRut(dto.getRut());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        cliente.setComuna(dto.getComuna());
    }
}
