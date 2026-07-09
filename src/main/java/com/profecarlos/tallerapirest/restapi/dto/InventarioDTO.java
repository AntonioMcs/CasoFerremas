package com.profecarlos.tallerapirest.restapi.dto;

import jakarta.validation.constraints.NotNull;

public class InventarioDTO {

    @NotNull(message = "El ID del producto no puede estar vacío")
    private Integer productoId;

    @NotNull(message = "El stock actual no puede estar vacío")
    private Integer stockActual;

    private Integer stockMinimo = 0;

    private String ubicacionBodega;

    private String sucursal;

    private Integer proveedorId;

    public InventarioDTO() {
    }

    public InventarioDTO(Integer productoId, Integer stockActual, Integer stockMinimo, String ubicacionBodega) {
        this.productoId = productoId;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.ubicacionBodega = ubicacionBodega;
    }

    public InventarioDTO(Integer productoId, Integer stockActual, Integer stockMinimo, String ubicacionBodega, Integer proveedorId) {
        this.productoId = productoId;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.ubicacionBodega = ubicacionBodega;
        this.proveedorId = proveedorId;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public String getUbicacionBodega() {
        return ubicacionBodega;
    }

    public void setUbicacionBodega(String ubicacionBodega) {
        this.ubicacionBodega = ubicacionBodega;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }
}
