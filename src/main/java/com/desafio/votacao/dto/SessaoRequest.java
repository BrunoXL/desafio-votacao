package com.desafio.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SessaoRequest(
        @Schema(description = "Duração da sessão em minutos (valor padrão: 1)", example = "3")
        Integer duracaoMinutos
) {
    public int getDuracaoOuDefault() {
        return (duracaoMinutos != null && duracaoMinutos > 0) ? duracaoMinutos : 1;
    }
}
