package com.desafio.votacao.dto;

import com.desafio.votacao.entity.Pauta;
import com.desafio.votacao.entity.enums.StatusPauta;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PautaResponse(
        UUID id,
        String titulo,
        String descricao,
        StatusPauta status,
        OffsetDateTime dataAbertura,
        OffsetDateTime dataFechamento,
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
