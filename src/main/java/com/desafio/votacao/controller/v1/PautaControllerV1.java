package com.desafio.votacao.controller.v1;

import com.desafio.votacao.dto.*;
import com.desafio.votacao.service.PautaService;
import com.desafio.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "Pautas (Legacy V1)", description = "Gerenciamento de pautas (Versão Depreciada)")
public class PautaControllerV1 {

    private final PautaService pautaService;
    private final VotoService votoService;

    @PostMapping
    @Operation(summary = "Cria uma nova pauta")
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody PautaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaService.criar(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma pauta por ID")
    public ResponseEntity<PautaResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(pautaService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Lista todas as pautas")
    public ResponseEntity<List<PautaResponse>> listarTodas() {
        return ResponseEntity.ok(pautaService.listarTodas());
    }

    @PostMapping("/{id}/sessao")
    @Operation(summary = "Abre uma sessão de votação em uma pauta")
    public ResponseEntity<PautaResponse> abrirSessao(@PathVariable UUID id,
            @RequestBody(required = false) SessaoRequest request) {
        return ResponseEntity.ok(pautaService.abrirSessao(id, request));
    }

    @PutMapping("/{id}/sessao/fechar")
    @Operation(summary = "Fecha manualmente uma sessão de votação")
    public ResponseEntity<PautaResponse> fecharSessao(@PathVariable UUID id) {
        return ResponseEntity.ok(pautaService.fecharSessao(id));
    }

    @PostMapping("/{id}/votar")
    @Operation(summary = "Registra um voto em uma pauta")
    public ResponseEntity<VotoResponse> votar(@PathVariable UUID id, @Valid @RequestBody VotoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votoService.votar(id, request));
    }

    @GetMapping("/{id}/resultado")
    @Operation(summary = "Obtém o resultado de uma pauta")
    public ResponseEntity<ResultadoResponse> obterResultado(@PathVariable UUID id) {
        return ResponseEntity.ok(pautaService.obterResultado(id));
    }
}
