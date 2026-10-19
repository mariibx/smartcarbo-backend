package com.smartcarbo.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartcarbo.model.entity.CurtidaPublicacao;

@Repository
public interface CurtidaPublicacaoRepository
        extends JpaRepository<CurtidaPublicacao, Long> {

    boolean existsByPublicacaoIdAndUsuarioId(Long publicacaoId, Long usuarioId);

    long countByPublicacaoId(Long publicacaoId);

    void deleteByPublicacaoIdAndUsuarioId(Long publicacaoId, Long usuarioId);

    void deleteAllByPublicacaoId(Long publicacaoId);
}
