package com.smartcarbo.model.service;

import com.smartcarbo.model.entity.AtividadeFisica;
import com.smartcarbo.model.repository.AtividadeFisicaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AtividadeFisicaService {

    private final AtividadeFisicaRepository atividadeFisicaRepository;

    public AtividadeFisicaService(
            AtividadeFisicaRepository atividadeFisicaRepository) {
        this.atividadeFisicaRepository = atividadeFisicaRepository;
    }

    public AtividadeFisica salvar(AtividadeFisica atividadeFisica) {
        validarDados(atividadeFisica);

        // O ID é gerado pelo banco no cadastro.
        atividadeFisica.setId(null);

        return atividadeFisicaRepository.save(atividadeFisica);
    }

    public List<AtividadeFisica> listar() {
        return atividadeFisicaRepository.findAll();
    }

    public Optional<AtividadeFisica> buscarPorId(Long id) {
        validarId(id);
        return atividadeFisicaRepository.findById(id);
    }

    public AtividadeFisica atualizar(
            Long id,
            AtividadeFisica atividadeFisica) {

        validarId(id);
        validarDados(atividadeFisica);

        AtividadeFisica existente = atividadeFisicaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Atividade física não encontrada."
                ));

        existente.setNome(atividadeFisica.getNome());
        existente.setDescricao(atividadeFisica.getDescricao());
        existente.setDuracao(atividadeFisica.getDuracao());
        existente.setCalorias(atividadeFisica.getCalorias());

        return atividadeFisicaRepository.save(existente);
    }

    public void excluir(Long id) {
        validarId(id);

        if (!atividadeFisicaRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Atividade física não encontrada."
            );
        }

        atividadeFisicaRepository.deleteById(id);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID da atividade física deve ser válido."
            );
        }
    }

    private void validarDados(AtividadeFisica atividadeFisica) {
        if (atividadeFisica == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados da atividade física são obrigatórios."
            );
        }

        if (atividadeFisica.getNome() == null
                || atividadeFisica.getNome().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da atividade física é obrigatório."
            );
        }

        if (atividadeFisica.getDuracao() == null
                || atividadeFisica.getDuracao() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A duração deve ser maior que zero."
            );
        }

        if (atividadeFisica.getCalorias() != null
                && atividadeFisica.getCalorias() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "As calorias não podem ser negativas."
            );
        }
    }
}