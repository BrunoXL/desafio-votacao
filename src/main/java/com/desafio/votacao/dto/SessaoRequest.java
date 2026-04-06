package com.desafio.votacao.dto;

public record SessaoRequest(
        Integer duracaoMinutos
) {
    public int getDuracaoOuDefault() {
        return (duracaoMinutos != null && duracaoMinutos > 0) ? duracaoMinutos : 1;
    }
}
