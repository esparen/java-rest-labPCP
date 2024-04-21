package br.com.fullstackedu.labpcp.service;

import br.com.fullstackedu.labpcp.controller.dto.response.NovoUsuarioResponse;
import br.com.fullstackedu.labpcp.database.entity.UsuarioEntity;
import br.com.fullstackedu.labpcp.database.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder bCryptEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder bCryptEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.bCryptEncoder = bCryptEncoder;
    }

    public NovoUsuarioResponse novoUsuario(UsuarioEntity usuario) throws Exception{
        try {
            usuario.setSenha(bCryptEncoder.encode(usuario.getSenha()));
            UsuarioEntity novoUsuarioEntity = usuarioRepository.save(usuario);
            log.info("Usuario adicionado com sucesso: {}", usuario);
            return new NovoUsuarioResponse(true, LocalDateTime.now(),"Usuário cadastrado com sucesso.", novoUsuarioEntity);
        } catch (Exception e) {
            log.info("Falha ao adicionar usuario. Erro: {}", e.getMessage());
            return new NovoUsuarioResponse(false, LocalDateTime.now() , e.getMessage() , null );
        }
    }
}
