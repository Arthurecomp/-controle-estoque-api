package com.arthur.controle_estoque_api.service;

import com.arthur.controle_estoque_api.entity.Loja;
import com.arthur.controle_estoque_api.exception.BadRequestException;
import com.arthur.controle_estoque_api.exception.ResourceNotFoundException;
import com.arthur.controle_estoque_api.repository.LojaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LojaService {

    private final LojaRepository lojaRepository;

    public LojaService(LojaRepository lojaRepository) {
        this.lojaRepository = lojaRepository;
    }

    public Loja criarLoja(Loja loja) {

        if (loja.getNome() == null || loja.getNome().isBlank()) {
            throw new BadRequestException("Nome da lója é obrigatório");
        }

        loja.setCriadoEm(LocalDateTime.now());

        return lojaRepository.save(loja);
    }


    public List<Loja> buscarTodas() {
        return lojaRepository.findAll();
    }


    public Loja buscarPorId(Long id) {

        return lojaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Loja não encontrada"));
    }


    public Loja atualizarLoja(Long id, Loja lojaAtualizada) {

        Loja loja = buscarPorId(id);

        if (lojaAtualizada.getNome() != null) {
            loja.setNome(lojaAtualizada.getNome());
        }

        return lojaRepository.save(loja);
    }


    public void deletarLoja(Long id) {

        Loja loja = buscarPorId(id);

        lojaRepository.delete(loja);
    }
}