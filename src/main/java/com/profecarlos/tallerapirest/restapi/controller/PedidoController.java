package com.profecarlos.tallerapirest.restapi.controller;

import java.util.HashSet;

//Hecho por Matias Caileo

import java.util.List;
import java.util.Set;

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

import com.profecarlos.tallerapirest.restapi.dto.PedidoDTO;
import com.profecarlos.tallerapirest.restapi.model.Pedido;
import com.profecarlos.tallerapirest.restapi.model.Usuario;
import com.profecarlos.tallerapirest.restapi.model.Category;
import com.profecarlos.tallerapirest.restapi.model.Product;
import com.profecarlos.tallerapirest.restapi.repository.CategoryRepository;
import com.profecarlos.tallerapirest.restapi.repository.PedidoRepository;
import com.profecarlos.tallerapirest.restapi.repository.ProductRepository;
import com.profecarlos.tallerapirest.restapi.repository.UserRepository;

@RestController
@RequestMapping("/api/v1")
public class PedidoController {
    // Inyección del repositorio
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private ProductRepository productRepository;

    // POST: Crear pedido usando PedidoDTO
    @PostMapping("pedido")
    public ResponseEntity<?> insertProduct(@RequestBody PedidoDTO pedidoDTO) {
        //Verifica si existe una categoria en el body
        Category categoria = categoryRepository.findById(pedidoDTO.getCategoria()).orElse(null);
        //Si no encuentra una retorna error
        if (categoria == null) {
            return new ResponseEntity<>("Categoría no encontrada ", HttpStatus.BAD_REQUEST);
        }

        //Variable para obtener la id_usuario y guardarla en (usuario)
        Usuario usuario = usuarioRepository.findById(pedidoDTO.getUsuarioId()).orElse(null);
        
        //Condicion para verificar si se ingresó una id
        if (pedidoDTO.getUsuarioId() == null) {
            return new ResponseEntity<>("Error: El ID del usuario (usuarioId) es requerido. ", HttpStatus.BAD_REQUEST);
        }

        //Condicion para verificar si existe el usuario
        if (usuario == null){
            return new ResponseEntity<>("Error: Usuario no encontrado",HttpStatus.NOT_FOUND);
        }

        Set<Product> productsInPedido = new HashSet<>();
        if (pedidoDTO.getProductIds() != null){
            for (Integer productId : pedidoDTO.getProductIds()){
                Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productId));
                productsInPedido.add(product);
            }
        }

        //Proceso de creacion y guardado del objeto
        Pedido pedido = new Pedido();
        pedido.setNombre(pedidoDTO.getNombre());
        pedido.setCategoria(categoria);
        pedido.setPrecio(pedidoDTO.getPrecio());
        pedido.setDescripcion(pedidoDTO.getDescripcion());
        pedido.setUsuario(usuario);
        pedido.setProducts(productsInPedido);
        Pedido savedPedido = pedidoRepository.save(pedido);
        return new ResponseEntity<>(savedPedido, HttpStatus.CREATED);
    }

    @GetMapping("pedidos")
    public ResponseEntity<List<Pedido>> getAllProducts() {
        List<Pedido> Pedidos = pedidoRepository.findAll();
        return new ResponseEntity<>(Pedidos, HttpStatus.OK);
    }

    // Métodos adicionales para completar el CRUD
    @GetMapping("pedido/{id}")
    public ResponseEntity<Pedido> getProductById(@PathVariable("id") int id) {
        return pedidoRepository.findById(id)                              // Busca el pedido por ID
                .map(order -> new ResponseEntity<>(order, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("pedidos/categoria/{categoria}")
    public ResponseEntity<List<Pedido>> getProductsByCategoria(@PathVariable int categoria) {
        List<Pedido> Pedidos = pedidoRepository.findByCategoriaId(categoria);   // Busca pedidos por ID de categoría
        return new ResponseEntity<>(Pedidos, HttpStatus.OK);
    }

    @PutMapping("pedido/{id}")
    public ResponseEntity<?> updatePedido(@PathVariable("id") int id, @RequestBody PedidoDTO pedidoDTO) {
    // Verifica si existe el pedido
    return pedidoRepository.findById(id).map(pedidoExistente -> {
        // Verifica si existe la categoría
        Category categoria = categoryRepository.findById(pedidoDTO.getCategoria()).orElse(null);  
        if (categoria == null) {                                                                     
            return new ResponseEntity<>("Categoría no encontrada", HttpStatus.BAD_REQUEST);
        }
        if (pedidoDTO.getUsuarioId() == null) {            // VERIFICA SI EL ID DEL USUSARIO ES NULO
            return new ResponseEntity<>("El ID del usuario (usuarioId) es requerido", HttpStatus.BAD_REQUEST);
        }
        Usuario usuario = usuarioRepository.findById(pedidoDTO.getUsuarioId()).orElse(null);   
        if (usuario == null) {                                                                        // VERIFICACION SI EL USUARIO EXISTE
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }
        pedidoExistente.setNombre(pedidoDTO.getNombre());  
        pedidoExistente.setDescripcion(pedidoDTO.getDescripcion());
        pedidoExistente.setPrecio(pedidoDTO.getPrecio());                    //ACTUALIZA LOS CAMBIOS DEL PEDIDO EXISTENTE
        pedidoExistente.setCategoria(categoria);
        pedidoExistente.setUsuario(usuario);
        Pedido pedidoActualizado = pedidoRepository.save(pedidoExistente);
        return new ResponseEntity<>(pedidoActualizado, HttpStatus.OK);
    }).orElse(new ResponseEntity<>("Pedido no encontrado", HttpStatus.NOT_FOUND));
}
       
    //ELIMINAR PEDIDO POR ID

    @DeleteMapping("pedido/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") int id) {
        return pedidoRepository.findById(id)
                .map(pedido -> {
                    Set<Product> productsInPedido = new HashSet<>();
                    if (pedido.getProducts() != null){
                        for (Product product : pedido.getProducts()){
                            Product prod = productRepository.findById(product.getId())
                                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + product.getId()));
                            productsInPedido.add(prod);
                        }
                    }
                    pedidoRepository.delete(pedido);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}
