package br.com.fullstackedu.labpcp.controller;

import br.com.fullstackedu.labpcp.controller.dto.request.CursoRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.CursoResponse;
import br.com.fullstackedu.labpcp.controller.dto.response.MateriaResponse;
import br.com.fullstackedu.labpcp.service.CursoService;
import br.com.fullstackedu.labpcp.service.MateriaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping("/cursos")
public class CursoController {
    private final CursoService cursoService;
    private final MateriaService materiaService;

    @PostMapping()
    public ResponseEntity<CursoResponse> insertCurso(
            @RequestHeader(name = "Authorization") String authToken,
            @Valid @RequestBody CursoRequest cursoRequest
    ) throws Exception {
        log.info("POST /cursos ");

        String actualToken = authToken.substring(7);
        CursoResponse response = cursoService.insertCurso(cursoRequest, actualToken);
        if (response.success()){
            log.info("POST /cursos -> Curso cadastrado com sucesso.");
        } else {
            log.info("POST /cursos -> Erro ao cadastrar novo curso: [{}].", response.message());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @GetMapping("/{cursoId}")
    public ResponseEntity<CursoResponse> getById(
            @RequestHeader(name = "Authorization") String authToken,
            @Valid @PathVariable Long cursoId) {
        log.info("GET /cursos/{} ", cursoId);
        String actualToken = authToken.substring(7);
        CursoResponse response = cursoService.getById(cursoId, actualToken);
        if (response.success()){
            log.info("GET /cursos/{} -> OK ", cursoId);
        } else {
            log.error("GET /cursos/{} -> {} ", cursoId, response.httpStatus());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @GetMapping()
    public ResponseEntity<CursoResponse> getAll(
            @RequestHeader(name = "Authorization") String authToken) throws Exception {
        log.info("GET /cursos ");
        String actualToken = authToken.substring(7);
        CursoResponse response = cursoService.getAll(actualToken);
        if (response.success()){
            log.info("GET /cursos -> OK ");
        } else {
            log.error("GET /cursos -> {}", response.httpStatus());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @GetMapping("/{cursoId}/materias")
    public ResponseEntity<MateriaResponse> getMateriasByCursoId(
            @RequestHeader(name = "Authorization") String authToken,
            @Valid @PathVariable Long cursoId) {
        log.info("GET /cursos/{}/materias ", cursoId);
        String actualToken = authToken.substring(7);
        MateriaResponse response = materiaService.getByCursoId(cursoId, actualToken);
        if (response.success()){
            log.info("GET /cursos/{}/materias -> OK ", cursoId);
        } else {
            log.error("GET /cursos/{}/materias -> {} ", cursoId, response.httpStatus());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @PutMapping("/{cursoId}")
    public ResponseEntity<CursoResponse> updateCurso(
            @PathVariable Long cursoId,
            @Valid @RequestBody CursoRequest cursoUpdateRequest,
            @RequestHeader(name = "Authorization") String authToken) {
        log.info("PUT /cursos");
        String actualToken = authToken.substring(7);
        CursoResponse response = cursoService.update(cursoId , cursoUpdateRequest, actualToken);
        if (response.success()) {
            log.info("PUT /cursos -> OK ");
        } else {
            log.error("PUT /cursos -> {}", response.httpStatus());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }

    @DeleteMapping("/{cursoId}")
    public ResponseEntity<CursoResponse> deleteCurso(
            @PathVariable @NotNull(message = "ID da curso é requerido para exclusão") Long cursoId,
            @RequestHeader(name = "Authorization") String authToken) {
        log.info("DELETE /cursos");
        String actualToken = authToken.substring(7);
        CursoResponse response = cursoService.deleteCurso(cursoId, actualToken);
        if (response.success()) {
            log.info("DELETE /cursos -> OK ");
        } else {
            log.error("DELETE /cursos -> {}", response.httpStatus());
        }
        return ResponseEntity.status(response.httpStatus()).body(response);
    }
}
