package com.profecarlos.tallerapirest.restapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @Email(message = "Debe ser un email valido")
    @NotBlank(message = "El email no puede estar vacio")
    private String email;

    @NotBlank(message = "La contrasena no puede estar vacia")
    private String contrasena;

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
}
