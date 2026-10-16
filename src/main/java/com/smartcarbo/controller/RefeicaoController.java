
package com.smartcarbo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.smartcarbo.model.entity.Refeicao;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.RefeicaoService;
import com.smartcarbo.model.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/refeicoes")
public class RefeicaoController {

    private final RefeicaoService refeicaoService;
    private final UsuarioService usuarioService;

    public RefeicaoController(
            RefeicaoService refeicaoService,
            UsuarioService usuarioService) {
        this.refeicaoService = refeicaoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Refeicao>> listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data,
            Authentication authentication) {

        Usuario usuario = obterUsuario(authentication);

        List<Refeicao> refeicoes;

        if (data != null) {
            refeicoes = refeicaoService.listarPorUsuarioEData(
                    usuario.getId(), data);
        } else {
            refeicoes = refeicaoService.listarPorUsuario(
                    usuario.getId());
        }

        return ResponseEntity.ok(refeicoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Refeicao> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {

        Usuario usuario = obterUsuario(authentication);

        Refeicao refeicao = refeicaoService.buscarPorIdEUsuario(
                id, usuario.getId());

        return ResponseEntity.ok(refeicao);
    }

    @PostMapping
    public ResponseEntity<Refeicao> criar(
            @RequestBody(required = false) Refeicao refeicao,
            Authentication authentication) {

        Usuario usuario = obterUsuario(authentication);

        Refeicao criada = refeicaoService.salvarParaUsuario(
                refeicao, usuario.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Refeicao> atualizar(
            @PathVariable Long id,
            @RequestBody(required = false) Refeicao dados,
            Authentication authentication) {

        Usuario usuario = obterUsuario(authentication);

        Refeicao atualizada = refeicaoService.atualizarParaUsuario(
                id, dados, usuario.getId());

        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication) {

        Usuario usuario = obterUsuario(authentication);

        refeicaoService.excluirParaUsuario(id, usuario.getId());

        return ResponseEntity.noContent().build();
    }

    private Usuario obterUsuario(Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().equals("anonymousUser")) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário não autenticado.");
        }

        Usuario usuario = usuarioService.buscarPorEmail(
                authentication.getName());

        if (usuario == null || usuario.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário autenticado não encontrado.");
        }

        return usuario;
    }
}
