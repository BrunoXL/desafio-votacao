package com.desafio.votacao.controller.v1;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.service.AssociadoService;
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
@RequestMapping("/api/v1/associados")
@RequiredArgsConstructor
@Tag(name = "Associados (Legacy V1)", description = "Gerenciamento de associados (Versão Depreciada)")
public class AssociadoControllerV1 {

    private final AssociadoService associadoService;

    @PostMapping
    @Operation(summary = "Cria um novo associado")
    public ResponseEntity<AssociadoResponse> criar(@Valid @RequestBody AssociadoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(associadoService.criar(request));
    }

    @GetMapping
    @Operation(summary = "Lista todos os associados")
    public ResponseEntity<List<AssociadoResponse>> listarTodos() {
        return ResponseEntity.ok(associadoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um associado por ID")
    public ResponseEntity<AssociadoResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(associadoService.buscarPorId(id));
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Busca uma associado por CPF")
    public ResponseEntity<AssociadoResponse> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(associadoService.buscarPorCpf(cpf));
    }
}
