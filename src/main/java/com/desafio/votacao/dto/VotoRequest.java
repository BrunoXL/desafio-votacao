package com.desafio.votacao.dto;

import com.desafio.votacao.entity.enums.VotoEscolha;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record VotoRequest(
        @Schema(description = "ID do associado que está votando", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "O ID do associado é obrigatório")
        UUID associadoId,

        @Schema(description = "Escolha do voto (SIM ou NAO)", example = "SIM")
        @NotNull(message = "O voto é obrigatório (SIM ou NAO)")
        VotoEscolha voto
) {
}
