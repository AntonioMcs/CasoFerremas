package com.profecarlos.tallerapirest.restapi.controller;

//Hecho por Gustavo Santana

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
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
import java.util.List;

import com.profecarlos.tallerapirest.restapi.model.Category;
import com.profecarlos.tallerapirest.restapi.repository.CategoryRepository;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    
    @Autowired
    private CategoryRepository categoryRepository;

    //GET: Listar todo
    @GetMapping("categories")
    public ResponseEntity<List<Category>> getAllCategories(){
        List<Category> category = categoryRepository.findAllByOrderByIdAsc();
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    //GET: Busqueda por id
    @GetMapping("category/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable int id){
        return categoryRepository.findById(id)
        .map(category -> {
            // Enlace a sí mismo
            category.add(linkTo(methodOn(CategoryController.class).getCategoryById(id)).withSelfRel());
            // Enlace a todas las categorías
            category.add(linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("all-categories"));
            return new ResponseEntity<>(category, HttpStatus.OK);
        })
        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
}

    //POST: Creacion de categoria
    @PostMapping("category/post")
    public ResponseEntity<Category>insertCategory(@RequestBody Category category){
        Category savedCategory = categoryRepository.save(category);
        return new ResponseEntity<>(savedCategory, HttpStatus.OK);
    }

    //PUT: Actualizar una categoria
    @PutMapping("updCategory/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") int id,@RequestBody Category categoryUpdated){
            return categoryRepository.findById(id)
            .map(categoryExistent -> {
                categoryExistent.setNombre(categoryUpdated.getNombre());
                Category updated = categoryRepository.save(categoryExistent);
                return new ResponseEntity<>(updated,HttpStatus.OK);

            }).orElse( new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    //DELETE: Eliminar una categoria
    @DeleteMapping("DelCategory/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") int id){
        if (categoryRepository.existsById(id)){
            categoryRepository.deleteById(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
