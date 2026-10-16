
package com.smartcarbo.model.service;

import com.smartcarbo.dto.MetaCaloricaResponse;
import com.smartcarbo.model.entity.Usuario;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Locale;

@Service
public class CalculoMetaCaloricaService {

    public MetaCaloricaResponse calcular(Usuario usuario) {
        if (usuario == null) {
            return resposta(null, "DADOS_INCOMPLETOS",
                    "Não foi possível localizar o perfil.");
        }

        LocalDate nascimento = usuario.getDataNascimento();
        Double peso = usuario.getPeso();
        Double altura = usuario.getAltura();

        if (nascimento == null || peso == null || altura == null) {
            return resposta(null, "DADOS_INCOMPLETOS",
                    "Complete sua data de nascimento, peso e altura no perfil.");
        }

        int idade = Period.between(nascimento, LocalDate.now()).getYears();

        if (idade < 0 || idade > 120 || peso <= 0 || altura <= 0) {
            return resposta(null, "DADOS_INVALIDOS",
                    "Confira os dados de nascimento, peso e altura.");
        }

        // Aceita altura em metros ou centímetros.
        double alturaMetros = altura > 3 ? altura / 100.0 : altura;

        if (alturaMetros < 0.3 || alturaMetros > 2.7) {
            return resposta(null, "DADOS_INVALIDOS",
                    "Confira a altura cadastrada no perfil.");
        }

        // Não definir metas calóricas automáticas para menores de idade.
        if (idade < 18) {
            return resposta(null, "ORIENTACAO_PROFISSIONAL",
                    "Para menores de 18 anos, as necessidades energéticas "
                    + "devem ser avaliadas individualmente por um profissional "
                    + "de saúde. Nenhuma meta numérica foi definida.");
        }

        String sexo = normalizar(usuario.getSexo());
        String atividade = normalizar(usuario.getNivelAtividade());
        String objetivo = normalizar(usuario.getObjetivo());

        if (atividade.isBlank() || objetivo.isBlank()) {
            return resposta(null, "DADOS_INCOMPLETOS",
                    "Informe o nível de atividade física e o objetivo no perfil.");
        }

        double alturaCm = alturaMetros * 100.0;

        // Equações de estimativa do metabolismo basal.
        double basalFeminino =
                10 * peso + 6.25 * alturaCm - 5 * idade - 161;

        double basalMasculino =
                10 * peso + 6.25 * alturaCm - 5 * idade + 5;

        double metabolismoBasal;

        if (sexo.equals("feminino") || sexo.equals("f")) {
            metabolismoBasal = basalFeminino;
        } else if (sexo.equals("masculino") || sexo.equals("m")) {
            metabolismoBasal = basalMasculino;
        } else if (sexo.equals("outro")) {
            // Aproximação matemática: média das duas equações.
            metabolismoBasal = (basalFeminino + basalMasculino) / 2.0;
        } else {
            return resposta(null, "DADOS_INCOMPLETOS",
                    "Confira o campo sexo no perfil para calcular a estimativa.");
        }

        double fator = obterFatorAtividade(atividade);

        if (fator <= 0) {
            return resposta(null, "DADOS_INCOMPLETOS",
                    "Selecione um nível de atividade física válido.");
        }

        double manutencao = metabolismoBasal * fator;

        if (!Double.isFinite(manutencao) || manutencao <= 0) {
            return resposta(null, "DADOS_INVALIDOS",
                    "Não foi possível calcular a estimativa energética.");
        }

        int estimativa;
        String mensagem;

        switch (objetivo) {
            case "perder peso":
            case "emagrecer":
                estimativa = (int) Math.round(manutencao * 0.90);
                mensagem = "Simulação inicial de meta para perda de peso, "
                        + "calculada a partir de uma estimativa de manutenção "
                        + "com redução ilustrativa de 10%. Não é prescrição "
                        + "nutricional; valide com um nutricionista.";
                break;

            case "ganhar peso":
                estimativa = (int) Math.round(manutencao * 1.10);
                mensagem = "Simulação inicial de meta para ganho de peso, "
                        + "calculada a partir de uma estimativa de manutenção "
                        + "com aumento ilustrativo de 10%. Não é prescrição "
                        + "nutricional; valide com um nutricionista.";
                break;

            case "ganhar massa muscular":
                estimativa = (int) Math.round(manutencao * 1.10);
                mensagem = "Simulação inicial para ganho de massa muscular, "
                        + "com aumento ilustrativo de 10% sobre a manutenção. "
                        + "O resultado não considera individualmente treino, "
                        + "composição corporal ou necessidades nutricionais. "
                        + "Procure orientação profissional.";
                break;

            case "manter peso e saude geral":
            case "manter meu peso e saude geral":
            case "manter meu peso":
            case "manter peso":
            case "manter-peso":
            case "manutencao":
                estimativa = (int) Math.round(manutencao);
                mensagem = "Estimativa diária de manutenção calculada "
                        + "considerando os dados e o nível de atividade. "
                        + "Não é uma prescrição nutricional.";
                break;

            case "melhorar minha alimentacao":
            case "melhorar alimentacao":
                return resposta(null, "ORIENTACAO_ALIMENTAR",
                        "Seu objetivo é melhorar a alimentação. Priorize "
                        + "refeições variadas e equilibradas. Não foi definida "
                        + "uma meta calórica específica para esse objetivo.");

            default:
                return resposta(null, "OBJETIVO_INVALIDO",
                        "Objetivo não reconhecido. Atualize o objetivo no perfil.");
        }

        if (estimativa <= 0) {
            return resposta(null, "DADOS_INVALIDOS",
                    "Não foi possível calcular a estimativa energética.");
        }

        if (sexo.equals("outro")) {
            mensagem += " A equação basal utiliza a média de duas equações "
                    + "como aproximação matemática, não como equação "
                    + "validada individualmente para este perfil.";
        }

        return resposta(estimativa, "ESTIMATIVA_DISPONIVEL", mensagem);
    }

    private double obterFatorAtividade(String atividade) {
        return switch (atividade) {
            case "sedentario", "sedentaria" -> 1.2;
            case "leve", "levemente ativo", "levemente ativa" -> 1.375;
            case "moderado", "moderada", "moderadamente ativo",
                    "moderadamente ativa" -> 1.55;
            case "alto", "alta", "muito ativo", "muito ativa" -> 1.725;
            case "muito alto", "muito alta" -> 1.9;
            default -> 0;
        };
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }

        return valor.trim()
                .toLowerCase(Locale.ROOT)
                .replace('á', 'a')
                .replace('à', 'a')
                .replace('ã', 'a')
                .replace('â', 'a')
                .replace('é', 'e')
                .replace('ê', 'e')
                .replace('í', 'i')
                .replace('ó', 'o')
                .replace('ô', 'o')
                .replace('õ', 'o')
                .replace('ú', 'u')
                .replace('ç', 'c')
                .replace('-', ' ')
                .replace('_', ' ')
                .replaceAll("\\s+", " ");
    }

    private MetaCaloricaResponse resposta(
            Integer estimativa,
            String status,
            String mensagem) {
        return new MetaCaloricaResponse(estimativa, status, mensagem);
    }
}
