package com.smartcarbo.model.service;

import com.smartcarbo.model.entity.AtividadeFisica;
import com.smartcarbo.model.repository.AtividadeFisicaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtividadeFisicaServiceTest {

    @Mock
    private AtividadeFisicaRepository atividadeFisicaRepository;

    private AtividadeFisicaService atividadeFisicaService;

    @BeforeEach
    void configurar() {
        atividadeFisicaService = new AtividadeFisicaService(
                atividadeFisicaRepository
        );
    }

    @Test
    void deveCadastrarAtividadeComIdGeradoPeloBanco() {
        AtividadeFisica atividade = criarAtividade();
        atividade.setId(99L);

        when(atividadeFisicaRepository.save(any(AtividadeFisica.class)))
                .thenAnswer(invocacao -> {
                    AtividadeFisica salva = invocacao.getArgument(0);
                    salva.setId(1L);
                    return salva;
                });

        AtividadeFisica salva = atividadeFisicaService.salvar(atividade);

        assertEquals(1L, salva.getId());
        assertEquals("Caminhada", salva.getNome());

        verify(atividadeFisicaRepository).save(atividade);
    }

    @Test
    void deveListarAtividades() {
        List<AtividadeFisica> atividades = List.of(criarAtividade());

        when(atividadeFisicaRepository.findAll()).thenReturn(atividades);

        List<AtividadeFisica> resultado =
                atividadeFisicaService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Caminhada", resultado.get(0).getNome());

        verify(atividadeFisicaRepository).findAll();
    }

    @Test
    void deveBuscarAtividadePorId() {
        AtividadeFisica atividade = criarAtividade();
        atividade.setId(1L);

        when(atividadeFisicaRepository.findById(1L))
                .thenReturn(Optional.of(atividade));

        Optional<AtividadeFisica> resultado =
                atividadeFisicaService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Caminhada", resultado.get().getNome());
    }

    @Test
    void deveRetornarVazioQuandoAtividadeNaoExiste() {
        when(atividadeFisicaRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<AtividadeFisica> resultado =
                atividadeFisicaService.buscarPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveRejeitarIdInvalido() {
        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> atividadeFisicaService.buscarPorId(0L)
        );

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());

        verifyNoInteractions(atividadeFisicaRepository);
    }

    @Test
    void deveRejeitarCadastroSemAtividade() {
        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> atividadeFisicaService.salvar(null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());

        verifyNoInteractions(atividadeFisicaRepository);
    }

    @Test
    void deveRejeitarAtividadeSemNome() {
        AtividadeFisica atividade = criarAtividade();
        atividade.setNome(" ");

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> atividadeFisicaService.salvar(atividade)
        );

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());

        verifyNoInteractions(atividadeFisicaRepository);
    }

    @Test
    void deveRejeitarDuracaoInvalida() {
        AtividadeFisica atividade = criarAtividade();
        atividade.setDuracao(0);

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> atividadeFisicaService.salvar(atividade)
        );

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());

        verifyNoInteractions(atividadeFisicaRepository);
    }

    @Test
    void deveRejeitarCaloriasNegativas() {
        AtividadeFisica atividade = criarAtividade();
        atividade.setCalorias(-1);

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> atividadeFisicaService.salvar(atividade)
        );

        assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());

        verifyNoInteractions(atividadeFisicaRepository);
    }

    @Test
    void deveAtualizarAtividadeExistente() {
        AtividadeFisica existente = criarAtividade();
        existente.setId(1L);

        AtividadeFisica atualizacao = criarAtividade();
        atualizacao.setNome("Corrida");
        atualizacao.setDuracao(30);
        atualizacao.setCalorias(250);

        when(atividadeFisicaRepository.findById(1L))
                .thenReturn(Optional.of(existente));

        when(atividadeFisicaRepository.save(any(AtividadeFisica.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        AtividadeFisica resultado =
                atividadeFisicaService.atualizar(1L, atualizacao);

        assertEquals("Corrida", resultado.getNome());
        assertEquals(30, resultado.getDuracao());
        assertEquals(250, resultado.getCalorias());

        verify(atividadeFisicaRepository).save(existente);
    }

    @Test
    void deveRetornarNotFoundAoAtualizarAtividadeInexistente() {
        when(atividadeFisicaRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> atividadeFisicaService.atualizar(99L, criarAtividade())
        );

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());

        verify(atividadeFisicaRepository, never())
                .save(any(AtividadeFisica.class));
    }

    @Test
    void deveExcluirAtividadeExistente() {
        when(atividadeFisicaRepository.existsById(1L))
                .thenReturn(true);

        atividadeFisicaService.excluir(1L);

        verify(atividadeFisicaRepository).deleteById(1L);
    }

    @Test
    void deveRetornarNotFoundAoExcluirAtividadeInexistente() {
        when(atividadeFisicaRepository.existsById(99L))
                .thenReturn(false);

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> atividadeFisicaService.excluir(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());

        verify(atividadeFisicaRepository, never()).deleteById(anyLong());
    }

    private AtividadeFisica criarAtividade() {
        AtividadeFisica atividade = new AtividadeFisica();
        atividade.setNome("Caminhada");
        atividade.setDescricao("Caminhada leve");
        atividade.setDuracao(20);
        atividade.setCalorias(100);
        return atividade;
    }
}