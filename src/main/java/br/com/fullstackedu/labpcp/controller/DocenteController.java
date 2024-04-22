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
@RequestMapping("/docente")
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
        String actualToken = authToken.substring(7);
        log.info("POST /docente -> Novo Docente ");
        NovoDocenteResponse response = docenteService.novoDocente(novoDocenteRequest, actualToken);
        if (response.success()){
            log.info("POST /docente -> Docente cadastrado com sucesso.");
        } else {
            log.info("POST /docente -> Erro ao cadastrar novo Docente: [{}].", response.message());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }
}
