package com.smartcarbo.dto;

import java.time.LocalDate;

public record PesoResponse(
        Long id,
        Double peso,
        LocalDate data
) {
}