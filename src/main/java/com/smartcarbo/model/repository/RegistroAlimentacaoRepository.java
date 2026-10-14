
package com.smartcarbo.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcarbo.model.entity.RegistroAlimentacao;

public interface RegistroAlimentacaoRepository
        extends JpaRepository<RegistroAlimentacao, Long> {

    List<RegistroAlimentacao> findAllByUsuarioIdOrderByDataHoraDesc(
            Long usuarioId);

    Optional<RegistroAlimentacao> findByIdAndUsuarioId(
            Long id, Long usuarioId);

    boolean existsByIdAndUsuarioId(Long id, Long usuarioId);

    void deleteByIdAndUsuarioId(Long id, Long usuarioId);
}
