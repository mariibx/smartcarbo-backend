package com.smartcarbo.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.smartcarbo.model.entity.RegistroAlimentacao;
import com.smartcarbo.model.repository.RegistroAlimentacaoRepository;

@Service
public class RegistroAlimentacaoService {

    private final RegistroAlimentacaoRepository registroAlimentacaoRepository;

    public RegistroAlimentacaoService(
            RegistroAlimentacaoRepository registroAlimentacaoRepository) {
        this.registroAlimentacaoRepository = registroAlimentacaoRepository;
    }

    public RegistroAlimentacao salvar(RegistroAlimentacao registro) {
        if (registro == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados do registro alimentar são obrigatórios."
            );
        }

        validarDados(registro);

        // O ID é gerado pelo banco no cadastro.
        registro.setId(null);

        if (registro.getDataHora() == null) {
            registro.setDataHora(java.time.LocalDateTime.now());
        }

        return registroAlimentacaoRepository.save(registro);
    }

    public List<RegistroAlimentacao> listar() {
        return registroAlimentacaoRepository.findAll();
    }

    public Optional<RegistroAlimentacao> buscarPorId(Long id) {
        validarId(id);
        return registroAlimentacaoRepository.findById(id);
    }

    public RegistroAlimentacao atualizar(
            Long id,
            RegistroAlimentacao registro) {

        validarId(id);

        if (registro == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados do registro alimentar são obrigatórios."
            );
        }

        validarDados(registro);

        RegistroAlimentacao existente =
                registroAlimentacaoRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Registro alimentar não encontrado."
                        ));

        existente.setNomeAlimento(registro.getNomeAlimento());
        existente.setQuantidade(registro.getQuantidade());
        existente.setTipoRefeicao(registro.getTipoRefeicao());
        existente.setCalorias(registro.getCalorias());
        existente.setCarboidratos(registro.getCarboidratos());
        existente.setProteinas(registro.getProteinas());
        existente.setGorduras(registro.getGorduras());
        existente.setFibras(registro.getFibras());
        existente.setSodio(registro.getSodio());
        existente.setDataHora(registro.getDataHora());
        existente.setUsuarioId(registro.getUsuarioId());

        return registroAlimentacaoRepository.save(existente);
    }

    public void excluir(Long id) {
        validarId(id);

        if (!registroAlimentacaoRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Registro alimentar não encontrado."
            );
        }

        registroAlimentacaoRepository.deleteById(id);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID do registro alimentar deve ser válido."
            );
        }
    }

    private void validarDados(RegistroAlimentacao registro) {
        if (registro.getNomeAlimento() == null
                || registro.getNomeAlimento().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome do alimento é obrigatório."
            );
        }

        if (registro.getQuantidade() == null
                || registro.getQuantidade() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade deve ser maior que zero."
            );
        }

        validarValorNaoNegativo(registro.getCalorias(), "calorias");
        validarValorNaoNegativo(registro.getCarboidratos(), "carboidratos");
        validarValorNaoNegativo(registro.getProteinas(), "proteínas");
        validarValorNaoNegativo(registro.getGorduras(), "gorduras");
        validarValorNaoNegativo(registro.getFibras(), "fibras");
        validarValorNaoNegativo(registro.getSodio(), "sódio");
    }

    private void validarValorNaoNegativo(Double valor, String campo) {
        if (valor != null && (!Double.isFinite(valor) || valor < 0)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O valor de " + campo + " deve ser não negativo e válido."
            );
        }
    }
}