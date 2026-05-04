package com.profecarlos.tallerapirest.restapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.profecarlos.tallerapirest.restapi.dto.ReviewDTO;
import com.profecarlos.tallerapirest.restapi.model.Review;
import com.profecarlos.tallerapirest.restapi.model.Usuario;
import com.profecarlos.tallerapirest.restapi.repository.ReviewRepository;
import com.profecarlos.tallerapirest.restapi.repository.UserRepository;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1")
public class ReviewController {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> review = reviewRepository.findAllByOrderByIdAsc();
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @GetMapping("/review/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable int id) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(review, HttpStatus.OK);
        }
    }

    @PostMapping("/review/post")
    public ResponseEntity<?> insertReview(@RequestBody ReviewDTO reviewDTO) {
        // Buscar el usuario
        Usuario usuario = userRepository.findById(reviewDTO.getUsuarioId())
                .orElse(null);
        if (usuario == null) {
            return new ResponseEntity<>("Usuario no encontrado con ID: " + reviewDTO.getUsuarioId(), HttpStatus.BAD_REQUEST);
        }
        // Crear la entidad Review a partir del DTO
        Review review = new Review();
        review.setNombre(reviewDTO.getNombre());
        review.setComentario(reviewDTO.getComentario());
        review.setCalificacion(reviewDTO.getCalificacion());
        review.setUsuario(usuario);
        Review savedReview = reviewRepository.save(review);
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED); 
    }

    @PutMapping("/review/{id}")
    public ResponseEntity<?> updateReview(@PathVariable int id, @Valid @RequestBody ReviewDTO reviewDTO) {
        // Verificar si la review existe
        return reviewRepository.findById(id)
                .map(existingReview -> {
                    // Verificar si el usuario existe
                    Usuario usuario = userRepository.findById(reviewDTO.getUsuarioId())
                                     .orElse(null);
                    if (usuario == null) {
                         throw new RuntimeException("Usuario no encontrado con ID: " + reviewDTO.getUsuarioId() + " al intentar actualizar la review.");
                    }
                    existingReview.setNombre(reviewDTO.getNombre());
                    existingReview.setComentario(reviewDTO.getComentario());
                    existingReview.setCalificacion(reviewDTO.getCalificacion());
                    Review updatedReview = reviewRepository.save(existingReview);
                    return new ResponseEntity<>(updatedReview, HttpStatus.OK);
                }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("delReview/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") int id){
        if (reviewRepository.existsById(id)){
            reviewRepository.deleteById(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}