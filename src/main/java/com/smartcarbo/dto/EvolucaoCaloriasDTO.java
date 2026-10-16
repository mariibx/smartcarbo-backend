package com.smartcarbo.model.dto;

import java.time.LocalDate;

public class EvolucaoCaloriasDTO {

private LocalDate data;
private double calorias;

public EvolucaoCaloriasDTO() {
}

public EvolucaoCaloriasDTO(LocalDate data, double calorias) {
    this.data = data;
    this.calorias = calorias;
}

public LocalDate getData() {
    return data;
}

public void setData(LocalDate data) {
    this.data = data;
}

public double getCalorias() {
    return calorias;
}

public void setCalorias(double calorias) {
    this.calorias = calorias;
}

}