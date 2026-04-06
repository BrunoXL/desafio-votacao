package com.desafio.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AssociadoRequest(
        @Schema(description = "Nome do associado", example = "Bruno Silva")
        String nome,

        @Schema(description = "CPF do associado (apenas números)", example = "12345678901")
        @NotBlank(message = "O CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "O CPF deve conter exatamente 11 dígitos numéricos")
        String cpf
) {
}
