package com.arthur.controle_estoque_api.entity;

public enum Role {

    ADMIN("ROLE_ADMIN"),
    ESTOQUISTA("ROLE_ESTOQUISTA");

    private String role;

    Role(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}

