package com.arthur.controle_estoque_api.exception;

//Qnd envia dadlos invalidos: Nome vazio, quantidade <=0, estoque minimo negativo
public class BadRequestException extends RuntimeException{
    public BadRequestException (String mensagem){
        super(mensagem);
    }
}
