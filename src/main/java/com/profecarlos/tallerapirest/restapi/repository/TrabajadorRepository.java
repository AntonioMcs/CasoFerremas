package com.profecarlos.tallerapirest.restapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.profecarlos.tallerapirest.restapi.model.Trabajador;

@Repository
public interface TrabajadorRepository extends JpaRepository<Trabajador, Integer> {

    List<Trabajador> findByRolIgnoreCase(String rol);

    Optional<Trabajador> findByEmail(String email);
}
