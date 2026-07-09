package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.CategoriaDTO;
import com.profecarlos.tallerapirest.restapi.model.Categoria;
import com.profecarlos.tallerapirest.restapi.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listarTodos() {
        return categoriaRepository.findAllByOrderByIdAsc();
    }

    public Optional<Categoria> buscarPorId(Integer id) {
        return categoriaRepository.findById(id);
    }

    public Categoria crear(CategoriaDTO dto) {
        return categoriaRepository.save(new Categoria(null, dto.getNombreCategoria()));
    }

    public Categoria actualizar(Integer id, CategoriaDTO dto) {
        Categoria existing = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        existing.setNombreCategoria(dto.getNombreCategoria());
        return categoriaRepository.save(existing);
    }

    public void eliminar(Integer id) {
        categoriaRepository.deleteById(id);
    }
}
