
package com.smartcarbo.model.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.smartcarbo.dto.LoginResponse;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void configurar() {
        authService = new AuthService(
                usuarioRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void deveRetornarNullQuandoEmailForVazio() {
        LoginResponse resposta = authService.autenticar(
                "",
                "Senha123"
        );

        assertNull(resposta);
        verifyNoInteractions(
                usuarioRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void deveRetornarNullQuandoSenhaForVazia() {
        LoginResponse resposta = authService.autenticar(
                "teste@teste.com",
                " "
        );

        assertNull(resposta);
        verifyNoInteractions(
                usuarioRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void deveRetornarNullQuandoUsuarioNaoExistir() {
        when(usuarioRepository.findByEmail("teste@teste.com"))
                .thenReturn(Optional.empty());

        LoginResponse resposta = authService.autenticar(
                "teste@teste.com",
                "Senha123"
        );

        assertNull(resposta);

        verify(usuarioRepository).findByEmail("teste@teste.com");
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void deveRetornarNullQuandoSenhaEstiverIncorreta() {
        Usuario usuario = criarUsuario();

        when(usuarioRepository.findByEmail("teste@teste.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("SenhaErrada", "hash-da-senha"))
                .thenReturn(false);

        LoginResponse resposta = authService.autenticar(
                "teste@teste.com",
                "SenhaErrada"
        );

        assertNull(resposta);

        verify(passwordEncoder).matches(
                "SenhaErrada",
                "hash-da-senha"
        );

        verifyNoInteractions(jwtService);
    }

    @Test
    void deveGerarTokenQuandoLoginForValido() {
        Usuario usuario = criarUsuario();

        when(usuarioRepository.findByEmail("teste@teste.com"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("Senha123", "hash-da-senha"))
                .thenReturn(true);

        when(jwtService.gerarToken(1L, "teste@teste.com"))
                .thenReturn("token-falso-de-teste");

        LoginResponse resposta = authService.autenticar(
                "  TESTE@TESTE.COM  ",
                "Senha123"
        );

        assertNotNull(resposta);

        verify(usuarioRepository).findByEmail("teste@teste.com");

        verify(passwordEncoder).matches(
                "Senha123",
                "hash-da-senha"
        );

        verify(jwtService).gerarToken(
                1L,
                "teste@teste.com"
        );
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
