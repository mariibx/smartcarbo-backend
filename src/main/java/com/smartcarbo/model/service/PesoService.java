
package com.smartcarbo.model.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.smartcarbo.dto.PesoRequest;
import com.smartcarbo.dto.PesoResponse;
import com.smartcarbo.model.entity.HistoricoPeso;
import com.smartcarbo.model.repository.PesoRepository;

@Service
public class PesoService {

    private final PesoRepository repository;

    public PesoService(PesoRepository repository) {
        this.repository = repository;
    }

    public List<PesoResponse> listar(Long usuarioId) {
        validarUsuarioId(usuarioId);

        return repository
                .findAllByUsuarioIdOrderByDataAsc(usuarioId)
                .stream()
                .map(this::paraResponse)
                .toList();
    }

    public PesoResponse buscarPorId(
            Long id, Long usuarioId) {

        validarId(id);
        validarUsuarioId(usuarioId);

        HistoricoPeso registro = repository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(this::naoEncontrado);

        return paraResponse(registro);
    }

    @Transactional
    public PesoResponse cadastrar(
            PesoRequest request, Long usuarioId) {

        validarUsuarioId(usuarioId);
        validarDados(request);

        HistoricoPeso registro = new HistoricoPeso();
        registro.setUsuarioId(usuarioId);
        registro.setPeso(request.peso());
        registro.setData(request.data());

        return paraResponse(repository.save(registro));
    }

    @Transactional
    public PesoResponse atualizar(
            Long id, PesoRequest request, Long usuarioId) {

        validarId(id);
        validarUsuarioId(usuarioId);
        validarDados(request);

        HistoricoPeso registro = repository
                .findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(this::naoEncontrado);

        registro.setPeso(request.peso());
        registro.setData(request.data());

        return paraResponse(repository.save(registro));
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        validarId(id);
        validarUsuarioId(usuarioId);

        if (!repository.existsByIdAndUsuarioId(id, usuarioId)) {
            throw naoEncontrado();
        }

        repository.deleteByIdAndUsuarioId(id, usuarioId);
    }

    private void validarDados(PesoRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados da medição são obrigatórios.");
        }

        if (request.peso() == null
                || !Double.isFinite(request.peso())
                || request.peso() < 20
                || request.peso() > 400) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O peso deve estar entre 20 e 400 kg.");
        }

        if (request.data() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data da medição é obrigatória.");
        }

        if (request.data().isAfter(
                java.time.LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data não pode estar no futuro.");
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID inválido.");
        }
    }

    private void validarUsuarioId(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário autenticado inválido.");
        }
    }

    private PesoResponse paraResponse(
            HistoricoPeso registro) {
        return new PesoResponse(
                registro.getId(),
                registro.getPeso(),
                registro.getData());
    }

    private ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Registro de peso não encontrado.");
    }
}
