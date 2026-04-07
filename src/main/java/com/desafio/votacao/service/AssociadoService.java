package com.desafio.votacao.service;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.entity.Associado;
import com.desafio.votacao.exception.BusinessException;
import com.desafio.votacao.exception.NotFoundException;
import com.desafio.votacao.repository.AssociadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AssociadoService {

    private final AssociadoRepository associadoRepository;

    public AssociadoService(AssociadoRepository associadoRepository) {
        this.associadoRepository = associadoRepository;
    }

    @Transactional
    public AssociadoResponse criar(AssociadoRequest request) {
        if (associadoRepository.existsByCpf(request.cpf())) {
            log.warn("Falha ao criar associado: CPF {} já está cadastrado", request.cpf());
            throw new BusinessException("CPF já cadastrado: " + request.cpf());
        }

        Associado associado = new Associado();
        associado.setNome(request.nome());
        associado.setCpf(request.cpf());

        associado = associadoRepository.save(associado);
        log.info("Associado criado: {}", associado.getId());
        return AssociadoResponse.fromEntity(associado);
    }

    @Transactional(readOnly = true)
    public List<AssociadoResponse> listarTodos() {
        return associadoRepository.findAll().stream()
                .map(AssociadoResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssociadoResponse buscarPorId(UUID id) {
        Associado associado = findAssociadoOrThrow(id);
        return AssociadoResponse.fromEntity(associado);
    }

    @Transactional(readOnly = true)
    public AssociadoResponse buscarPorCpf(String cpf) {
        Associado associado = associadoRepository.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("Associado não encontrado com CPF: " + cpf));
        return AssociadoResponse.fromEntity(associado);
    }

    public Associado findAssociadoOrThrow(UUID id) {
        return associadoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Associado não encontrado: " + id));
    }
}
