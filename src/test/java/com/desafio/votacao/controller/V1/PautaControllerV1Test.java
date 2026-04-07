package com.desafio.votacao.controller.V1;

import com.desafio.votacao.dto.PautaRequest;
import com.desafio.votacao.dto.PautaResponse;
import com.desafio.votacao.dto.ResultadoResponse;
import com.desafio.votacao.dto.VotoRequest;
import com.desafio.votacao.dto.VotoResponse;
import com.desafio.votacao.entity.enums.StatusPauta;
import com.desafio.votacao.entity.enums.VotoEscolha;
import com.desafio.votacao.service.PautaService;
import com.desafio.votacao.service.VotoService;
import com.desafio.votacao.controller.v1.PautaControllerV1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PautaControllerV1.class)
class PautaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @MockitoBean
        private PautaService pautaService;

        @MockitoBean
        private VotoService votoService;

        @Test
        void criarPauta_ComSucesso() throws Exception {
                UUID id = UUID.randomUUID();
                PautaRequest request = new PautaRequest("Pauta Teste", "Descrição");
                PautaResponse response = new PautaResponse(id, "Pauta Teste", "Descrição", StatusPauta.CRIADA, null,
                                null,
                                null);

                when(pautaService.criar(any(PautaRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/v1/pautas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(id.toString()))
                                .andExpect(jsonPath("$.titulo").value("Pauta Teste"));
        }

        @Test
        void buscarPautaPorId_ComSucesso() throws Exception {
                UUID id = UUID.randomUUID();
                PautaResponse response = new PautaResponse(id, "Pauta Teste", "Descrição", StatusPauta.CRIADA, null,
                                null,
                                null);

                when(pautaService.buscarPorId(id)).thenReturn(response);

                mockMvc.perform(get("/api/v1/pautas/" + id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(id.toString()));
        }

        @Test
        void abrirSessao_ComSucesso() throws Exception {
                UUID id = UUID.randomUUID();
                PautaResponse response = new PautaResponse(id, "Pauta Teste", "Descrição", StatusPauta.EM_VOTACAO, null,
                                null,
                                null);

                when(pautaService.abrirSessao(eq(id), any())).thenReturn(response);

                mockMvc.perform(post("/api/v1/pautas/" + id + "/sessao"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("EM_VOTACAO"));
        }

        @Test
        void votar_ComSucesso() throws Exception {
                UUID pautaId = UUID.randomUUID();
                UUID associadoId = UUID.randomUUID();
                VotoRequest request = new VotoRequest(associadoId, VotoEscolha.SIM);
                VotoResponse response = new VotoResponse(UUID.randomUUID(), pautaId, associadoId, VotoEscolha.SIM,
                                null);

                when(votoService.votar(eq(pautaId), any(VotoRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.voto").value("SIM"));
        }

        @Test
        void fecharSessaoEObterResultado_ComSucesso() throws Exception {
                UUID pautaId = UUID.randomUUID();
                PautaResponse pautaEncerrada = new PautaResponse(pautaId, "Pauta Encerrada", "Descrição",
                                StatusPauta.ENCERRADA,
                                null, null, null);
                ResultadoResponse resultado = new ResultadoResponse(pautaId, "Pauta Encerrada", StatusPauta.ENCERRADA,
                                10L, 7L,
                                3L);

                when(pautaService.fecharSessao(pautaId)).thenReturn(pautaEncerrada);
                when(pautaService.obterResultado(pautaId)).thenReturn(resultado);

                mockMvc.perform(put("/api/v1/pautas/" + pautaId + "/sessao/fechar"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("ENCERRADA"));

                mockMvc.perform(get("/api/v1/pautas/" + pautaId + "/resultado"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("ENCERRADA"))
                                .andExpect(jsonPath("$.totalVotos").value(10))
                                .andExpect(jsonPath("$.votosSim").value(7))
                                .andExpect(jsonPath("$.votosNao").value(3));
        }
}
