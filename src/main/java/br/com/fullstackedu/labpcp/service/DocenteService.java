package br.com.fullstackedu.labpcp.service;

import br.com.fullstackedu.labpcp.controller.dto.request.NovoDocenteRequest;
import br.com.fullstackedu.labpcp.controller.dto.response.NovoDocenteResponse;
import br.com.fullstackedu.labpcp.database.entity.DocenteEntity;
import br.com.fullstackedu.labpcp.database.entity.UsuarioEntity;
import br.com.fullstackedu.labpcp.database.repository.DocenteRepository;
import br.com.fullstackedu.labpcp.database.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class DocenteService {
    private final DocenteRepository docenteRepository;
    private final UsuarioRepository usuarioRepository;
    private final LoginService loginService;

    public DocenteService(DocenteRepository docenteRepository, UsuarioRepository usuarioRepository, LoginService loginService) {
        this.docenteRepository = docenteRepository;
        this.usuarioRepository = usuarioRepository;
        this.loginService = loginService;
    }

    public NovoDocenteResponse novoDocente(NovoDocenteRequest novoDocenteRequest, String authToken) throws Exception{
        try {
            String papelName =  loginService.getFieldInToken(authToken, "scope");
            List<String> authorizedPapeis =  Arrays.asList("ADM", "PEDAGOGICO", "RECRUITER");
            if (!authorizedPapeis.contains(papelName)){
                String errMessage = "Usuários com papel [" + papelName + "] não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new NovoDocenteResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
            }
            UsuarioEntity targetUsuario = usuarioRepository.findById(novoDocenteRequest.id_usuario()).orElse(null);   //getReferenceById();
            if (Objects.isNull(targetUsuario)){
                String errMessage = "Erro ao cadastrar docente: Nenhum usuário com id ["+ novoDocenteRequest.id_usuario() +"] encontrado";
                log.error(errMessage);
                return new NovoDocenteResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.NOT_FOUND);
            }
            DocenteEntity newDocenteEntity = docenteRepository.save(
                    new DocenteEntity(
                            novoDocenteRequest.nome(),
                            novoDocenteRequest.data_entrada(),
                            targetUsuario)
                    );
            log.info("Docente adicionado com sucesso: {}", newDocenteEntity);
            return new NovoDocenteResponse(true, LocalDateTime.now(),"Docente cadastrado com sucesso.", Collections.singletonList(newDocenteEntity), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Falha ao adicionar Docente. Erro: {}", e.getMessage());
            return new NovoDocenteResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
        }
    }


    public NovoDocenteResponse getDocenteById(Long docenteId, String authToken) {
        try {
            String papelName =  loginService.getFieldInToken(authToken, "scope");
            List<String> authorizedPapeis =  Arrays.asList("ADM", "PEDAGOGICO", "RECRUITER");
            if (!authorizedPapeis.contains(papelName)){
                String errMessage = "Usuários com papel [" + papelName + "] não tem acesso a essa funcionalidade";
                log.error(errMessage);
                return new NovoDocenteResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
            }
            DocenteEntity targetDocente = docenteRepository.findById(docenteId).orElse(null);
            if (Objects.isNull(targetDocente)){
                return new NovoDocenteResponse(false, LocalDateTime.now() , "Docente ID "+docenteId+" não encontrado." , null, HttpStatus.NOT_FOUND);
            } else
                return new NovoDocenteResponse(true, LocalDateTime.now() , "Docente encontrado" , Collections.singletonList(targetDocente), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Falha ao buscar Docente ID {}. Erro: {}", docenteId, e.getMessage());
            return new NovoDocenteResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
        }
    }

        public NovoDocenteResponse getAllDocentes(String authToken) {
            try {
                String papelName =  loginService.getFieldInToken(authToken, "scope");
                List<String> authorizedPapeis =  Arrays.asList("ADM", "PEDAGOGICO", "RECRUITER");
                if (!authorizedPapeis.contains(papelName)){
                    String errMessage = "Usuários com papel [" + papelName + "] não tem acesso a essa funcionalidade";
                    log.error(errMessage);
                    return new NovoDocenteResponse(false, LocalDateTime.now() , errMessage , null, HttpStatus.UNAUTHORIZED);
                }
                List<DocenteEntity> listDocentes = docenteRepository.findAll();
                if (listDocentes.isEmpty()){
                    return new NovoDocenteResponse(false, LocalDateTime.now() , "Não há docentes cadastrados." , null, HttpStatus.NOT_FOUND);
                } else
                    return new NovoDocenteResponse(true, LocalDateTime.now(), "Docentes encontrados: " + listDocentes.size() , listDocentes, HttpStatus.OK);
            } catch(Exception e) {
                log.error("Falha ao buscar Docentes cadastrados. Erro: {}", e.getMessage());
                return new NovoDocenteResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
            }

    }
}
