package br.com.fullstackedu.labpcp.service;

import br.com.fullstackedu.labpcp.controller.dto.request.AlunoRequest;
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

    public AlunoResponse insertAluno(AlunoRequest alunoRequest, String actualToken) {
        try {
            String papelName = loginService.getFieldInToken(actualToken, "scope");
            List<String> authorizedPapeis = Arrays.asList("ADM", "PEDAGOGICO");
            if (!authorizedPapeis.contains(papelName)) {
                String errMessage = "Usuários com papel [" + papelName + "] não tem acesso a essa funcionalidade";
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
}
