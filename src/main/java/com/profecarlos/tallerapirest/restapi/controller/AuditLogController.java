package com.profecarlos.tallerapirest.restapi.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.profecarlos.tallerapirest.restapi.dto.AuditLogDTO;
import com.profecarlos.tallerapirest.restapi.model.AuditLog;
import com.profecarlos.tallerapirest.restapi.repository.AuditLogRepository;
import com.profecarlos.tallerapirest.restapi.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogRepository auditLogRepository, AuditLogService auditLogService) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> listar() {
        List<AuditLog> logs = auditLogRepository.findAll().stream()
                .sorted(Comparator.comparing(AuditLog::getFecha, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
        return ResponseEntity.ok(logs);
    }

    @PostMapping
    public ResponseEntity<AuditLog> crear(@Valid @RequestBody AuditLogDTO dto,
            HttpServletRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String ip = forwardedFor != null && !forwardedFor.isBlank()
                ? forwardedFor.split(",")[0].trim()
                : request.getRemoteAddr();
        return new ResponseEntity<>(auditLogService.registrar(dto, ip, userAgent), HttpStatus.CREATED);
    }
}
