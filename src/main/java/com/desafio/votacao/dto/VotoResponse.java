package com.desafio.votacao.dto;

import com.desafio.votacao.entity.Voto;
import com.desafio.votacao.entity.enums.VotoEscolha;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

public record VotoResponse(
        @Schema(description = "Identificador único do voto", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "ID da pauta associada", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID pautaId,

        @Schema(description = "ID do associado que votou", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID associadoId,

        @Schema(description = "Escolha do voto", example = "SIM")
        VotoEscolha voto,

        @Schema(description = "Data e hora do registro do voto", example = "2024-05-20T10:05:00")
        LocalDateTime dataVoto
) {
    public static VotoResponse fromEntity(Voto voto) {
        return new VotoResponse(
                voto.getId(),
                voto.getPauta().getId(),
                voto.getAssociado().getId(),
                voto.getVoto(),
                voto.getDataVoto()
        );
    }
}
