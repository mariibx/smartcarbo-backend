package com.smartcarbo.controller;

import com.smartcarbo.dto.LoginRequest;
import com.smartcarbo.dto.LoginResponse;
import com.smartcarbo.model.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody(required = false) LoginRequest loginRequest) {

        if (loginRequest == null
                || loginRequest.getEmail() == null
                || loginRequest.getEmail().isBlank()
                || loginRequest.getSenha() == null
                || loginRequest.getSenha().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }

        LoginResponse resposta = authService.autenticar(
                loginRequest.getEmail(),
                loginRequest.getSenha()
        );

        if (resposta == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return ResponseEntity.ok(resposta);
    }
}