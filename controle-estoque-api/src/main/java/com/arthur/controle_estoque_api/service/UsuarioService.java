package com.arthur.controle_estoque_api.service;

import com.arthur.controle_estoque_api.entity.Role;
import com.arthur.controle_estoque_api.entity.Usuario;
import com.arthur.controle_estoque_api.exception.BadRequestException;
import com.arthur.controle_estoque_api.exception.ConflictException;
import com.arthur.controle_estoque_api.exception.ResourceNotFoundException;
import com.arthur.controle_estoque_api.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // CREATE
    public Usuario criarUsuario(Usuario usuario) {

        if(usuario.getNome() == null || usuario.getNome().isBlank()
                || usuario.getEmail() == null || usuario.getEmail().isBlank()
                || usuario.getSenhaHash() == null || usuario.getSenhaHash().isBlank()
                || usuario.getLoja() == null){

            throw new BadRequestException("Algum campo está faltando");
        }

        Usuario logado = getUsuarioLogado();

        if (!usuario.getLoja().getId().equals(logado.getLoja().getId())) {
            throw new BadRequestException("Operação negada: Você só pode cadastrar usuários para a sua própria loja.");
        }

        Optional<Usuario> user = usuarioRepository.findByEmail(usuario.getEmail());

        if(user.isPresent()){
            throw new ConflictException("Email já cadastrado");
        }
        usuario.setCriadoEm(LocalDateTime.now());

        usuario.setSenhaHash(
                passwordEncoder.encode(usuario.getSenhaHash())
        );

        if(usuario.getRole() == null){
            usuario.setRole(Role.ESTOQUISTA);
        }

        return usuarioRepository.save(usuario);
    }


    // READ ALL
    public List<Usuario> buscarTodos(){

        Usuario logado = getUsuarioLogado();
        return usuarioRepository.findByLojaId(logado.getLoja().getId());

    }


    // READ BY ID
    public Usuario buscarPorId(Long id){

        Usuario usuarioBuscado = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        validarMesmaLoja(usuarioBuscado);
        return usuarioBuscado;
    }



// UPDATE
public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado){
    Usuario usuario = buscarPorId(id); // O buscarPorId já valida a loja implicitamente
    if(usuarioAtualizado.getNome() != null){
        usuario.setNome(usuarioAtualizado.getNome());
    }

    if(usuarioAtualizado.getEmail() != null){
        Optional<Usuario> user = usuarioRepository.findByEmail(usuarioAtualizado.getEmail());
        if(user.isPresent() && !user.get().getId().equals(id)){
            throw new ConflictException("Email já cadastrado");
        }
        usuario.setEmail(usuarioAtualizado.getEmail());
    }

    return usuarioRepository.save(usuario);
}


    // DELETE
    public void deletarUsuario(Long id){
        Usuario usuario = buscarPorId(id); // O buscarPorId já valida a loja implicitamente
        usuarioRepository.delete(usuario);
    }



    private Usuario getUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Usuario usuario) {
            return usuario;
        }
        throw new BadRequestException("Usuário não autenticado no contexto de segurança.");
    }

    private void validarMesmaLoja(Usuario usuarioAlvo) {
        Usuario logado = getUsuarioLogado();
        if (usuarioAlvo.getLoja() == null || logado.getLoja() == null
                || !usuarioAlvo.getLoja().getId().equals(logado.getLoja().getId())) {
            throw new BadRequestException("Operação negada: Este usuário pertence a outra loja.");
        }
    }
}