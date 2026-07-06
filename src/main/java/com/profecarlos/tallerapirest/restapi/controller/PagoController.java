package com.profecarlos.tallerapirest.restapi.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.profecarlos.tallerapirest.restapi.dto.PagoDTO;
import com.profecarlos.tallerapirest.restapi.model.Pago;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.repository.PagoRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;
import com.profecarlos.tallerapirest.restapi.service.TransbankService;
import com.profecarlos.tallerapirest.restapi.service.TransbankTransactionResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final TransbankService transbankService;

    public PagoController(PagoRepository pagoRepository, PedidoRepository pedidoRepository, TransbankService transbankService) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.transbankService = transbankService;
    }

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            List<Pago> pagos = pagoRepository.findAll();
            if (pagos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⚠️ No hay pagos registrados");
            }
            return ResponseEntity.ok(pagos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al obtener pagos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de pago inválido");
            }
            return pagoRepository.findById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("⚠️ Pago no encontrado con ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al buscar pago: " + e.getMessage());
        }
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> buscarPorPedido(@PathVariable Integer pedidoId) {
        try {
            if (pedidoId == null || pedidoId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de pedido inválido");
            }
            return pagoRepository.findByPedidoIdPedido(pedidoId)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("⚠️ No hay pagos para el pedido ID: " + pedidoId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al buscar pago por pedido: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PagoDTO dto) {
        try {
            if (dto.getPedidoId() == null || dto.getPedidoId() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de pedido inválido");
            }

            if (pagoRepository.existsByPedidoIdPedido(dto.getPedidoId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("⚠️ Ya existe un pago para este pedido");
            }

            Pedido pedido = pedidoRepository.findById(dto.getPedidoId()).orElse(null);
            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ Pedido no encontrado con ID: " + dto.getPedidoId());
            }

            if (dto.getMonto() == null || dto.getMonto().signum() <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ El monto del pago debe ser mayor a 0");
            }

            Pago pago = new Pago();
            pago.setPedido(pedido);
            pago.setMonto(dto.getMonto());
            pago.setMetodoPago(dto.getMetodoPago() != null ? dto.getMetodoPago() : "PENDIENTE");
            pago.setEstadoPago(dto.getEstadoPago() != null ? dto.getEstadoPago() : "PENDIENTE");
            pago.setFechaPago(dto.getFechaPago());

            Pago pagGuardado = pagoRepository.save(pago);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("✓ Pago creado exitosamente con ID: " + pagGuardado.getIdPago());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al crear pago: " + e.getMessage());
        }
    }

    @PostMapping("/{pagoId}/transbank")
    public ResponseEntity<?> procesarConTransbank(@PathVariable Integer pagoId, 
                                                    @RequestParam String retorno) {
        try {
            if (pagoId == null || pagoId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de pago inválido");
            }

            Pago pago = pagoRepository.findById(pagoId).orElse(null);
            if (pago == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⚠️ Pago no encontrado con ID: " + pagoId);
            }

            if (pago.getEstadoPago().equals("COMPLETADO")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("⚠️ Este pago ya ha sido procesado");
            }

            if (retorno == null || retorno.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ URL de retorno es requerida");
            }

            // Crear transacción con Transbank
            String ordenCompra = "ORD_" + pago.getIdPago() + "_" + System.currentTimeMillis();
            String sesionId = UUID.randomUUID().toString();
            
            TransbankTransactionResponse response = transbankService.crearTransaccion(pago.getMonto(), ordenCompra, sesionId, retorno);

            pago.setMetodoPago("TRANSBANK");
            pago.setEstadoPago("PROCESANDO");
            pagoRepository.save(pago);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al procesar pago con Transbank: " + e.getMessage());
        }
    }

    @GetMapping("/{pagoId}/transbank/estado/{token}")
    public ResponseEntity<?> obtenerEstadoTransbank(@PathVariable Integer pagoId, 
                                                      @PathVariable String token) {
        try {
            if (pagoId == null || pagoId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de pago inválido");
            }

            Pago pago = pagoRepository.findById(pagoId).orElse(null);
            if (pago == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⚠️ Pago no encontrado con ID: " + pagoId);
            }

            TransbankTransactionResponse estado = transbankService.obtenerEstadoTransaccion(token);

            if ("AUTHORIZED".equalsIgnoreCase(estado.getStatus())) {
                pago.setEstadoPago("COMPLETADO");
                pagoRepository.save(pago);
            } else if ("REVERSED".equalsIgnoreCase(estado.getStatus())) {
                pago.setEstadoPago("RECHAZADO");
                pagoRepository.save(pago);
            }

            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al obtener estado: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @Valid @RequestBody PagoDTO dto) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de pago inválido");
            }

            return pagoRepository.findById(id).map(existing -> {
                try {
                    if (dto.getPedidoId() == null || dto.getPedidoId() <= 0) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("⚠️ ID de pedido inválido");
                    }

                    Pedido pedido = pedidoRepository.findById(dto.getPedidoId()).orElse(null);
                    if (pedido == null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("⚠️ Pedido no encontrado con ID: " + dto.getPedidoId());
                    }

                    if (existing.getEstadoPago().equals("COMPLETADO")) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body("⚠️ No se pueden actualizar pagos completados");
                    }

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

                    Pago actualizado = pagoRepository.save(existing);
                    return ResponseEntity.ok("✓ Pago actualizado exitosamente | ID: " + actualizado.getIdPago());
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("⚠️ Error al actualizar pago: " + e.getMessage());
                }
            }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("⚠️ Pago no encontrado con ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error del servidor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("⚠️ ID de pago inválido");
            }

            if (!pagoRepository.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("⚠️ Pago no encontrado con ID: " + id);
            }

            Pago pago = pagoRepository.findById(id).orElse(null);
            if (pago != null && pago.getEstadoPago().equals("COMPLETADO")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("⚠️ No se pueden eliminar pagos completados");
            }

            pagoRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("✓ Pago eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("⚠️ Error al eliminar pago: " + e.getMessage());
        }
    }
}