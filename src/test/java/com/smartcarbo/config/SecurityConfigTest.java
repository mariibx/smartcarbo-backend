
package com.smartcarbo.config;

import com.smartcarbo.controller.AuthController;
import com.smartcarbo.controller.UsuarioController;
import com.smartcarbo.controller.AtividadeFisicaController;
import com.smartcarbo.model.service.AuthService;
import com.smartcarbo.model.service.UsuarioService;
import com.smartcarbo.model.service.AtividadeFisicaService;
import com.smartcarbo.model.service.JwtService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        AuthController.class,
        UsuarioController.class,
        AtividadeFisicaController.class
})
@AutoConfigureMockMvc
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private AtividadeFisicaService atividadeFisicaService;

    @Test
    void devePermitirAcessoAoLoginSemToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveBloquearRotaProtegidaSemToken() throws Exception {
        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isUnauthorized());
    }
}
