package com.arthur.controle_estoque_api.service;

import com.arthur.controle_estoque_api.entity.Produto;
import com.arthur.controle_estoque_api.entity.Usuario;
import com.arthur.controle_estoque_api.exception.BadRequestException;
import com.arthur.controle_estoque_api.exception.ConflictException;
import com.arthur.controle_estoque_api.exception.ResourceNotFoundException;
import com.arthur.controle_estoque_api.repository.ProdutoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto criarProduto(Produto produto){
        if(produto.getNome() == null || produto.getNome().isBlank()
                || produto.getSku() == null || produto.getSku().isBlank()
                || produto.getQuantidade() == null
                || produto.getEstoqueMinimo() == null
                || produto.getLoja() == null){
            throw new BadRequestException("Preencha todos os campos");
        }

        if(produto.getQuantidade() < 0 || produto.getEstoqueMinimo() < 0){
            throw new BadRequestException("Quantidade não pode ser negativa");
        }

        // Garante que o usuário só crie produtos vinculados à sua própria loja
        Usuario logado = getUsuarioLogado();
        if (!produto.getLoja().getId().equals(logado.getLoja().getId())) {
            throw new BadRequestException("Operação negada: Você só pode cadastrar produtos na sua própria loja.");
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

    // BUSCAR TODOS (Filtrado por Loja do Usuário Logado)
    public List<Produto> buscarTodos(){
        Usuario logado = getUsuarioLogado();
        return produtoRepository.findByLojaId(logado.getLoja().getId());
    }

    // BUSCAR POR ID (Com Validação de Loja)

    @Cacheable(value = "produtos", key = "#id")
    public Produto buscarPorId(Long id){
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        // Impede a visualização de produtos de outros tenants
        validarMesmaLoja(produto);

        return produto;
    }

    // ATUALIZAR
    @CacheEvict(value = "produtos", key = "#id")
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

    // DELETE
    public void deletarProduto(Long id){
        Produto produto = buscarPorId(id);
        produtoRepository.delete(produto);
    }


    private Usuario getUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Usuario usuario) {
            return usuario;
        }
        throw new BadRequestException("Usuário não autenticado no contexto de segurança.");
    }

    private void validarMesmaLoja(Produto produto) {
        Usuario logado = getUsuarioLogado();
        if (produto.getLoja() == null || logado.getLoja() == null
                || !produto.getLoja().getId().equals(logado.getLoja().getId())) {
            throw new BadRequestException("Operação negada: Este produto pertence a outra loja.");
        }
    }
}
