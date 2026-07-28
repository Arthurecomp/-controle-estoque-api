package com.arthur.controle_estoque_api.service;

import com.arthur.controle_estoque_api.entity.*;
import com.arthur.controle_estoque_api.event.EstoqueBaixoEvent;
import com.arthur.controle_estoque_api.exception.BadRequestException;
import com.arthur.controle_estoque_api.exception.ResourceNotFoundException;
import com.arthur.controle_estoque_api.rabbitmq.EstoqueBaixoProducer;
import com.arthur.controle_estoque_api.repository.MovimentacaoRepository;
import com.arthur.controle_estoque_api.repository.ProdutoRepository;
import com.arthur.controle_estoque_api.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MovimentacaoServiceTest {


    @Mock
    private MovimentacaoRepository movimentacaoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EstoqueBaixoProducer estoqueBaixoProducer;


    @InjectMocks
    private MovimentacaoService movimentacaoService;



    private Loja criarLoja(Long id){

        Loja loja = new Loja();
        loja.setId(id);

        return loja;
    }


    private Produto criarProduto(){

        Produto produto = new Produto();

        produto.setId(1L);
        produto.setNome("Teclado");
        produto.setQuantidade(100);
        produto.setEstoqueMinimo(10);
        produto.setLoja(criarLoja(1L));

        return produto;
    }


    private Usuario criarUsuario(){

        Usuario usuario = new Usuario();

        usuario.setId(1L);
        usuario.setNome("Arthur");
        usuario.setLoja(criarLoja(1L));

        return usuario;
    }





    @Test
    void deveRealizarEntradaDeProduto() throws InterruptedException {

        Produto produto = criarProduto();
        Usuario usuario = criarUsuario();


        Movimentacao movimentacao = new Movimentacao();

        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setQuantidade(20);
        movimentacao.setTipo(TipoMovimentacao.ENTRADA);



        when(produtoRepository.findByIdComLock(1L))
                .thenReturn(Optional.of(produto));


        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));


        when(movimentacaoRepository.save(any()))
                .thenReturn(movimentacao);



        Movimentacao resultado =
                movimentacaoService.criarMovimentacao(movimentacao);




        assertEquals(120, produto.getQuantidade());

        verify(produtoRepository)
                .save(produto);

        verify(movimentacaoRepository)
                .save(movimentacao);

    }






    @Test
    void deveRealizarSaidaDeProduto() throws InterruptedException {


        Produto produto = criarProduto();
        Usuario usuario = criarUsuario();


        Movimentacao movimentacao = new Movimentacao();

        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setQuantidade(30);
        movimentacao.setTipo(TipoMovimentacao.SAIDA);



        when(produtoRepository.findByIdComLock(1L))
                .thenReturn(Optional.of(produto));


        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));


        when(movimentacaoRepository.save(any()))
                .thenReturn(movimentacao);



        movimentacaoService.criarMovimentacao(movimentacao);



        assertEquals(70, produto.getQuantidade());

    }







    @Test
    void naoDevePermitirSaidaMaiorQueEstoque(){


        Produto produto = criarProduto();

        Usuario usuario = criarUsuario();



        Movimentacao movimentacao = new Movimentacao();

        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setQuantidade(200);
        movimentacao.setTipo(TipoMovimentacao.SAIDA);



        when(produtoRepository.findByIdComLock(1L))
                .thenReturn(Optional.of(produto));


        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));




        assertThrows(
                BadRequestException.class,
                () -> movimentacaoService.criarMovimentacao(movimentacao)
        );

    }







    @Test
    void deveLancarErroQuandoProdutoNaoExiste(){

        Movimentacao movimentacao = new Movimentacao();

        Produto produto = new Produto();
        produto.setId(99L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setQuantidade(10);
        movimentacao.setTipo(TipoMovimentacao.ENTRADA);


        when(produtoRepository.findByIdComLock(99L))
                .thenReturn(Optional.empty());


        assertThrows(
                ResourceNotFoundException.class,
                () -> movimentacaoService.criarMovimentacao(movimentacao)
        );


        verify(produtoRepository)
                .findByIdComLock(99L);
    }








    @Test
    void deveBloquearProdutoDeOutraLoja(){


        Produto produto = criarProduto();


        Usuario usuario = criarUsuario();


        usuario.setLoja(criarLoja(2L));



        Movimentacao movimentacao = new Movimentacao();

        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setQuantidade(10);
        movimentacao.setTipo(TipoMovimentacao.ENTRADA);



        when(produtoRepository.findByIdComLock(1L))
                .thenReturn(Optional.of(produto));


        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));



        assertThrows(
                BadRequestException.class,
                () -> movimentacaoService.criarMovimentacao(movimentacao)
        );


    }








    @Test
    void deveEnviarEventoQuandoEstoqueFicarBaixo() throws InterruptedException {


        Produto produto = criarProduto();

        produto.setQuantidade(15);
        produto.setEstoqueMinimo(10);


        Usuario usuario = criarUsuario();



        Movimentacao movimentacao = new Movimentacao();


        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);
        movimentacao.setQuantidade(10);
        movimentacao.setTipo(TipoMovimentacao.SAIDA);



        when(produtoRepository.findByIdComLock(1L))
                .thenReturn(Optional.of(produto));


        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));



        movimentacaoService.criarMovimentacao(movimentacao);



        verify(estoqueBaixoProducer)
                .enviarEstoqueBaixo(any(EstoqueBaixoEvent.class));

    }


}