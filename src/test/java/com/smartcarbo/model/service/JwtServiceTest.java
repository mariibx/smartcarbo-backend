
package com.smartcarbo.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private static final String SEGREDO = Base64.getEncoder()
            .encodeToString(
                    "chave-de-teste-com-pelo-menos-32-bytes!"
                            .getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

    private JwtService jwtService;

    @BeforeEach
    void configurar() {
        jwtService = new JwtService(SEGREDO, 3600000);
    }

    @Test
    void deveGerarTokenComIdEEmail() {
        String token = jwtService.gerarToken(1L, "teste@email.com");

        Claims claims = jwtService.validarToken(token);

        assertEquals("teste@email.com", claims.getSubject());
        assertEquals(1L, claims.get("usuarioId", Long.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void deveRejeitarTokenVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> jwtService.validarToken("")
        );
    }

    @Test
    void deveRejeitarIdInvalido() {
        assertThrows(
                IllegalArgumentException.class,
                () -> jwtService.gerarToken(0L, "teste@email.com")
        );
    }

    @Test
    void deveRejeitarEmailVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> jwtService.gerarToken(1L, " ")
        );
    }

    @Test
    void deveRejeitarSegredoVazio() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService(" ", 3600000)
        );
    }

    @Test
    void deveRejeitarExpiracaoInvalida() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new JwtService(SEGREDO, 0)
        );
    }

    @Test
    void deveRejeitarTokenComAssinaturaInvalida() {
        SecretKey outraChave = Keys.hmacShaKeyFor(
                "outra-chave-de-teste-com-32-bytes!"
                        .getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        String tokenFalso = Jwts.builder()
                .subject("teste@email.com")
                .claim("usuarioId", 1L)
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(outraChave)
                .compact();

        assertThrows(
                Exception.class,
                () -> jwtService.validarToken(tokenFalso)
        );
    }

    @Test
    void deveRejeitarTokenMalformado() {
        assertThrows(
                Exception.class,
                () -> jwtService.validarToken("isso-nao-e-um-jwt")
        );
    }

    @Test
    void deveRejeitarTokenExpirado() {
        SecretKey chave = Keys.hmacShaKeyFor(
                Base64.getDecoder().decode(SEGREDO)
        );

        String tokenExpirado = Jwts.builder()
                .subject("teste@email.com")
                .claim("usuarioId", 1L)
                .issuedAt(new Date(System.currentTimeMillis() - 120000))
                .expiration(new Date(System.currentTimeMillis() - 60000))
                .signWith(chave)
                .compact();

        assertThrows(
                Exception.class,
                () -> jwtService.validarToken(tokenExpirado)
        );
    }

    @Test
    void deveInformarValidadeConfigurada() {
        assertEquals(3600000, jwtService.getExpiracaoMs());
        assertTrue(jwtService.getExpiracaoMs() > 0);
    }
}
