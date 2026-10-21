
package com.smartcarbo.model.service;

import com.smartcarbo.model.entity.Notificacao;
import com.smartcarbo.model.repository.NotificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(
            NotificacaoRepository notificacaoRepository
    ) {
        this.notificacaoRepository = notificacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listar(Long destinatarioId) {
        return notificacaoRepository
                .findByDestinatarioIdOrderByCriadoEmDesc(
                        destinatarioId
                );
    }

    @Transactional(readOnly = true)
    public List<Notificacao> listarNaoLidas(Long destinatarioId) {
        return notificacaoRepository
                .findByDestinatarioIdAndLidaFalseOrderByCriadoEmDesc(
                        destinatarioId
                );
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(Long destinatarioId) {
        return notificacaoRepository
                .countByDestinatarioIdAndLidaFalse(destinatarioId);
    }

    @Transactional
    public void marcarComoLida(Long notificacaoId, Long destinatarioId) {
        Notificacao notificacao = notificacaoRepository
                .findByIdAndDestinatarioId(
                        notificacaoId,
                        destinatarioId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Notificação não encontrada."
                        )
                );

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void marcarTodasComoLidas(Long destinatarioId) {
        List<Notificacao> notificacoes =
                notificacaoRepository
                        .findByDestinatarioIdAndLidaFalseOrderByCriadoEmDesc(
                                destinatarioId
                        );

        notificacoes.forEach(notificacao ->
                notificacao.setLida(true)
        );

        notificacaoRepository.saveAll(notificacoes);
    }

    @Transactional
    public Notificacao criar(
            Long destinatarioId,
            Long atorId,
            String tipo,
            String titulo,
            String mensagem,
            Long referenciaId
    ) {
        Notificacao notificacao = new Notificacao();

        notificacao.setDestinatarioId(destinatarioId);
        notificacao.setAtorId(atorId);
        notificacao.setTipo(tipo);
        notificacao.setTitulo(titulo);
        notificacao.setMensagem(mensagem);
        notificacao.setReferenciaId(referenciaId);
        notificacao.setLida(false);

        return notificacaoRepository.save(notificacao);
    }
}
