package br.com.fullstackedu.labpcp.service;

import br.com.fullstackedu.labpcp.controller.dto.request.CursoRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.CursoResponse;
import br.com.fullstackedu.labpcp.database.entity.CursoEntity;
import br.com.fullstackedu.labpcp.database.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CursoService {
    private final CursoRepository cursoRepository;
    private final LoginService loginService;

    private boolean _isAuthorized(String actualToken) {
        String papelName =  loginService.getFieldInToken(actualToken, "scope");
        List<String> authorizedPapeis =  Arrays.asList("ADM", "PEDAGOGICO");
        return authorizedPapeis.contains(papelName);
    }

    public CursoResponse insertCurso(CursoRequest cursoRequest, String actualToken) {
        try {
            if (!_isAuthorized(actualToken)){
                String errMessage = "Usuários logado não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new CursoResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
            }
            return _insertCurso(cursoRequest);
        } catch (Exception e) {
            log.error("Falha ao adicionar Curso. Erro: {}", e.getMessage());
            return new CursoResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
        }
    }

    private CursoResponse _insertCurso(CursoRequest cursoRequest) {
        CursoEntity newCurso = new CursoEntity();
        newCurso.setNome(cursoRequest.nome());
        CursoEntity insertedCurso = cursoRepository.save(newCurso);
        log.info("Curso adicionada com sucesso: {}", newCurso.getId());
        return new CursoResponse(true, LocalDateTime.now(),"Curso adicionada com sucesso.", Collections.singletonList(insertedCurso), HttpStatus.CREATED);
    }

    public CursoResponse getById(Long cursoId, String actualToken) {
        try {
            if (!_isAuthorized(actualToken)){
                String errMessage = "Usuário logado não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new CursoResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
            }
            CursoEntity targetCurso = cursoRepository.findById(cursoId).orElse(null);
            if (Objects.isNull(targetCurso)){
                return new CursoResponse(false, LocalDateTime.now() , "Curso ID "+cursoId+" não encontrado." , null, HttpStatus.NOT_FOUND);
            } else
                return new CursoResponse(true, LocalDateTime.now() , "Curso encontrado" , Collections.singletonList(targetCurso), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Falha ao buscar Curso ID {}. Erro: {}", cursoId, e.getMessage());
            return new CursoResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
        }
    }
}
