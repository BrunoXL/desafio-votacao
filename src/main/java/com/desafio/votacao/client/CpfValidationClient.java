package com.desafio.votacao.client;

import com.desafio.votacao.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CpfValidationClient {

    private static final Logger log = LoggerFactory.getLogger(CpfValidationClient.class);
    private final Random random = new Random();

    public enum CpfStatus {
        ABLE_TO_VOTE,
        UNABLE_TO_VOTE
    }

    public CpfStatus validarCpf(String cpf) {
        // ~20% de chance do xpf ser inválido
        if (random.nextInt(5) == 0) {
            log.warn("CPF {} considerado inválido pelo serviço externo", cpf);
            throw new NotFoundException("CPF inválido: " + cpf);
        }

        // dentro os válidos, 80% podem votar
        CpfStatus status = random.nextInt(5) == 0 ? CpfStatus.UNABLE_TO_VOTE : CpfStatus.ABLE_TO_VOTE;
        log.info("CPF {} validado com status: {}", cpf, status);
        return status;
    }
}
