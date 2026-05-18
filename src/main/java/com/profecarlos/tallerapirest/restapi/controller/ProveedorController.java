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

import com.profecarlos.tallerapirest.restapi.dto.ProveedorDTO;
import com.profecarlos.tallerapirest.restapi.model.Proveedor;
import com.profecarlos.tallerapirest.restapi.repository.ProveedorRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/proveedores")
public class ProveedorController {

    private final ProveedorRepository proveedorRepository;

    public ProveedorController(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            List<Proveedor> proveedores = proveedorRepository.findAll();
            if (proveedores.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⚠️ No hay proveedores registrados");
            }
            return ResponseEntity.ok(proveedores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al obtener proveedores: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID del proveedor inválido");
            }
            return proveedorRepository.findById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("⚠️ Proveedor no encontrado con ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al buscar proveedor: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ProveedorDTO dto) {
        try {
            if (dto.getNombreProveedor() == null || dto.getNombreProveedor().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ El nombre del proveedor es requerido");
            }

            if (proveedorRepository.findByNombreProveedor(dto.getNombreProveedor()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("⚠️ Ya existe un proveedor con el nombre: " + dto.getNombreProveedor());
            }

            Proveedor proveedor = new Proveedor();
            proveedor.setNombreProveedor(dto.getNombreProveedor());
            proveedor.setDatosContacto(dto.getDatosContacto());
            proveedor.setEmail(dto.getEmail());
            proveedor.setTelefono(dto.getTelefono());
            proveedor.setDireccion(dto.getDireccion());

            Proveedor guardado = proveedorRepository.save(proveedor);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al crear proveedor: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody ProveedorDTO dto) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID del proveedor inválido");
            }

            return proveedorRepository.findById(id).map(proveedor -> {
                try {
                    if (dto.getNombreProveedor() != null && !dto.getNombreProveedor().trim().isEmpty()) {
                        proveedor.setNombreProveedor(dto.getNombreProveedor());
                    }
                    if (dto.getDatosContacto() != null && !dto.getDatosContacto().trim().isEmpty()) {
                        proveedor.setDatosContacto(dto.getDatosContacto());
                    }
                    if (dto.getEmail() != null) {
                        proveedor.setEmail(dto.getEmail());
                    }
                    if (dto.getTelefono() != null) {
                        proveedor.setTelefono(dto.getTelefono());
                    }
                    if (dto.getDireccion() != null) {
                        proveedor.setDireccion(dto.getDireccion());
                    }

                    Proveedor actualizado = proveedorRepository.save(proveedor);
                    return ResponseEntity.ok(actualizado);
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("⚠️ Error al actualizar proveedor: " + e.getMessage());
                }
            }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("⚠️ Proveedor no encontrado con ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error del servidor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID del proveedor inválido");
            }

            if (!proveedorRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⚠️ Proveedor no encontrado con ID: " + id);
            }

            proveedorRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("✓ Proveedor eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al eliminar proveedor: " + e.getMessage());
        }
    }
}
