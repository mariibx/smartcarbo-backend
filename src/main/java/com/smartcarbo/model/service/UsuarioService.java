package com.smartcarbo.model.service;

import com.smartcarbo.dto.UsuarioAtualizacaoRequest;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario salvar(Usuario usuario) {
        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados do usuário são obrigatórios."
            );
        }

        String email = normalizarEmail(usuario.getEmail());
        validarEmail(email);
        validarSenha(usuario.getSenha());

        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este e-mail já está cadastrado."
            );
        }

        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        validarId(id);
        return usuarioRepository.findById(id);
    }

    public Usuario buscarPorEmail(String email) {
        String emailNormalizado = normalizarEmail(email);
        validarEmail(emailNormalizado);

        return usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));
    }

    public Usuario atualizar(Long id, Usuario usuario) {
        validarId(id);

        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Os dados do usuário são obrigatórios."
            );
        }

        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));

        if (usuario.getEmail() != null) {
            String email = normalizarEmail(usuario.getEmail());
            validarEmail(email);

            boolean emailEmUso =
                    usuarioRepository.existsByEmail(email)
                    && !email.equalsIgnoreCase(existente.getEmail());

            if (emailEmUso) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Este e-mail já está cadastrado."
                );
            }

            existente.setEmail(email);
        }

        String senhaRecebida = usuario.getSenha();

        if (senhaRecebida != null && !senhaRecebida.isBlank()) {
            validarSenha(senhaRecebida);
            existente.setSenha(passwordEncoder.encode(senhaRecebida));
        }

        if (usuario.getNome() != null) {
            existente.setNome(usuario.getNome());
        }

        if (usuario.getDataNascimento() != null) {
            existente.setDataNascimento(usuario.getDataNascimento());
        }

        if (usuario.getAltura() != null) {
            existente.setAltura(usuario.getAltura());
        }

        if (usuario.getPeso() != null) {
            existente.setPeso(usuario.getPeso());
        }

        if (usuario.getSexo() != null) {
            existente.setSexo(usuario.getSexo());
        }

        if (usuario.getObjetivo() != null) {
            existente.setObjetivo(usuario.getObjetivo());
        }

        if (usuario.getNivelAtividade() != null) {
            existente.setNivelAtividade(usuario.getNivelAtividade());
        }

        if (usuario.getRestricoes() != null) {
            existente.setRestricoes(usuario.getRestricoes());
        }

        if (usuario.getCondicaoSaude() != null) {
            existente.setCondicaoSaude(usuario.getCondicaoSaude());
        }

        if (usuario.getAvaliacaoAlimentacao() != null) {
            existente.setAvaliacaoAlimentacao(
                    usuario.getAvaliacaoAlimentacao()
            );
        }

        return usuarioRepository.save(existente);
    }

    public void excluir(Long id) {
        validarId(id);

        if (!usuarioRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuário não encontrado."
            );
        }

        usuarioRepository.deleteById(id);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O ID do usuário deve ser válido."
            );
        }
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O e-mail é obrigatório."
            );
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validarEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe um e-mail válido."
            );
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A senha é obrigatória."
            );
        }

        if (senha.length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A senha deve ter pelo menos 8 caracteres."
            );
        }
    }
}