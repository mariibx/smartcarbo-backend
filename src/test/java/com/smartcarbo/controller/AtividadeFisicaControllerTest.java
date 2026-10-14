package com.smartcarbo.controller;

import com.smartcarbo.model.entity.AtividadeFisica;
import com.smartcarbo.model.service.AtividadeFisicaService;

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
class AtividadeFisicaControllerTest {

    @Mock
    private AtividadeFisicaService atividadeFisicaService;

    private AtividadeFisicaController controller;

    @BeforeEach
    void configurar() {
        controller = new AtividadeFisicaController(atividadeFisicaService);
    }

    @Test
    void deveListarAtividadesFisicas() {
        when(atividadeFisicaService.listar())
                .thenReturn(List.of(criarAtividade()));

        ResponseEntity<List<AtividadeFisica>> resposta =
                controller.listar();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(1, resposta.getBody().size());
        verify(atividadeFisicaService).listar();
    }

    @Test
    void deveCadastrarAtividadeFisica() {
        AtividadeFisica atividade = criarAtividade();

        when(atividadeFisicaService.salvar(atividade))
                .thenReturn(atividade);

        ResponseEntity<AtividadeFisica> resposta =
                controller.salvar(atividade);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertSame(atividade, resposta.getBody());
        verify(atividadeFisicaService).salvar(atividade);
    }

    @Test
    void deveRejeitarCadastroNulo() {
        ResponseEntity<AtividadeFisica> resposta =
                controller.salvar(null);

        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        verifyNoInteractions(atividadeFisicaService);
    }

    @Test
    void deveBuscarAtividadeExistente() {
        AtividadeFisica atividade = criarAtividade();

        when(atividadeFisicaService.buscarPorId(1L))
                .thenReturn(Optional.of(atividade));

        ResponseEntity<AtividadeFisica> resposta =
                controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertSame(atividade, resposta.getBody());
        verify(atividadeFisicaService).buscarPorId(1L);
    }

    @Test
    void deveRetornar404QuandoAtividadeNaoExistir() {
        when(atividadeFisicaService.buscarPorId(99L))
                .thenReturn(Optional.empty());

        ResponseEntity<AtividadeFisica> resposta =
                controller.buscarPorId(99L);

        assertEquals(HttpStatus.NOT_FOUND, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(atividadeFisicaService).buscarPorId(99L);
    }

    @Test
    void deveAtualizarAtividadeFisica() {
        AtividadeFisica atividade = criarAtividade();

        when(atividadeFisicaService.atualizar(1L, atividade))
                .thenReturn(atividade);

        ResponseEntity<AtividadeFisica> resposta =
                controller.atualizar(1L, atividade);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertSame(atividade, resposta.getBody());
        verify(atividadeFisicaService).atualizar(1L, atividade);
    }

    @Test
    void deveRejeitarAtualizacaoNula() {
        ResponseEntity<AtividadeFisica> resposta =
                controller.atualizar(1L, null);

        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        verifyNoInteractions(atividadeFisicaService);
    }

    @Test
    void deveExcluirAtividadeFisica() {
        doNothing().when(atividadeFisicaService).excluir(1L);

        ResponseEntity<Void> resposta =
                controller.excluir(1L);

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());
        verify(atividadeFisicaService).excluir(1L);
    }

    private AtividadeFisica criarAtividade() {
        AtividadeFisica atividade = new AtividadeFisica();
        atividade.setId(1L);
        atividade.setNome("Caminhada");
        atividade.setDescricao("Caminhada ao ar livre");
        atividade.setDuracao(30);
        atividade.setCalorias(150);
        return atividade;
    }
}