package com.profecarlos.tallerapirest.restapi.controller;

import com.profecarlos.tallerapirest.restapi.model.Usuario;
import com.profecarlos.tallerapirest.restapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {
    @Autowired
    private UserRepository userRepository;
       // GET /api/usuarios - Obtener todos los usuarios
    @GetMapping
    public CollectionModel<EntityModel<Usuario>> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = userRepository.findAll();
        List<EntityModel<Usuario>> usuariosConLinks = usuarios.stream()
                .map(this::agregarLinksAUsuario)
                .toList();
        return CollectionModel.of(usuariosConLinks)
                .add(linkTo(methodOn(UserController.class).obtenerTodosLosUsuarios()).withSelfRel())
                .add(linkTo(methodOn(UserController.class).crearUsuario(null)).withRel("crearusuario"))
                .add(linkTo(methodOn(UserController.class).obtenerUsuariosActivos()).withRel("usuarioactivos"))
                .add(linkTo(methodOn(UserController.class).obtenerUsuariosInactivos()).withRel("usuarioinactivos"));
    }
    
    // GET /api/usuarios/activos - Obtener usuarios activos
    @GetMapping("/activos")
    public CollectionModel<EntityModel<Usuario>> obtenerUsuariosActivos() {
        List<Usuario> usuarios = userRepository.findByActivo(true);
        List<EntityModel<Usuario>> usuariosConLinks = usuarios.stream()
                .map(this::agregarLinksAUsuario)
                .toList();
        return CollectionModel.of(usuariosConLinks)
                .add(linkTo(methodOn(UserController.class).obtenerUsuariosActivos()).withSelfRel())
                .add(linkTo(methodOn(UserController.class).obtenerTodosLosUsuarios()).withRel("todoslos-usuarios"));
    }
    // GET /api/usuarios/inactivos - Obtener usuarios inactivos
    @GetMapping("/inactivos")
    public CollectionModel<EntityModel<Usuario>> obtenerUsuariosInactivos() {
        List<Usuario> usuarios = userRepository.findByActivo(false);
        List<EntityModel<Usuario>> usuariosConLinks = usuarios.stream()
                .map(this::agregarLinksAUsuario)
                .toList();
        return CollectionModel.of(usuariosConLinks)
                .add(linkTo(methodOn(UserController.class).obtenerUsuariosInactivos()).withSelfRel())
                .add(linkTo(methodOn(UserController.class).obtenerTodosLosUsuarios()).withRel("todoslos-usuarios"));
    }

    public CollectionModel<EntityModel<Usuario>> obtenerUsuariosPorRol(@PathVariable Long rolId) {
        List<Usuario> usuarios = userRepository.findByRolId(rolId);
        List<EntityModel<Usuario>> usuariosConLinks = usuarios.stream()
        .map(this::agregarLinksAUsuario).toList();
        return CollectionModel.of(usuariosConLinks)
            .add(linkTo(methodOn(UserController.class).obtenerUsuariosPorRol(rolId)).withSelfRel())
            .add(linkTo(methodOn(UserController.class).obtenerTodosLosUsuarios()).withRel("todoslos-usuarios"));
    }

    public ResponseEntity<EntityModel<Usuario>> crearUsuario(@RequestBody Usuario usuario) {
        // Validar email único
        Optional<Usuario> usuarioExistente = userRepository.findByEmail(usuario.getEmail());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Usuario usuarioGuardado = userRepository.save(usuario);
        return ResponseEntity.ok(agregarLinksAUsuario(usuarioGuardado));
    }
        
    // PUT /api/usuarios/{id} - Actualizar usuario
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizarUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioActualizado) {
        Optional<Usuario> usuarioExistente = userRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            // Validar email único si se está cambiando
            if (!usuario.getEmail().equals(usuarioActualizado.getEmail())) {
                Optional<Usuario> usuarioConEmail = userRepository.findByEmail(usuarioActualizado.getEmail());
                if (usuarioConEmail.isPresent()) {
                    return ResponseEntity.badRequest().build();
                }
            }
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setEmail(usuarioActualizado.getEmail());
            usuario.setTelefono(usuarioActualizado.getTelefono());
            usuario.setRol(usuarioActualizado.getRol());
            Usuario usuarioGuardado = userRepository.save(usuario);
            return ResponseEntity.ok(agregarLinksAUsuario(usuarioGuardado));
        }
        return ResponseEntity.notFound().build();
    }
    
    // PUT /api/usuarios/{id}/desactivar - Desactivar usuario (soft delete)
    @PutMapping("/{id}/desactivar")
    public ResponseEntity<EntityModel<Usuario>> desactivarUsuario(@PathVariable Integer id) {
        Optional<Usuario> usuarioExistente = userRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setActivo(false);
            Usuario usuarioGuardado = userRepository.save(usuario);
            return ResponseEntity.ok(agregarLinksAUsuario(usuarioGuardado));
        }
        return ResponseEntity.notFound().build();
    }

    // PUT /api/usuarios/{id}/activar - Activar usuario
    @PutMapping("/{id}/activar")
    public ResponseEntity<EntityModel<Usuario>> activarUsuario(@PathVariable Integer id) {
        Optional<Usuario> usuarioExistente = userRepository.findById(id);
        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();
            usuario.setActivo(true);
            Usuario usuarioGuardado = userRepository.save(usuario);
            return ResponseEntity.ok(agregarLinksAUsuario(usuarioGuardado));
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE /api/usuarios/{id} - Eliminar usuario completamente
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok()
                    .header("mensaje", "Usuario eliminado exitosamente")
                    .build();
        }
        return ResponseEntity.notFound().build();
    }
    
    // Método auxiliar para agregar enlaces HATEOAS
    private EntityModel<Usuario> agregarLinksAUsuario(Usuario usuario) {
        return EntityModel.of(usuario)
            .add(linkTo(methodOn(UserController.class).obtenerUsuariosPorRol(usuario.getId().longValue())).withSelfRel())
            .add(linkTo(methodOn(UserController.class).obtenerTodosLosUsuarios()).withRel("todoslos-usuarios"))
            .add(linkTo(methodOn(UserController.class).actualizarUsuario(usuario.getId(), null)).withRel("actualizar"));
    }
}