package com.profecarlos.tallerapirest.restapi.dto;

import jakarta.validation.constraints.NotBlank;

public class EstadoPedidoDTO {

    @NotBlank
    private String nombreEstado;

    public EstadoPedidoDTO() {
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }
}