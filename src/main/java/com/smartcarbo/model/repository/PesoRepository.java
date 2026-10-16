
package com.smartcarbo.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartcarbo.model.entity.HistoricoPeso;

@Repository
public interface PesoRepository
        extends JpaRepository<HistoricoPeso, Long> {

    List<HistoricoPeso> findAllByUsuarioIdOrderByDataAsc(
            Long usuarioId);

    Optional<HistoricoPeso> findByIdAndUsuarioId(
            Long id, Long usuarioId);

    boolean existsByIdAndUsuarioId(
            Long id, Long usuarioId);

    void deleteByIdAndUsuarioId(
            Long id, Long usuarioId);
}
