package com.profecarlos.tallerapirest.restapi.service;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.model.AuditLog;
import com.profecarlos.tallerapirest.restapi.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void registrar(String entidad, Integer entidadId, String accion, String detalle) {
        auditLogRepository.save(new AuditLog(entidad, entidadId, accion, detalle));
    }

    public void registrar(String entidad, Integer entidadId, String accion, String detalle, Integer usuarioId,
            String usuarioTipo, String usuarioNombre) {
        AuditLog log = new AuditLog(entidad, entidadId, accion, detalle);
        log.setUsuarioId(usuarioId);
        log.setUsuarioTipo(usuarioTipo);
        log.setUsuarioNombre(usuarioNombre);
        auditLogRepository.save(log);
    }
}
