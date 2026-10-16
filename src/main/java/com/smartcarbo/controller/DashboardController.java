package com.smartcarbo.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.smartcarbo.model.dto.DashboardResumoDTO;
import com.smartcarbo.model.entity.Usuario;
import com.smartcarbo.model.service.DashboardService;
import com.smartcarbo.model.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UsuarioService usuarioService;

    public DashboardController(
            DashboardService dashboardService,
            UsuarioService usuarioService) {
        this.dashboardService = dashboardService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/resumo")
    public ResponseEntity<DashboardResumoDTO> obterResumo(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate data,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()
                || "anonymousUser".equals(authentication.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "É necessário estar autenticado.");
        }

        Usuario usuario = usuarioService.buscarPorEmail(
                authentication.getName());

        if (data == null) {
            data = LocalDate.now();
        }

        DashboardResumoDTO resumo =
                dashboardService.obterResumo(usuario.getId(), data);

        return ResponseEntity.ok(resumo);
    }
}