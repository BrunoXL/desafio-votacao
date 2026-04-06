package com.desafio.votacao.service;

import com.desafio.votacao.client.CpfValidationClient;
import com.desafio.votacao.dto.VotoRequest;
import com.desafio.votacao.dto.VotoResponse;
import com.desafio.votacao.entity.Associado;
import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.Voto;
import com.desafio.votacao.entity.enums.VotoEscolha;
import com.desafio.votacao.exception.BusinessException;
import com.desafio.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaService pautaService;

    @Mock
    private AssociadoService associadoService;

    @Mock
    private CpfValidationClient cpfValidationClient;

    @InjectMocks
    private VotoService votoService;

    private Pauta pauta;
    private Associado associado;
    private final UUID pautaId = UUID.randomUUID();
    private final UUID associadoId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        pauta = new Pauta();
        pauta.setId(pautaId);

        associado = new Associado();
        associado.setId(associadoId);
        associado.setCpf("12345678901");
    }

    @Test
    void votar_ComSucesso() {
        VotoRequest request = new VotoRequest(associadoId, VotoEscolha.SIM);
        when(pautaService.findPautaOrThrow(pautaId)).thenReturn(pauta);
        when(pautaService.isSessaoAberta(pauta)).thenReturn(true);
        when(associadoService.findAssociadoOrThrow(associadoId)).thenReturn(associado);
        when(cpfValidationClient.validarCpf(associado.getCpf())).thenReturn(CpfValidationClient.CpfStatus.ABLE_TO_VOTE);
        when(votoRepository.existsByPautaIdAndAssociadoId(pautaId, associadoId)).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenAnswer(i -> i.getArgument(0));

        VotoResponse response = votoService.votar(pautaId, request);

        assertNotNull(response);
        assertEquals(VotoEscolha.SIM, response.voto());
        verify(votoRepository).save(any(Voto.class));
    }

    @Test
    void votar_ErroSessaoFechada() {
        VotoRequest request = new VotoRequest(associadoId, VotoEscolha.SIM);
        when(pautaService.findPautaOrThrow(pautaId)).thenReturn(pauta);
        when(pautaService.isSessaoAberta(pauta)).thenReturn(false);

        assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
        verify(votoRepository, never()).save(any(Voto.class));
    }

    @Test
    void votar_ErroVotoDuplicado() {
        VotoRequest request = new VotoRequest(associadoId, VotoEscolha.SIM);
        when(pautaService.findPautaOrThrow(pautaId)).thenReturn(pauta);
        when(pautaService.isSessaoAberta(pauta)).thenReturn(true);
        when(associadoService.findAssociadoOrThrow(associadoId)).thenReturn(associado);
        when(cpfValidationClient.validarCpf(associado.getCpf())).thenReturn(CpfValidationClient.CpfStatus.ABLE_TO_VOTE);
        when(votoRepository.existsByPautaIdAndAssociadoId(pautaId, associadoId)).thenReturn(true);

        assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
    }

    @Test
    void votar_ErroCpfNaoPodeVotar() {
        VotoRequest request = new VotoRequest(associadoId, VotoEscolha.SIM);
        when(pautaService.findPautaOrThrow(pautaId)).thenReturn(pauta);
        when(pautaService.isSessaoAberta(pauta)).thenReturn(true);
        when(associadoService.findAssociadoOrThrow(associadoId)).thenReturn(associado);
        when(cpfValidationClient.validarCpf(associado.getCpf())).thenReturn(CpfValidationClient.CpfStatus.UNABLE_TO_VOTE);

        assertThrows(BusinessException.class, () -> votoService.votar(pautaId, request));
    }
}
