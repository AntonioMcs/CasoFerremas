package com.profecarlos.tallerapirest.restapi.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.profecarlos.tallerapirest.restapi.model.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Integer> {

    List<Usuario> findByNombre(String nombre);

    List<Usuario> findByActivo(Boolean activo);

    List<Usuario> findByRolId(Long rolId); // Corregido aquí

    Optional<Usuario> findByEmail(String email); // Para login más adelante
}
