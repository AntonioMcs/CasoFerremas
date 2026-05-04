package com.profecarlos.tallerapirest.restapi.dto;

import java.util.Set;

/*
    Este archivo es un DTO(Data Transfer Object)
    Sirve como una Link table(SQL)
    Hecho por Gustavo Santana
*/
public class PedidoDTO {

    private String nombre;
    private int categoria;
    private double precio;
    private String descripcion;
    private Integer usuarioId;
    private Set<Integer> productIds;

    //Constructors
    public PedidoDTO(String nombre, int categoria, double precio, String descripcion, Integer usuarioId, Set<Integer> productIds) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
        this.productIds = productIds;
    }
    
    public PedidoDTO() {
    }

    //Getters & Setters
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public int getCategoria() {
        return categoria;
    }
    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Integer getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }
    public Set<Integer> getProductIds() {
        return productIds;
    }
    public void setProductIds(Set<Integer> productIds) {
        this.productIds = productIds;
    }
}
