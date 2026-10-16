
package com.smartcarbo.model.repository;

import java.time.LocalDateTime;
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

    boolean existsByIdAndUsuarioId(
            Long id, Long usuarioId);

    void deleteByIdAndUsuarioId(
            Long id, Long usuarioId);

    List<RegistroAlimentacao>
            findAllByUsuarioIdAndDataHoraGreaterThanEqualAndDataHoraLessThan(
                    Long usuarioId,
                    LocalDateTime inicio,
                    LocalDateTime fim);

    // Localiza os alimentos vinculados a uma refeição de um usuário.
    List<RegistroAlimentacao> findAllByRefeicaoIdAndUsuarioId(
            Long refeicaoId,
            Long usuarioId);

    // Exclui somente os alimentos vinculados à refeição daquele usuário.
    void deleteAllByRefeicaoIdAndUsuarioId(
            Long refeicaoId,
            Long usuarioId);
}
