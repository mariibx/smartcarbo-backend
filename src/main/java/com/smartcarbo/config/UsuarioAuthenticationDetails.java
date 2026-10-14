package com.smartcarbo.config;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class UsuarioAuthenticationDetails
        extends WebAuthenticationDetails {

    private final Long usuarioId;

    public UsuarioAuthenticationDetails(
            Long usuarioId,
            HttpServletRequest request) {
        super(request);
        this.usuarioId = usuarioId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
}