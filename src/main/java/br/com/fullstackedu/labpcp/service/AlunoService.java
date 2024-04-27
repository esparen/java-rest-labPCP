package br.com.fullstackedu.labpcp.service;

import br.com.fullstackedu.labpcp.controller.dto.request.AlunoRequest;
import br.com.fullstackedu.labpcp.controller.dto.request.AlunoUpdateRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.AlunoResponse;
import br.com.fullstackedu.labpcp.database.entity.AlunoEntity;
import br.com.fullstackedu.labpcp.database.entity.TurmaEntity;
import br.com.fullstackedu.labpcp.database.entity.UsuarioEntity;
import br.com.fullstackedu.labpcp.database.repository.AlunoRepository;
import br.com.fullstackedu.labpcp.database.repository.TurmaRepository;
import br.com.fullstackedu.labpcp.database.repository.UsuarioRepository;
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
public class AlunoService {
    private final AlunoRepository alunoRepository;
    private final LoginService loginService;
    private final UsuarioRepository usuarioRepository;
    private final TurmaRepository turmaRepository;

    private static final List<String> commonPermissions = List.of("ADM", "PEDAGOGICO");
    private static final List<String> deletePermission = List.of("ADM");

    private boolean _isAuthorized(String actualToken, List<String> authorizedPerfis) {
        String papelName =  loginService.getFieldInToken(actualToken, "scope");
        return authorizedPerfis.contains(papelName);
    }
    private boolean _isAuthorized(String actualToken) {
        return _isAuthorized(actualToken, commonPermissions);
    }

    public AlunoResponse insertAluno(AlunoRequest alunoRequest, String actualToken) {
        try {
            if (!_isAuthorized(actualToken)){
                String errMessage = "O Usuário logado não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new AlunoResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
            }
            UsuarioEntity targetUsuario = usuarioRepository.findById(alunoRequest.id_usuario()).orElse(null);
            if (Objects.isNull(targetUsuario)){
                String errMessage = "Erro ao cadastrar aluno: Nenhum usuário com id ["+ alunoRequest.id_usuario() +"] encontrado";
                log.error(errMessage);
                return new AlunoResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.NOT_FOUND);
            }

            TurmaEntity targetTurma = null;
            if (Objects.nonNull(alunoRequest.id_turma())) {
                targetTurma = turmaRepository.findById(alunoRequest.id_turma()).orElse(null);
                if (Objects.isNull(targetTurma)) {
                    String errMessage = "Erro ao cadastrar aluno: Nenhuma turma com id [" + alunoRequest.id_turma() + "] encontrada";
                    log.error(errMessage);
                    return new AlunoResponse(false, LocalDateTime.now(), errMessage, null, HttpStatus.NOT_FOUND);
                }
            }

            AlunoEntity newAluno = new AlunoEntity();
            newAluno.setUsuario(targetUsuario);
            newAluno.setNome(alunoRequest.nome());
            newAluno.setDataNascimento(alunoRequest.data_nascimento());
            newAluno.setTurma(targetTurma);
            AlunoEntity savedAluno = alunoRepository.save(newAluno);
            log.info("Aluno adicionado com sucesso");
            return new AlunoResponse(true, LocalDateTime.now(), "Aluno cadastrado com sucesso.", Collections.singletonList(savedAluno), HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Falha ao adicionar usuario. Erro: {}", e.getMessage());
            return new AlunoResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
        }

    }

