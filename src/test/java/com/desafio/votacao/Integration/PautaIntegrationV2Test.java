package com.desafio.votacao.Integration;

import com.desafio.votacao.client.CpfValidationClient;
import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.PautaRequest;
import com.desafio.votacao.dto.SessaoRequest;
import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.enums.StatusPauta;
import com.desafio.votacao.repository.PautaRepository;
import com.desafio.votacao.scheduler.SessaoScheduler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PautaIntegrationV2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoScheduler sessaoScheduler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CpfValidationClient cpfValidationClient;

    @Test
    void chamadaV1_DeveRetornarHeaderDeDepreciacao() throws Exception {
        mockMvc.perform(get("/api/v1/pautas"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-API-Deprecated", "true"))
                .andExpect(header().string("Warning", org.hamcrest.Matchers.containsString("esta depreciada")));
    }

    @Test
    void chamadaV2_NaoDeveRetornarHeaderDeDepreciacao() throws Exception {
        mockMvc.perform(get("/api/v2/pautas"))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist("X-API-Deprecated"));
    }

    @Test
    void scheduler_DeveFecharPautaExpiradaAutomaticamente(){
        // 1. Criar uma pauta ja aberta mas com data de fechamento no PASSADO
        Pauta pauta = new Pauta();
        pauta.setTitulo("Pauta Expirada");
        pauta.setStatus(StatusPauta.EM_VOTACAO);
        pauta.setDataAbertura(OffsetDateTime.now().minusMinutes(10));
        pauta.setDataFechamento(OffsetDateTime.now().minusMinutes(5));
        pauta = pautaRepository.save(pauta);

        // 2. Chamar o método do scheduler manualmente (para não ter que esperar o ciclo de 1 min)
        sessaoScheduler.fecharSessoesExpiradas();

        // 3. Verificar no repositório se o status mudou para ENCERRADA
        Pauta pautaAtualizada = pautaRepository.findById(pauta.getId()).orElseThrow();
        assertThat(pautaAtualizada.getStatus()).isEqualTo(StatusPauta.ENCERRADA);
    }

    @Test
    void fluxoCompletoV2_CriarPauta_Votar_Resultado() throws Exception {
        when(cpfValidationClient.validarCpf(anyString()))
                .thenReturn(CpfValidationClient.CpfStatus.ABLE_TO_VOTE);

        // Criar Associado V2
        MvcResult resAssoc = mockMvc.perform(post("/api/v1/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssociadoRequest("Sócio V1", "99988877766"))))
                .andExpect(status().isCreated())
                .andReturn();
        UUID assocId = UUID.fromString(com.jayway.jsonpath.JsonPath.read(resAssoc.getResponse().getContentAsString(), "$.id"));

        // Criar Pauta V2
        MvcResult resPauta = mockMvc.perform(post("/api/v2/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PautaRequest("Pauta V2", "Desc"))))
                .andExpect(status().isCreated())
                .andReturn();
        UUID pautaId = UUID.fromString(com.jayway.jsonpath.JsonPath.read(resPauta.getResponse().getContentAsString(), "$.id"));

        // Abrir Sessão V2
        mockMvc.perform(post("/api/v2/pautas/" + pautaId + "/sessao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SessaoRequest(1))))
                .andExpect(status().isOk());

        // Votar V2 - Mudança no path: /api/v2/pautas/{id}/votos (plural no v2)
        mockMvc.perform(post("/api/v2/pautas/" + pautaId + "/votar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new com.desafio.votacao.dto.VotoRequest(assocId, com.desafio.votacao.entity.enums.VotoEscolha.SIM))))
                .andExpect(status().isCreated());

        // Fechar V2
        mockMvc.perform(put("/api/v2/pautas/" + pautaId + "/sessao/fechar"))
                .andExpect(status().isOk());

        // Resultado V2
        mockMvc.perform(get("/api/v2/pautas/" + pautaId + "/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVotos").value(1));
    }
}
