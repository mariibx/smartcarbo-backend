
package com.smartcarbo.model.service;

import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.repository.UsuarioRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public Usuario salvar(Usuario usuario) {
        if (usuario.getEmail() == null ||
                usuario.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O e-mail é obrigatório."
            );
        }

        if (usuario.getSenha() == null ||
                usuario.getSenha().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A senha é obrigatória."
            );
        }

        String email = usuario.getEmail().trim();

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
        return usuarioRepository.findById(id);
    }

    public Usuario atualizar(Long id, Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));

        if (usuario.getEmail() != null &&
                !usuario.getEmail().isBlank()) {
            String email = usuario.getEmail().trim();

            boolean emailEmUso = usuarioRepository.existsByEmail(email)
                    && !email.equalsIgnoreCase(usuarioExistente.getEmail());

            if (emailEmUso) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Este e-mail já está cadastrado."
                );
            }
        }

        String senhaRecebida = usuario.getSenha();

        BeanUtils.copyProperties(usuario, usuarioExistente, "id", "senha");

        if (usuario.getEmail() != null &&
                !usuario.getEmail().isBlank()) {
            usuarioExistente.setEmail(usuario.getEmail().trim());
        }

        if (senhaRecebida != null && !senhaRecebida.isBlank()) {
            usuarioExistente.setSenha(
                    passwordEncoder.encode(senhaRecebida)
            );
        }

        return usuarioRepository.save(usuarioExistente);
    }

    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuário não encontrado."
            );
        }

        usuarioRepository.deleteById(id);
    }
}
