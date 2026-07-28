package com.arthur.controle_estoque_api.event;

public record EstoqueBaixoEvent(
        Long produtoId,
        String nomeProduto,
        Integer quantidadeAtual
) {
}