package com.smartcarbo.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

        // Log temporário para investigar o erro 403
        System.out.println(
            "JWT FILTER: "
            + request.getMethod()
            + " "
            + request.getRequestURI()
        );

        String authorization = request.getHeader("Authorization");

        // Se não houver token, continua a requisição normalmente
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            Claims claims = jwtService.validarToken(token);
            String email = claims.getSubject();

            if (email != null
                    && SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                UsernamePasswordAuthenticationToken autenticacao =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.emptyList()
                        );

                autenticacao.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(autenticacao);
            }

        } catch (JwtException | IllegalArgumentException e) {
            SecurityContextHolder.clearContext();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(
                    "{\"erro\":\"Token JWT inválido ou expirado.\"}"
            );

            return;
        }

        filterChain.doFilter(request, response);
    }
}