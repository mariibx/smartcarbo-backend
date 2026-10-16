
package com.smartcarbo.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RegistroAgua", schema = "dbo")
public class RegistroAgua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuarioId", nullable = false)
    private Long usuarioId;

    @Column(name = "quantidadeMl", nullable = false)
    private Integer quantidadeMl;

    @Column(name = "dataHora", nullable = false)
    private LocalDateTime dataHora;

    public RegistroAgua() {
    }

    public RegistroAgua(Long usuarioId, Integer quantidadeMl,
                        LocalDateTime dataHora) {
        this.usuarioId = usuarioId;
        this.quantidadeMl = quantidadeMl;
        this.dataHora = dataHora;
    }

    @PrePersist
    public void antesDeSalvar() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Integer getQuantidadeMl() {
        return quantidadeMl;
    }

    public void setQuantidadeMl(Integer quantidadeMl) {
        this.quantidadeMl = quantidadeMl;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
