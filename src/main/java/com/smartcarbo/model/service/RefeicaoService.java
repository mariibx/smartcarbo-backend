
package com.smartcarbo.model.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.smartcarbo.model.entity.Refeicao;
import com.smartcarbo.model.repository.RefeicaoRepository;
import com.smartcarbo.model.repository.RegistroAlimentacaoRepository;

@Service
public class RefeicaoService {

    private final RefeicaoRepository refeicaoRepository;
    private final RegistroAlimentacaoRepository registroRepository;

    public RefeicaoService(
            RefeicaoRepository refeicaoRepository,
            RegistroAlimentacaoRepository registroRepository) {
        this.refeicaoRepository = refeicaoRepository;
        this.registroRepository = registroRepository;
    }

    public List<Refeicao> listarPorUsuario(Long usuarioId) {
        validarUsuarioId(usuarioId);

        return refeicaoRepository
                .findAllByUsuarioIdOrderByDataRefeicaoDesc(usuarioId);
    }

    public List<Refeicao> listarPorUsuarioEData(
            Long usuarioId, LocalDate data) {
        validarUsuarioId(usuarioId);

        if (data == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data da refeição é obrigatória.");
        }

        return refeicaoRepository
                .findAllByUsuarioIdAndDataRefeicao(usuarioId, data);
    }

    public Refeicao buscarPorIdEUsuario(Long id, Long usuarioId) {
        validarId(id);
        validarUsuarioId(usuarioId);

        return refeicaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(this::naoEncontrado);
    }

    @Transactional
    public Refeicao salvarParaUsuario(
            Refeicao refeicao, Long usuarioId) {
        validarUsuarioId(usuarioId);
        validarDados(refeicao);

        refeicao.setId(null);
        refeicao.setUsuarioId(usuarioId);
        refeicao.setNome(refeicao.getNome().trim());

        return refeicaoRepository.save(refeicao);
    }

    @Transactional
    public Refeicao atualizarParaUsuario(
            Long id, Refeicao dados, Long usuarioId) {
        validarId(id);
        validarUsuarioId(usuarioId);
        validarDados(dados);

        Refeicao existente = buscarPorIdEUsuario(id, usuarioId);

        existente.setNome(dados.getNome().trim());
        existente.setDescricao(dados.getDescricao());
        existente.setDataRefeicao(dados.getDataRefeicao());

        return refeicaoRepository.save(existente);
    }

    @Transactional
    public void excluirParaUsuario(Long id, Long usuarioId) {
        validarId(id);
        validarUsuarioId(usuarioId);

        Refeicao refeicao = refeicaoRepository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(this::naoEncontrado);

        // Exclui somente os registros vinculados a esta refeição
        // e pertencentes ao usuário autenticado.
        registroRepository.deleteAllByRefeicaoIdAndUsuarioId(
                refeicao.getId(), usuarioId);

        // Remove a refeição depois dos registros associados.
        refeicaoRepository.delete(refeicao);
    }

    private void validarDados(Refeicao refeicao) {
        if (refeicao == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados da refeição são obrigatórios.");
        }

        if (refeicao.getNome() == null
                || refeicao.getNome().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome da refeição é obrigatório.");
        }

        if (refeicao.getDataRefeicao() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data da refeição é obrigatória.");
        }

        if (refeicao.getNome().trim().length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O nome deve ter no máximo 255 caracteres.");
        }

        if (refeicao.getDescricao() != null
                && refeicao.getDescricao().length() > 500) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A descrição deve ter no máximo 500 caracteres.");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID da refeição é inválido.");
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
                "Refeição não encontrada.");
    }
}
