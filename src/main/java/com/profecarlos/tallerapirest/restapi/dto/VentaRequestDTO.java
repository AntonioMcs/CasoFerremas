package com.profecarlos.tallerapirest.restapi.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class VentaRequestDTO {

    @NotNull
    private Integer clienteId;

    private Integer trabajadorId;

    @NotBlank
    private String metodoPago;

    @NotBlank
    private String tipoEntrega;

    @Valid
    @NotEmpty
    private List<VentaItemDTO> items;

    public VentaRequestDTO() {
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Integer getTrabajadorId() {
        return trabajadorId;
    }

    public void setTrabajadorId(Integer trabajadorId) {
        this.trabajadorId = trabajadorId;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public List<VentaItemDTO> getItems() {
        return items;
    }

    public void setItems(List<VentaItemDTO> items) {
        this.items = items;
    }
}
