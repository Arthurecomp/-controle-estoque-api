package com.arthur.controle_estoque_api.controller;
import com.arthur.controle_estoque_api.dto.UsuarioRequestDTO;
import com.arthur.controle_estoque_api.dto.UsuarioResponseDTO;
import com.arthur.controle_estoque_api.entity.Loja;
import com.arthur.controle_estoque_api.entity.Usuario;
import com.arthur.controle_estoque_api.service.LojaService;
import com.arthur.controle_estoque_api.service.UsuarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuarios", description = "Operações relacionadas aos usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final LojaService lojaService;

    public UsuarioController(UsuarioService usuarioService, LojaService lojaService) {
        this.usuarioService = usuarioService;
        this.lojaService = lojaService;
    }


    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody UsuarioRequestDTO novoUser){

        Usuario usuario = new Usuario();
        usuario.setNome(novoUser.getNome());
        usuario.setEmail(novoUser.getEmail());
        usuario.setSenhaHash(novoUser.getSenha());

        Loja loja = lojaService.buscarPorId(novoUser.getLojaId());
        usuario.setLoja(loja);

        Usuario usuarioSalvo = usuarioService.criarUsuario(usuario);

        UsuarioResponseDTO dto = converterParaDTO(usuarioSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id){

        Usuario usuario = usuarioService.buscarPorId(id);

        UsuarioResponseDTO dto = converterParaDTO(usuario);

        return ResponseEntity.ok(dto);
    }


    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> buscarTodos(){

        List<Usuario> usuarios = usuarioService.buscarTodos();

        List<UsuarioResponseDTO> resposta = new ArrayList<>();

        for(Usuario usuario : usuarios){

            resposta.add(converterParaDTO(usuario));

        }

        return ResponseEntity.ok(resposta);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody UsuarioRequestDTO usuarioDTO){

        Usuario usuarioAtualizado = new Usuario();

        usuarioAtualizado.setNome(usuarioDTO.getNome());
        usuarioAtualizado.setEmail(usuarioDTO.getEmail());
        usuarioAtualizado.setSenhaHash(usuarioDTO.getSenha());

        Loja loja = lojaService.buscarPorId(usuarioDTO.getLojaId());
        usuarioAtualizado.setLoja(loja);


        Usuario usuario = usuarioService.atualizarUsuario(id, usuarioAtualizado);


        UsuarioResponseDTO resposta = converterParaDTO(usuario);

        return ResponseEntity.ok(resposta);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){

        usuarioService.deletarUsuario(id);

        return ResponseEntity.noContent().build();
    }


    private UsuarioResponseDTO converterParaDTO(Usuario usuario){

        UsuarioResponseDTO dto = new UsuarioResponseDTO();

        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setLojaId(usuario.getLoja().getId());
        dto.setCriadoEm(usuario.getCriadoEm());
        dto.setRole(usuario.getRole());

        return dto;
    }

}