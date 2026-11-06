
package com.smartcarbo.model.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.smartcarbo.dto.ComunidadePerfilResponse;
import com.smartcarbo.dto.ComunidadePublicacaoRequest;
import com.smartcarbo.dto.ComunidadePublicacaoResponse;
import com.smartcarbo.model.entity.CurtidaPublicacao;
import com.smartcarbo.model.entity.PublicacaoComunidade;
import com.smartcarbo.model.entity.SeguimentoUsuario;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.repository.CurtidaPublicacaoRepository;
import com.smartcarbo.model.repository.PublicacaoComunidadeRepository;
import com.smartcarbo.model.repository.SeguimentoUsuarioRepository;
import com.smartcarbo.model.repository.UsuarioRepository;

@Service
public class ComunidadeService {

    private static final String CATEGORIA_PADRAO = "Conquista do dia";

    private final PublicacaoComunidadeRepository publicacaoRepository;
    private final CurtidaPublicacaoRepository curtidaRepository;
    private final SeguimentoUsuarioRepository seguimentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacaoService notificacaoService;

    public ComunidadeService(
            PublicacaoComunidadeRepository publicacaoRepository,
            CurtidaPublicacaoRepository curtidaRepository,
            SeguimentoUsuarioRepository seguimentoRepository,
            UsuarioRepository usuarioRepository,
            NotificacaoService notificacaoService) {

        this.publicacaoRepository = publicacaoRepository;
        this.curtidaRepository = curtidaRepository;
        this.seguimentoRepository = seguimentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacaoService = notificacaoService;
    }

    @Transactional(readOnly = true)
    public List<ComunidadePublicacaoResponse> listarPublicacoes(
            Long usuarioId) {

        validarUsuario(usuarioId);

        return publicacaoRepository.findTop20ByOrderByCriadoEmDesc()
                .stream()
                .map(publicacao -> paraResponse(publicacao, usuarioId))
                .toList();
    }

    @Transactional
    public ComunidadePublicacaoResponse publicar(
            ComunidadePublicacaoRequest request,
            Long usuarioId) {

        validarUsuario(usuarioId);

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados da publicação são obrigatórios.");
        }

        String texto = normalizar(request.text());
        String imagem = normalizar(request.image());
        String categoria = normalizar(request.category());

        if (texto == null && imagem == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Escreva uma mensagem ou adicione uma imagem.");
        }

        if (imagem != null
                && !imagem.startsWith("data:image/")
                && !imagem.startsWith("https://")
                && !imagem.startsWith("http://")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A imagem precisa ser uma imagem válida ou uma URL http/https.");
        }

        PublicacaoComunidade publicacao = new PublicacaoComunidade();
        publicacao.setUsuarioId(usuarioId);
        publicacao.setTexto(texto);
        publicacao.setImagem(imagem);
        publicacao.setCategoria(
                categoria == null ? CATEGORIA_PADRAO : categoria);

        PublicacaoComunidade salva =
                publicacaoRepository.saveAndFlush(publicacao);

