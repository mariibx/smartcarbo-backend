package com.smartcarbo.controller;

import com.smartcarbo.dto.MetaCaloricaResponse;
import com.smartcarbo.dto.UsuarioAtualizacaoRequest;
import com.smartcarbo.dto.UsuarioCadastroRequest;
import com.smartcarbo.dto.UsuarioResponse;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.CalculoMetaCaloricaService;
import com.smartcarbo.model.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final CalculoMetaCaloricaService calculoMetaCaloricaService;

    public UsuarioController(
            UsuarioService usuarioService,
            CalculoMetaCaloricaService calculoMetaCaloricaService) {
        this.usuarioService = usuarioService;
        this.calculoMetaCaloricaService = calculoMetaCaloricaService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(
            @Valid @RequestBody UsuarioCadastroRequest request) {

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());
        usuario.setDataNascimento(request.dataNascimento());
        usuario.setAltura(request.altura());
        usuario.setPeso(request.peso());
        usuario.setSexo(request.sexo());
        usuario.setObjetivo(request.objetivo());
        usuario.setNivelAtividade(request.nivelAtividade());
        usuario.setRestricoes(request.restricoes());
        usuario.setCondicaoSaude(request.condicaoSaude());
        usuario.setAvaliacaoAlimentacao(request.avaliacaoAlimentacao());

        Usuario usuarioSalvo = usuarioService.salvar(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.de(usuarioSalvo));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> meuPerfil(
            Authentication authentication) {
        Usuario usuario = obterUsuarioAutenticado(authentication);
        return ResponseEntity.ok(UsuarioResponse.de(usuario));
    }

    @GetMapping("/me/meta-calorica")
    public ResponseEntity<MetaCaloricaResponse> minhaMetaCalorica(
            Authentication authentication) {
        Usuario usuario = obterUsuarioAutenticado(authentication);
        return ResponseEntity.ok(calculoMetaCaloricaService.calcular(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @PathVariable Long id,
            Authentication authentication) {

        Usuario autenticado = obterUsuarioAutenticado(authentication);
        verificarProprietario(id, autenticado);

        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado."));

        return ResponseEntity.ok(UsuarioResponse.de(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizacaoRequest request,
            Authentication authentication) {

        Usuario autenticado = obterUsuarioAutenticado(authentication);
        verificarProprietario(id, autenticado);

        Usuario dados = new Usuario();
        dados.setNome(request.nome());
        dados.setEmail(request.email());
        dados.setDataNascimento(request.dataNascimento());
        dados.setAltura(request.altura());
        dados.setPeso(request.peso());
        dados.setSexo(request.sexo());
        dados.setObjetivo(request.objetivo());
        dados.setNivelAtividade(request.nivelAtividade());
        dados.setRestricoes(request.restricoes());
        dados.setCondicaoSaude(request.condicaoSaude());
        dados.setAvaliacaoAlimentacao(request.avaliacaoAlimentacao());

        Usuario usuarioAtualizado = usuarioService.atualizar(id, dados);
        return ResponseEntity.ok(UsuarioResponse.de(usuarioAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            Authentication authentication) {

        Usuario autenticado = obterUsuarioAutenticado(authentication);
        verificarProprietario(id, autenticado);
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private Usuario obterUsuarioAutenticado(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }

        return usuarioService.buscarPorEmail(authentication.getName());
    }

    private void verificarProprietario(Long id, Usuario autenticado) {
        if (id == null
                || autenticado.getId() == null
                || !autenticado.getId().equals(id)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para acessar este usuário.");
        }
    }
}
