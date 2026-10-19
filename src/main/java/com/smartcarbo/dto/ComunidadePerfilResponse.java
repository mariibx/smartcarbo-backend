package com.smartcarbo.dto;

public record ComunidadePerfilResponse(
    Long id,
    String name,
    String goal,
    boolean followed
) {
}