    public AlunoResponse getById(Long alunoId, String actualToken) {
        try {
            if (!_isAuthorized(actualToken)){
                String errMessage = "O Usuário logado não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new AlunoResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
            }
            AlunoEntity targetAluno = alunoRepository.findById(alunoId).orElse(null);
            if (Objects.isNull(targetAluno)){
                return new AlunoResponse(false, LocalDateTime.now() , "Aluno ID "+alunoId+" não encontrado." , null, HttpStatus.NOT_FOUND);
            } else
                return new AlunoResponse(true, LocalDateTime.now() , "Aluno encontrado" , Collections.singletonList(targetAluno), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Falha ao buscar Aluno ID {}. Erro: {}", alunoId, e.getMessage());
            return new AlunoResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
        }
    }

    public AlunoResponse updateAluno(Long alunoId, AlunoUpdateRequest alunoUpdateRequest, String actualToken) {
        try {
            if (!_isAuthorized(actualToken)){
                String errMessage = "O Usuário logado não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new AlunoResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
            }
            return _updateAluno(alunoUpdateRequest,alunoId);

        } catch (Exception e) {
            log.error("Falha ao atualizar Aluno ID {}. Erro: {}", alunoId, e.getMessage());
            return new AlunoResponse(false, LocalDateTime.now(), e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    private AlunoResponse _updateAluno(AlunoUpdateRequest alunoUpdateRequest, Long alunoId) {
        AlunoEntity targetAlunoEntity = alunoRepository.findById(alunoId).orElse(null);
        if (Objects.isNull(targetAlunoEntity))
            return new AlunoResponse(false, LocalDateTime.now() , "Aluno id [" + alunoId + "] não encontrada" , null, HttpStatus.NOT_FOUND);

        if (alunoUpdateRequest.id_turma() != null) {
            TurmaEntity targetTurma = turmaRepository.findById(alunoUpdateRequest.id_turma()).orElse(null);
            if (targetTurma != null) targetAlunoEntity.setTurma(targetTurma);
            else return new AlunoResponse(false, LocalDateTime.now() , "Falha ao associar Turma ID "+ alunoUpdateRequest.id_turma() +" à Aluno ID ["+ alunoId +"]: A turma não existe." , null, HttpStatus.NOT_FOUND);
        }

        if (alunoUpdateRequest.id_usuario() != null) {
            UsuarioEntity targetUsuario = usuarioRepository.findById(alunoUpdateRequest.id_usuario()).orElse(null);
            if (targetUsuario != null) targetAlunoEntity.setUsuario(targetUsuario);
            else return new AlunoResponse(false, LocalDateTime.now() , "Falha ao associar Usuario ID "+ alunoUpdateRequest.id_usuario() +" à Aluno ID ["+ alunoId +"]: O usuário não existe." , null, HttpStatus.NOT_FOUND);
        }

        if (alunoUpdateRequest.nome() != null) targetAlunoEntity.setNome(alunoUpdateRequest.nome());
        if (alunoUpdateRequest.data_nascimento() != null) targetAlunoEntity.setDataNascimento(alunoUpdateRequest.data_nascimento());
        AlunoEntity savedAlunoEntity = alunoRepository.save(targetAlunoEntity);
        return new AlunoResponse(true, LocalDateTime.now(), "Aluno atualizado", Collections.singletonList(savedAlunoEntity) , HttpStatus.OK);
    }

    public AlunoResponse getAllAlunos(String actualToken) {
        try {
            if (!_isAuthorized(actualToken)){
                String errMessage = "O Usuário logado não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new AlunoResponse(false, LocalDateTime.now(), errMessage, null, HttpStatus.UNAUTHORIZED);
            }
            List<AlunoEntity> listAlunos = alunoRepository.findAll();
            if (listAlunos.isEmpty()) {
                return new AlunoResponse(false, LocalDateTime.now(), "Não há alunos cadastrados.", null, HttpStatus.NOT_FOUND);
            } else
                return new AlunoResponse(true, LocalDateTime.now(), "Alunos encontrados: " + listAlunos.size(), listAlunos, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Falha ao buscar Alunos cadastrados. Erro: {}", e.getMessage());
            return new AlunoResponse(false, LocalDateTime.now(), e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
    }

    public AlunoResponse deleteAluno(Long alunoId, String actualToken) {
        try {
            if (!_isAuthorized(actualToken, deletePermission)){
                String errMessage = "O Usuário logado não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new AlunoResponse(false, LocalDateTime.now(), errMessage, null, HttpStatus.UNAUTHORIZED);
            }
            return _deleteAluno(alunoId);

        } catch (Exception e) {
            log.error("Falha ao excluir a aluno {}. Erro: {}", alunoId, e.getMessage());
            return new AlunoResponse(false, LocalDateTime.now(), e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }

    }
    private AlunoResponse _deleteAluno(Long alunoId) {
        AlunoEntity targetAlunoEntity = alunoRepository.findById(alunoId).orElse(null);
        if (Objects.isNull(targetAlunoEntity))
            return new AlunoResponse(false, LocalDateTime.now() , "Aluno id [" + alunoId + "] não encontrada" , null, HttpStatus.NOT_FOUND);
        else {
            alunoRepository.delete(targetAlunoEntity);
            return new AlunoResponse(true, LocalDateTime.now() , "Docente id [" + alunoId + "] excluido" , null, HttpStatus.NO_CONTENT);
        }
    }
}
