package br.com.fullstackedu.labpcp.controller;

import br.com.fullstackedu.labpcp.controller.dto.request.MateriaRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.MateriaResponse;
import br.com.fullstackedu.labpcp.service.MateriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping("/materias")
public class MateriaController {
    private final MateriaService materiaService;

    @PostMapping()
    public ResponseEntity<MateriaResponse> insertMateria(
            @RequestHeader(name = "Authorization") String authToken,
            @Valid @RequestBody MateriaRequest materiaRequest
    ) throws Exception {
        log.info("POST /materias ");

        String actualToken = authToken.substring(7);
        MateriaResponse response = materiaService.insert(materiaRequest, actualToken);
        if (response.success()){
            log.info("POST /materias -> Materia cadastrado com sucesso.");
        } else {
            log.info("POST /materias -> Erro ao cadastrar novo materia: [{}].", response.message());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

}
