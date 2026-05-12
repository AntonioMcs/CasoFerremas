package com.profecarlos.tallerapirest.restapi.model;

import org.springframework.hateoas.RepresentationModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "facturacion_cliente")
public class FacturacionCliente extends RepresentationModel<FacturacionCliente> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_facturacion")
    private Integer idFacturacion;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", unique = true)
    private Usuario usuario;

    @Column(name = "rut", length = 20)
    private String rut;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    @Column(name = "apellidos", nullable = false)
    private String apellidos;

    @Column(name = "telefono", length = 50)
    private String telefono;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "comuna", length = 100)
    private String comuna;

    public FacturacionCliente() {
    }

    public FacturacionCliente(Integer idFacturacion, Usuario usuario, String rut, String nombre, String apellidos, String telefono, String direccion, String comuna) {
        this.idFacturacion = idFacturacion;
        this.usuario = usuario;
        this.rut = rut;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
        this.comuna = comuna;
    }

    public Integer getIdFacturacion() {
        return idFacturacion;
    }

    public void setIdFacturacion(Integer idFacturacion) {
        this.idFacturacion = idFacturacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
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

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }
}
