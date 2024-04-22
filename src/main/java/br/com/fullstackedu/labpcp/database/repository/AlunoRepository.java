package br.com.fullstackedu.labpcp.database.repository;

import br.com.fullstackedu.labpcp.database.entity.AlunoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<AlunoEntity, Long> {
}
