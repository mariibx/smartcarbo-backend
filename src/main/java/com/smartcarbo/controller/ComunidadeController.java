package com.smartcarbo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.smartcarbo.dto.ComunidadePerfilResponse;
import com.smartcarbo.dto.ComunidadePublicacaoRequest;
import com.smartcarbo.dto.ComunidadePublicacaoResponse;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.ComunidadeService;
import com.smartcarbo.model.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/comunidade")
public class ComunidadeController {

    private final ComunidadeService comunidadeService;
    private final UsuarioService usuarioService;

    public ComunidadeController(
            ComunidadeService comunidadeService,
            UsuarioService usuarioService) {
        this.comunidadeService = comunidadeService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/publicacoes")
    public ResponseEntity<List<ComunidadePublicacaoResponse>> listarPublicacoes(
            Authentication authentication) {
        return ResponseEntity.ok(
                comunidadeService.listarPublicacoes(obterUsuarioId(authentication)));
    }

    @PostMapping("/publicacoes")
    public ResponseEntity<ComunidadePublicacaoResponse> publicar(
            @Valid @RequestBody ComunidadePublicacaoRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                comunidadeService.publicar(request, obterUsuarioId(authentication)));
    }

    @DeleteMapping("/publicacoes/{id}")
    public ResponseEntity<Void> excluirPublicacao(
            @PathVariable Long id,
            Authentication authentication) {
        comunidadeService.excluirPublicacao(id, obterUsuarioId(authentication));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/publicacoes/{id}/curtida")
    public ResponseEntity<Void> curtir(
            @PathVariable Long id,
            Authentication authentication) {
        comunidadeService.curtir(id, obterUsuarioId(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/publicacoes/{id}/curtida")
    public ResponseEntity<Void> descurtir(
            @PathVariable Long id,
            Authentication authentication) {
        comunidadeService.descurtir(id, obterUsuarioId(authentication));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/perfis")
    public ResponseEntity<List<ComunidadePerfilResponse>> listarPerfis(
            Authentication authentication) {
        return ResponseEntity.ok(
                comunidadeService.listarPerfis(obterUsuarioId(authentication)));
    }

    @PostMapping("/perfis/{id}/seguir")
    public ResponseEntity<Void> seguir(
            @PathVariable Long id,
            Authentication authentication) {
        comunidadeService.seguir(id, obterUsuarioId(authentication));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/perfis/{id}/seguir")
    public ResponseEntity<Void> deixarDeSeguir(
            @PathVariable Long id,
            Authentication authentication) {
        comunidadeService.deixarDeSeguir(id, obterUsuarioId(authentication));
        return ResponseEntity.noContent().build();
    }

    private Long obterUsuarioId(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado.");
        }

        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        return usuario.getId();
    }
}
