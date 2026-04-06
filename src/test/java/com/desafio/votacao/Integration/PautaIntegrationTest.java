package com.desafio.votacao.Integration;

import com.desafio.votacao.client.CpfValidationClient;
import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.PautaRequest;
import com.desafio.votacao.dto.SessaoRequest;
import com.desafio.votacao.dto.VotoRequest;
import com.desafio.votacao.entity.enums.VotoEscolha;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PautaIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        private final ObjectMapper objectMapper = new ObjectMapper();

        @MockitoBean
        private CpfValidationClient cpfValidationClient;

        @Test
        void fluxoCompleto_CriarPauta_Votar_Fechar_Resultado() throws Exception {
                // 1. O Fake client precisa garantir que todos os associados criados aqui têm
                // permissão pra votar, senão o Random pode falhar o teste
                when(cpfValidationClient.validarCpf(anyString()))
                                .thenReturn(CpfValidationClient.CpfStatus.ABLE_TO_VOTE);

                // 2. Criar Associado 1 (Vota SIM)
                MvcResult resAssoc1 = mockMvc.perform(post("/api/v1/associados")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                                .writeValueAsString(new AssociadoRequest("João SIM", "11122233344"))))
                                .andExpect(status().isCreated())
                                .andReturn();
                String associado1IdStr = com.jayway.jsonpath.JsonPath.read(resAssoc1.getResponse().getContentAsString(),
                                "$.id");
                UUID associado1Id = UUID.fromString(associado1IdStr);

                // 3. Criar Associado 2 (Vota NÃO)
                MvcResult resAssoc2 = mockMvc.perform(post("/api/v1/associados")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                                .writeValueAsString(new AssociadoRequest("Maria NÃO", "55566677788"))))
                                .andExpect(status().isCreated())
                                .andReturn();
                String associado2IdStr = com.jayway.jsonpath.JsonPath.read(resAssoc2.getResponse().getContentAsString(),
                                "$.id");
                UUID associado2Id = UUID.fromString(associado2IdStr);

                // 4. Criar Pauta
                MvcResult resPauta = mockMvc.perform(post("/api/v1/pautas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new PautaRequest(
                                                "Aprovação do Orçamento DB Real", "Validação completa via H2"))))
                                .andExpect(status().isCreated())
                                .andReturn();
                String pautaIdStr = com.jayway.jsonpath.JsonPath.read(resPauta.getResponse().getContentAsString(),
                                "$.id");
                UUID pautaId = UUID.fromString(pautaIdStr);

                // 5. Trancar a tentativa de fechar sem estar em votação pra provar que as
                // regras de negócio valem
                mockMvc.perform(put("/api/v1/pautas/" + pautaId + "/sessao/fechar"))
                                .andExpect(status().is(422));

                // 6. Abrir Sessão de Votação (3 minutos)
                mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/sessao")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(new SessaoRequest(3))))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("EM_VOTACAO"));

                // 7. Voto do Associado 1 -> SIM
                mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                                .writeValueAsString(new VotoRequest(associado1Id, VotoEscolha.SIM))))
                                .andExpect(status().isCreated());

                // 8. Voto do Associado 2 -> NAO
                mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                                .writeValueAsString(new VotoRequest(associado2Id, VotoEscolha.NAO))))
                                .andExpect(status().isCreated());

                // 9. Tentar votar de novo com Associado 1 (Deve rejeitar com 422 - Voto
                // Duplicado)
                mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                                .writeValueAsString(new VotoRequest(associado1Id, VotoEscolha.SIM))))
                                .andExpect(status().is(422));

                // 10. Tentar extrair o resultado sem fechar (Deve dar 422 que só pode obter
                // resultado se fechar)
                mockMvc.perform(get("/api/v1/pautas/" + pautaId + "/resultado"))
                                .andExpect(status().is(422));

                // 11. Fechar a Sessão (Manual - pra pular a restrição temporal)
                mockMvc.perform(put("/api/v1/pautas/" + pautaId + "/sessao/fechar"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("ENCERRADA"));

                // 12. Obter Resultado do Banco Real! 1x Sim, 1x Não = Total 2
                mockMvc.perform(get("/api/v1/pautas/" + pautaId + "/resultado"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("ENCERRADA"))
                                .andExpect(jsonPath("$.totalVotos").value(2))
                                .andExpect(jsonPath("$.votosSim").value(1))
                                .andExpect(jsonPath("$.votosNao").value(1));
        }
}
