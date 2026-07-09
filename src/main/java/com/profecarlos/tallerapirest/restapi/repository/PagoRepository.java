package com.profecarlos.tallerapirest.restapi.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profecarlos.tallerapirest.restapi.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    Optional<Pago> findByPedidoIdPedido(Integer pedidoId);

<<<<<<< HEAD
    Optional<Pago> findByTokenTransbank(String tokenTransbank);
=======
    List<Pago> findAllByPedidoIdPedidoOrderByIdPagoAsc(Integer pedidoId);
>>>>>>> b85cc7793ad42ad14d8b3a5307c8dfe08d1df517

    boolean existsByPedidoIdPedido(Integer pedidoId);
}
