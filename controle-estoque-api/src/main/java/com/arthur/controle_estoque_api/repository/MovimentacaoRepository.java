package com.arthur.controle_estoque_api.repository;

import com.arthur.controle_estoque_api.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findByProdutoId(Long produtoId);
    List<Movimentacao> findByUsuarioId(Long usuarioId);
}
