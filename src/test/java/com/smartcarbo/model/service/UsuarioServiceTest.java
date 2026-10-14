
package com.smartcarbo.model.service;

import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private UsuarioService usuarioService;

    @BeforeEach
    void configurar() {
        usuarioService = new UsuarioService();

        // Injeta o repositório simulado sem iniciar o Spring.
        org.springframework.test.util.ReflectionTestUtils.setField(
                usuarioService,
                "usuarioRepository",
                usuarioRepository
        );
    }

    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {
        Usuario usuario = criarUsuario();

        when(usuarioRepository.existsByEmail("teste@teste.com"))
                .thenReturn(false);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario salvo = usuarioService.salvar(usuario);

        assertEquals("teste@teste.com", salvo.getEmail());
        assertNotEquals("Senha123", salvo.getSenha());

        assertTrue(
                new BCryptPasswordEncoder().matches(
                        "Senha123",
                        salvo.getSenha()
                )
        );

        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveNormalizarEmailAoCadastrar() {
        Usuario usuario = criarUsuario();
        usuario.setEmail("  TESTE@TESTE.COM  ");

        when(usuarioRepository.existsByEmail("teste@teste.com"))
                .thenReturn(false);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario salvo = usuarioService.salvar(usuario);

        assertEquals("teste@teste.com", salvo.getEmail());

        verify(usuarioRepository).existsByEmail("teste@teste.com");
    }

    @Test
    void deveRejeitarEmailInvalido() {
        Usuario usuario = criarUsuario();
        usuario.setEmail("email-invalido");

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.salvar(usuario)
        );

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void deveRejeitarSenhaCurta() {
        Usuario usuario = criarUsuario();
        usuario.setSenha("123");

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.salvar(usuario)
        );

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    void deveRejeitarEmailDuplicado() {
        Usuario usuario = criarUsuario();

        when(usuarioRepository.existsByEmail("teste@teste.com"))
                .thenReturn(true);

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.salvar(usuario)
        );

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveRejeitarCadastroSemUsuario() {
        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.salvar(null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
        verifyNoInteractions(usuarioRepository);
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de Teste");
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("Senha123");
        return usuario;
    }
}
