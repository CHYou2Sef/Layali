package com.example.layali.models;

public class CartItem {
    private String id;
    private String spectacleId;
    private String titre;
    private String dateS;
    private String imageUrl;
    private String typeBillet;
    private int quantite;
    private double prixUnitaire;
    private double total;

    public CartItem() {} // nécessaire pour Firestore

    // getters & setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSpectacleId() { return spectacleId; }
    public void setSpectacleId(String spectacleId) { this.spectacleId = spectacleId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDateS() { return dateS; }
    public void setDateS(String dateS) { this.dateS = dateS; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getTypeBillet() { return typeBillet; }
    public void setTypeBillet(String typeBillet) { this.typeBillet = typeBillet; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
