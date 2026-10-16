package com.smartcarbo.dto;

public record MetaCaloricaResponse(
        Integer estimativaKcal,
        String status,
        String mensagem
) {
}