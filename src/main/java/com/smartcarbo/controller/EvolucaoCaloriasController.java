package com.smartcarbo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.smartcarbo.model.dto.EvolucaoCaloriasDTO;
import com.smartcarbo.model.service.EvolucaoCaloriasService;
import com.smartcarbo.model.service.UsuarioService;
import com.smartcarbo.model.entity.Usuario;

@RestController
@RequestMapping("/api/v1/dashboard")
public class EvolucaoCaloriasController {

    private final EvolucaoCaloriasService evolucaoCaloriasService;
    private final UsuarioService usuarioService;

    public EvolucaoCaloriasController(
            EvolucaoCaloriasService evolucaoCaloriasService,
            UsuarioService usuarioService) {
        this.evolucaoCaloriasService = evolucaoCaloriasService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/calorias")
    public List<EvolucaoCaloriasDTO> consultar(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim,

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

        return evolucaoCaloriasService.consultar(
                usuario.getId(), inicio, fim);
    }
}