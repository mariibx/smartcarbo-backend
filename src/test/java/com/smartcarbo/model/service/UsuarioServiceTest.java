
package com.smartcarbo.model.service;

import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.repository.UsuarioRepository;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioService usuarioService;

    @BeforeEach
    void configurar() {
        usuarioService = new UsuarioService(
                usuarioRepository,
                passwordEncoder
        );
    }

    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {
        Usuario usuario = criarUsuario();

        when(usuarioRepository.existsByEmail("teste@teste.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Senha123"))
                .thenReturn("senha-criptografada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario salvo = usuarioService.salvar(usuario);

        assertEquals("teste@teste.com", salvo.getEmail());
        assertEquals("senha-criptografada", salvo.getSenha());
        assertNotEquals("Senha123", salvo.getSenha());

        verify(passwordEncoder).encode("Senha123");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveNormalizarEmailAoCadastrar() {
        Usuario usuario = criarUsuario();
        usuario.setEmail("  TESTE@TESTE.COM  ");

        when(usuarioRepository.existsByEmail("teste@teste.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Senha123"))
                .thenReturn("senha-criptografada");

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
        verifyNoInteractions(passwordEncoder);
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
        verifyNoInteractions(passwordEncoder);
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
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void deveRejeitarCadastroSemUsuario() {
        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.salvar(null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
        verifyNoInteractions(usuarioRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void deveAtualizarSomentePesoEManterOutrosDados() {
        Usuario existente = criarUsuario();
        existente.setId(1L);
        existente.setNome("Maria");
        existente.setEmail("teste@teste.com");
        existente.setPeso(60.0);
        existente.setAltura(1.65);
        existente.setSenha("senha-ja-criptografada");

        Usuario dados = new Usuario();
        dados.setPeso(65.0);

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario atualizado = usuarioService.atualizar(1L, dados);

        assertEquals(65.0, atualizado.getPeso());
        assertEquals("Maria", atualizado.getNome());
        assertEquals("teste@teste.com", atualizado.getEmail());
        assertEquals(1.65, atualizado.getAltura());
        assertEquals("senha-ja-criptografada", atualizado.getSenha());

        verify(usuarioRepository).save(existente);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void devePermitirManterProprioEmailNaAtualizacao() {
        Usuario existente = criarUsuario();
        existente.setId(1L);

        Usuario dados = new Usuario();
        dados.setEmail(" TESTE@TESTE.COM ");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(usuarioRepository.existsByEmail("teste@teste.com"))
                .thenReturn(true);

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario atualizado = usuarioService.atualizar(1L, dados);

        assertEquals("teste@teste.com", atualizado.getEmail());

        verify(usuarioRepository).save(existente);
    }

    @Test
    void deveRejeitarEmailDeOutroUsuarioNaAtualizacao() {
        Usuario existente = criarUsuario();
        existente.setId(1L);

        Usuario dados = new Usuario();
        dados.setEmail("outro@teste.com");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(usuarioRepository.existsByEmail("outro@teste.com"))
                .thenReturn(true);

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.atualizar(1L, dados)
        );

        assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveRejeitarAtualizacaoDeUsuarioInexistente() {
        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.atualizar(99L, new Usuario())
        );

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveCriptografarSenhaAoAtualizar() {
        Usuario existente = criarUsuario();
        existente.setId(1L);
        existente.setSenha("senha-antiga-criptografada");

        Usuario dados = new Usuario();
        dados.setSenha("NovaSenha123");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(passwordEncoder.encode("NovaSenha123"))
                .thenReturn("nova-senha-criptografada");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Usuario atualizado = usuarioService.atualizar(1L, dados);

        assertEquals("nova-senha-criptografada", atualizado.getSenha());

        verify(passwordEncoder).encode("NovaSenha123");
        verify(usuarioRepository).save(existente);
    }

    @Test
    void deveRejeitarSenhaCurtaNaAtualizacao() {
        Usuario existente = criarUsuario();
        existente.setId(1L);

        Usuario dados = new Usuario();
        dados.setSenha("123");

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.atualizar(1L, dados)
        );

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verifyNoInteractions(passwordEncoder);
    }

    private Usuario criarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuario de Teste");
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("Senha123");
        return usuario;
    }
}
