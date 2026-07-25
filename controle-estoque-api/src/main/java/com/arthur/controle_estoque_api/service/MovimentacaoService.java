package com.arthur.controle_estoque_api.service;

import com.arthur.controle_estoque_api.entity.Movimentacao;
import com.arthur.controle_estoque_api.entity.Produto;
import com.arthur.controle_estoque_api.entity.TipoMovimentacao;
import com.arthur.controle_estoque_api.entity.Usuario;
import com.arthur.controle_estoque_api.exception.BadRequestException;
import com.arthur.controle_estoque_api.exception.ResourceNotFoundException;
import com.arthur.controle_estoque_api.repository.MovimentacaoRepository;
import com.arthur.controle_estoque_api.repository.ProdutoRepository;
import com.arthur.controle_estoque_api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;


    public MovimentacaoService(
            MovimentacaoRepository movimentacaoRepository,
            ProdutoRepository produtoRepository,
            UsuarioRepository usuarioRepository) {

        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Movimentacao criarMovimentacao(Movimentacao movimentacao) {

        if(movimentacao.getProduto() == null
                || movimentacao.getUsuario() == null
                || movimentacao.getQuantidade() == null
                || movimentacao.getTipo() == null){

            throw new ResourceNotFoundException("Dados da movimentação incompletos") ;
        }


        if(movimentacao.getQuantidade() <= 0){
            throw new BadRequestException("Quantidade deve ser maior que zero");
        }


        Produto produto = produtoRepository.findById(
                movimentacao.getProduto().getId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Produto não encontrado")
        );


        Usuario usuario = usuarioRepository.findById(
                movimentacao.getUsuario().getId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado")
        );


        if(movimentacao.getTipo() == TipoMovimentacao.ENTRADA){

            produto.setQuantidade(
                    produto.getQuantidade() + movimentacao.getQuantidade()
            );

        } else if(movimentacao.getTipo() == TipoMovimentacao.SAIDA){

            if(produto.getQuantidade() < movimentacao.getQuantidade()){
                throw new BadRequestException("Estoque insuficiente") ;
            }

            produto.setQuantidade(
                    produto.getQuantidade() - movimentacao.getQuantidade()
            );
        }


        produtoRepository.save(produto);


        movimentacao.setData(LocalDateTime.now());
        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);


        return movimentacaoRepository.save(movimentacao);
    }


    public List<Movimentacao> buscarTodas(){

        return movimentacaoRepository.findAll();
    }

    public Movimentacao buscarPorId(Long id){

        return movimentacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimentação não encontrada"));
    }

    public List<Movimentacao> buscarPorProduto(Long produtoId){

        return movimentacaoRepository.findByProdutoId(produtoId);
    }

    public List<Movimentacao> buscarPorUsuario(Long usuarioId){

        return movimentacaoRepository.findByUsuarioId(usuarioId);
    }

}