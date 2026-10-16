
package com.smartcarbo.controller;

import com.smartcarbo.model.entity.RegistroAgua;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.RegistroAguaService;
import com.smartcarbo.model.service.UsuarioService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/agua")
public class RegistroAguaController {

    private final RegistroAguaService registroAguaService;
    private final UsuarioService usuarioService;

    public RegistroAguaController(
            RegistroAguaService registroAguaService,
            UsuarioService usuarioService) {
        this.registroAguaService = registroAguaService;
        this.usuarioService = usuarioService;
    }

    private Long obterUsuarioId(Authentication authentication) {
        Usuario usuario =
                usuarioService.buscarPorEmail(authentication.getName());
        return usuario.getId();
    }

    @PostMapping
    public ResponseEntity<RegistroAgua> registrar(
            @RequestBody Map<String, Integer> request,
            Authentication authentication) {

        Integer quantidadeMl = request.get("quantidadeMl");

        RegistroAgua registro = registroAguaService.registrar(
                obterUsuarioId(authentication), quantidadeMl);

        return ResponseEntity.status(HttpStatus.CREATED).body(registro);
    }

    @GetMapping
    public ResponseEntity<List<RegistroAgua>> listar(
            Authentication authentication) {

        return ResponseEntity.ok(
                registroAguaService.listar(obterUsuarioId(authentication)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroAgua> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                registroAguaService.buscarPorId(
                        id, obterUsuarioId(authentication)));
    }

    @GetMapping("/data/{data}")
    public ResponseEntity<List<RegistroAgua>> listarPorData(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data,
            Authentication authentication) {

        return ResponseEntity.ok(
                registroAguaService.listarPorData(
                        obterUsuarioId(authentication), data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroAgua> atualizar(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request,
            Authentication authentication) {

        RegistroAgua registro = registroAguaService.atualizar(
                id,
                obterUsuarioId(authentication),
                request.get("quantidadeMl"));

        return ResponseEntity.ok(registro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication) {

        registroAguaService.excluir(
                id, obterUsuarioId(authentication));

        return ResponseEntity.noContent().build();
    }
}
