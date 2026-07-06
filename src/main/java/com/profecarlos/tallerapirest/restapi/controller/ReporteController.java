package com.profecarlos.tallerapirest.restapi.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.profecarlos.tallerapirest.restapi.repository.AuditLogRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;

@RestController
@RequestMapping("/api/v1/reportes")
public class ReporteController {

    private final PedidoRepository pedidoRepository;
    private final AuditLogRepository auditLogRepository;

    public ReporteController(PedidoRepository pedidoRepository, AuditLogRepository auditLogRepository) {
        this.pedidoRepository = pedidoRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/operaciones")
    public ResponseEntity<Map<String, Object>> operaciones() {
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("totalPedidos", pedidoRepository.count());
        reporte.put("pedidosPendientes", pedidoRepository.findByEstadoPedidoNombreEstadoIgnoreCase("pendiente").size());
        reporte.put("pedidosListos", pedidoRepository.findByEstadoPedidoNombreEstadoIgnoreCase("listo").size());
        reporte.put("pedidosEntregando", pedidoRepository.findByEstadoPedidoNombreEstadoIgnoreCase("entregando").size());
        reporte.put("pedidosEntregados", pedidoRepository.findByEstadoPedidoNombreEstadoIgnoreCase("entregado").size());
        reporte.put("totalAuditoria", auditLogRepository.count());
        reporte.put("ultimosLogs", auditLogRepository.findAll().stream().limit(10).toList());
        return ResponseEntity.ok(reporte);
    }
}
