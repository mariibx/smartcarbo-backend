package com.smartcarbo.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "SeguimentoUsuario",
    uniqueConstraints = @UniqueConstraint(
        name = "UX_SeguimentoUsuario_Seguidor_Seguido",
        columnNames = {"seguidorId", "seguidoId"}
    )
)
public class SeguimentoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "seguidorId", nullable = false)
    private Long seguidorId;

    @Column(name = "seguidoId", nullable = false)
    private Long seguidoId;

    @Column(name = "criadoEm", nullable = false)
    private LocalDateTime criadoEm;

    public SeguimentoUsuario() {
    }

    @PrePersist
    public void prepararCriacao() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeguidorId() {
        return seguidorId;
    }

    public void setSeguidorId(Long seguidorId) {
        this.seguidorId = seguidorId;
    }

    public Long getSeguidoId() {
        return seguidoId;
    }

    public void setSeguidoId(Long seguidoId) {
        this.seguidoId = seguidoId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
