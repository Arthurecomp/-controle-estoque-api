package com.arthur.controle_estoque_api.controller;


import com.arthur.controle_estoque_api.dto.MovimentacaoRequestDTO;
import com.arthur.controle_estoque_api.dto.MovimentacaoResponseDTO;
import com.arthur.controle_estoque_api.dto.ProdutoResponseDTO;
import com.arthur.controle_estoque_api.entity.Movimentacao;
import com.arthur.controle_estoque_api.entity.Produto;
import com.arthur.controle_estoque_api.entity.Usuario;
import com.arthur.controle_estoque_api.service.LojaService;
import com.arthur.controle_estoque_api.service.MovimentacaoService;
import com.arthur.controle_estoque_api.service.ProdutoService;
import com.arthur.controle_estoque_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/movimentacao")
@Tag(name = "Movimentacoes", description = "Operações relacionadas as movimentações")
public class MovimentacaoController {

    private final ProdutoService produtoService;

    private final UsuarioService usuarioService;
    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(ProdutoService produtoService, LojaService lojaService, UsuarioService usuarioService, MovimentacaoService movimentacaoService) {
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
        this.movimentacaoService = movimentacaoService;
    }

    @PostMapping
    public ResponseEntity<MovimentacaoResponseDTO> criar(@RequestBody MovimentacaoRequestDTO movimentacaoRequestDTO){
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo(movimentacaoRequestDTO.getTipo());
        movimentacao.setQuantidade(movimentacaoRequestDTO.getQuantidade());
        Usuario usuario = usuarioService.buscarPorId(movimentacaoRequestDTO.getUsuarioId());
        Produto produto = produtoService.buscarPorId(movimentacaoRequestDTO.getProdutoId());

        movimentacao.setProduto(produto);
        movimentacao.setUsuario(usuario);

        Movimentacao movimentacaoRealizada =  movimentacaoService.criarMovimentacao(movimentacao);

        MovimentacaoResponseDTO movimentacaoResponseDTO = converterParaDTO(movimentacaoRealizada);

        return  ResponseEntity.status(HttpStatus.CREATED).body(movimentacaoResponseDTO);


    }@GetMapping
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarTodas(){

        List<Movimentacao> movimentacoes = movimentacaoService.buscarTodas();

        List<MovimentacaoResponseDTO> resposta = new ArrayList<>();

        for(Movimentacao movimentacao : movimentacoes){

            resposta.add(converterParaDTO(movimentacao));

        }

        return ResponseEntity.ok(resposta);
    }


    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoResponseDTO> buscarPorId(
            @PathVariable Long id){

        Movimentacao movimentacao = movimentacaoService.buscarPorId(id);

        MovimentacaoResponseDTO resposta = converterParaDTO(movimentacao);

        return ResponseEntity.ok(resposta);
    }


    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarPorProduto(
            @PathVariable Long produtoId){

        List<Movimentacao> movimentacoes =
                movimentacaoService.buscarPorProduto(produtoId);

        List<MovimentacaoResponseDTO> resposta = new ArrayList<>();

        for(Movimentacao movimentacao : movimentacoes){

            resposta.add(converterParaDTO(movimentacao));

        }

        return ResponseEntity.ok(resposta);
    }


    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MovimentacaoResponseDTO>> buscarPorUsuario(
            @PathVariable Long usuarioId){

        List<Movimentacao> movimentacoes =
                movimentacaoService.buscarPorUsuario(usuarioId);

        List<MovimentacaoResponseDTO> resposta = new ArrayList<>();

        for(Movimentacao movimentacao : movimentacoes){

            resposta.add(converterParaDTO(movimentacao));

        }

        return ResponseEntity.ok(resposta);
    }


    private MovimentacaoResponseDTO converterParaDTO(Movimentacao movimentacao){

        MovimentacaoResponseDTO dto = new MovimentacaoResponseDTO();

        dto.setId(movimentacao.getId());
        dto.setTipo(movimentacao.getTipo());
        dto.setQuantidade(movimentacao.getQuantidade());
        dto.setProdutoId(movimentacao.getProduto().getId());
        dto.setUsuarioId(movimentacao.getUsuario().getId());

        return dto;
    }

}
