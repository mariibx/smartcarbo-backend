
package com.smartcarbo.model.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.smartcarbo.model.entity.RegistroAlimentacao;
import com.smartcarbo.model.repository.RefeicaoRepository;
import com.smartcarbo.model.repository.RegistroAlimentacaoRepository;

@Service
public class RegistroAlimentacaoService {

    private final RegistroAlimentacaoRepository repository;
    private final RefeicaoRepository refeicaoRepository;

    public RegistroAlimentacaoService(
            RegistroAlimentacaoRepository repository,
            RefeicaoRepository refeicaoRepository) {
        this.repository = repository;
        this.refeicaoRepository = refeicaoRepository;
    }

    public List<RegistroAlimentacao> listarPorUsuario(Long usuarioId) {
        validarUsuarioId(usuarioId);
        return repository.findAllByUsuarioIdOrderByDataHoraDesc(usuarioId);
    }

    public List<RegistroAlimentacao> listarPorUsuarioEPeriodo(
            Long usuarioId,
            LocalDateTime inicio,
            LocalDateTime fim) {

        validarUsuarioId(usuarioId);

        if (inicio == null || fim == null || !inicio.isBefore(fim)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O período informado é inválido.");
        }

        return repository
                .findAllByUsuarioIdAndDataHoraGreaterThanEqualAndDataHoraLessThan(
                        usuarioId, inicio, fim);
    }

    public RegistroAlimentacao buscarPorIdEUsuario(
            Long id, Long usuarioId) {

        validarId(id);
        validarUsuarioId(usuarioId);

        return repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(this::naoEncontrado);
    }

    @Transactional
    public RegistroAlimentacao salvarParaUsuario(
            RegistroAlimentacao registro, Long usuarioId) {

        validarUsuarioId(usuarioId);
        validarDados(registro);
        validarRefeicao(registro.getRefeicaoId(), usuarioId);

        registro.setId(null);
        registro.setUsuarioId(usuarioId);

        if (registro.getDataHora() == null) {
            registro.setDataHora(LocalDateTime.now());
        }

        return repository.save(registro);
    }

    @Transactional
    public RegistroAlimentacao atualizarParaUsuario(
            Long id,
            RegistroAlimentacao registro,
            Long usuarioId) {

        validarId(id);
        validarUsuarioId(usuarioId);
        validarDados(registro);

        RegistroAlimentacao existente =
                buscarPorIdEUsuario(id, usuarioId);

        // Se o cliente informar um novo vínculo, ele precisa ser válido.
        // Se não informar, o vínculo existente é preservado.
        if (registro.getRefeicaoId() != null) {
            validarRefeicao(registro.getRefeicaoId(), usuarioId);
            existente.setRefeicaoId(registro.getRefeicaoId());
        }

        existente.setNomeAlimento(registro.getNomeAlimento().trim());
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

        // Não permite alterar o usuário proprietário pelo corpo da requisição.
        return repository.save(existente);
    }

    @Transactional
    public void excluirParaUsuario(Long id, Long usuarioId) {
        validarId(id);
        validarUsuarioId(usuarioId);

        if (!repository.existsByIdAndUsuarioId(id, usuarioId)) {
            throw naoEncontrado();
        }

        repository.deleteByIdAndUsuarioId(id, usuarioId);
    }

    private void validarRefeicao(Long refeicaoId, Long usuarioId) {
        // NULL é permitido temporariamente para manter compatibilidade
        // com os registros antigos durante a migração.
        if (refeicaoId == null) {
            return;
        }

        if (refeicaoId <= 0
                || !refeicaoRepository.existsByIdAndUsuarioId(
                        refeicaoId, usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A refeição informada não existe ou não pertence ao usuário.");
        }
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

        if (registro.getTipoRefeicao() != null
                && registro.getTipoRefeicao().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O tipo de refeição não pode estar vazio.");
        }
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
