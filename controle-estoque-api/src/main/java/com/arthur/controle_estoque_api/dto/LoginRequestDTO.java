package com.arthur.controle_estoque_api.dto;

public record LoginRequestDTO(
        String email,
        String senha
) {}