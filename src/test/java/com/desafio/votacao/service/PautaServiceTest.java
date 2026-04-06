package com.desafio.votacao.service;

import com.desafio.votacao.dto.PautaRequest;
import com.desafio.votacao.dto.PautaResponse;
import com.desafio.votacao.dto.ResultadoResponse;
import com.desafio.votacao.dto.SessaoRequest;
import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.enums.StatusPauta;
import com.desafio.votacao.exception.BusinessException;
import com.desafio.votacao.repository.PautaRepository;
import com.desafio.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private PautaService pautaService;

    private Pauta pauta;
    private final UUID pautaId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        pauta = new Pauta();
        pauta.setId(pautaId);
        pauta.setTitulo("Pauta Teste");
        pauta.setStatus(StatusPauta.CRIADA);
    }

    @Test
    void criar_ComSucesso() {
        PautaRequest request = new PautaRequest("Nova Pauta", "Descrição");
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        PautaResponse response = pautaService.criar(request);

        assertNotNull(response);
        assertEquals(StatusPauta.CRIADA, response.status());
        verify(pautaRepository).save(any(Pauta.class));
    }

    @Test
    void abrirSessao_ComSucesso_DefaultDuration() {
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        PautaResponse response = pautaService.abrirSessao(pautaId, null);

        assertNotNull(response);
        assertEquals(StatusPauta.EM_VOTACAO, pauta.getStatus());
        assertNotNull(pauta.getDataAbertura());
        assertNotNull(pauta.getDataFechamento());
        // Deve fechar em aproximadamente 1 minuto
        assertTrue(pauta.getDataFechamento().isAfter(pauta.getDataAbertura().plusSeconds(59)));
    }

    @Test
    void abrirSessao_ComSucesso_CustomDuration() {
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);
        SessaoRequest request = new SessaoRequest(5);

        pautaService.abrirSessao(pautaId, request);

        assertEquals(StatusPauta.EM_VOTACAO, pauta.getStatus());
        assertTrue(pauta.getDataFechamento().isAfter(pauta.getDataAbertura().plusMinutes(4).plusSeconds(59)));
    }

    @Test
    void abrirSessao_ErroStatusInvalido() {
        pauta.setStatus(StatusPauta.EM_VOTACAO);
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));

        assertThrows(BusinessException.class, () -> pautaService.abrirSessao(pautaId, null));
    }

    @Test
    void obterResultado_ComSucesso_PautaEncerrada() {
        pauta.setStatus(StatusPauta.ENCERRADA);
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(votoRepository.countSimByPautaId(pautaId)).thenReturn(10L);
        when(votoRepository.countNaoByPautaId(pautaId)).thenReturn(5L);

        ResultadoResponse response = pautaService.obterResultado(pautaId);

        assertNotNull(response);
        assertEquals(15L, response.totalVotos());
        assertEquals(10L, response.votosSim());
        assertEquals(5L, response.votosNao());
    }

    @Test
    void obterResultado_AutoEncerraSeExpirada() {
        pauta.setStatus(StatusPauta.EM_VOTACAO);
        pauta.setDataFechamento(OffsetDateTime.now().minusMinutes(1)); // Expirou há 1 min
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));
        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);
        when(votoRepository.countSimByPautaId(pautaId)).thenReturn(0L);
        when(votoRepository.countNaoByPautaId(pautaId)).thenReturn(0L);

        ResultadoResponse response = pautaService.obterResultado(pautaId);

        assertEquals(StatusPauta.ENCERRADA, pauta.getStatus());
        assertNotNull(response);
        verify(pautaRepository).save(pauta);
    }

    @Test
    void obterResultado_ErroSeSessaoAbertaEnaoExpirada() {
        pauta.setStatus(StatusPauta.EM_VOTACAO);
        pauta.setDataFechamento(OffsetDateTime.now().plusMinutes(1)); // Fecha em 1 min
        when(pautaRepository.findById(pautaId)).thenReturn(Optional.of(pauta));

        assertThrows(BusinessException.class, () -> pautaService.obterResultado(pautaId));
    }
}
