package br.com.fullstackedu.labpcp.controller;

import br.com.fullstackedu.labpcp.controller.dto.request.TurmaCreateRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.TurmaCreateResponse;
import br.com.fullstackedu.labpcp.service.TurmaService;
import jakarta.validation.Valid;
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
}
