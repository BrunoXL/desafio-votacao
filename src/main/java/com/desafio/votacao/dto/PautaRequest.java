package com.desafio.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PautaRequest(
        @Schema(description = "Título da pauta", example = "Aprovação de orçamento")
        @NotBlank(message = "O título é obrigatório")
        String titulo,

        @Schema(description = "Descrição detalhada da pauta", example = "Votação para decidir o orçamento anual")
        String descricao
) {
}
