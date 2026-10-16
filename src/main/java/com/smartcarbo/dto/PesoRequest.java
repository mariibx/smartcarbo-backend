package com.smartcarbo.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record PesoRequest(

        @NotNull(message = "Informe o peso.")
        @DecimalMin(value = "20.0", message = "O peso mínimo é 20 kg.")
        @DecimalMax(value = "400.0", message = "O peso máximo é 400 kg.")
        Double peso,

        @NotNull(message = "Informe a data da medição.")
        @PastOrPresent(message = "A data não pode estar no futuro.")
        LocalDate data

) {
}