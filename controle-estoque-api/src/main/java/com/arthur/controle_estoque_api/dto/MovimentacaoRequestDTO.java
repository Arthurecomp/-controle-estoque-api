package com.arthur.controle_estoque_api.dto;


import com.arthur.controle_estoque_api.entity.TipoMovimentacao;


public class MovimentacaoRequestDTO {

    private TipoMovimentacao tipo;
    private Integer quantidade;
    private Long produtoId;
    //private Long usuarioId;

    public TipoMovimentacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    //public Long getUsuarioId() {
    //    return usuarioId;
    //}

    //public void setUsuarioId(Long usuarioId) {
//this.usuarioId = usuarioId;
    //}
}
