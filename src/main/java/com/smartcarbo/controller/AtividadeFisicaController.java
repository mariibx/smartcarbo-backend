package com.smartcarbo.controller;

import com.smartcarbo.model.entity.AtividadeFisica;
import com.smartcarbo.model.service.AtividadeFisicaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/atividades-fisicas")
public class AtividadeFisicaController {

    private final AtividadeFisicaService atividadeFisicaService;

    public AtividadeFisicaController(
            AtividadeFisicaService atividadeFisicaService) {
        this.atividadeFisicaService = atividadeFisicaService;
    }

    @GetMapping
    public ResponseEntity<List<AtividadeFisica>> listar() {
        return ResponseEntity.ok(atividadeFisicaService.listar());
    }

    @PostMapping
    public ResponseEntity<AtividadeFisica> salvar(
            @RequestBody(required = false) AtividadeFisica atividadeFisica) {

        if (atividadeFisica == null) {
            return ResponseEntity.badRequest().build();
        }

        AtividadeFisica novaAtividade =
                atividadeFisicaService.salvar(atividadeFisica);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novaAtividade);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtividadeFisica> buscarPorId(
            @PathVariable Long id) {

        return atividadeFisicaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtividadeFisica> atualizar(
            @PathVariable Long id,
            @RequestBody(required = false) AtividadeFisica atividadeFisica) {

        if (atividadeFisica == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                atividadeFisicaService.atualizar(id, atividadeFisica)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        atividadeFisicaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}