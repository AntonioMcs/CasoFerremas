package com.profecarlos.tallerapirest.restapi.service;

import org.springframework.stereotype.Service;

import com.profecarlos.tallerapirest.restapi.dto.AuditLogDTO;
import com.profecarlos.tallerapirest.restapi.model.AuditLog;
import com.profecarlos.tallerapirest.restapi.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog registrar(AuditLogDTO dto, String ip, String userAgent) {
        AuditLog log = new AuditLog();
        log.setTipoUsuario(dto.getTipoUsuario());
        log.setIdUsuario(dto.getIdUsuario());
        log.setNombreUsuario(dto.getNombreUsuario());
        log.setRol(dto.getRol());
        log.setModulo(dto.getModulo());
        log.setAccion(dto.getAccion());
        log.setDescripcion(dto.getDescripcion());
        log.setEntidad(dto.getEntidad());
        log.setEntidadId(dto.getEntidadId());
        log.setIp(ip);
        log.setUserAgent(userAgent);
        return auditLogRepository.save(log);
    }

    public void registrar(String entidad, Integer entidadId, String accion, String detalle) {
        auditLogRepository.save(new AuditLog(entidad, entidadId, accion, detalle));
    }

    public void registrar(String entidad, Integer entidadId, String accion, String detalle, Integer usuarioId,
            String usuarioTipo, String usuarioNombre) {
        AuditLog log = new AuditLog(entidad, entidadId, accion, detalle);
        log.setIdUsuario(usuarioId);
        log.setTipoUsuario(usuarioTipo);
        log.setNombreUsuario(usuarioNombre);
        log.setRol(usuarioTipo);
        log.setModulo(entidad);
        auditLogRepository.save(log);
    }
}
