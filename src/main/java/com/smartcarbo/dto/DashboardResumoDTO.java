package com.smartcarbo.model.dto;

import java.time.LocalDate;

public class DashboardResumoDTO {

    private LocalDate data;
    private double caloriasConsumidas;
    private double carboidratos;
    private double proteinas;
    private double gorduras;
    private double fibras;
    private double sodio;
    private int quantidadeRefeicoes;

    public DashboardResumoDTO() {
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public double getCaloriasConsumidas() {
        return caloriasConsumidas;
    }

    public void setCaloriasConsumidas(double caloriasConsumidas) {
        this.caloriasConsumidas = caloriasConsumidas;
    }

    public double getCarboidratos() {
        return carboidratos;
    }

    public void setCarboidratos(double carboidratos) {
        this.carboidratos = carboidratos;
    }

    public double getProteinas() {
        return proteinas;
    }

    public void setProteinas(double proteinas) {
        this.proteinas = proteinas;
    }

    public double getGorduras() {
        return gorduras;
    }

    public void setGorduras(double gorduras) {
        this.gorduras = gorduras;
    }

    public double getFibras() {
        return fibras;
    }

    public void setFibras(double fibras) {
        this.fibras = fibras;
    }

    public double getSodio() {
        return sodio;
    }

    public void setSodio(double sodio) {
        this.sodio = sodio;
    }

    public int getQuantidadeRefeicoes() {
        return quantidadeRefeicoes;
    }

    public void setQuantidadeRefeicoes(int quantidadeRefeicoes) {
        this.quantidadeRefeicoes = quantidadeRefeicoes;
    }
}