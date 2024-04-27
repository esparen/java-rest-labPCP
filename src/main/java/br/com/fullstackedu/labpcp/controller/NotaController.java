package br.com.fullstackedu.labpcp.controller;

import br.com.fullstackedu.labpcp.controller.dto.request.NotaRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.NotaResponse;
import br.com.fullstackedu.labpcp.service.NotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/notas")
@Slf4j
@Validated
@RequiredArgsConstructor
public class NotaController {
    private final NotaService notaService;

    @PostMapping()
    public ResponseEntity<NotaResponse> insertNota(
            @RequestHeader(name = "Authorization") String authToken,
            @Valid @RequestBody NotaRequest notaRequest
    ) throws Exception {
        log.info("POST /notas ");

        String actualToken = authToken.substring(7);
        NotaResponse response = notaService.insert(notaRequest, actualToken);
        if (response.success()){
            log.info("POST /notas -> Nota cadastrado com sucesso.");
        } else {
            log.info("POST /notas -> Erro ao cadastrar novo nota: [{}].", response.message());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }
}
