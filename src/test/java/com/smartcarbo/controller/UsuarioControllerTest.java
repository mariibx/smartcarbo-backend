
package com.smartcarbo.controller;

import com.smartcarbo.dto.UsuarioAtualizacaoRequest;
import com.smartcarbo.dto.UsuarioCadastroRequest;
import com.smartcarbo.dto.UsuarioResponse;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.CalculoMetaCaloricaService;
import com.smartcarbo.model.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CalculoMetaCaloricaService calculoMetaCaloricaService;

    private UsuarioController controller;

    @BeforeEach
    void configurar() {
        controller = new UsuarioController(
                usuarioService,
                calculoMetaCaloricaService
        );
    }

    private Authentication autenticarComo(String email) {
        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                java.util.List.of(
                        new org.springframework.security.core.authority
                                .SimpleGrantedAuthority("ROLE_USER")
                )
        );
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário de Teste");
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("hash-da-senha");
        usuario.setDataNascimento(LocalDate.of(2000, 1, 1));
        return usuario;
    }

    private UsuarioAtualizacaoRequest criarRequestAtualizacao() {
        return new UsuarioAtualizacaoRequest(
                "Usuário de Teste",
                "teste@teste.com",
                LocalDate.of(2000, 1, 1),
                1.70,
                65.0,
                "Masculino",
                "Emagrecer",
                "Moderado",
                "Nenhuma",
                "Nenhuma",
                8
        );
    }

    @Test
    void deveCadastrarUsuario() {
        Usuario usuario = criarUsuario();

        UsuarioCadastroRequest request = new UsuarioCadastroRequest(
                "Usuário de Teste",
                "teste@teste.com",
                "senha123",
                LocalDate.of(2000, 1, 1),
                1.70,
                65.0,
                "Masculino",
                "Emagrecer",
                "Moderado",
                "Nenhuma",
                "Nenhuma",
                8
        );

        when(usuarioService.salvar(any(Usuario.class)))
                .thenReturn(usuario);

        ResponseEntity<UsuarioResponse> resposta =
                controller.cadastrar(request);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1L, resposta.getBody().id());
        assertEquals("Usuário de Teste", resposta.getBody().nome());
        assertEquals("teste@teste.com", resposta.getBody().email());

        verify(usuarioService).salvar(any(Usuario.class));
    }

    @Test
    void deveConsultarProprioPerfil() {
        Usuario usuario = criarUsuario();
        Authentication authentication =
                autenticarComo("teste@teste.com");

        when(usuarioService.buscarPorEmail("teste@teste.com"))
                .thenReturn(usuario);

        ResponseEntity<UsuarioResponse> resposta =
                controller.meuPerfil(authentication);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1L, resposta.getBody().id());
        assertEquals("Usuário de Teste", resposta.getBody().nome());

        verify(usuarioService).buscarPorEmail("teste@teste.com");
    }

    @Test
    void deveRetornar404QuandoPerfilNaoExistir() {
        Authentication authentication =
                autenticarComo("naoexiste@teste.com");

        when(usuarioService.buscarPorEmail("naoexiste@teste.com"))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> controller.meuPerfil(authentication)
        );

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void deveBuscarProprioUsuarioPorId() {
        Usuario usuario = criarUsuario();
        Authentication authentication =
                autenticarComo("teste@teste.com");

        when(usuarioService.buscarPorEmail("teste@teste.com"))
                .thenReturn(usuario);
        when(usuarioService.buscarPorId(1L))
                .thenReturn(Optional.of(usuario));

        ResponseEntity<UsuarioResponse> resposta =
                controller.buscarPorId(1L, authentication);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1L, resposta.getBody().id());
    }

    @Test
    void deveBloquearConsultaDeOutroUsuario() {
        Usuario usuario = criarUsuario();
        Authentication authentication =
                autenticarComo("teste@teste.com");

        when(usuarioService.buscarPorEmail("teste@teste.com"))
                .thenReturn(usuario);

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> controller.buscarPorId(2L, authentication)
        );

        assertEquals(HttpStatus.FORBIDDEN, erro.getStatusCode());
        verify(usuarioService, never()).buscarPorId(any());
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExistir() {
        Usuario usuario = criarUsuario();
        Authentication authentication =
                autenticarComo("teste@teste.com");

        when(usuarioService.buscarPorEmail("teste@teste.com"))
                .thenReturn(usuario);
        when(usuarioService.buscarPorId(1L))
                .thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> controller.buscarPorId(1L, authentication)
        );

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void deveAtualizarProprioUsuario() {
        Usuario usuario = criarUsuario();
        Authentication authentication =
                autenticarComo("teste@teste.com");

        when(usuarioService.buscarPorEmail("teste@teste.com"))
                .thenReturn(usuario);
        when(usuarioService.atualizar(
                eq(1L),
                any(Usuario.class)
        )).thenReturn(usuario);

        ResponseEntity<UsuarioResponse> resposta =
                controller.atualizar(
                        1L,
                        criarRequestAtualizacao(),
                        authentication
                );

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1L, resposta.getBody().id());

        verify(usuarioService).atualizar(
                eq(1L),
                any(Usuario.class)
        );
    }

    @Test
    void deveBloquearAtualizacaoDeOutroUsuario() {
        Usuario usuario = criarUsuario();
        Authentication authentication =
                autenticarComo("teste@teste.com");

        when(usuarioService.buscarPorEmail("teste@teste.com"))
                .thenReturn(usuario);

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> controller.atualizar(
                        2L,
                        criarRequestAtualizacao(),
                        authentication
                )
        );

        assertEquals(HttpStatus.FORBIDDEN, erro.getStatusCode());
        verify(usuarioService, never()).atualizar(any(), any());
    }

    @Test
    void deveExcluirProprioUsuario() {
        Usuario usuario = criarUsuario();
        Authentication authentication =
                autenticarComo("teste@teste.com");

        when(usuarioService.buscarPorEmail("teste@teste.com"))
                .thenReturn(usuario);

        ResponseEntity<Void> resposta =
                controller.excluir(1L, authentication);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(usuarioService).excluir(1L);
    }

    @Test
    void deveBloquearExclusaoDeOutroUsuario() {
        Usuario usuario = criarUsuario();
        Authentication authentication =
                autenticarComo("teste@teste.com");

        when(usuarioService.buscarPorEmail("teste@teste.com"))
                .thenReturn(usuario);

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> controller.excluir(2L, authentication)
        );

        assertEquals(HttpStatus.FORBIDDEN, erro.getStatusCode());
        verify(usuarioService, never()).excluir(any());
    }

    @Test
    void deveRejeitarUsuarioNaoAutenticado() {
        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> controller.meuPerfil(null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, erro.getStatusCode());
        verifyNoInteractions(usuarioService);
    }
}
