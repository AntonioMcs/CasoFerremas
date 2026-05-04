package com.profecarlos.tallerapirest.restapi.controller;

import com.profecarlos.tallerapirest.restapi.model.Rol;
import com.profecarlos.tallerapirest.restapi.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/roles")
public class RolController {
    @Autowired
    private RolRepository rolRepository;

    // GET /api/roles - Obtener todos los roles
    @GetMapping
    public CollectionModel<EntityModel<Rol>> obtenerTodosLosRoles() {
        List<Rol> roles = rolRepository.findAll();
        List<EntityModel<Rol>> rolesConLinks = roles.stream()
                .map(rol -> EntityModel.of(rol)
                        .add(linkTo(methodOn(RolController.class).obtenerRolPorId(rol.getId())).withSelfRel())
                        .add(linkTo(methodOn(RolController.class).obtenerTodosLosRoles()).withRel("todos-losroles")))
                .toList();
        return CollectionModel.of(rolesConLinks)
                .add(linkTo(methodOn(RolController.class).obtenerTodosLosRoles()).withSelfRel())
                .add(linkTo(methodOn(RolController.class).crearRol(null)).withRel("crearrol"));
    }

    // GET /api/roles/{id} - Obtener rol por ID
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Rol>> obtenerRolPorId(@PathVariable Long id) {
        Optional<Rol> rol = rolRepository.findById(id);
        if (rol.isPresent()) {
            EntityModel<Rol> rolConLinks = EntityModel.of(rol.get())
                    .add(linkTo(methodOn(RolController.class).obtenerRolPorId(id)).withSelfRel())
                    .add(linkTo(methodOn(RolController.class).obtenerTodosLosRoles()).withRel("todos-losroles"))
                    .add(linkTo(methodOn(RolController.class).actualizarRol(id,
                            null)).withRel("actualizar"))
                    .add(linkTo(methodOn(RolController.class).eliminarRol(id)).withRel("eliminar"));
            return ResponseEntity.ok(rolConLinks);
        }
        return ResponseEntity.notFound().build();
    }

    // POST /api/roles - Crear nuevo rol
    @PostMapping
    public EntityModel<Rol> crearRol(@RequestBody Rol rol) {
        Rol rolGuardado = rolRepository.save(rol);
        return EntityModel.of(rolGuardado)
            .add(linkTo(methodOn(RolController.class).obtenerRolPorId(rolGuardado.getId())).withSelfRel())
            .add(linkTo(methodOn(RolController.class).obtenerTodosLosRoles()).withRel("todos-losroles"))
            .add(linkTo(methodOn(RolController.class).actualizarRol(rolGuardado.getId(), null)).withRel("actualizar"))
            .add(linkTo(methodOn(RolController.class).eliminarRol(rolGuardado.getId())).withRel("eliminar"));
    }

    // PUT /api/roles/{id} - Actualizar rol
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Rol>> actualizarRol(@PathVariable Long id,@RequestBody Rol rolActualizado) {
        Optional<Rol> rolExistente = rolRepository.findById(id);
        if (rolExistente.isPresent()) {
            Rol rol = rolExistente.get();
            rol.setTipoRol(rolActualizado.getTipoRol());
            Rol rolGuardado = rolRepository.save(rol);
            EntityModel<Rol> rolConLinks = EntityModel.of(rolGuardado)
            .add(linkTo(methodOn(RolController.class).obtenerRolPorId(id)).withSelfRel())
            .add(linkTo(methodOn(RolController.class).obtenerTodosLosRoles()).withRel("todos-losroles"))
            .add(linkTo(methodOn(RolController.class).eliminarRol(id)).withRel("eliminar"));
            return ResponseEntity.ok(rolConLinks);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /api/roles/{id} - Eliminar rol
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Long id) {
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
            return ResponseEntity.ok()
                    .header("mensaje", "Rol eliminado exitosamente")
                    .build();
        }
        return ResponseEntity.notFound().build();
    }
}
