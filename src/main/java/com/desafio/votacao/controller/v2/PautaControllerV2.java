package com.desafio.votacao.controller.v2;

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
@RequestMapping("/api/v2/pautas")
@RequiredArgsConstructor
@Tag(name = "Pautas (V2)", description = "Gerenciamento de pautas (Contrato Versão 2 com Controle de Versão)")
public class PautaControllerV2 {

    private final PautaService pautaService;
    private final VotoService votoService;

    @PostMapping
    @Operation(summary = "Cria uma nova pauta")
    public ResponseEntity<PautaResponseV2> criar(@Valid @RequestBody PautaRequest request) {
        var pautaResp = pautaService.criar(request);
        var pautaEntity = pautaService.findPautaOrThrow(pautaResp.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(PautaResponseV2.fromEntity(pautaEntity));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma pauta por ID")
    public ResponseEntity<PautaResponseV2> buscarPorId(@PathVariable UUID id) {
        var pautaEntity = pautaService.findPautaOrThrow(id);
        return ResponseEntity.ok(PautaResponseV2.fromEntity(pautaEntity));
    }

    @GetMapping
    @Operation(summary = "Lista todas as pautas")
    public ResponseEntity<List<PautaResponseV2>> listarTodas() {
        return ResponseEntity.ok(pautaService.listarTodas().stream()
                .map(p -> PautaResponseV2.fromEntity(pautaService.findPautaOrThrow(p.id())))
                .toList());
    }

    @PostMapping("/{id}/sessao")
    @Operation(summary = "Abre uma sessão de votação em uma pauta (Requer Versão)")
    public ResponseEntity<PautaResponseV2> abrirSessao(@PathVariable UUID id,
            @RequestBody(required = false) SessaoRequestV2 request) {
        var pauta = pautaService.findPautaOrThrow(id);
        if (request != null && request.version() != null) {
            pauta.setVersion(request.version());
        }

        SessaoRequest legacyRequest = (request != null) ? new SessaoRequest(request.duracaoMinutos()) : null;
        pautaService.abrirSessao(id, legacyRequest);

        return ResponseEntity.ok(PautaResponseV2.fromEntity(pautaService.findPautaOrThrow(id)));
    }

    @PutMapping("/{id}/sessao/fechar")
    @Operation(summary = "Fecha manualmente uma sessão de votação")
    public ResponseEntity<PautaResponseV2> fecharSessao(@PathVariable UUID id) {
        var pautaResp = pautaService.fecharSessao(id);
        return ResponseEntity.ok(PautaResponseV2.fromEntity(pautaService.findPautaOrThrow(pautaResp.id())));
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
