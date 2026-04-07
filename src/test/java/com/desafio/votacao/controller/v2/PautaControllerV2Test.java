package com.desafio.votacao.controller.v2;

import com.desafio.votacao.dto.*;
import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.service.PautaService;
import com.desafio.votacao.service.VotoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PautaControllerV2.class)
class PautaControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PautaService pautaService;

    @MockitoBean
    private VotoService votoService;

    @Test
    void criarPauta_V2_DeveExporVersion() throws Exception {
        UUID id = UUID.randomUUID();
        PautaRequest request = new PautaRequest("Titulo", "Desc");
        Pauta entity = new Pauta();
        entity.setId(id);
        entity.setVersion(0);

        when(pautaService.criar(any())).thenReturn(new PautaResponse(id, "T", "D", null, null, null, null));
        when(pautaService.findPautaOrThrow(id)).thenReturn(entity);

        mockMvc.perform(post("/api/v2/pautas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.version").value(0));
    }

    @Test
    void abrirSessao_ComConcorrencia_DeveRetornar409() throws Exception {
        UUID id = UUID.randomUUID();
        SessaoRequestV2 request = new SessaoRequestV2(1, 0); // Espera versão 0

        Pauta entity = new Pauta();
        entity.setId(id);
        entity.setVersion(0);

        when(pautaService.findPautaOrThrow(id)).thenReturn(entity);
        // Simula erro de concorrência do Hibernate
        when(pautaService.abrirSessao(any(), any()))
                .thenThrow(new ObjectOptimisticLockingFailureException(Pauta.class, id));

        mockMvc.perform(post("/api/v2/pautas/" + id + "/sessao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }
}
