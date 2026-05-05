package com.profecarlos.tallerapirest.restapi.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DespachoDTO {

    @NotNull
    private Integer pedidoId;

    @NotBlank
    private String direccionEntrega;

    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntrega;

    @NotBlank
    private String estadoDespacho;

    public DespachoDTO() {
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Integer pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEstadoDespacho() {
        return estadoDespacho;
    }

    public void setEstadoDespacho(String estadoDespacho) {
        this.estadoDespacho = estadoDespacho;
    }
}