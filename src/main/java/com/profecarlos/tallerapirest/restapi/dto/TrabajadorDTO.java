package com.profecarlos.tallerapirest.restapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class TrabajadorDTO {

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @Email(message = "Debe ser un email valido")
    private String email;

    @NotBlank(message = "La contrasena no puede estar vacia")
    private String contrasena;

    @NotBlank(message = "El rol no puede estar vacio")
    private String rol;

    private Boolean activo;

    public TrabajadorDTO() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
