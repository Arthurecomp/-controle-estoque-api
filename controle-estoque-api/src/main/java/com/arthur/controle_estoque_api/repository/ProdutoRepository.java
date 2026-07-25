package com.arthur.controle_estoque_api.repository;

import com.arthur.controle_estoque_api.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findBySkuAndLojaId(String sku, Long lojaId );
}
