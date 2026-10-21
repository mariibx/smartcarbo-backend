
package com.smartcarbo.controller;

import com.smartcarbo.model.entity.Notificacao;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.NotificacaoService;
import com.smartcarbo.model.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final UsuarioService usuarioService;

    public NotificacaoController(
            NotificacaoService notificacaoService,
            UsuarioService usuarioService) {
        this.notificacaoService = notificacaoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Notificacao>> listar(
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.ok(
                notificacaoService.listar(usuarioId)
        );
    }

    @GetMapping("/nao-lidas")
    public ResponseEntity<List<Notificacao>> listarNaoLidas(
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.ok(
                notificacaoService.listarNaoLidas(usuarioId)
        );
    }

    @GetMapping("/nao-lidas/contagem")
    public ResponseEntity<Map<String, Long>> contarNaoLidas(
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        long quantidade =
                notificacaoService.contarNaoLidas(usuarioId);

        return ResponseEntity.ok(
                Map.of("quantidade", quantidade)
        );
    }

    @PatchMapping("/{id}/lida")
    public ResponseEntity<Void> marcarComoLida(
            @PathVariable Long id,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        try {
            notificacaoService.marcarComoLida(id, usuarioId);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Notificação não encontrada."
            );
        }
    }

    @PatchMapping("/lidas")
    public ResponseEntity<Void> marcarTodasComoLidas(
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        notificacaoService.marcarTodasComoLidas(usuarioId);

        return ResponseEntity.noContent().build();
    }

    private Long obterUsuarioId(Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado."
            );
        }

        Usuario usuario;

        try {
            usuario = usuarioService.buscarPorEmail(
                    authentication.getName()
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Não foi possível identificar o usuário autenticado."
            );
        }

        if (usuario == null || usuario.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário autenticado não encontrado."
            );
        }

        return usuario.getId();
    }
}
