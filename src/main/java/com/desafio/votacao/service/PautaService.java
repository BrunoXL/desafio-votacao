package com.desafio.votacao.service;

import com.desafio.votacao.dto.*;
import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.enums.StatusPauta;
import com.desafio.votacao.exception.BusinessException;
import com.desafio.votacao.exception.NotFoundException;
import com.desafio.votacao.repository.PautaRepository;
import com.desafio.votacao.repository.VotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PautaService {

    private final PautaRepository pautaRepository;
    private final VotoRepository votoRepository;

    public PautaService(PautaRepository pautaRepository, VotoRepository votoRepository) {
        this.pautaRepository = pautaRepository;
        this.votoRepository = votoRepository;
    }

    @Transactional
    public PautaResponse criar(PautaRequest request) {
        Pauta pauta = new Pauta();
        pauta.setTitulo(request.titulo());
        pauta.setDescricao(request.descricao());
        pauta.setStatus(StatusPauta.CRIADA);

        pauta = pautaRepository.save(pauta);
        log.info("Pauta criada: {}", pauta.getId());
        return PautaResponse.fromEntity(pauta);
    }

    public List<PautaResponse> listarTodas() {
        return pautaRepository.findAll()
                .stream()
                .map(PautaResponse::fromEntity)
                .toList();
    }

    public PautaResponse buscarPorId(UUID id) {
        Pauta pauta = findPautaOrThrow(id);
        return PautaResponse.fromEntity(pauta);
    }

    @Transactional
    public PautaResponse abrirSessao(UUID pautaId, SessaoRequest request) {
        Pauta pauta = findPautaOrThrow(pautaId);

        if (pauta.getStatus() != StatusPauta.CRIADA) {
            log.warn("Falha ao abrir sessão: Pauta {} já está no status {}", pautaId, pauta.getStatus());
            throw new BusinessException("A pauta não está no status CRIADA. Status atual: " + pauta.getStatus());
        }

        int duracao = (request != null) ? request.getDuracaoOuDefault() : 1;

        OffsetDateTime agora = OffsetDateTime.now();
        pauta.setStatus(StatusPauta.EM_VOTACAO);
        pauta.setDataAbertura(agora);
        pauta.setDataFechamento(agora.plusMinutes(duracao));

        pauta = pautaRepository.save(pauta);
        log.info("Sessão aberta na pauta {} por {} minuto(s)", pautaId, duracao);
        return PautaResponse.fromEntity(pauta);
    }

    @Transactional
    public PautaResponse fecharSessao(UUID pautaId) {
        Pauta pauta = findPautaOrThrow(pautaId);

        if (pauta.getStatus() != StatusPauta.EM_VOTACAO) {
            log.warn("Falha ao fechar sessão: Pauta {} não está EM_VOTACAO. Status: {}", pautaId, pauta.getStatus());
            throw new BusinessException("A pauta não está em votação. Status atual: " + pauta.getStatus());
        }

        pauta.setStatus(StatusPauta.ENCERRADA);
        pauta.setDataFechamento(OffsetDateTime.now());
        pauta = pautaRepository.save(pauta);
        log.info("Pauta {} encerrada manualmente", pautaId);
        return PautaResponse.fromEntity(pauta);
    }

    @Transactional
    public ResultadoResponse obterResultado(UUID pautaId) {
        Pauta pauta = findPautaOrThrow(pautaId);

        if (isSessaoExpirada(pauta)) {
            pauta.setStatus(StatusPauta.ENCERRADA);
            pauta = pautaRepository.save(pauta);
            log.info("Pauta {} encerrada automaticamente", pautaId);
        }

        if (pauta.getStatus() != StatusPauta.ENCERRADA) {
            throw new BusinessException("O resultado só pode ser obtido após o encerramento da votação. Status atual: "
                    + pauta.getStatus());
        }

        long votosSim = votoRepository.countSimByPautaId(pautaId);
        long votosNao = votoRepository.countNaoByPautaId(pautaId);
        long total = votosSim + votosNao;

        return new ResultadoResponse(
                pauta.getId(),
                pauta.getTitulo(),
                pauta.getStatus(),
                total,
                votosSim,
                votosNao);
    }

    public boolean isSessaoAberta(Pauta pauta) {
        if (pauta.getStatus() != StatusPauta.EM_VOTACAO) {
            return false;
        }
        return pauta.getDataFechamento() != null && OffsetDateTime.now().isBefore(pauta.getDataFechamento());
    }

    public Pauta findPautaOrThrow(UUID id) {
        return pautaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada: " + id));
    }

    private boolean isSessaoExpirada(Pauta pauta) {
        return pauta.getStatus() == StatusPauta.EM_VOTACAO
                && pauta.getDataFechamento() != null
                && OffsetDateTime.now().isAfter(pauta.getDataFechamento());
    }

}
