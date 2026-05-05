package com.profecarlos.tallerapirest.restapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profecarlos.tallerapirest.restapi.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findAllByOrderByIdAsc();
}