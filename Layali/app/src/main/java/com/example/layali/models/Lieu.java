package com.example.layali.models;

import com.google.firebase.firestore.GeoPoint;

public class Lieu {

    private String idLieu;
    private String nomLieu, adresse, ville;
    private int capacite;
    private GeoPoint geoPoint;



    public Lieu(String adresse) {
        this.adresse = adresse != null ? adresse : "";
    }
    // Constructeurs
    public Lieu() {
    }

    public Lieu(String idLieu, String nomLieu, String adresse, String ville, int capacite , GeoPoint geoPoint) {
        this.idLieu = idLieu;
        this.nomLieu = nomLieu;
        this.adresse = adresse;
        this.ville = ville;
        this.capacite = capacite;
        this.geoPoint = geoPoint;
    }

    // Getters et Setters

    public String getIdLieu() {
        return idLieu;
    }

    public void setIdLieu(String idLieu) {
        this.idLieu = idLieu;
    }

    public String getNomLieu() {
        return nomLieu;
    }

    public void setNomLieu(String nomLieu) {
        this.nomLieu = nomLieu;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public GeoPoint getLocation() {
        return geoPoint;
    }

    public void setLocation(GeoPoint location) {
        this.geoPoint = location;
    }
}
