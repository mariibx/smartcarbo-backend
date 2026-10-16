package com.smartcarbo.model.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.smartcarbo.model.dto.EvolucaoCaloriasDTO;
import com.smartcarbo.model.entity.RegistroAlimentacao;
import com.smartcarbo.model.repository.RegistroAlimentacaoRepository;

@Service
public class EvolucaoCaloriasService {

    private final RegistroAlimentacaoRepository repository;

    public EvolucaoCaloriasService(
            RegistroAlimentacaoRepository repository) {
        this.repository = repository;
    }

    public List<EvolucaoCaloriasDTO> consultar(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fim) {

        if (usuarioId == null || usuarioId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário autenticado inválido.");
        }

        if (inicio == null || fim == null || inicio.isAfter(fim)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe um período válido.");
        }

        if (inicio.plusDays(365).isBefore(fim)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O período máximo permitido é de 366 dias.");
        }

        LocalDateTime inicioData = inicio.atStartOfDay();
        LocalDateTime fimExclusivo = fim.plusDays(1).atStartOfDay();

        List<RegistroAlimentacao> registros =
                repository
                    .findAllByUsuarioIdAndDataHoraGreaterThanEqualAndDataHoraLessThan(
                            usuarioId, inicioData, fimExclusivo);

        Map<LocalDate, Double> totais = new TreeMap<>();

        for (RegistroAlimentacao registro : registros) {
            if (registro.getDataHora() == null) {
                continue;
            }

            double calorias = registro.getCalorias() == null
                    ? 0.0
                    : registro.getCalorias();

            LocalDate dia = registro.getDataHora().toLocalDate();

            totais.merge(dia, calorias, Double::sum);
        }

        List<EvolucaoCaloriasDTO> resultado = new ArrayList<>();

        for (LocalDate dia = inicio;
                !dia.isAfter(fim);
                dia = dia.plusDays(1)) {

            resultado.add(new EvolucaoCaloriasDTO(
                    dia,
                    totais.getOrDefault(dia, 0.0)));
        }

        return resultado;
    }
}