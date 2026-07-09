package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.EstadoPedidoDTO;
import com.profecarlos.tallerapirest.restapi.model.EstadoPedido;
import com.profecarlos.tallerapirest.restapi.repository.EstadoPedidoRepository;

@Service
public class EstadoPedidoService {

    private final EstadoPedidoRepository estadoPedidoRepository;

    public EstadoPedidoService(EstadoPedidoRepository estadoPedidoRepository) {
        this.estadoPedidoRepository = estadoPedidoRepository;
    }

    public List<EstadoPedido> listarTodos() {
        return estadoPedidoRepository.findAll();
    }

    public Optional<EstadoPedido> buscarPorId(Integer id) {
        return estadoPedidoRepository.findById(id);
    }

    public Optional<EstadoPedido> buscarPorNombre(String nombreEstado) {
        return estadoPedidoRepository.findByNombreEstado(nombreEstado);
    }

    public EstadoPedido crear(EstadoPedidoDTO dto) {
        EstadoPedido estadoPedido = new EstadoPedido();
        estadoPedido.setNombreEstado(dto.getNombreEstado());
        return estadoPedidoRepository.save(estadoPedido);
    }

    public EstadoPedido actualizar(Integer id, EstadoPedidoDTO dto) {
        EstadoPedido existing = estadoPedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estado de pedido no encontrado"));
        existing.setNombreEstado(dto.getNombreEstado());
        return estadoPedidoRepository.save(existing);
    }

    public void eliminar(Integer id) {
        estadoPedidoRepository.deleteById(id);
    }
}
