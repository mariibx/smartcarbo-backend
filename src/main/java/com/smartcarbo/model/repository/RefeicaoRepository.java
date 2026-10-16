
package com.smartcarbo.model.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcarbo.model.entity.Refeicao;

public interface RefeicaoRepository extends JpaRepository<Refeicao, Long> {

    List<Refeicao> findAllByUsuarioIdOrderByDataRefeicaoDesc(
            Long usuarioId
    );

    List<Refeicao> findAllByUsuarioIdAndDataRefeicao(
            Long usuarioId,
            LocalDate dataRefeicao
    );

    Optional<Refeicao> findByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );

    boolean existsByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );

    void deleteByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );
}
