package com.desafio.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requisição para gerenciar sessão - Versão 2 (Com Controle de Versão)")
public record SessaoRequestV2(
        @Schema(description = "Duração da sessão em minutos (valor padrão: 1)", example = "3")
        Integer duracaoMinutos,
        
        @Schema(description = "Versão atual da pauta para garantir integridade (Otimista)", example = "0")
        Integer version
) {
    public int getDuracaoOuDefault() {
        return (duracaoMinutos != null && duracaoMinutos > 0) ? duracaoMinutos : 1;
    }
}
