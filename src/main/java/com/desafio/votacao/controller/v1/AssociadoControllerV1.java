package com.desafio.votacao.controller.v1;

import com.desafio.votacao.dto.AssociadoRequest;
import com.desafio.votacao.dto.AssociadoResponse;
import com.desafio.votacao.service.AssociadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/associados")
@Slf4j
public class AssociadoController {
@RequiredArgsConstructor
@Tag(name = "Associados (Legacy V1)", description = "Gerenciamento de associados (Versão Depreciada)")
public class AssociadoControllerV1 {

    private final AssociadoService associadoService;

    public AssociadoController(AssociadoService associadoService) {
        this.associadoService = associadoService;
    }

    @Operation(summary = "Cadastrar um novo associado")
    @PostMapping
    public ResponseEntity<AssociadoResponse> criar(@Valid @RequestBody AssociadoRequest request) {
        log.info("Requisição para cadastrar associado: {} (CPF: {})", request.nome(), request.cpf());
        AssociadoResponse response = associadoService.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todos os associados")
    @GetMapping
    @Operation(summary = "Lista todos os associados")
    public ResponseEntity<List<AssociadoResponse>> listarTodos() {
        log.info("Listando todos os associados");
        return ResponseEntity.ok(associadoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociadoResponse> buscarPorId(@PathVariable UUID id) {
        log.info("Buscando associado por ID: {}", id);
        return ResponseEntity.ok(associadoService.buscarPorId(id));
    }

    @Operation(summary = "Buscar associado por CPF")
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<AssociadoResponse> buscarPorCpf(@PathVariable String cpf) {
        log.info("Buscando associado por CPF: {}", cpf);
        return ResponseEntity.ok(associadoService.buscarPorCpf(cpf));
    }
}
