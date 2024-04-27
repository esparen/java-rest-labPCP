package br.com.fullstackedu.labpcp.service;

import br.com.fullstackedu.labpcp.controller.dto.request.MateriaRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.MateriaResponse;
import br.com.fullstackedu.labpcp.database.entity.CursoEntity;
import br.com.fullstackedu.labpcp.database.entity.MateriaEntity;
import br.com.fullstackedu.labpcp.database.repository.CursoRepository;
import br.com.fullstackedu.labpcp.database.repository.MateriaRepository;
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
public class MateriaService {
    private final MateriaRepository materiaRepository;
    private final CursoRepository cursoRepository;
    private final LoginService loginService;


    private static final List<String> commonPermissions = List.of("ADM", "PEDAGOGICO");
    private static final List<String> deletePermission = List.of("ADM");

    private boolean _isAuthorized(String actualToken, List<String> authorizedPerfis) {
        String papelName =  loginService.getFieldInToken(actualToken, "scope");
        return authorizedPerfis.contains(papelName);
    }
    private boolean _isAuthorized(String actualToken) {
        return _isAuthorized(actualToken, commonPermissions);
    }

    public MateriaResponse insert(MateriaRequest materiaRequest, String actualToken) {
        try {
            if (!_isAuthorized(actualToken)){
                String errMessage = "O Usuário logado não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new MateriaResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
            }
            return _insertMateria(materiaRequest);
        } catch (Exception e) {
            log.error("Falha ao adicionar Materia. Erro: {}", e.getMessage());
            return new MateriaResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
        }
    }

    private MateriaResponse _insertMateria(MateriaRequest materiaRequest) {
        CursoEntity targetCurso = cursoRepository.findById(materiaRequest.id_curso()).orElse(null);
        if (Objects.isNull(targetCurso)){
            String errMessage = "Erro ao cadastrar turma: Nenhum curso com id ["+ materiaRequest.id_curso() +"] encontrado";
            log.error(errMessage);
            return new MateriaResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.NOT_FOUND);
        }

        MateriaEntity newMateria = new MateriaEntity();
        newMateria.setNome(materiaRequest.nome());
        newMateria.setCurso(targetCurso);
        MateriaEntity insertedMateria = materiaRepository.save(newMateria);
        log.info("Materia adicionada com sucesso: {}", newMateria.getId());
        return new MateriaResponse(true, LocalDateTime.now(),"Materia adicionada com sucesso.", Collections.singletonList(insertedMateria), HttpStatus.CREATED);
    }

    public MateriaResponse getById(Long materiaId, String actualToken) {
        try {
            if (!_isAuthorized(actualToken)){
                String errMessage = "O Usuário logado não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new MateriaResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
            }
            MateriaEntity targetMateria = materiaRepository.findById(materiaId).orElse(null);
            if (Objects.isNull(targetMateria)){
                return new MateriaResponse(false, LocalDateTime.now() , "Materia ID "+materiaId+" não encontrado." , null, HttpStatus.NOT_FOUND);
            } else
                return new MateriaResponse(true, LocalDateTime.now() , "Materia encontrada" , Collections.singletonList(targetMateria), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Falha ao buscar Materia ID {}. Erro: {}", materiaId, e.getMessage());
            return new MateriaResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
        }
    }
}
