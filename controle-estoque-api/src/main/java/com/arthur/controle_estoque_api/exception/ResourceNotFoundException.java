package com.arthur.controle_estoque_api.exception;


//Algo não existe: Loja, produto, usuario, movimentação não encontrada
public class ResourceNotFoundException  extends RuntimeException{
    public  ResourceNotFoundException(String mensagem){
        super(mensagem);
    }
}
