
package com.smartcarbo.model.service;

import com.smartcarbo.model.entity.RegistroAlimentacao;
import com.smartcarbo.model.repository.RegistroAlimentacaoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroAlimentacaoServiceTest {

    @Mock
    private RegistroAlimentacaoRepository repository;

    @InjectMocks
    private RegistroAlimentacaoService service;

    private RegistroAlimentacao registro;

    @BeforeEach
    void configurar() {
        registro = new RegistroAlimentacao();
        registro.setId(1L);
        registro.setNomeAlimento("Banana");
        registro.setQuantidade(100.0);
        registro.setTipoRefeicao("Café da manhã");
        registro.setCalorias(89.0);
        registro.setCarboidratos(23.0);
        registro.setProteinas(1.1);
        registro.setGorduras(0.3);
        registro.setFibras(2.6);
        registro.setSodio(1.0);
        registro.setUsuarioId(1L);
    }

    @Test
    void deveListarRegistrosDoUsuario() {
        when(repository.findAllByUsuarioIdOrderByDataHoraDesc(1L))
                .thenReturn(List.of(registro));

        List<RegistroAlimentacao> resultado =
                service.listarPorUsuario(1L);

        assertEquals(1, resultado.size());
        assertEquals("Banana", resultado.get(0).getNomeAlimento());
    }

    @Test
    void deveRejeitarListagemComUsuarioInvalido() {
        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> service.listarPorUsuario(null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, erro.getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void deveSalvarRegistroVinculadoAoUsuario() {
        when(repository.save(any(RegistroAlimentacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegistroAlimentacao resultado =
                service.salvarParaUsuario(registro, 5L);

        assertEquals(5L, resultado.getUsuarioId());
        assertNull(resultado.getId());
        assertNotNull(resultado.getDataHora());
        verify(repository).save(registro);
    }

    @Test
    void deveRejeitarNomeVazio() {
        registro.setNomeAlimento(" ");

        assertThrows(
                ResponseStatusException.class,
                () -> service.salvarParaUsuario(registro, 1L)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarQuantidadeZero() {
        registro.setQuantidade(0.0);

        assertThrows(
                ResponseStatusException.class,
                () -> service.salvarParaUsuario(registro, 1L)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void deveRejeitarCaloriasNegativas() {
        registro.setCalorias(-10.0);

        assertThrows(
                ResponseStatusException.class,
                () -> service.salvarParaUsuario(registro, 1L)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void deveBuscarRegistroDoUsuario() {
        when(repository.findByIdAndUsuarioId(1L, 1L))
                .thenReturn(Optional.of(registro));

        RegistroAlimentacao resultado =
                service.buscarPorIdEUsuario(1L, 1L);

        assertEquals("Banana", resultado.getNomeAlimento());
    }

    @Test
    void naoDeveBuscarRegistroDeOutroUsuario() {
        when(repository.findByIdAndUsuarioId(1L, 2L))
                .thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> service.buscarPorIdEUsuario(1L, 2L)
        );

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }

    @Test
    void deveAtualizarRegistroDoUsuario() {
        RegistroAlimentacao atualizado = new RegistroAlimentacao();
        atualizado.setNomeAlimento("Maçã");
        atualizado.setQuantidade(150.0);
        atualizado.setCalorias(78.0);

        when(repository.findByIdAndUsuarioId(1L, 1L))
                .thenReturn(Optional.of(registro));
        when(repository.save(any(RegistroAlimentacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RegistroAlimentacao resultado =
                service.atualizarParaUsuario(1L, atualizado, 1L);

        assertEquals("Maçã", resultado.getNomeAlimento());
        assertEquals(150.0, resultado.getQuantidade());
        assertEquals(1L, resultado.getUsuarioId());
    }

    @Test
    void naoDeveAtualizarRegistroDeOutroUsuario() {
        when(repository.findByIdAndUsuarioId(1L, 2L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> service.atualizarParaUsuario(1L, registro, 2L)
        );

        verify(repository, never()).save(any());
    }

    @Test
    void deveExcluirRegistroDoUsuario() {
        when(repository.existsByIdAndUsuarioId(1L, 1L))
                .thenReturn(true);

        assertDoesNotThrow(() -> service.excluirParaUsuario(1L, 1L));

        verify(repository).deleteByIdAndUsuarioId(1L, 1L);
    }

    @Test
    void naoDeveExcluirRegistroDeOutroUsuario() {
        when(repository.existsByIdAndUsuarioId(1L, 2L))
                .thenReturn(false);

        assertThrows(
                ResponseStatusException.class,
                () -> service.excluirParaUsuario(1L, 2L)
        );

        verify(repository, never()).deleteByIdAndUsuarioId(anyLong(), anyLong());
    }

    @Test
    void deveRetornarNaoEncontradoParaRegistroInexistente() {
        when(repository.findByIdAndUsuarioId(999L, 1L))
                .thenReturn(Optional.empty());

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> service.buscarPorIdEUsuario(999L, 1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
    }
}
