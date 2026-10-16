
package com.smartcarbo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data,
            Authentication authentication) {

        Usuario usuario = obterUsuario(authentication);

        if (data == null) {
            return ResponseEntity.ok(
                    registroService.listarPorUsuario(usuario.getId()));
        }

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();

        return ResponseEntity.ok(
                registroService.listarPorUsuarioEPeriodo(
                        usuario.getId(), inicio, fim));
    }

    // Compatibilidade com chamadas Java existentes.
    public ResponseEntity<List<RegistroAlimentacao>> listar(
            Authentication authentication) {
        return listar(null, authentication);
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

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "É necessário estar autenticado.");
        }

        return usuarioService.buscarPorEmail(authentication.getName());
    }
}
