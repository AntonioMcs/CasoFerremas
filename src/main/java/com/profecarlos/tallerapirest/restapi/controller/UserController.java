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

import com.profecarlos.tallerapirest.restapi.model.Usuario;
import com.profecarlos.tallerapirest.restapi.repository.UserRepository;
import com.profecarlos.tallerapirest.restapi.dto.UsuarioDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipoUsuario}")
    public ResponseEntity<List<Usuario>> listarPorTipo(@PathVariable String tipoUsuario) {
        return ResponseEntity.ok(userRepository.findByTipoUsuario(tipoUsuario));
    }

    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario(null, usuarioDTO.getNombre(), usuarioDTO.getEmail(), 
                usuarioDTO.getContrasena(), usuarioDTO.getTipoUsuario());
        return new ResponseEntity<>(userRepository.save(usuario), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioDTO usuarioDTO) {
        return userRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(usuarioDTO.getNombre());
                    existing.setEmail(usuarioDTO.getEmail());
                    existing.setContrasena(usuarioDTO.getContrasena());
                    existing.setTipoUsuario(usuarioDTO.getTipoUsuario());
                    return ResponseEntity.ok(userRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}