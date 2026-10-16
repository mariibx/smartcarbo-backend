package com.smartcarbo.model.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smartcarbo.model.dto.DashboardResumoDTO;
import com.smartcarbo.model.entity.RegistroAlimentacao;
import com.smartcarbo.model.repository.RegistroAlimentacaoRepository;

@Service
public class DashboardService {

    private final RegistroAlimentacaoRepository repository;

    public DashboardService(
            RegistroAlimentacaoRepository repository) {
        this.repository = repository;
    }

    public DashboardResumoDTO obterResumo(
            Long usuarioId, LocalDate data) {

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();

        List<RegistroAlimentacao> registros =
                repository
                    .findAllByUsuarioIdAndDataHoraGreaterThanEqualAndDataHoraLessThan(
                            usuarioId, inicio, fim);

        DashboardResumoDTO resumo = new DashboardResumoDTO();

        resumo.setData(data);
        resumo.setQuantidadeRefeicoes(registros.size());

        for (RegistroAlimentacao registro : registros) {
            resumo.setCaloriasConsumidas(
                    resumo.getCaloriasConsumidas()
                    + valor(registro.getCalorias()));

            resumo.setCarboidratos(
                    resumo.getCarboidratos()
                    + valor(registro.getCarboidratos()));

            resumo.setProteinas(
                    resumo.getProteinas()
                    + valor(registro.getProteinas()));

            resumo.setGorduras(
                    resumo.getGorduras()
                    + valor(registro.getGorduras()));

            resumo.setFibras(
                    resumo.getFibras()
                    + valor(registro.getFibras()));

            resumo.setSodio(
                    resumo.getSodio()
                    + valor(registro.getSodio()));
        }

        return resumo;
    }

    private double valor(Double valor) {
        return valor == null ? 0.0 : valor;
    }
}