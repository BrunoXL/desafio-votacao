package com.desafio.votacao.dto;

import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.enums.StatusPauta;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PautaResponse(
        @Schema(description = "Identificador único da pauta", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Título da pauta", example = "Aprovação de orçamento")
        String titulo,

        @Schema(description = "Descrição detalhada da pauta", example = "Votação para decidir o orçamento anual")
        String descricao,

        @Schema(description = "Status atual da pauta", example = "EM_VOTACAO")
        StatusPauta status,

        @Schema(description = "Data e hora de abertura da sessão", example = "2024-05-20T10:00:00Z")
        OffsetDateTime dataAbertura,

        @Schema(description = "Data e hora de fechamento da sessão", example = "2024-05-20T10:01:00Z")
        OffsetDateTime dataFechamento,

        @Schema(description = "Data e hora em que a pauta foi criada", example = "2024-05-20T09:30:00Z")
        OffsetDateTime criadaEm
) {
    public static PautaResponse fromEntity(Pauta pauta) {
        return new PautaResponse(
                pauta.getId(),
                pauta.getTitulo(),
                pauta.getDescricao(),
                pauta.getStatus(),
                pauta.getDataAbertura(),
                pauta.getDataFechamento(),
                pauta.getCriadaEm()
        );
    }
}
