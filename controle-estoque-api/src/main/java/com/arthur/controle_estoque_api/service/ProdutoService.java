package com.arthur.controle_estoque_api.service;

import com.arthur.controle_estoque_api.entity.Produto;
import com.arthur.controle_estoque_api.exception.BadRequestException;
import com.arthur.controle_estoque_api.exception.ConflictException;
import com.arthur.controle_estoque_api.exception.ResourceNotFoundException;
import com.arthur.controle_estoque_api.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRespository) {
        this.produtoRepository = produtoRespository;
    }

    public Produto criarProduto(Produto produto){

        if(produto.getNome() == null || produto.getNome().isBlank()
                || produto.getSku() == null || produto.getSku().isBlank()
                || produto.getQuantidade() == null
                || produto.getEstoqueMinimo() == null
                || produto.getLoja() == null){

            throw new BadRequestException("Preencha todos os campos");
        }


        if(produto.getQuantidade() < 0
                || produto.getEstoqueMinimo() < 0){

            throw new BadRequestException("Quantidade não pode ser negativa");
        }


        Optional<Produto> produtoExistente =
                produtoRepository.findBySkuAndLojaId(
                        produto.getSku(),
                        produto.getLoja().getId()
                );


        if(produtoExistente.isPresent()){
            throw new ConflictException("SKU já cadastrado nessa loja");
        }


        return produtoRepository.save(produto);
    }

    // BUSCAR TODOS
    public List<Produto> buscarTodos(){

        return produtoRepository.findAll();
    }

    // BUSCAR POR ID
    public Produto buscarPorId(Long id){

        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
    }


    // ATUALIZAR
    public Produto atualizarProduto(Long id, Produto produtoAtualizado){

        Produto produto = buscarPorId(id);

        if(produtoAtualizado.getNome() != null){
            produto.setNome(produtoAtualizado.getNome());
        }

        if(produtoAtualizado.getEstoqueMinimo() != null){
            if(produtoAtualizado.getEstoqueMinimo() < 0){
                throw new BadRequestException("Estoque mínimo não pode ser negativo") ;
            }

            produto.setEstoqueMinimo(produtoAtualizado.getEstoqueMinimo());
        }

        return produtoRepository.save(produto);
    }

    public void deletarProduto(Long id){

        Produto produto = buscarPorId(id);

        produtoRepository.delete(produto);
    }
}
