package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.DespachoDTO;
import com.profecarlos.tallerapirest.restapi.model.Despacho;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.repository.DespachoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;

@Service
public class DespachoService {

    private final DespachoRepository despachoRepository;
    private final PedidoRepository pedidoRepository;

    public DespachoService(DespachoRepository despachoRepository, PedidoRepository pedidoRepository) {
        this.despachoRepository = despachoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<Despacho> listarTodos() {
        return despachoRepository.findAll();
    }

    public Optional<Despacho> buscarPorId(Integer id) {
        return despachoRepository.findById(id);
    }

    public Optional<Despacho> buscarPorPedido(Integer pedidoId) {
        return despachoRepository.findByPedidoIdPedido(pedidoId);
    }

    public Despacho crear(DespachoDTO dto) {
        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        Despacho despacho = new Despacho();
        despacho.setPedido(pedido);
        despacho.setDireccionEntrega(dto.getDireccionEntrega());
        despacho.setFechaEnvio(dto.getFechaEnvio());
        despacho.setFechaEntrega(dto.getFechaEntrega());
        despacho.setEstadoDespacho(dto.getEstadoDespacho());
        return despachoRepository.save(despacho);
    }

    public Despacho actualizar(Integer id, DespachoDTO dto) {
        Despacho existing = despachoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Despacho no encontrado"));

        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        existing.setPedido(pedido);
        existing.setDireccionEntrega(dto.getDireccionEntrega());
        existing.setFechaEnvio(dto.getFechaEnvio());
        existing.setFechaEntrega(dto.getFechaEntrega());
        existing.setEstadoDespacho(dto.getEstadoDespacho());
        return despachoRepository.save(existing);
    }

    public void eliminar(Integer id) {
        despachoRepository.deleteById(id);
    }
}
