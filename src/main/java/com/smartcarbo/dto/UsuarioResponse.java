package com.smartcarbo.dto;

import com.smartcarbo.model.entity.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email
) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}