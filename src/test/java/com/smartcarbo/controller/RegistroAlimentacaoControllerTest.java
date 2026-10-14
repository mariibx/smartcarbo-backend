
package com.smartcarbo.controller;

import com.smartcarbo.model.entity.RegistroAlimentacao;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.RegistroAlimentacaoService;
import com.smartcarbo.model.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroAlimentacaoControllerTest {

    @Mock
    private RegistroAlimentacaoService registroService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private RegistroAlimentacaoController controller;

    private Authentication authentication;
    private Usuario usuario;
    private RegistroAlimentacao registro;

    @BeforeEach
    void configurar() {
        authentication = new UsernamePasswordAuthenticationToken(
                "teste@smartcarbo.com",
                "token",
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@smartcarbo.com");

        registro = new RegistroAlimentacao();
        registro.setId(10L);
        registro.setNomeAlimento("Banana");
        registro.setQuantidade(100.0);
        registro.setTipoRefeicao("Café da manhã");
        registro.setCalorias(89.0);
        registro.setUsuarioId(1L);
    }

    @Test
    void deveListarRegistrosDoUsuarioAutenticado() {
        when(usuarioService.buscarPorEmail("teste@smartcarbo.com"))
                .thenReturn(usuario);
        when(registroService.listarPorUsuario(1L))
                .thenReturn(List.of(registro));

        var resposta = controller.listar(authentication);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(1, resposta.getBody().size());
        assertEquals(
                "Banana",
                resposta.getBody().get(0).getNomeAlimento()
        );

        verify(registroService).listarPorUsuario(1L);
    }

    @Test
    void deveCadastrarRegistroParaUsuarioAutenticado() {
        when(usuarioService.buscarPorEmail("teste@smartcarbo.com"))
                .thenReturn(usuario);
        when(registroService.salvarParaUsuario(registro, 1L))
                .thenReturn(registro);

        var resposta = controller.salvar(registro, authentication);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());

        verify(registroService).salvarParaUsuario(registro, 1L);
    }

    @Test
    void deveBuscarRegistroDoUsuarioAutenticado() {
        when(usuarioService.buscarPorEmail("teste@smartcarbo.com"))
                .thenReturn(usuario);
        when(registroService.buscarPorIdEUsuario(10L, 1L))
                .thenReturn(registro);

        var resposta = controller.buscarPorId(10L, authentication);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals("Banana", resposta.getBody().getNomeAlimento());

        verify(registroService).buscarPorIdEUsuario(10L, 1L);
    }

    @Test
    void deveAtualizarRegistroDoUsuarioAutenticado() {
        when(usuarioService.buscarPorEmail("teste@smartcarbo.com"))
                .thenReturn(usuario);
        when(registroService.atualizarParaUsuario(10L, registro, 1L))
                .thenReturn(registro);

        var resposta = controller.atualizar(
                10L, registro, authentication
        );

        assertEquals(HttpStatus.OK, resposta.getStatusCode());

        verify(registroService).atualizarParaUsuario(
                10L, registro, 1L
        );
    }

    @Test
    void deveExcluirRegistroDoUsuarioAutenticado() {
        when(usuarioService.buscarPorEmail("teste@smartcarbo.com"))
                .thenReturn(usuario);

        var resposta = controller.excluir(10L, authentication);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());

        verify(registroService).excluirParaUsuario(10L, 1L);
    }

    @Test
    void naoDevePermitirListagemSemAutenticacao() {
        assertThrows(
                ResponseStatusException.class,
                () -> controller.listar(null)
        );

        verifyNoInteractions(usuarioService, registroService);
    }

    @Test
    void naoDevePermitirCadastroSemAutenticacao() {
        assertThrows(
                ResponseStatusException.class,
                () -> controller.salvar(registro, null)
        );

        verifyNoInteractions(usuarioService, registroService);
    }

    @Test
    void deveRejeitarUsuarioNaoEncontrado() {
        when(usuarioService.buscarPorEmail("teste@smartcarbo.com"))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> controller.listar(authentication)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                erro.getStatusCode()
        );

        verify(usuarioService).buscarPorEmail("teste@smartcarbo.com");
        verifyNoInteractions(registroService);
    }
}
