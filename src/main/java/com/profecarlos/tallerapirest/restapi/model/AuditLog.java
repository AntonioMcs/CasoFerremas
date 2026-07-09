package com.profecarlos.tallerapirest.restapi.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "logs_movimientos")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Integer idLog;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "tipo_usuario", nullable = false, length = 30)
    private String tipoUsuario;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "nombre_usuario", length = 120)
    private String nombreUsuario;

    @Column(length = 40)
    private String rol;

    @Column(nullable = false, length = 80)
    private String modulo;

    @Column(nullable = false, length = 80)
    private String accion;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 80)
    private String entidad;

    @Column(name = "entidad_id")
    private Integer entidadId;

    @Column(length = 80)
    private String ip;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    public AuditLog() {
    }

    public AuditLog(String entidad, Integer entidadId, String accion, String descripcion) {
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.modulo = entidad;
        this.accion = accion;
        this.descripcion = descripcion;
        this.tipoUsuario = "sistema";
        this.idUsuario = 0;
        this.nombreUsuario = "Sistema";
        this.rol = "sistema";
    }

    @PrePersist
    public void prePersist() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
        if (tipoUsuario == null || tipoUsuario.isBlank()) {
            tipoUsuario = "sistema";
        }
        if (idUsuario == null) {
            idUsuario = 0;
        }
        if (modulo == null || modulo.isBlank()) {
            modulo = entidad != null ? entidad : "general";
        }
        if (accion == null || accion.isBlank()) {
            accion = "MOVIMIENTO";
        }
    }

    public Integer getIdLog() {
        return idLog;
    }

    public void setIdLog(Integer idLog) {
        this.idLog = idLog;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public Integer getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Integer entidadId) {
        this.entidadId = entidadId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
