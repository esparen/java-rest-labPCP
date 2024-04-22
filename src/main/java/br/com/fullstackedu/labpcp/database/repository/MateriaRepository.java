package br.com.fullstackedu.labpcp.database.repository;

import br.com.fullstackedu.labpcp.database.entity.MateriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository<MateriaEntity, Long> {
}
