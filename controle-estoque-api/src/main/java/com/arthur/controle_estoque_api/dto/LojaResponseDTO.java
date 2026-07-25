package com.arthur.controle_estoque_api.dto;


public class LojaResponseDTO {

    private Long id;
    private String nome;

    public LojaResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
