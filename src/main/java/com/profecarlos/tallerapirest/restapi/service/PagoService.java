package com.profecarlos.tallerapirest.restapi.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.PagoDTO;
import com.profecarlos.tallerapirest.restapi.model.Pago;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.repository.PagoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final TransbankService transbankService;

    public PagoService(PagoRepository pagoRepository, PedidoRepository pedidoRepository,
            TransbankService transbankService) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.transbankService = transbankService;
    }

    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> buscarPorId(Integer id) {
        return pagoRepository.findById(id);
    }

    public Optional<Pago> buscarPorPedido(Integer pedidoId) {
        return pagoRepository.findByPedidoIdPedido(pedidoId);
    }

    public Pago crear(PagoDTO dto) {
        if (pagoRepository.existsByPedidoIdPedido(dto.getPedidoId())) {
            throw new IllegalArgumentException("Ya existe un pago para este pedido");
        }

        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstadoPago(dto.getEstadoPago());
        pago.setFechaPago(dto.getFechaPago());
        return pagoRepository.save(pago);
    }

    public TransbankTransactionResponse procesarConTransbank(Integer pagoId, String retorno) throws Exception {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));

        String ordenCompra = "ORD_" + pago.getIdPago() + "_" + System.currentTimeMillis();
        String sesionId = UUID.randomUUID().toString();
        TransbankTransactionResponse response = transbankService.crearTransaccion(pago.getMonto(), ordenCompra, sesionId, retorno);

        pago.setMetodoPago("TRANSBANK");
        pago.setEstadoPago("PROCESANDO");
        pagoRepository.save(pago);
        return response;
    }

    public TransbankTransactionResponse obtenerEstadoTransbank(Integer pagoId, String token) throws Exception {
        Pago pago = pagoRepository.findById(pagoId)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));

        TransbankTransactionResponse estado = transbankService.obtenerEstadoTransaccion(token);
        if ("AUTHORIZED".equals(estado.getStatus())) {
            pago.setEstadoPago("COMPLETADO");
            pagoRepository.save(pago);
        } else if ("REVERSED".equals(estado.getStatus())) {
            pago.setEstadoPago("RECHAZADO");
            pagoRepository.save(pago);
        }
        return estado;
    }

    public Pago actualizar(Integer id, PagoDTO dto) {
        Pago existing = pagoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado"));

        Pedido pedido = pedidoRepository.findById(dto.getPedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));

        existing.setPedido(pedido);
        if (dto.getMonto() != null && dto.getMonto().signum() > 0) {
            existing.setMonto(dto.getMonto());
        }
        if (dto.getMetodoPago() != null) {
            existing.setMetodoPago(dto.getMetodoPago());
        }
        if (dto.getEstadoPago() != null) {
            existing.setEstadoPago(dto.getEstadoPago());
        }
        if (dto.getFechaPago() != null) {
            existing.setFechaPago(dto.getFechaPago());
        }
        return pagoRepository.save(existing);
    }

    public void eliminar(Integer id) {
        pagoRepository.deleteById(id);
    }
}
