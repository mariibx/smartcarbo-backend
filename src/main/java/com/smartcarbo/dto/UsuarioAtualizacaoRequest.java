
package com.smartcarbo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UsuarioAtualizacaoRequest(

        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String nome,

        @Email(message = "Informe um e-mail válido.")
        @Size(max = 254, message = "O e-mail deve ter no máximo 254 caracteres.")
        String email,

        @Past(message = "A data de nascimento deve estar no passado.")
        LocalDate dataNascimento,

        @Positive(message = "A altura deve ser maior que zero.")
        @DecimalMin(value = "0.3", message = "A altura deve ser de pelo menos 0,3 metro.")
        Double altura,

        @Positive(message = "O peso deve ser maior que zero.")
        Double peso,

        @Size(max = 30, message = "O campo sexo deve ter no máximo 30 caracteres.")
        String sexo,

        @Size(max = 100, message = "O objetivo deve ter no máximo 100 caracteres.")
        String objetivo,

        @Size(max = 50, message = "O nível de atividade deve ter no máximo 50 caracteres.")
        String nivelAtividade,

        String restricoes,

        String condicaoSaude,

        @Positive(message = "A avaliação alimentar deve ser maior que zero.")
        Integer avaliacaoAlimentacao

) {
}
