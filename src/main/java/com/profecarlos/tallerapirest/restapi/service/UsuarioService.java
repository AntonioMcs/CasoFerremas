package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.UsuarioDTO;
import com.profecarlos.tallerapirest.restapi.model.Usuario;
import com.profecarlos.tallerapirest.restapi.repository.UserRepository;

@Service
public class UsuarioService {

    private final UserRepository userRepository;

    public UsuarioService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Usuario> listarTodos() {
        return userRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return userRepository.findById(id);
    }

    public List<Usuario> listarPorTipo(String tipoUsuario) {
        return userRepository.findByTipoUsuario(tipoUsuario);
    }

    public Usuario crear(UsuarioDTO dto) {
        Usuario usuario = new Usuario(null, dto.getNombre(), dto.getEmail(), dto.getContrasena(), dto.getTipoUsuario());
        return userRepository.save(usuario);
    }

    public Usuario actualizar(Integer id, UsuarioDTO dto) {
        Usuario existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        existing.setNombre(dto.getNombre());
        existing.setEmail(dto.getEmail());
        existing.setContrasena(dto.getContrasena());
        existing.setTipoUsuario(dto.getTipoUsuario());
        return userRepository.save(existing);
    }

    public void eliminar(Integer id) {
        userRepository.deleteById(id);
    }
}
