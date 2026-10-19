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
    name = "CurtidaPublicacao",
    uniqueConstraints = @UniqueConstraint(
        name = "UX_CurtidaPublicacao_Publicacao_Usuario",
        columnNames = {"publicacaoId", "usuarioId"}
    )
)
public class CurtidaPublicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "publicacaoId", nullable = false)
    private Long publicacaoId;

    @Column(name = "usuarioId", nullable = false)
    private Long usuarioId;

    @Column(name = "criadoEm", nullable = false)
    private LocalDateTime criadoEm;

    public CurtidaPublicacao() {
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

    public Long getPublicacaoId() {
        return publicacaoId;
    }

    public void setPublicacaoId(Long publicacaoId) {
        this.publicacaoId = publicacaoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
