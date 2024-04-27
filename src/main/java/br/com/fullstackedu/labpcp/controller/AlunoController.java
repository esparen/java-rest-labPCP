package br.com.fullstackedu.labpcp.controller;

import br.com.fullstackedu.labpcp.controller.dto.request.AlunoRequest;

import br.com.fullstackedu.labpcp.controller.dto.request.AlunoUpdateRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.AlunoResponse;

import br.com.fullstackedu.labpcp.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alunos")
@Slf4j
@Validated
@RequiredArgsConstructor
public class AlunoController {
    private final AlunoService alunoService;

    @PostMapping()
    public ResponseEntity<AlunoResponse> insertAluno(
            @RequestHeader(name = "Authorization") String authToken,
            @Valid @RequestBody AlunoRequest alunoRequest
    ) throws Exception {
        log.info("POST /alunos ");

        String actualToken = authToken.substring(7);
        AlunoResponse response = alunoService.insertAluno(alunoRequest, actualToken);
        if (response.success()){
            log.info("POST /alunos -> Aluno cadastrado com sucesso.");
        } else {
            log.info("POST /alunos -> Erro ao cadastrar novo aluno: [{}].", response.message());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @GetMapping("/{alunoId}")
    public ResponseEntity<AlunoResponse> getDocenteById(
            @RequestHeader(name = "Authorization") String authToken,
            @Valid @PathVariable Long alunoId) {
        log.info("GET /alunos/{} ", alunoId);
        String actualToken = authToken.substring(7);
        AlunoResponse response = alunoService.getById(alunoId, actualToken);
        if (response.success()){
            log.info("GET /alunos/{} -> OK ", alunoId);
        } else {
            log.error("GET /alunos/{} -> {} ", alunoId, response.httpStatus());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @PutMapping("/{alunoId}")
    public ResponseEntity<AlunoResponse> updateAluno(
            @PathVariable Long alunoId,
            @Valid @RequestBody AlunoUpdateRequest alunoUpdateRequest,
            @RequestHeader(name = "Authorization") String authToken) {
        log.info("PUT /alunos");
        String actualToken = authToken.substring(7);
        AlunoResponse response = alunoService.updateAluno(alunoId , alunoUpdateRequest, actualToken);
        if (response.success()) {
            log.info("PUT /alunos -> OK ");
        } else {
            log.error("PUT /alunos -> {}", response.httpStatus());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @GetMapping()
    public ResponseEntity<AlunoResponse> getAllAlunos(
            @RequestHeader(name = "Authorization") String authToken) throws Exception {
        log.info("GET /alunos ");
        String actualToken = authToken.substring(7);
        AlunoResponse response = alunoService.getAllAlunos(actualToken);
        if (response.success()){
            log.info("GET /alunos -> OK ");
        } else {
            log.error("GET /alunos -> {}", response.httpStatus());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }
}