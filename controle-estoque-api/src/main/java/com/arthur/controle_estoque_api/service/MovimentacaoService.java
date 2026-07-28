package com.arthur.controle_estoque_api.service;

import com.arthur.controle_estoque_api.entity.Movimentacao;
import com.arthur.controle_estoque_api.entity.Produto;
import com.arthur.controle_estoque_api.entity.TipoMovimentacao;
import com.arthur.controle_estoque_api.entity.Usuario;
import com.arthur.controle_estoque_api.event.EstoqueBaixoEvent;
import com.arthur.controle_estoque_api.exception.BadRequestException;
import com.arthur.controle_estoque_api.exception.ResourceNotFoundException;
import com.arthur.controle_estoque_api.rabbitmq.EstoqueBaixoProducer;
import com.arthur.controle_estoque_api.repository.MovimentacaoRepository;
import com.arthur.controle_estoque_api.repository.ProdutoRepository;
import com.arthur.controle_estoque_api.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstoqueBaixoProducer estoqueProducer;

    public MovimentacaoService(
            MovimentacaoRepository movimentacaoRepository,
            ProdutoRepository produtoRepository,
            UsuarioRepository usuarioRepository, EstoqueBaixoProducer estoqueProducer) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.estoqueProducer = estoqueProducer;
    }

    @Transactional
    public Movimentacao criarMovimentacao(Movimentacao movimentacao) throws InterruptedException {
        if(movimentacao.getProduto() == null
                || movimentacao.getUsuario() == null
                || movimentacao.getQuantidade() == null
                || movimentacao.getTipo() == null){
            throw new ResourceNotFoundException("Dados da movimentação incompletos") ;
        }

        if(movimentacao.getQuantidade() <= 0){
            throw new BadRequestException("Quantidade deve ser maior que zero");
        }

        Produto produto = produtoRepository.findByIdComLock(
                movimentacao.getProduto().getId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Produto não encontrado")
        );



        Usuario usuario = usuarioRepository.findById(
                movimentacao.getUsuario().getId()
        ).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado")
        );

        if (!produto.getLoja().getId().equals(usuario.getLoja().getId())) {
            throw new BadRequestException("Operação negada: Este produto pertence a outra loja.");
        }

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

        if(produto.getQuantidade()<= produto.getEstoqueMinimo()){
            EstoqueBaixoEvent evento = new EstoqueBaixoEvent(
                    produto.getId(),
                    produto.getNome(),
                    produto.getQuantidade()
            );
            estoqueProducer.enviarEstoqueBaixo(evento);
        }

        return movimentacaoRepository.save(movimentacao);
    }

    // READ ALL (Filtrado por Loja do Usuário Logado)
    public List<Movimentacao> buscarTodas(){
        Usuario logado = getUsuarioLogado();
        return movimentacaoRepository.findByProdutoLojaId(logado.getLoja().getId());
    }

    // READ BY ID (Com Validação de Loja)
    public Movimentacao buscarPorId(Long id){
        Movimentacao movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimentação não encontrada"));

        validarMesmaLoja(movimentacao);
        return movimentacao;
    }

    // READ BY PRODUTO (Filtrado e Validado)
    public List<Movimentacao> buscarPorProduto(Long produtoId){
        Usuario logado = getUsuarioLogado();

        // Busca as movimentações do produto
        List<Movimentacao> movimentacoes = movimentacaoRepository.findByProdutoId(produtoId);

        // Garante que o produto consultado pertence à mesma loja do usuário logado
        if (!movimentacoes.isEmpty()) {
            validarMesmaLoja(movimentacoes.get(0));
        }

        return movimentacoes;
    }

    // READ BY USUÁRIO (Filtrado e Validado)
    public List<Movimentacao> buscarPorUsuario(Long usuarioId){
        Usuario logado = getUsuarioLogado();

        // 1. Busca o usuário que se deseja consultar no banco
        Usuario usuarioConsultado = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // 2. Garante que o usuário consultado pertence à mesma loja do administrador logado
        if (usuarioConsultado.getLoja() == null ||
                !usuarioConsultado.getLoja().getId().equals(logado.getLoja().getId())) {
            throw new BadRequestException("Operação negada: Este usuário pertence a outra loja.");
        }

        // 3. Se a loja for a mesma, aí sim busca e retorna as movimentações com segurança
        return movimentacaoRepository.findByUsuarioId(usuarioId);
    }
    // ==================== MÉTODOS AUXILIARES DE TENANT ====================

    private Usuario getUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Usuario usuario) {
            return usuario;
        }
        throw new BadRequestException("Usuário não autenticado no contexto de segurança.");
    }

    private void validarMesmaLoja(Movimentacao movimentacao) {
        Usuario logado = getUsuarioLogado();

        // Navega através do produto da movimentação para achar o ID da loja
        if (movimentacao.getProduto() == null || movimentacao.getProduto().getLoja() == null
                || !movimentacao.getProduto().getLoja().getId().equals(logado.getLoja().getId())) {
            throw new BadRequestException("Operação negada: Esta movimentação pertence a outra loja.");
        }
    }
}
