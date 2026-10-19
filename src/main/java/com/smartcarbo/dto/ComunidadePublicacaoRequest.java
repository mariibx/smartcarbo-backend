package com.smartcarbo.dto;

import jakarta.validation.constraints.Size;

public record ComunidadePublicacaoRequest(
    @Size(max = 500, message = "O texto pode ter no máximo 500 caracteres.")
    String text,

    @Size(max = 5_000_000, message = "A imagem enviada é muito grande. Escolha uma imagem menor.")
    String image,

    @Size(max = 80, message = "A categoria pode ter no máximo 80 caracteres.")
    String category
) {
}
