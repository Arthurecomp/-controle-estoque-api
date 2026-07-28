package com.arthur.controle_estoque_api.controller;

import com.arthur.controle_estoque_api.dto.LojaRequestDTO;
import com.arthur.controle_estoque_api.dto.LojaResponseDTO;
import com.arthur.controle_estoque_api.entity.Loja;
import com.arthur.controle_estoque_api.service.LojaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/loja")
@Tag(name = "Lojas", description = "Operações relacionadas as lojas")
public class LojaController {
    private final LojaService lojaService;

    public LojaController(LojaService lojaService) {
        this.lojaService = lojaService;
    }


    @PostMapping
    public ResponseEntity<LojaResponseDTO> criar(@RequestBody LojaRequestDTO novaLoja){

        Loja loja = new Loja();
        loja.setNome(novaLoja.getNome());

        Loja lojaSalva = lojaService.criarLoja(loja);

        LojaResponseDTO lojaResponseDTO = new LojaResponseDTO();
        lojaResponseDTO.setNome(lojaSalva.getNome());
        lojaResponseDTO.setId(lojaSalva.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(lojaResponseDTO);
    }


    @GetMapping("/{id}")
    public  ResponseEntity<LojaResponseDTO> buscarPorId(@PathVariable Long id){
        Loja loja= lojaService.buscarPorId(id);

        LojaResponseDTO lojaResponseDTO = new LojaResponseDTO();
        lojaResponseDTO.setNome(loja.getNome());
        lojaResponseDTO.setId(loja.getId());

        return ResponseEntity.ok(lojaResponseDTO);
    }

    /*
    @GetMapping
    public ResponseEntity<List<LojaResponseDTO>> buscarTodos() {

        List<Loja> lojas = lojaService.buscarTodas();

        List<LojaResponseDTO> resposta = new ArrayList<>();

        for (Loja loja : lojas) {
            LojaResponseDTO dto = new LojaResponseDTO();
            dto.setId(loja.getId());
            dto.setNome(loja.getNome());

            resposta.add(dto);
        }


        return ResponseEntity.ok(resposta);
    }
*/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        lojaService.deletarLoja(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LojaResponseDTO> atualizar(@PathVariable Long id, @RequestBody LojaRequestDTO lojaRequestDTO){
        Loja loja = new Loja();
        loja.setNome(lojaRequestDTO.getNome());
        Loja lojaAtualizada = lojaService.atualizarLoja(id, loja);
        LojaResponseDTO lojaResponseDTO = new LojaResponseDTO();
        lojaResponseDTO.setId(lojaAtualizada.getId());
        lojaResponseDTO.setNome(lojaAtualizada.getNome());
        return ResponseEntity.ok(lojaResponseDTO);
    }





}
