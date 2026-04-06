package com.desafio.votacao.controller;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.service.AssociadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssociadoController.class)
class AssociadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AssociadoService associadoService;

    @Test
    void criarAssociado_ComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        AssociadoRequest request = new AssociadoRequest("José Teste", "12345678901");
        AssociadoResponse response = new AssociadoResponse(id, "José Teste", "12345678901");

        when(associadoService.criar(any(AssociadoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/associados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("José Teste"))
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    void listarTodos_ComSucesso() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<AssociadoResponse> associados = List.of(
                new AssociadoResponse(id1, "Associado 1", "11111111111"),
                new AssociadoResponse(id2, "Associado 2", "22222222222")
        );

        when(associadoService.listarTodos()).thenReturn(associados);

        mockMvc.perform(get("/api/v1/associados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[1].cpf").value("22222222222"));
    }

    @Test
    void buscarPorId_ComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        AssociadoResponse response = new AssociadoResponse(id, "José Teste", "12345678901");

        when(associadoService.buscarPorId(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/associados/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("José Teste"));
    }

    @Test
    void buscarPorCpf_ComSucesso() throws Exception {
        UUID id = UUID.randomUUID();
        String cpf = "12345678901";
        AssociadoResponse response = new AssociadoResponse(id, "José Teste", cpf);

        when(associadoService.buscarPorCpf(eq(cpf))).thenReturn(response);

        mockMvc.perform(get("/api/v1/associados/cpf/" + cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value(cpf))
                .andExpect(jsonPath("$.nome").value("José Teste"));
    }
}
