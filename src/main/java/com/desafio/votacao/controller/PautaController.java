package com.desafio.votacao.controller;

import com.desafio.votacao.dto.*;
import com.desafio.votacao.service.PautaService;
import com.desafio.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Pauta", description = "Gerenciamento de pautas e sessões de votação")
@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {

    private final PautaService pautaService;
    private final VotoService votoService;

    public PautaController(PautaService pautaService, VotoService votoService) {
        this.pautaService = pautaService;
        this.votoService = votoService;
    }

    @Operation(summary = "Criar uma nova pauta")
    @PostMapping
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody PautaRequest request) {
        PautaResponse response = pautaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todas as pautas")
    @GetMapping
    public ResponseEntity<List<PautaResponse>> listarTodas() {
        return ResponseEntity.ok(pautaService.listarTodas());
    }

    @Operation(summary = "Buscar pauta por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PautaResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(pautaService.buscarPorId(id));
    }

    @Operation(summary = "Abrir sessão de votação em uma pauta")
    @PostMapping("/{id}/sessao")
    public ResponseEntity<PautaResponse> abrirSessao(
            @PathVariable UUID id,
            @RequestBody(required = false) SessaoRequest request) {
        PautaResponse response = pautaService.abrirSessao(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Fechar sessão de votação manualmente")
    @PutMapping("/{id}/sessao/fechar")
    public ResponseEntity<PautaResponse> fecharSessao(@PathVariable UUID id) {
        return ResponseEntity.ok(pautaService.fecharSessao(id));
    }

    @Operation(summary = "Votar em uma pauta")
    @PostMapping("/{id}/votos")
    public ResponseEntity<VotoResponse> votar(
            @PathVariable UUID id,
            @Valid @RequestBody VotoRequest request) {
        VotoResponse response = votoService.votar(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todos os votos de uma pauta")
    @GetMapping("/{id}/votos")
    public ResponseEntity<List<VotoResponse>> listarVotos(@PathVariable UUID id) {
        return ResponseEntity.ok(votoService.listarVotosPorPauta(id));
    }

    @Operation(summary = "Obter o resultado da votação de uma pauta")
    @GetMapping("/{id}/resultado")
    public ResponseEntity<ResultadoResponse> obterResultado(@PathVariable UUID id) {
        return ResponseEntity.ok(pautaService.obterResultado(id));
    }
}
