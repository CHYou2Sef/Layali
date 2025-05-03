package com.example.layali.models;

public class Rubrique {

    private int idRub, idSpec, idArt;
    private String H_DebutR, dureeRub, type;

    public Rubrique(int idRub, String H_DebutR, String dureeRub, String type, int idSpec, int idArt) {
        this.idRub = idRub;
        this.H_DebutR = H_DebutR;
        this.dureeRub = dureeRub;
        this.type = type;
        this.idSpec = idSpec;
        this.idArt = idArt;
    }
    // Getters et Setters
    public int getIdRub() {
        return idRub;
    }

    public void setIdRub(int idRub) {
        this.idRub = idRub;
    }

    public int getIdSpec() {
        return idSpec;
    }

    public void setIdSpec(int idSpec) {
        this.idSpec = idSpec;
    }

    public int getIdArt() {
        return idArt;
    }

    public void setIdArt(int idArt) {
        this.idArt = idArt;
    }

    public String getH_DebutR() {
        return H_DebutR;
    }

    public void setH_DebutR(String h_DebutR) {
        H_DebutR = h_DebutR;
    }

    public String getDureeRub() {
        return dureeRub;
    }

    public void setDureeRub(String dureeRub) {
        this.dureeRub = dureeRub;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }





}