        return paraResponse(salva, usuarioId);
    }

    @Transactional
    public void excluirPublicacao(Long publicacaoId, Long usuarioId) {

        validarUsuario(usuarioId);

        PublicacaoComunidade publicacao =
                buscarPublicacao(publicacaoId);

        if (!publicacao.getUsuarioId().equals(usuarioId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você só pode excluir as suas próprias publicações.");
        }

        curtidaRepository.deleteAllByPublicacaoId(publicacaoId);
        publicacaoRepository.deleteByIdAndUsuarioId(
                publicacaoId, usuarioId);
        publicacaoRepository.flush();
    }

    @Transactional
    public void curtir(Long publicacaoId, Long usuarioId) {

        validarUsuario(usuarioId);

        PublicacaoComunidade publicacao =
                buscarPublicacao(publicacaoId);

        boolean jaCurtiu =
                curtidaRepository.existsByPublicacaoIdAndUsuarioId(
                        publicacaoId, usuarioId);

        if (!jaCurtiu) {
            CurtidaPublicacao curtida = new CurtidaPublicacao();
            curtida.setPublicacaoId(publicacaoId);
            curtida.setUsuarioId(usuarioId);

            curtidaRepository.saveAndFlush(curtida);

            Long donoPublicacaoId = publicacao.getUsuarioId();

            if (!donoPublicacaoId.equals(usuarioId)) {
                Usuario ator = validarUsuario(usuarioId);
                String nome = nomePublico(ator);

                notificacaoService.criar(
                        donoPublicacaoId,
                        usuarioId,
                        "CURTIDA_PUBLICACAO",
                        "Nova curtida",
                        nome + " curtiu sua publicação.",
                        publicacaoId
                );
            }
        }
    }

    @Transactional
    public void descurtir(Long publicacaoId, Long usuarioId) {

        validarUsuario(usuarioId);
        buscarPublicacao(publicacaoId);

        curtidaRepository.deleteByPublicacaoIdAndUsuarioId(
                publicacaoId, usuarioId);
    }

    @Transactional(readOnly = true)
    public List<ComunidadePerfilResponse> listarPerfis(Long usuarioId) {

        Usuario atual = validarUsuario(usuarioId);

        return usuarioRepository.findAll()
                .stream()
                .filter(usuario ->
                        !usuario.getId().equals(atual.getId()))
                .map(usuario -> new ComunidadePerfilResponse(
                        usuario.getId(),
                        nomePublico(usuario),
                        "Compartilhando hábitos saudáveis",
                        seguimentoRepository.existsBySeguidorIdAndSeguidoId(
                                usuarioId, usuario.getId())))
                .toList();
    }

    @Transactional
    public void seguir(Long seguidoId, Long seguidorId) {

        validarUsuario(seguidorId);
        validarIdUsuario(seguidoId);

        if (seguidorId.equals(seguidoId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Você não pode seguir a própria conta.");
        }

        if (!usuarioRepository.existsById(seguidoId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "O perfil que você tentou seguir não foi encontrado.");
        }

        boolean jaSegue =
                seguimentoRepository.existsBySeguidorIdAndSeguidoId(
                        seguidorId, seguidoId);

        if (!jaSegue) {
            SeguimentoUsuario seguimento = new SeguimentoUsuario();
            seguimento.setSeguidorId(seguidorId);
            seguimento.setSeguidoId(seguidoId);

            seguimentoRepository.saveAndFlush(seguimento);

            Usuario ator = validarUsuario(seguidorId);
            String nome = nomePublico(ator);

            notificacaoService.criar(
                    seguidoId,
                    seguidorId,
                    "NOVO_SEGUIDOR",
                    "Novo seguidor",
                    nome + " começou a seguir você.",
                    seguidoId
            );
        }
    }

    @Transactional
    public void deixarDeSeguir(Long seguidoId, Long seguidorId) {

        validarUsuario(seguidorId);
        validarIdUsuario(seguidoId);

        if (seguidorId.equals(seguidoId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é possível deixar de seguir a própria conta.");
        }

        if (!usuarioRepository.existsById(seguidoId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "O perfil não foi encontrado.");
        }

        seguimentoRepository.deleteBySeguidorIdAndSeguidoId(
                seguidorId, seguidoId);
    }

    private ComunidadePublicacaoResponse paraResponse(
            PublicacaoComunidade publicacao,
            Long usuarioId) {

        String autor = usuarioRepository
                .findById(publicacao.getUsuarioId())
                .map(this::nomePublico)
                .orElse("Usuário SmartCarbo");

        return new ComunidadePublicacaoResponse(
                publicacao.getId(),
                publicacao.getUsuarioId(),
                autor,
                publicacao.getTexto(),
                publicacao.getImagem(),
                publicacao.getCategoria(),
                publicacao.getCriadoEm(),
                curtidaRepository.countByPublicacaoId(
                        publicacao.getId()),
                curtidaRepository.existsByPublicacaoIdAndUsuarioId(
                        publicacao.getId(), usuarioId),
                publicacao.getUsuarioId().equals(usuarioId));
    }

    private PublicacaoComunidade buscarPublicacao(Long id) {

        validarIdUsuario(id);

        return publicacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Publicação não encontrada."));
    }

    private Usuario validarUsuario(Long usuarioId) {

        validarIdUsuario(usuarioId);

        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "O usuário autenticado não foi encontrado."));
    }

    private void validarIdUsuario(Long id) {

        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O identificador informado é inválido.");
        }
    }

    private String nomePublico(Usuario usuario) {

        String nome = normalizar(usuario.getNome());

        return nome == null ? "Usuário SmartCarbo" : nome;
    }

    private String normalizar(String valor) {

        if (valor == null || valor.isBlank()) {
            return null;
        }

        return valor.trim();
    }
}
