
package com.smartcarbo.model.service;

import com.smartcarbo.model.entity.RegistroAgua;
import com.smartcarbo.model.repository.RegistroAguaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistroAguaService {

    private final RegistroAguaRepository repository;

    public RegistroAguaService(RegistroAguaRepository repository) {
        this.repository = repository;
    }

    // Cadastrar consumo de água
    @Transactional
    public RegistroAgua registrar(
            Long usuarioId, Integer quantidadeMl) {

        validarQuantidade(quantidadeMl);

        RegistroAgua registro = new RegistroAgua();
        registro.setUsuarioId(usuarioId);
        registro.setQuantidadeMl(quantidadeMl);
        registro.setDataHora(LocalDateTime.now());

        return repository.save(registro);
    }

    // Listar registros do usuário
    @Transactional(readOnly = true)
    public List<RegistroAgua> listar(Long usuarioId) {
        return repository
                .findAllByUsuarioIdOrderByDataHoraDesc(usuarioId);
    }

    // Buscar registro específico do usuário
    @Transactional(readOnly = true)
    public RegistroAgua buscarPorId(Long id, Long usuarioId) {
        return repository.findById(id)
                .filter(registro ->
                        registro.getUsuarioId().equals(usuarioId))
                .orElseThrow(() ->
                        new RegistroNaoEncontradoException(
                                "Registro de água não encontrado."));
    }

    // Atualizar quantidade consumida
    @Transactional
    public RegistroAgua atualizar(
            Long id,
            Long usuarioId,
            Integer quantidadeMl) {

        validarQuantidade(quantidadeMl);

        RegistroAgua registro = buscarPorId(id, usuarioId);
        registro.setQuantidadeMl(quantidadeMl);

        return repository.save(registro);
    }

    // Excluir registro do usuário
    @Transactional
    public void excluir(Long id, Long usuarioId) {
        RegistroAgua registro = buscarPorId(id, usuarioId);
        repository.delete(registro);
    }

    // Listar registros de um dia específico
    @Transactional(readOnly = true)
    public List<RegistroAgua> listarPorData(
            Long usuarioId, LocalDate data) {

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();

        return repository
                .findAllByUsuarioIdAndDataHoraGreaterThanEqualAndDataHoraLessThan(
                        usuarioId, inicio, fim);
    }

    // Validar quantidade de água
    private void validarQuantidade(Integer quantidadeMl) {
        if (quantidadeMl == null
                || quantidadeMl < 1
                || quantidadeMl > 5000) {

            throw new IllegalArgumentException(
                    "A quantidade deve estar entre 1 e 5000 ml.");
        }
    }

    // Exceção para registro não encontrado
    public static class RegistroNaoEncontradoException
            extends RuntimeException {

        public RegistroNaoEncontradoException(String mensagem) {
            super(mensagem);
        }
    }
}
