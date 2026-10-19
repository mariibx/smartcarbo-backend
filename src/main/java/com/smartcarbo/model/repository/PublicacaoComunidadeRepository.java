package com.smartcarbo.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartcarbo.model.entity.PublicacaoComunidade;

@Repository
public interface PublicacaoComunidadeRepository
        extends JpaRepository<PublicacaoComunidade, Long> {

    List<PublicacaoComunidade> findTop20ByOrderByCriadoEmDesc();

    boolean existsByIdAndUsuarioId(Long id, Long usuarioId);

    void deleteByIdAndUsuarioId(Long id, Long usuarioId);
}
