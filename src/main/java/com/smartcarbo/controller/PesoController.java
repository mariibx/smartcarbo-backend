
package com.smartcarbo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.smartcarbo.dto.PesoRequest;
import com.smartcarbo.dto.PesoResponse;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.PesoService;
import com.smartcarbo.model.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/pesos")
public class PesoController {

    private final PesoService pesoService;
    private final UsuarioService usuarioService;

    public PesoController(
            PesoService pesoService,
            UsuarioService usuarioService) {
        this.pesoService = pesoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<PesoResponse>> listar(
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.ok(
                pesoService.listar(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PesoResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.ok(
                pesoService.buscarPorId(id, usuarioId));
    }

    @PostMapping
    public ResponseEntity<PesoResponse> cadastrar(
            @Valid @RequestBody PesoRequest request,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pesoService.cadastrar(request, usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PesoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PesoRequest request,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        return ResponseEntity.ok(
                pesoService.atualizar(id, request, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication) {

        Long usuarioId = obterUsuarioId(authentication);

        pesoService.excluir(id, usuarioId);

        return ResponseEntity.noContent().build();
    }

    private Long obterUsuarioId(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado.");
        }

        Usuario usuario = usuarioService.buscarPorEmail(
                authentication.getName());

        return usuario.getId();
    }
}
