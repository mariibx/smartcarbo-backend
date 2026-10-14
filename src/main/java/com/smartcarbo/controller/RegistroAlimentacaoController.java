package com.smartcarbo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.smartcarbo.model.entity.RegistroAlimentacao;
import com.smartcarbo.model.service.RegistroAlimentacaoService;

@RestController
@RequestMapping("/api/v1/registros-alimentacao")
public class RegistroAlimentacaoController {

    private final RegistroAlimentacaoService registroAlimentacaoService;

    public RegistroAlimentacaoController(
            RegistroAlimentacaoService registroAlimentacaoService) {
        this.registroAlimentacaoService = registroAlimentacaoService;
    }

    @GetMapping
    public ResponseEntity<List<RegistroAlimentacao>> listar() {
        return ResponseEntity.ok(registroAlimentacaoService.listar());
    }

    @PostMapping
    public ResponseEntity<RegistroAlimentacao> salvar(
            @RequestBody(required = false) RegistroAlimentacao registro) {

        if (registro == null) {
            return ResponseEntity.badRequest().build();
        }

        RegistroAlimentacao novoRegistro =
                registroAlimentacaoService.salvar(registro);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novoRegistro);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroAlimentacao> buscarPorId(
            @PathVariable Long id) {

        return registroAlimentacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroAlimentacao> atualizar(
            @PathVariable Long id,
            @RequestBody(required = false) RegistroAlimentacao registro) {

        if (registro == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                registroAlimentacaoService.atualizar(id, registro)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        registroAlimentacaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}