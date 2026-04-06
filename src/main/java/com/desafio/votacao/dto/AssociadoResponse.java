package com.desafio.votacao.dto;

import com.desafio.votacao.entity.Associado;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

public record AssociadoResponse(
        @Schema(description = "Identificador único do associado", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome do associado", example = "Bruno Silva")
        String nome,

        @Schema(description = "CPF do associado (apenas números)", example = "12345678901")
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
