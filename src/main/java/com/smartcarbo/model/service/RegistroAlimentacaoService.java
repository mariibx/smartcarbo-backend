
package com.smartcarbo.model.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.smartcarbo.model.entity.RegistroAlimentacao;
import com.smartcarbo.model.repository.RegistroAlimentacaoRepository;

@Service
public class RegistroAlimentacaoService {

    private final RegistroAlimentacaoRepository repository;

    public RegistroAlimentacaoService(
            RegistroAlimentacaoRepository repository) {
        this.repository = repository;
    }

    // Métodos usados pelo usuário autenticado

    public List<RegistroAlimentacao> listarPorUsuario(Long usuarioId) {
        validarUsuarioId(usuarioId);
        return repository.findAllByUsuarioIdOrderByDataHoraDesc(usuarioId);
    }

    public RegistroAlimentacao buscarPorIdEUsuario(
            Long id, Long usuarioId) {
        validarId(id);
        validarUsuarioId(usuarioId);

        return repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> naoEncontrado());
    }

    public RegistroAlimentacao salvarParaUsuario(
            RegistroAlimentacao registro, Long usuarioId) {
        validarUsuarioId(usuarioId);
        validarDados(registro);

        registro.setId(null);
        registro.setUsuarioId(usuarioId);

        if (registro.getDataHora() == null) {
            registro.setDataHora(LocalDateTime.now());
        }

        return repository.save(registro);
    }

    public RegistroAlimentacao atualizarParaUsuario(
            Long id, RegistroAlimentacao registro, Long usuarioId) {
        validarId(id);
        validarUsuarioId(usuarioId);
        validarDados(registro);

        RegistroAlimentacao existente =
                buscarPorIdEUsuario(id, usuarioId);

        existente.setNomeAlimento(registro.getNomeAlimento());
        existente.setQuantidade(registro.getQuantidade());
        existente.setTipoRefeicao(registro.getTipoRefeicao());
        existente.setCalorias(registro.getCalorias());
        existente.setCarboidratos(registro.getCarboidratos());
        existente.setProteinas(registro.getProteinas());
        existente.setGorduras(registro.getGorduras());
        existente.setFibras(registro.getFibras());
        existente.setSodio(registro.getSodio());

        if (registro.getDataHora() != null) {
            existente.setDataHora(registro.getDataHora());
        }

        // O usuarioId existente não é alterado.
        return repository.save(existente);
    }

    public void excluirParaUsuario(Long id, Long usuarioId) {
        validarId(id);
        validarUsuarioId(usuarioId);

        if (!repository.existsByIdAndUsuarioId(id, usuarioId)) {
            throw naoEncontrado();
        }

        repository.deleteByIdAndUsuarioId(id, usuarioId);
    }

    // Métodos antigos mantidos para compatibilidade

    public RegistroAlimentacao salvar(RegistroAlimentacao registro) {
        validarDados(registro);

        registro.setId(null);

        if (registro.getDataHora() == null) {
            registro.setDataHora(LocalDateTime.now());
        }

        return repository.save(registro);
    }

    public List<RegistroAlimentacao> listar() {
        return repository.findAll();
    }

    public java.util.Optional<RegistroAlimentacao> buscarPorId(Long id) {
        validarId(id);
        return repository.findById(id);
    }

    public RegistroAlimentacao atualizar(
            Long id, RegistroAlimentacao registro) {
        validarId(id);
        validarDados(registro);

        RegistroAlimentacao existente = repository.findById(id)
                .orElseThrow(() -> naoEncontrado());

        copiarDados(registro, existente);
        existente.setUsuarioId(registro.getUsuarioId());

        return repository.save(existente);
    }

    public void excluir(Long id) {
        validarId(id);

        if (!repository.existsById(id)) {
            throw naoEncontrado();
        }

        repository.deleteById(id);
    }

    private void copiarDados(
            RegistroAlimentacao origem,
            RegistroAlimentacao destino) {
        destino.setNomeAlimento(origem.getNomeAlimento());
        destino.setQuantidade(origem.getQuantidade());
        destino.setTipoRefeicao(origem.getTipoRefeicao());
        destino.setCalorias(origem.getCalorias());
        destino.setCarboidratos(origem.getCarboidratos());
        destino.setProteinas(origem.getProteinas());
        destino.setGorduras(origem.getGorduras());
        destino.setFibras(origem.getFibras());
        destino.setSodio(origem.getSodio());
        destino.setDataHora(origem.getDataHora());
    }

    private void validarDados(RegistroAlimentacao registro) {
        if (registro == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados do registro alimentar são obrigatórios.");
        }

        if (registro.getNomeAlimento() == null
                || registro.getNomeAlimento().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome do alimento é obrigatório.");
        }

        if (registro.getQuantidade() == null
                || !Double.isFinite(registro.getQuantidade())
                || registro.getQuantidade() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade deve ser maior que zero.");
        }

        validarNaoNegativo(registro.getCalorias(), "calorias");
        validarNaoNegativo(registro.getCarboidratos(), "carboidratos");
        validarNaoNegativo(registro.getProteinas(), "proteínas");
        validarNaoNegativo(registro.getGorduras(), "gorduras");
        validarNaoNegativo(registro.getFibras(), "fibras");
        validarNaoNegativo(registro.getSodio(), "sódio");
    }

    private void validarNaoNegativo(Double valor, String campo) {
        if (valor != null && (!Double.isFinite(valor) || valor < 0)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O valor de " + campo
                            + " deve ser não negativo e válido.");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID do registro alimentar deve ser válido.");
        }
    }

    private void validarUsuarioId(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário autenticado inválido.");
        }
    }

    private ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Registro alimentar não encontrado.");
    }
}
