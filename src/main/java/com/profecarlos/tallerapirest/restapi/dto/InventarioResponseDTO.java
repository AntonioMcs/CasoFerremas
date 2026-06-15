package com.profecarlos.tallerapirest.restapi.dto;

public class InventarioResponseDTO {

    private Integer idInventario;
    private Integer productoId;
    private String nombreProducto;
    private Integer proveedorId;
    private String nombreProveedor;
    private Integer stockActual;
    private Integer stockMinimo;
    private String ubicacionBodega;

    public InventarioResponseDTO() {
    }

    public InventarioResponseDTO(Integer idInventario, Integer productoId, String nombreProducto, Integer proveedorId,
            String nombreProveedor, Integer stockActual, Integer stockMinimo, String ubicacionBodega) {
        this.idInventario = idInventario;
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.proveedorId = proveedorId;
        this.nombreProveedor = nombreProveedor;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.ubicacionBodega = ubicacionBodega;
    }

    public Integer getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
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
}