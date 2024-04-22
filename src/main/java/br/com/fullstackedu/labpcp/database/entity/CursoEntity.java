package br.com.fullstackedu.labpcp.database.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "curso")
public class CursoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<TurmaEntity> turmas;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    private List<MateriaEntity> materias;

}