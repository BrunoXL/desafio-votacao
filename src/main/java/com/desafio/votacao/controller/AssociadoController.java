package com.desafio.votacao.controller;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.service.AssociadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/associados")
public class AssociadoController {

    private final AssociadoService associadoService;

    public AssociadoController(AssociadoService associadoService) {
        this.associadoService = associadoService;
    }

    @PostMapping
    public ResponseEntity<AssociadoResponse> criar(@Valid @RequestBody AssociadoRequest request) {
        AssociadoResponse response = associadoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AssociadoResponse>> listarTodos() {
        return ResponseEntity.ok(associadoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociadoResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(associadoService.buscarPorId(id));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<AssociadoResponse> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(associadoService.buscarPorCpf(cpf));
    }
}
