package com.arthur.controle_estoque_api.exception;



//Email ja cadastrado, sku ja cadastrado na loja
public class ConflictException extends RuntimeException{

    public ConflictException(String message) {
        super(message);
    }
}
