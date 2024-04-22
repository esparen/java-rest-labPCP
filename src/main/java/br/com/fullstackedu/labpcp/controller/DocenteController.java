package br.com.fullstackedu.labpcp.controller;

import br.com.fullstackedu.labpcp.controller.dto.request.NovoDocenteRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.NovoDocenteResponse;
import br.com.fullstackedu.labpcp.service.DocenteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/docentes")
@Slf4j
@Validated
public class DocenteController {
    private final DocenteService docenteService;

    public DocenteController(DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    @PostMapping()
    public ResponseEntity<NovoDocenteResponse> newDocente(
            @RequestHeader(name = "Authorization") String authToken,
            @Valid @RequestBody NovoDocenteRequest novoDocenteRequest
    ) throws Exception {
        log.info("POST /docentes -> Novo Docente ");
        String actualToken = authToken.substring(7);
        NovoDocenteResponse response = docenteService.novoDocente(novoDocenteRequest, actualToken);
        if (response.success()){
            log.info("POST /docentes -> Docente cadastrado com sucesso.");
        } else {
            log.error("POST /docentes -> Erro ao cadastrar novo Docente: [{}].", response.message());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @GetMapping("/{docenteId}")
    public ResponseEntity<NovoDocenteResponse> getDocenteById(
            @RequestHeader(name = "Authorization") String authToken,
            @Valid @PathVariable Long docenteId) {
        log.info("GET /docente/{} ", docenteId);
        String actualToken = authToken.substring(7);
        NovoDocenteResponse response = docenteService.getDocenteById(docenteId, actualToken);
        if (response.success()){
            log.info("GET /docentes/{} -> OK ", docenteId);
        } else {
            log.error("GET /docentes/{} -> 404", docenteId);
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @GetMapping()
    public ResponseEntity<NovoDocenteResponse> getAllDocentes(
            @RequestHeader(name = "Authorization") String authToken) {
        log.info("GET /docentes ");
        String actualToken = authToken.substring(7);
        NovoDocenteResponse response = docenteService.getAllDocentes(actualToken);
        if (response.success()) {
            log.info("GET /docentes -> OK ");
        } else {
            log.error("GET /docentes -> 404");
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }
}
