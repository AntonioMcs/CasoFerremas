package com.profecarlos.tallerapirest.restapi.repository;

//Hecho por Gustavo Santana

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.profecarlos.tallerapirest.restapi.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    //Sirve para que en el metodo GET de listar todos ordene la lista por id de manera ascendiente
	List<Category> findAllByOrderByIdAsc();
}
