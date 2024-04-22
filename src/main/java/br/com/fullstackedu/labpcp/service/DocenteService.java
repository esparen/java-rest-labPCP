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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
            return new NovoDocenteResponse(true, LocalDateTime.now(),"Docente cadastrado com sucesso.", newDocenteEntity, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Falha ao adicionar Docente. Erro: {}", e.getMessage());
            return new NovoDocenteResponse(false, LocalDateTime.now() , e.getMessage() , null, HttpStatus.BAD_REQUEST );
        }
    }


}
