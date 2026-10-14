package com.smartcarbo.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.smartcarbo.model.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        // Sem token: deixa o Spring Security decidir se a rota é pública.
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7).trim();

        if (token.isEmpty()) {
            SecurityContextHolder.clearContext();
            responderNaoAutorizado(
                response,
                "Token JWT inválido ou vazio."
            );
            return;
        }

        try {
            Claims claims = jwtService.validarToken(token);

            String email = claims.getSubject();
            Long usuarioId = claims.get("usuarioId", Long.class);

            if (email == null || email.isBlank() || usuarioId == null) {
                SecurityContextHolder.clearContext();
                responderNaoAutorizado(
                    response,
                    "Token JWT inválido ou incompleto."
                );
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken autenticacao =
                    new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.emptyList()
                    );

                autenticacao.setDetails(
                    new UsuarioAuthenticationDetails(usuarioId, request)
                );

                SecurityContextHolder.getContext()
                    .setAuthentication(autenticacao);
            }

        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();
            responderNaoAutorizado(
                response,
                "Token JWT inválido ou expirado."
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void responderNaoAutorizado(
            HttpServletResponse response,
            String mensagem) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
            "{\"erro\":\"" + mensagem + "\"}"
        );
    }
}