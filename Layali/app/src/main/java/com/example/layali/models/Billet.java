package com.example.layali.models;

import java.io.Serializable;

public class Billet implements Serializable {
    private int idBillet;
    private String categorie;
    private double prix;
    private int idSpec;
    private int vendu;
    
    // Constructors
    public Billet() {}
    
    public Billet(int idBillet, String categorie, double prix, int idSpec, int vendu) {
        this.idBillet = idBillet;
        this.categorie = categorie;
        this.prix = prix;
        this.idSpec = idSpec;
        this.vendu = vendu;
    }
    
    // Getters and Setters
    public int getIdBillet() { return idBillet; }
    public void setIdBillet(int idBillet) { this.idBillet = idBillet; }
    
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    
    public int getIdSpec() { return idSpec; }
    public void setIdSpec(int idSpec) { this.idSpec = idSpec; }
    
    public int getVendu() { return vendu; }
    public void setVendu(int vendu) { this.vendu = vendu; }
}
