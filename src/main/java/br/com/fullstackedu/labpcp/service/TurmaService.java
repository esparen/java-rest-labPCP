package br.com.fullstackedu.labpcp.service;

import br.com.fullstackedu.labpcp.controller.dto.request.TurmaCreateRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.TurmaCreateResponse;
import br.com.fullstackedu.labpcp.database.entity.CursoEntity;
import br.com.fullstackedu.labpcp.database.entity.DocenteEntity;
import br.com.fullstackedu.labpcp.database.entity.TurmaEntity;
import br.com.fullstackedu.labpcp.database.repository.CursoRepository;
import br.com.fullstackedu.labpcp.database.repository.DocenteRepository;
import br.com.fullstackedu.labpcp.database.repository.TurmaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class TurmaService {
    private final LoginService loginService;
    private final CursoRepository cursoRepository;
    private final DocenteRepository docenteRepository;
    private final TurmaRepository turmaRepository;

    public TurmaService(LoginService loginService, CursoRepository cursoRepository, DocenteRepository docenteRepository, TurmaRepository turmaRepository) {
        this.loginService = loginService;
        this.cursoRepository = cursoRepository;
        this.docenteRepository = docenteRepository;
        this.turmaRepository = turmaRepository;
    }

    public TurmaCreateResponse novaTurma(TurmaCreateRequest turmaCreateRequest, String authToken) throws Exception{
        try {
            String papelName =  loginService.getFieldInToken(authToken, "scope");
            List<String> authorizedPapeis =  Arrays.asList("ADM", "PEDAGOGICO");
            if (!authorizedPapeis.contains(papelName)){
                String errMessage = "Usuários com papel [" + papelName + "] não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new TurmaCreateResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
            }
            return _insertTurma(turmaCreateRequest);
        } catch (Exception e) {
            log.error("Falha ao adicionar Turma. Erro: {}", e.getMessage());
            return new TurmaCreateResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
        }
    }

    private TurmaCreateResponse _insertTurma(TurmaCreateRequest turmaCreateRequest) {
        //curso
        CursoEntity targetCurso = cursoRepository.findById(turmaCreateRequest.id_curso()).orElse(null);
        if (Objects.isNull(targetCurso)){
            String errMessage = "Erro ao cadastrar turma: Nenhum curso com id ["+ turmaCreateRequest.id_curso() +"] encontrado";
            log.error(errMessage);
            return new TurmaCreateResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.NOT_FOUND);
        }
        TurmaEntity newTurma = new TurmaEntity();

        //professor (not required)
        if (Objects.isNull(turmaCreateRequest.id_professor())) {
            newTurma.setProfessor(null);
        } else {
            DocenteEntity targetProfessor = docenteRepository.findById(turmaCreateRequest.id_professor()).orElse(null);
            if (Objects.isNull(targetProfessor)){
                String errMessage = "Erro ao cadastrar turma: Nenhum professor com id ["+ turmaCreateRequest.id_professor() +"] encontrado";
                log.error(errMessage);
                return new TurmaCreateResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.NOT_FOUND);
            } else
                newTurma.setProfessor(targetProfessor);
        }
        newTurma.setCurso(targetCurso);
        newTurma.setNome(turmaCreateRequest.nome());
        TurmaEntity insertedTurma = turmaRepository.save(newTurma);
        log.info("Turma adicionada com sucesso: {}", newTurma.getId());
        return new TurmaCreateResponse(true, LocalDateTime.now(),"Turma adicionada com sucesso.", Collections.singletonList(newTurma), HttpStatus.CREATED);
    }
}
