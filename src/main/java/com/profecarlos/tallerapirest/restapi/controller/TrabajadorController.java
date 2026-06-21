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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/trabajadores")
public class TrabajadorController {

    private final TrabajadorRepository trabajadorRepository;

    public TrabajadorController(TrabajadorRepository trabajadorRepository) {
        this.trabajadorRepository = trabajadorRepository;
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
    public ResponseEntity<Trabajador> crear(@Valid @RequestBody TrabajadorDTO dto) {
        Trabajador trabajador = new Trabajador(null, dto.getNombre(), dto.getEmail(), dto.getContrasena(), dto.getRol());
        trabajador.setActivo(dto.getActivo() == null ? true : dto.getActivo());
        return new ResponseEntity<>(trabajadorRepository.save(trabajador), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trabajador> actualizar(@PathVariable Integer id, @Valid @RequestBody TrabajadorDTO dto) {
        return trabajadorRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(dto.getNombre());
                    existing.setEmail(dto.getEmail());
                    existing.setContrasena(dto.getContrasena());
                    existing.setRol(dto.getRol());
                    existing.setActivo(dto.getActivo() == null ? existing.getActivo() : dto.getActivo());
                    return ResponseEntity.ok(trabajadorRepository.save(existing));
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
}
