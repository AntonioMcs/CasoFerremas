package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.PedidoDTO;
import com.profecarlos.tallerapirest.restapi.model.Cliente;
import com.profecarlos.tallerapirest.restapi.model.EstadoPedido;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.model.Trabajador;
import com.profecarlos.tallerapirest.restapi.repository.ClienteRepository;
import com.profecarlos.tallerapirest.restapi.repository.EstadoPedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.TrabajadorRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository,
            TrabajadorRepository trabajadorRepository, EstadoPedidoRepository estadoPedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.trabajadorRepository = trabajadorRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Integer id) {
        return pedidoRepository.findById(id);
    }

    public List<Pedido> listarPorCliente(Integer clienteId) {
        return pedidoRepository.findByClienteId(clienteId);
    }

    public Pedido crear(PedidoDTO dto) {
        Pedido pedido = new Pedido();
        aplicarDatos(pedido, dto);
        return pedidoRepository.save(pedido);
    }

    public Pedido actualizar(Integer id, PedidoDTO dto) {
        Pedido existing = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        aplicarDatos(existing, dto);
        return pedidoRepository.save(existing);
    }

    public void eliminar(Integer id) {
        pedidoRepository.deleteById(id);
    }

    private void aplicarDatos(Pedido pedido, PedidoDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        EstadoPedido estadoPedido = estadoPedidoRepository.findById(dto.getEstadoId())
                .orElseThrow(() -> new IllegalArgumentException("Estado de pedido no encontrado"));

        Trabajador trabajador = null;
        if (dto.getTrabajadorId() != null) {
            trabajador = trabajadorRepository.findById(dto.getTrabajadorId())
                    .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado"));
        }

        pedido.setCliente(cliente);
        pedido.setTrabajador(trabajador);
        pedido.setEstadoPedido(estadoPedido);
        if (dto.getFechaPedido() != null) {
            pedido.setFechaPedido(dto.getFechaPedido());
        }
        pedido.setTotal(dto.getTotal());
        pedido.setMetodoPago(dto.getMetodoPago());
        pedido.setTipoEntrega(dto.getTipoEntrega());
        pedido.setDireccionEntrega(dto.getDireccionEntrega());
        pedido.setComunaEntrega(dto.getComunaEntrega());
        pedido.setSucursalRetiro(dto.getSucursalRetiro());
    }
}
