package br.com.fullstackedu.labpcp.controller.dto.response;

import br.com.fullstackedu.labpcp.database.entity.NotaEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public record NotaResponse(Boolean success, LocalDateTime timestamp, String message, List<NotaEntity> notaData, HttpStatus httpStatus) {

}