package com.smartcarbo.model.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartcarbo.dto.LoginResponse;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse autenticar(String email, String senha) {

        if (email == null || email.isBlank()
                || senha == null || senha.isBlank()) {
            return null;
        }

        String emailNormalizado =
                email.trim().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository
                .findByEmail(emailNormalizado)
                .orElse(null);

        if (usuario == null || usuario.getSenha() == null) {
            return null;
        }

        boolean senhaValida = passwordEncoder.matches(
                senha,
                usuario.getSenha()
        );

        if (!senhaValida) {
            return null;
        }

        String token = jwtService.gerarToken(
                usuario.getId(),
                usuario.getEmail()
        );

        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                token
        );
    }
}