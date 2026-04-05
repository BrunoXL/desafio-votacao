package com.desafio.votacao.service;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.entity.Associado;
import com.desafio.votacao.exception.BusinessException;
import com.desafio.votacao.exception.NotFoundException;
import com.desafio.votacao.repository.AssociadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssociadoServiceTest {

    @Mock
    private AssociadoRepository associadoRepository;

    @InjectMocks
    private AssociadoService associadoService;

    private Associado associado;
    private final UUID id = UUID.randomUUID();
    private final String cpf = "12345678901";

    @BeforeEach
    void setUp() {
        associado = new Associado();
        associado.setId(id);
        associado.setNome("João Silva");
        associado.setCpf(cpf);
    }

    @Test
    void criar_ComSucesso() {
        AssociadoRequest request = new AssociadoRequest("João Silva", cpf);
        when(associadoRepository.existsByCpf(cpf)).thenReturn(false);
        when(associadoRepository.save(any(Associado.class))).thenReturn(associado);

        AssociadoResponse response = associadoService.criar(request);

        assertNotNull(response);
        assertEquals(cpf, response.cpf());
        verify(associadoRepository).save(any(Associado.class));
    }

    @Test
    void criar_ComCpfDuplicado() {
        AssociadoRequest request = new AssociadoRequest("João Silva", cpf);
        when(associadoRepository.existsByCpf(cpf)).thenReturn(true);

        assertThrows(BusinessException.class, () -> associadoService.criar(request));
        verify(associadoRepository, never()).save(any(Associado.class));
    }

    @Test
    void buscarPorId_ComSucesso() {
        when(associadoRepository.findById(id)).thenReturn(Optional.of(associado));

        AssociadoResponse response = associadoService.buscarPorId(id);

        assertNotNull(response);
        assertEquals(id, response.id());
    }

    @Test
    void buscarPorId_NaoEncontrado() {
        when(associadoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> associadoService.buscarPorId(id));
    }

    @Test
    void buscarPorCpf_ComSucesso() {
        when(associadoRepository.findByCpf(cpf)).thenReturn(Optional.of(associado));

        AssociadoResponse response = associadoService.buscarPorCpf(cpf);

        assertNotNull(response);
        assertEquals(cpf, response.cpf());
    }
}
