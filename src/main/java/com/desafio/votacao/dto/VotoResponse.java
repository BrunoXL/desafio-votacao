package com.desafio.votacao.dto;

import com.desafio.votacao.entity.Voto;
import com.desafio.votacao.entity.enums.VotoEscolha;

import java.time.LocalDateTime;
import java.util.UUID;

public record VotoResponse(
        UUID id,
        UUID pautaId,
        UUID associadoId,
        VotoEscolha voto,
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
