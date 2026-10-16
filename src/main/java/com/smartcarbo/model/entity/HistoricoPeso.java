
package com.smartcarbo.model.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "HistoricoPeso", schema = "dbo")
public class HistoricoPeso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "usuarioId", nullable = false)
    private Long usuarioId;

    @Column(name = "peso", nullable = false)
    private Double peso;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    public HistoricoPeso() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
