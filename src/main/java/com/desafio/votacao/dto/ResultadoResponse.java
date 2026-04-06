package com.desafio.votacao.dto;

import com.desafio.votacao.entity.enums.StatusPauta;

import java.util.UUID;

public record ResultadoResponse(
        UUID pautaId,
        String titulo,
        StatusPauta status,
        long totalVotos,
        long votosSim,
        long votosNao
) {
}
