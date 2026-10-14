
package com.smartcarbo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.smartcarbo.model.entity.RegistroAlimentacao;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.RegistroAlimentacaoService;
import com.smartcarbo.model.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/registros-alimentacao")
public class RegistroAlimentacaoController {

    private final RegistroAlimentacaoService registroService;
    private final UsuarioService usuarioService;

    public RegistroAlimentacaoController(
            RegistroAlimentacaoService registroService,
            UsuarioService usuarioService) {
        this.registroService = registroService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroAlimentacao>> listar(
            Authentication authentication) {

        Usuario usuario = obterUsuario(authentication);

        return ResponseEntity.ok(
                registroService.listarPorUsuario(usuario.getId()));
    }

    @PostMapping
    public ResponseEntity<RegistroAlimentacao> salvar(
            @RequestBody(required = false) RegistroAlimentacao registro,
            Authentication authentication) {

        Usuario usuario = obterUsuario(authentication);

        RegistroAlimentacao novo =
                registroService.salvarParaUsuario(
                        registro, usuario.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroAlimentacao> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {

        Usuario usuario = obterUsuario(authentication);

        return ResponseEntity.ok(
                registroService.buscarPorIdEUsuario(
                        id, usuario.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroAlimentacao> atualizar(
            @PathVariable Long id,
            @RequestBody(required = false) RegistroAlimentacao registro,
            Authentication authentication) {

        Usuario usuario = obterUsuario(authentication);

        return ResponseEntity.ok(
                registroService.atualizarParaUsuario(
                        id, registro, usuario.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication) {

        Usuario usuario = obterUsuario(authentication);

        registroService.excluirParaUsuario(id, usuario.getId());

        return ResponseEntity.noContent().build();
    }

    private Usuario obterUsuario(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()
                || "anonymousUser".equals(authentication.getName())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "É necessário estar autenticado.");
        }

        return usuarioService.buscarPorEmail(authentication.getName());
    }
}
