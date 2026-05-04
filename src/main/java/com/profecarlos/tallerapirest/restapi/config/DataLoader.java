package com.profecarlos.tallerapirest.restapi.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.CategoryRepository;
import net.datafaker.Faker;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        // Solo cargar datos si la base está vacía
        if (productRepository.count() == 0) {
            loadSampleData();
        }
    }

    private void loadSampleData() {
        List<Product> products = new ArrayList<>();
        List<com.profecarlos.tallerapirest.restapi.model.Category> categorias = categoryRepository.findAll();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías en la base de datos. Agrega categorías antes de cargar productos.");
            return;
        }
        // Generar 100 productos
        for (int i = 0; i < 100; i++) {
            Product product = createRandomProduct(categorias);
            products.add(product);
        }
        // Guardar todos los productos
        productRepository.saveAll(products);
        System.out.println("Se han cargado " + products.size() + " productos de ejemplo");
    }

    private Product createRandomProduct(List<com.profecarlos.tallerapirest.restapi.model.Category> categorias) {
        Product product = new Product();
        product.setNombre(faker.commerce().productName());
        product.setPrecio(faker.number().randomDouble(2, 10, 1000));
        product.setDescripcion(faker.lorem().sentence(10, 5));
        // Asignar una categoría aleatoria existente
        com.profecarlos.tallerapirest.restapi.model.Category categoria = categorias.get(random.nextInt(categorias.size()));
        // Si tu Product espera un String, usa el nombre de la categoría
        product.setCategoria(categoria.getNombre());
        // Si tu Product espera un id, usa: product.setCategoriaId(categoria.getId());
        // Agrega aquí otros campos si tu Product los tiene
        return product;
    }
}
