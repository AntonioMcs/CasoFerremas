package com.profecarlos.tallerapirest.restapi.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductDTO {

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String nombreProducto;

    private String marca;

    private String descripcion;

    @NotNull(message = "El precio no puede estar vacío")
    private BigDecimal precio;

    private Integer stock = 0;

    private String unidadMedida;

    private String codigoSku;

    private Integer categoriaId;

    private Integer proveedorId;

    public ProductDTO() {
    }

    public ProductDTO(String nombreProducto, String marca, String descripcion, BigDecimal precio, Integer stock, String unidadMedida, String codigoSku, Integer categoriaId) {
        this.nombreProducto = nombreProducto;
        this.marca = marca;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.unidadMedida = unidadMedida;
        this.codigoSku = codigoSku;
        this.categoriaId = categoriaId;
    }

    public ProductDTO(String nombreProducto, String marca, String descripcion, BigDecimal precio, Integer stock, String unidadMedida, String codigoSku, Integer categoriaId, Integer proveedorId) {
        this.nombreProducto = nombreProducto;
        this.marca = marca;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.unidadMedida = unidadMedida;
        this.codigoSku = codigoSku;
        this.categoriaId = categoriaId;
        this.proveedorId = proveedorId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getCodigoSku() {
        return codigoSku;
    }

    public void setCodigoSku(String codigoSku) {
        this.codigoSku = codigoSku;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }
}
