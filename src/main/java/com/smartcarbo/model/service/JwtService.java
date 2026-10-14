package com.smartcarbo.model.service;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiracaoMs;

    public JwtService(
            @Value("${app.jwt.secret}") String segredo,
            @Value("${app.jwt.expiration-ms:3600000}") long expiracaoMs) {

        if (segredo == null || segredo.isBlank()) {
            throw new IllegalArgumentException(
                "A chave de assinatura do JWT não foi configurada."
            );
        }

        if (expiracaoMs <= 0) {
            throw new IllegalArgumentException(
                "A validade do JWT deve ser maior que zero."
            );
        }

        byte[] chaveBytes;

        try {
            chaveBytes = Decoders.BASE64.decode(segredo);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "A chave JWT deve estar codificada em Base64.", e
            );
        }

        this.secretKey = Keys.hmacShaKeyFor(chaveBytes);
        this.expiracaoMs = expiracaoMs;
    }

    public String gerarToken(Long usuarioId, String email) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException(
                "O ID do usuário deve ser válido."
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                "O e-mail do usuário é obrigatório para gerar o token."
            );
        }

        Instant agora = Instant.now();

        return Jwts.builder()
                .subject(email)
                .claim("usuarioId", usuarioId)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusMillis(expiracaoMs)))
                .signWith(secretKey)
                .compact();
    }

    public Claims validarToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                "O token JWT não pode estar vazio."
            );
        }

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiracaoMs() {
        return expiracaoMs;
    }
}