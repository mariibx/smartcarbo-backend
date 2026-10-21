
package com.smartcarbo.model.repository;

import com.smartcarbo.model.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificacaoRepository
        extends JpaRepository<Notificacao, Long> {

    List<Notificacao>
    findByDestinatarioIdOrderByCriadoEmDesc(Long destinatarioId);

    List<Notificacao>
    findByDestinatarioIdAndLidaFalseOrderByCriadoEmDesc(
            Long destinatarioId
    );

    long countByDestinatarioIdAndLidaFalse(Long destinatarioId);

    Optional<Notificacao> findByIdAndDestinatarioId(
            Long id,
            Long destinatarioId
    );
}
