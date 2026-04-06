package com.desafio.votacao.dto;

import com.desafio.votacao.entity.enums.StatusPauta;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record ResultadoResponse(
        @Schema(description = "Identificador único da pauta", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID pautaId,

        @Schema(description = "Título da pauta", example = "Aprovação de orçamento")
        String titulo,

        @Schema(description = "Status atual da pauta", example = "ENCERRADA")
        StatusPauta status,

        @Schema(description = "Total de votos contabilizados", example = "10")
        long totalVotos,

        @Schema(description = "Total de votos 'SIM'", example = "7")
        long votosSim,

        @Schema(description = "Total de votos 'NÃO'", example = "3")
        long votosNao
) {
}
