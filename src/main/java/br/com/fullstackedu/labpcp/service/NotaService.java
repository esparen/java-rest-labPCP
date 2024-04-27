package br.com.fullstackedu.labpcp.service;

import br.com.fullstackedu.labpcp.controller.dto.request.NotaRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.NotaResponse;
import br.com.fullstackedu.labpcp.database.entity.AlunoEntity;
import br.com.fullstackedu.labpcp.database.entity.DocenteEntity;
import br.com.fullstackedu.labpcp.database.entity.MateriaEntity;
import br.com.fullstackedu.labpcp.database.entity.NotaEntity;
import br.com.fullstackedu.labpcp.database.repository.AlunoRepository;
import br.com.fullstackedu.labpcp.database.repository.DocenteRepository;
import br.com.fullstackedu.labpcp.database.repository.MateriaRepository;
import br.com.fullstackedu.labpcp.database.repository.NotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotaService {
    private final NotaRepository notaRepository;
    private final DocenteRepository docenteRepository;
    private final AlunoRepository alunoRepository;
    private final MateriaRepository materiaRepository;
    private final LoginService loginService;

    private static final List<String> commonPermissions = List.of("ADM", "PROFESSOR");
    private static final List<String> deletePermission = List.of("ADM");

    private boolean _isAuthorized(String actualToken, List<String> authorizedPerfis) {
        String papelName =  loginService.getFieldInToken(actualToken, "scope");
        return authorizedPerfis.contains(papelName);
    }
    private boolean _isAuthorized(String actualToken) {
        return _isAuthorized(actualToken, commonPermissions);
    }

    public NotaResponse insert(NotaRequest notaRequest, String actualToken) {
        try {
            if (!_isAuthorized(actualToken)){
                String errMessage = "O Usuário logado não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new NotaResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
            }
            return _insertNota(notaRequest);
        } catch (Exception e) {
            log.error("Falha ao adicionar Nota. Erro: {}", e.getMessage());
            return new NotaResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
        }
    }

    private NotaResponse _insertNota(NotaRequest notaRequest) {
        AlunoEntity targetAluno = alunoRepository.findById(notaRequest.id_aluno()).orElse(null);
        if (Objects.isNull(targetAluno)){
            String errMessage = "Erro ao cadastrar nota: Nenhum aluno com id ["+ notaRequest.id_aluno() +"] encontrado";
            log.error(errMessage);
            return new NotaResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.NOT_FOUND);
        }

        DocenteEntity targetProfessor = docenteRepository.findById(notaRequest.id_professor()).orElse(null);
        if (Objects.isNull(targetProfessor)){
            String errMessage = "Erro ao cadastrar nota: Nenhum professor com id ["+ notaRequest.id_professor() +"] encontrado";
            log.error(errMessage);
            return new NotaResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.NOT_FOUND);
        } else if (!targetProfessor.getUsuario().getPapel().getNome().toUpperCase().contains("PROFESSOR")) {
            String errMessage = "Erro ao cadastrar nota: Docente Id ["+ notaRequest.id_professor() + "] não possui o papel de Professor";
            log.error(errMessage);
            return new NotaResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.NOT_FOUND);
        }

        MateriaEntity targetMateria = materiaRepository.findById(notaRequest.id_materia()).orElse(null);
        if (Objects.isNull(targetMateria)){
            String errMessage = "Erro ao cadastrar nota: Nenhuma materia com id ["+ notaRequest.id_materia() +"] encontrada";
            log.error(errMessage);
            return new NotaResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.NOT_FOUND);
        }

        NotaEntity newNota = new NotaEntity();


        newNota.setAluno(targetAluno);
        newNota.setProfessor(targetProfessor);
        newNota.setMateria(targetMateria);
        newNota.setData(notaRequest.data());
        newNota.setValor(notaRequest.valor());
        NotaEntity insertedNota = notaRepository.save(newNota);
        log.info("Nota adicionada com sucesso: {}", newNota.getId());
        return new NotaResponse(true, LocalDateTime.now(),"Nota adicionada com sucesso.", Collections.singletonList(insertedNota), HttpStatus.CREATED);
    }
}
