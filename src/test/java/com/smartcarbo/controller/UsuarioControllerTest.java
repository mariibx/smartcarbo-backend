
package com.smartcarbo.controller;

import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    private UsuarioController controller;

    @BeforeEach
    void configurar() {
        controller = new UsuarioController(usuarioService);
    }

    @Test
    void deveListarUsuarios() {
        when(usuarioService.listar()).thenReturn(List.of(new Usuario()));

        ResponseEntity<List<Usuario>> resposta = controller.listar();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().size());
        verify(usuarioService).listar();
    }

    @Test
    void deveCadastrarUsuario() {
        Usuario usuario = criarUsuario();

        when(usuarioService.salvar(usuario)).thenReturn(usuario);

        ResponseEntity<Usuario> resposta = controller.salvar(usuario);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertSame(usuario, resposta.getBody());
        verify(usuarioService).salvar(usuario);
    }

    @Test
    void deveRejeitarCadastroNulo() {
        ResponseEntity<Usuario> resposta = controller.salvar(null);

        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        verifyNoInteractions(usuarioService);
    }

    @Test
    void deveBuscarUsuarioExistente() {
        Usuario usuario = criarUsuario();

        when(usuarioService.buscarPorId(1L))
                .thenReturn(Optional.of(usuario));

        ResponseEntity<Usuario> resposta = controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertSame(usuario, resposta.getBody());
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoExistir() {
        when(usuarioService.buscarPorId(99L))
                .thenReturn(Optional.empty());

        ResponseEntity<Usuario> resposta = controller.buscarPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
    }

    @Test
    void deveAtualizarUsuario() {
        Usuario usuario = criarUsuario();

        when(usuarioService.atualizar(1L, usuario))
                .thenReturn(usuario);

        ResponseEntity<Usuario> resposta =
                controller.atualizar(1L, usuario);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertSame(usuario, resposta.getBody());
    }

    @Test
    void deveRejeitarAtualizacaoNula() {
        ResponseEntity<Usuario> resposta = controller.atualizar(1L, null);

        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        verifyNoInteractions(usuarioService);
    }

    @Test
    void deveExcluirUsuario() {
        doNothing().when(usuarioService).excluir(1L);

        ResponseEntity<Void> resposta = controller.excluir(1L);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(usuarioService).excluir(1L);
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário de Teste");
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("hash-da-senha");
        return usuario;
    }
}
