package com.smartcarbo.dto;

import java.time.LocalDate;
import com.smartcarbo.model.entity.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        LocalDate dataNascimento,
        Double altura,
        Double peso,
        String sexo,
        String objetivo,
        String nivelAtividade,
        String restricoes,
        String condicaoSaude,
        Integer avaliacaoAlimentacao
) {
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataNascimento(),
                usuario.getAltura(),
                usuario.getPeso(),
                usuario.getSexo(),
                usuario.getObjetivo(),
                usuario.getNivelAtividade(),
                usuario.getRestricoes(),
                usuario.getCondicaoSaude(),
                usuario.getAvaliacaoAlimentacao()
        );
    }
}