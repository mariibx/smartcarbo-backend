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

        byte[] chaveBytes = Decoders.BASE64.decode(segredo);

        this.secretKey = Keys.hmacShaKeyFor(chaveBytes);
        this.expiracaoMs = expiracaoMs;

        if (expiracaoMs <= 0) {
            throw new IllegalArgumentException(
                    "A validade do JWT deve ser maior que zero."
            );
        }
    }

    public String gerarToken(Long usuarioId, String email) {
        Instant agora = Instant.now();

        return Jwts.builder()
                .subject(email)
                .claim("usuarioId", usuarioId)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(
                        agora.plusMillis(expiracaoMs)
                ))
                .signWith(secretKey)
                .compact();
    }

    public Claims validarToken(String token) {
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