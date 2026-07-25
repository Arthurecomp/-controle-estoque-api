package com.arthur.controle_estoque_api.controller;

import com.arthur.controle_estoque_api.dto.ProdutoRequestDTO;
import com.arthur.controle_estoque_api.dto.ProdutoResponseDTO;
import com.arthur.controle_estoque_api.entity.Loja;
import com.arthur.controle_estoque_api.entity.Produto;
import com.arthur.controle_estoque_api.service.LojaService;
import com.arthur.controle_estoque_api.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/produto")
@Tag(name = "Produtos", description = "Operações relacionadas aos produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final LojaService lojaService;

    public ProdutoController(ProdutoService produtoService, LojaService lojaService) {
        this.produtoService = produtoService;
        this.lojaService = lojaService;
    }

    @Operation(summary = "Cadastrar produto")
    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@RequestBody ProdutoRequestDTO novoProduto){

        Produto produto = new Produto();

        produto.setNome(novoProduto.getNome());
        produto.setSku(novoProduto.getSku());
        produto.setEstoqueMinimo(novoProduto.getEstoqueMinimo());

        Loja loja = lojaService.buscarPorId(novoProduto.getLojaId());
        produto.setLoja(loja);

        // estoque inicial
        produto.setQuantidade(0);

        Produto produtoSalvo = produtoService.criarProduto(produto);

        ProdutoResponseDTO resposta = converterParaDTO(produtoSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id){

        Produto produto = produtoService.buscarPorId(id);

        ProdutoResponseDTO resposta = converterParaDTO(produto);

        return ResponseEntity.ok(resposta);
    }


    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> buscarTodos(){

        List<Produto> produtos = produtoService.buscarTodos();

        List<ProdutoResponseDTO> resposta = new ArrayList<>();

        for(Produto produto : produtos){

            resposta.add(converterParaDTO(produto));

        }

        return ResponseEntity.ok(resposta);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody ProdutoRequestDTO produtoDTO){


        Produto produtoAtualizado = new Produto();

        produtoAtualizado.setNome(produtoDTO.getNome());
        produtoAtualizado.setSku(produtoDTO.getSku());
        produtoAtualizado.setEstoqueMinimo(produtoDTO.getEstoqueMinimo());


        Loja loja = lojaService.buscarPorId(produtoDTO.getLojaId());
        produtoAtualizado.setLoja(loja);


        Produto produto = produtoService.atualizarProduto(id, produtoAtualizado);


        ProdutoResponseDTO resposta = converterParaDTO(produto);

        return ResponseEntity.ok(resposta);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){

        produtoService.deletarProduto(id);

        return ResponseEntity.noContent().build();
    }



    private ProdutoResponseDTO converterParaDTO(Produto produto){

        ProdutoResponseDTO dto = new ProdutoResponseDTO();

        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setSku(produto.getSku());
        dto.setQuantidade(produto.getQuantidade());
        dto.setEstoqueMinimo(produto.getEstoqueMinimo());
        dto.setLojaId(produto.getLoja().getId());
        return dto;
    }

}