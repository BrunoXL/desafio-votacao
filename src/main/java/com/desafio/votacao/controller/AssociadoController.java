package com.desafio.votacao.controller;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.service.AssociadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Associado", description = "Gerenciamento de associados")
@RestController
@RequestMapping("/api/v1/associados")
public class AssociadoController {

    private final AssociadoService associadoService;

    public AssociadoController(AssociadoService associadoService) {
        this.associadoService = associadoService;
    }

    @Operation(summary = "Cadastrar um novo associado")
    @PostMapping
    public ResponseEntity<AssociadoResponse> criar(@Valid @RequestBody AssociadoRequest request) {
        AssociadoResponse response = associadoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todos os associados")
    @GetMapping
    public ResponseEntity<List<AssociadoResponse>> listarTodos() {
        return ResponseEntity.ok(associadoService.listarTodos());
    }

    @Operation(summary = "Buscar associado por ID")
    @GetMapping("/{id}")
    public ResponseEntity<AssociadoResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(associadoService.buscarPorId(id));
    }

    @Operation(summary = "Buscar associado por CPF")
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<AssociadoResponse> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(associadoService.buscarPorCpf(cpf));
    }
}
