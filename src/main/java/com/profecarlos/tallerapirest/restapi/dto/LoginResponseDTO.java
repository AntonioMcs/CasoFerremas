package com.profecarlos.tallerapirest.restapi.dto;

public class LoginResponseDTO {

    private Integer id;
    private String nombre;
    private String email;
    private String rol;
    private String tipoUsuario;
    private String comuna;

    public LoginResponseDTO(Integer id, String nombre, String email, String rol, String tipoUsuario, String comuna) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.tipoUsuario = tipoUsuario;
        this.comuna = comuna;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public String getComuna() {
        return comuna;
    }
}
