package com.smartcarbo.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void deveCriarConfiguracaoDeSeguranca() {
        JwtAuthenticationFilter jwtAuthenticationFilter =
                mock(JwtAuthenticationFilter.class);

        SecurityConfig securityConfig =
                new SecurityConfig(jwtAuthenticationFilter);

        assertNotNull(securityConfig);
    }
}