package br.com.fullstackedu.labpcp.database.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "turma")
public class TurmaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @OneToMany(mappedBy = "turma", cascade = CascadeType.ALL)
    List<AlunoEntity> alunos;

    @ManyToOne
    @JoinColumn(name = "id_professor")
    @NotNull(message = "É necessário um Docente(professor) Válido para cadastrar uma turma")
    private CursoEntity professor;

    @ManyToOne
    @JoinColumn(name = "id_curso")
    @NotNull(message = "É necessário um Curso Válido para cadastrar uma turma")
    private CursoEntity curso;
}


