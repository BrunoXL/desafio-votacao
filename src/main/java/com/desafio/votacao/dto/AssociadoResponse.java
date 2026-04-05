package com.desafio.votacao.dto;

import com.desafio.votacao.entity.Associado;

import java.util.UUID;

public record AssociadoResponse(
        UUID id,
        String nome,
        String cpf
) {
    public static AssociadoResponse fromEntity(Associado associado) {
        return new AssociadoResponse(
                associado.getId(),
                associado.getNome(),
                associado.getCpf()
        );
    }
}
