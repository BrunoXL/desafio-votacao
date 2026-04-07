package com.desafio.votacao.service;

import com.desafio.votacao.client.CpfValidationClient;
import com.desafio.votacao.dto.VotoRequest;
import com.desafio.votacao.dto.VotoResponse;
import com.desafio.votacao.entity.Associado;
import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.Voto;
import com.desafio.votacao.exception.BusinessException;
import com.desafio.votacao.repository.VotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VotoService {

    private final VotoRepository votoRepository;
    private final PautaService pautaService;
    private final AssociadoService associadoService;
    private final CpfValidationClient cpfValidationClient;

    public VotoService(VotoRepository votoRepository,
            PautaService pautaService,
            AssociadoService associadoService,
            CpfValidationClient cpfValidationClient) {
        this.votoRepository = votoRepository;
        this.pautaService = pautaService;
        this.associadoService = associadoService;
        this.cpfValidationClient = cpfValidationClient;
    }

    @Transactional
    public VotoResponse votar(UUID pautaId, VotoRequest request) {
        Pauta pauta = pautaService.findPautaOrThrow(pautaId);

        if (!pautaService.isSessaoAberta(pauta)) {
            log.warn("Tentativa de voto em pauta com sessão fechada: {}", pautaId);
            throw new BusinessException("A sessão de votação não está aberta para esta pauta");
        }

        Associado associado = associadoService.findAssociadoOrThrow(request.associadoId());

        CpfValidationClient.CpfStatus cpfStatus = cpfValidationClient.validarCpf(associado.getCpf());
        if (cpfStatus == CpfValidationClient.CpfStatus.UNABLE_TO_VOTE) {
            log.warn("Associado {} (CPF: {}) não habilitado para votar: UNABLE_TO_VOTE", associado.getId(), associado.getCpf());
            throw new BusinessException("O associado não está habilitado para votar (UNABLE_TO_VOTE)");
        }

        if (votoRepository.existsByPautaIdAndAssociadoId(pautaId, request.associadoId())) {
            log.warn("Voto duplicado: associado {} já votou na pauta {}", request.associadoId(), pautaId);
            throw new BusinessException("O associado já votou nesta pauta");
        }

        Voto voto = new Voto();
        voto.setPauta(pauta);
        voto.setAssociado(associado);
        voto.setVoto(request.voto());

        voto = votoRepository.save(voto);
        log.info("Voto registrado: associado={} pauta={} voto={}", associado.getId(), pautaId, request.voto());

        return VotoResponse.fromEntity(voto);
    }

    @Transactional(readOnly = true)
    public List<VotoResponse> listarVotosPorPauta(UUID pautaId) {
        pautaService.findPautaOrThrow(pautaId);

        return votoRepository.findAllByPautaId(pautaId).stream()
                .map(VotoResponse::fromEntity)
                .toList();
    }
}
