package com.smartcarbo.controller;

import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @PostMapping
    public ResponseEntity<Usuario> salvar(
            @RequestBody(required = false) Usuario usuario) {

        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }

        Usuario novoUsuario = usuarioService.salvar(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novoUsuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(
            @PathVariable Long id) {

        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(
            @PathVariable Long id,
            @RequestBody(required = false) Usuario usuario) {

        if (usuario == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                usuarioService.atualizar(id, usuario)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}