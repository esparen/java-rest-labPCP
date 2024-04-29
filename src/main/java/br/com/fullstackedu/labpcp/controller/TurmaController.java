package br.com.fullstackedu.labpcp.controller;

import br.com.fullstackedu.labpcp.controller.dto.request.TurmaCreateRequest;
import br.com.fullstackedu.labpcp.controller.dto.request.TurmaUpdateRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.TurmaCreateResponse;
import br.com.fullstackedu.labpcp.service.TurmaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/turmas")
@Slf4j
@Validated
public class TurmaController {
    private final TurmaService turmaService;

    public TurmaController(TurmaService turmaService) {
        this.turmaService = turmaService;
    }

    @PostMapping()
    public ResponseEntity<TurmaCreateResponse> newTurma(
            @RequestHeader(name = "Authorization") String authToken,
            @Valid @RequestBody TurmaCreateRequest turmaCreateRequest
    ) throws Exception {
        log.info("POST /turmas ");
        String actualToken = authToken.substring(7);
        TurmaCreateResponse response = turmaService.novaTurma(turmaCreateRequest, actualToken);
        if (response.success()){
            log.info("POST /turmas -> Turma cadastrada com sucesso.");
        } else {
            log.error("POST /turmas -> Erro ao cadastrar nova Turma: [{}].", response.message());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @GetMapping("/{turmaId}")
    public ResponseEntity<TurmaCreateResponse> getTurmaById(
            @RequestHeader(name = "Authorization") String authToken,
            @Valid @PathVariable Long turmaId) {
        log.info("GET /turmas/{} ", turmaId);
        String actualToken = authToken.substring(7);
        TurmaCreateResponse response = turmaService.getTurmaById(turmaId, actualToken);
        if (response.success()){
            log.info("GET /turmas/{} -> OK ", turmaId);
        } else {
            log.error("GET /turmas/{} -> 500", turmaId);
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @GetMapping()
    public ResponseEntity<TurmaCreateResponse> getAllTurmas(
            @RequestHeader(name = "Authorization") String authToken) throws Exception {
        log.info("GET /turmas ");
        String actualToken = authToken.substring(7);
        TurmaCreateResponse response = turmaService.getAllTurmas(actualToken);
        if (response.success()){
            log.info("GET /turmas -> OK ");
        } else {
            log.error("GET /turmas -> 500");
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @PutMapping("/{turmaId}")
    public ResponseEntity<TurmaCreateResponse> updateTurma(
            @PathVariable Long turmaId,
            @Valid @RequestBody TurmaUpdateRequest turmaUpdateRequest,
            @RequestHeader(name = "Authorization") String authToken) {
        log.info("PUT /turmas");
        String actualToken = authToken.substring(7);
        TurmaCreateResponse response = turmaService.updateTurma(turmaId , turmaUpdateRequest, actualToken);
        if (response.success()) {
            log.info("PUT /turmas -> OK ");
        } else {
            log.error("PUT /turmas -> 500");
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @DeleteMapping("/{turmaId}")
    public ResponseEntity<TurmaCreateResponse> deleteTurma(
            @PathVariable @NotNull(message = "ID da turma é requerido para exclusão") Long turmaId,
            @RequestHeader(name = "Authorization") String authToken) {
        log.info("DELETE /turmas");
        String actualToken = authToken.substring(7);
        TurmaCreateResponse response = turmaService.deleteTurma(turmaId, actualToken);
        if (response.success()) {
            log.info("DELETE /turmas -> OK ");
        } else {
            log.error("DELETE /turmas -> 500");
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }
}
