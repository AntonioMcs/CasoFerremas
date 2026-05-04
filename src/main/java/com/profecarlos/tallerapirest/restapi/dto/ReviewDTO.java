package com.profecarlos.tallerapirest.restapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReviewDTO {
   private String nombre;
   private String comentario;

   @Min(value = 1)
   @Max(value = 5)
   private Integer calificacion;

   @NotNull
   private Integer usuarioId;
   
   //Constructores
   public ReviewDTO(String nombre, String comentario, @Min(1) @Max(5) Integer calificacion, @NotNull Integer usuarioId) {
    this.nombre = nombre;
    this.comentario = comentario;
    this.calificacion = calificacion;
    this.usuarioId = usuarioId;
   }

   public ReviewDTO() {
   }
   //Getters & Setters
   public String getNombre() {
    return nombre;
   }

   public void setNombre(String nombre) {
    this.nombre = nombre;
   }

   public String getComentario() {
    return comentario;
   }

   public void setComentario(String comentario) {
    this.comentario = comentario;
   }

   public Integer getCalificacion() {
    return calificacion;
   }

   public void setCalificacion(Integer calificacion) {
    this.calificacion = calificacion;
   }

   public Integer getUsuarioId() {
    return usuarioId;
   }

   public void setUsuarioId(Integer usuarioId) {
    this.usuarioId = usuarioId;
   }

    
}
