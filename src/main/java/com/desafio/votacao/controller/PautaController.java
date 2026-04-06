package com.desafio.votacao.controller;

import com.desafio.votacao.dto.*;
import com.desafio.votacao.service.PautaService;
import com.desafio.votacao.service.VotoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {

    private final PautaService pautaService;
    private final VotoService votoService;

    public PautaController(PautaService pautaService, VotoService votoService) {
        this.pautaService = pautaService;
        this.votoService = votoService;
    }

    @PostMapping
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody PautaRequest request) {
        PautaResponse response = pautaService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PautaResponse>> listarTodas() {
        return ResponseEntity.ok(pautaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PautaResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(pautaService.buscarPorId(id));
    }

    @PostMapping("/{id}/sessao")
    public ResponseEntity<PautaResponse> abrirSessao(
            @PathVariable UUID id,
            @RequestBody(required = false) SessaoRequest request) {
        PautaResponse response = pautaService.abrirSessao(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/sessao/fechar")
    public ResponseEntity<PautaResponse> fecharSessao(@PathVariable UUID id) {
        return ResponseEntity.ok(pautaService.fecharSessao(id));
    }

    @PostMapping("/{id}/votos")
    public ResponseEntity<VotoResponse> votar(
            @PathVariable UUID id,
            @Valid @RequestBody VotoRequest request) {
        VotoResponse response = votoService.votar(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/votos")
    public ResponseEntity<List<VotoResponse>> listarVotos(@PathVariable UUID id) {
        return ResponseEntity.ok(votoService.listarVotosPorPauta(id));
    }

    @GetMapping("/{id}/resultado")
    public ResponseEntity<ResultadoResponse> obterResultado(@PathVariable UUID id) {
        return ResponseEntity.ok(pautaService.obterResultado(id));
    }
}
