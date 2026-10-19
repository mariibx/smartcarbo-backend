package com.smartcarbo.dto;

import java.time.LocalDateTime;

public record ComunidadePublicacaoResponse(
    Long id,
    Long authorId,
    String author,
    String text,
    String image,
    String category,
    LocalDateTime createdAt,
    long likes,
    boolean likedByMe,
    boolean mine
) {
}
