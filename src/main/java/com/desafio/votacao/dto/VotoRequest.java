package com.desafio.votacao.dto;

import com.desafio.votacao.entity.enums.VotoEscolha;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VotoRequest(
        @NotNull(message = "O ID do associado é obrigatório")
        UUID associadoId,

        @NotNull(message = "O voto é obrigatório (SIM ou NAO)")
        VotoEscolha voto
) {
}
