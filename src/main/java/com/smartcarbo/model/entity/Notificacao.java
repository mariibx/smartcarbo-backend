
package com.smartcarbo.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Notificacao")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "destinatarioId", nullable = false)
    private Long destinatarioId;

    @Column(name = "atorId")
    private Long atorId;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 300)
    private String mensagem;

    @Column(name = "referenciaId")
    private Long referenciaId;

    @Column(nullable = false)
    private Boolean lida = false;

    @Column(name = "criadoEm", nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void aoCriar() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
        if (lida == null) {
            lida = false;
        }
    }

    public Long getId() {
        return id;
    }

    public Long getDestinatarioId() {
        return destinatarioId;
    }

    public void setDestinatarioId(Long destinatarioId) {
        this.destinatarioId = destinatarioId;
    }

    public Long getAtorId() {
        return atorId;
    }

    public void setAtorId(Long atorId) {
        this.atorId = atorId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Long getReferenciaId() {
        return referenciaId;
    }

    public void setReferenciaId(Long referenciaId) {
        this.referenciaId = referenciaId;
    }

    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
