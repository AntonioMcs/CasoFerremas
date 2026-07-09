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

import com.profecarlos.tallerapirest.restapi.dto.TrabajadorDTO;
import com.profecarlos.tallerapirest.restapi.model.Trabajador;
import com.profecarlos.tallerapirest.restapi.repository.TrabajadorRepository;
import com.profecarlos.tallerapirest.restapi.service.AuditLogService;
import com.profecarlos.tallerapirest.restapi.service.PasswordPolicyService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/trabajadores")
public class TrabajadorController {

    private final TrabajadorRepository trabajadorRepository;
    private final PasswordPolicyService passwordPolicyService;
    private final AuditLogService auditLogService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public TrabajadorController(TrabajadorRepository trabajadorRepository, PasswordPolicyService passwordPolicyService,
            AuditLogService auditLogService) {
        this.trabajadorRepository = trabajadorRepository;
        this.passwordPolicyService = passwordPolicyService;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<Trabajador>> listarTodos() {
        return ResponseEntity.ok(trabajadorRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trabajador> buscarPorId(@PathVariable Integer id) {
        return trabajadorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<Trabajador>> listarPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(trabajadorRepository.findByRolIgnoreCase(rol));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody TrabajadorDTO dto) {
        if (trabajadorRepository.findByEmail(dto.getEmail().trim()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya está registrado");
        }
        passwordPolicyService.validate(dto.getContrasena());
        Trabajador trabajador = new Trabajador(null, dto.getNombre(), dto.getEmail().trim(), passwordEncoder.encode(dto.getContrasena()), normalizarRol(dto.getRol()));
        trabajador.setActivo(dto.getActivo() == null ? true : dto.getActivo());
        Trabajador guardado = trabajadorRepository.save(trabajador);
        auditLogService.registrar("TRABAJADOR", guardado.getId(), "CREAR", "Trabajador creado", guardado.getId(), "admin", guardado.getNombre());
        return new ResponseEntity<>(guardado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody TrabajadorDTO dto) {
        return trabajadorRepository.findById(id)
                .map(existing -> {
                    if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
                        passwordPolicyService.validate(dto.getContrasena());
                        existing.setContrasena(passwordEncoder.encode(dto.getContrasena()));
                    }
                    existing.setNombre(dto.getNombre());
                    existing.setEmail(dto.getEmail().trim());
                    existing.setRol(normalizarRol(dto.getRol()));
                    existing.setActivo(dto.getActivo() == null ? existing.getActivo() : dto.getActivo());
                    Trabajador actualizado = trabajadorRepository.save(existing);
                    auditLogService.registrar("TRABAJADOR", actualizado.getId(), "ACTUALIZAR", "Trabajador actualizado", actualizado.getId(), "admin", actualizado.getNombre());
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!trabajadorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        trabajadorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private String normalizarRol(String rol) {
        return rol == null ? null : rol.trim().toUpperCase();
    }
}
