
package com.smartcarbo.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartcarbo.dto.LoginResponse;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.repository.UsuarioRepository;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    @Transactional
    public LoginResponse autenticar(String email, String senha) {

        if (email == null || email.isBlank()
                || senha == null || senha.isBlank()) {
            return null;
        }

        Usuario usuario = usuarioRepository
                .findByEmail(email.trim())
                .orElse(null);

        if (usuario == null || usuario.getSenha() == null) {
            return null;
        }

        String senhaArmazenada = usuario.getSenha();

        boolean senhaValida;

        if (senhaArmazenada.startsWith("$2a$")
                || senhaArmazenada.startsWith("$2b$")
                || senhaArmazenada.startsWith("$2y$")) {

            senhaValida = passwordEncoder.matches(
                    senha,
                    senhaArmazenada
            );

        } else {
            // Compatibilidade temporária com senhas antigas em texto puro.
            senhaValida = senha.equals(senhaArmazenada);

            if (senhaValida) {
                usuario.setSenha(passwordEncoder.encode(senha));
                usuarioRepository.save(usuario);
            }
        }

        if (!senhaValida) {
            return null;
        }

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}
