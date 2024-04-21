package br.com.fullstackedu.labpcp.service;

import br.com.fullstackedu.labpcp.controller.dto.response.NovoUsuarioResponse;
import br.com.fullstackedu.labpcp.database.entity.UsuarioEntity;
import br.com.fullstackedu.labpcp.database.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UsuarioService {
    private UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public NovoUsuarioResponse novoUsuario(UsuarioEntity usuario) throws Exception{
        try {
            UsuarioEntity novoUsuarioEntity = usuarioRepository.save(usuario);
            log.info("Usuario adicionado com sucesso: {}", usuario);
            return new NovoUsuarioResponse(true, LocalDateTime.now(),"Usuário cadastrado com sucesso.", novoUsuarioEntity);
        } catch (Exception e) {
            log.info("Falha ao adicionar usuario. Erro: {}", e.getMessage());
            return new NovoUsuarioResponse(false, LocalDateTime.now() , e.getMessage() , null );
        }
    }
}
