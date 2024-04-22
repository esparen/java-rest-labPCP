package br.com.fullstackedu.labpcp.controller.dto.response;

import br.com.fullstackedu.labpcp.database.entity.DocenteEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record NovoDocenteResponse(Boolean success, LocalDateTime timestamp, String message, DocenteEntity docenteEntity, HttpStatus httpStatus) {
}

