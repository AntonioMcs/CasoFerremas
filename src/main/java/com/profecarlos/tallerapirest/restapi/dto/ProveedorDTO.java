package com.profecarlos.tallerapirest.restapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProveedorDTO {

    private Integer idProveedor;

    @NotBlank(message = "El nombre del proveedor no puede estar vacío")
    private String nombreProveedor;

    @NotBlank(message = "Los datos de contacto no pueden estar vacíos")
    private String datosContacto;

    private String email;
    private String telefono;
    private String direccion;

    public ProveedorDTO() {
    }

    public ProveedorDTO(String nombreProveedor, String datosContacto) {
        this.nombreProveedor = nombreProveedor;
        this.datosContacto = datosContacto;
    }

    public ProveedorDTO(String nombreProveedor, String datosContacto, String email, String telefono, String direccion) {
        this.nombreProveedor = nombreProveedor;
        this.datosContacto = datosContacto;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // Getters y Setters
    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getDatosContacto() {
        return datosContacto;
    }

    public void setDatosContacto(String datosContacto) {
        this.datosContacto = datosContacto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "ProveedorDTO{" +
                "idProveedor=" + idProveedor +
                ", nombreProveedor='" + nombreProveedor + '\'' +
                ", datosContacto='" + datosContacto + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", direccion='" + direccion + '\'' +
                '}';
    }
}
