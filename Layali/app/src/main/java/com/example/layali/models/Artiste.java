package com.example.layali.models;

public class Artiste {

    private int idArt;
    private String nomArt, prenomArt, specialite;

    public Artiste(int idArt, String nomArt, String prenomArt, String specialite) {
        this.idArt = idArt;
        this.nomArt = nomArt;
        this.prenomArt = prenomArt;
        this.specialite = specialite;
    }

    // Getters et Setters

    public int getIdArt() {
        return idArt;
    }

    public void setIdArt(int idArt) {
        this.idArt = idArt;
    }

    public String getNomArt() {
        return nomArt;
    }

    public void setNomArt(String nomArt) {
        this.nomArt = nomArt;
    }

    public String getPrenomArt() {
        return prenomArt;
    }

    public void setPrenomArt(String prenomArt) {
        this.prenomArt = prenomArt;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
}
