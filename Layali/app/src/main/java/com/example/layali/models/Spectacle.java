package com.example.layali.models;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.json.JSONObject;
import org.json.JSONException;

public class Spectacle implements Serializable {
    private String idSpec;
    private String titre;
    private String dateS;
    private String heur;
    private String H_DebutS;
    private String duree;
    private int nbrSpectateur;
    private String description;
    private String siteWeb;
    private String imageUrl;
    private float prix;
    private Lieu lieu;

    private String idLieu ;
    private Timestamp dateHeur ;

    private String nomLieu ;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.FRANCE);

    // Constructors
    public Spectacle() {  this.lieu = new Lieu();  }

    public Spectacle(String idSpec, String titre, String dateS, String heure, String duree,
            int nbrSpectateur, String description, String site_web, String imageUrl, Lieu lieu) {
        this.idSpec = idSpec;
        this.titre = titre;
        this.dateS = dateS;
        this.heur = heure;
        this.duree = duree;
        this.nbrSpectateur = nbrSpectateur;
        this.description = description;
        this.siteWeb = site_web;
        this.imageUrl = imageUrl;
        this.lieu = lieu;

           }

    // Validation methods
    public boolean isValid() {
        return titre != null && !titre.isEmpty() &&
                dateS != null &&
                H_DebutS != null &&
                duree != null && !duree.isEmpty() &&
                nbrSpectateur > 0 ;

    }

    // Date handling methods

    public String getHeure() {
        return heur;
    }

    public void setHeure(String heure) {
        this.heur = heure;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }
    public void setDateFromString(String dateStr) throws ParseException {
        this.dateS = String.valueOf(DATE_FORMAT.parse(dateStr));
    }

    public String getDateAsString() {
        return dateS != null ? DATE_FORMAT.format(dateS) : null;
    }

    public void setHeureDebutFromString(String timeStr) throws ParseException {
        this.H_DebutS = String.valueOf(TIME_FORMAT.parse(timeStr));
    }

    public String getHeureDebutAsString() {
        return H_DebutS != null ? TIME_FORMAT.format(H_DebutS) : null;
    }

    public String getNomLieu() {
        return nomLieu;
    }

    // Getters and Setters

    public String getimageUrl() {
        return imageUrl;
    }

    public void setimageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getIdSpec() {
        return idSpec;
    }

    public void setIdSpec(String idSpec) {
        this.idSpec = idSpec;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDateS() {
        return dateS;
    }

    public void setDateS(String dateS) {
        this.dateS = dateS;
    }

    public String getH_DebutS() {
        return H_DebutS;
    }

    public void setH_DebutS(String h_DebutS) {
        this.H_DebutS = h_DebutS;
    }

    public String getDuree() {
        return duree;
    }

    public void setDuree(String duree) {
        this.duree = duree;
    }

    public String getNbrSpectateur() {
        return String.valueOf(nbrSpectateur);
    }

    public void setNbrSpectateur(int nbrSpectateur) {
        if (nbrSpectateur > 0) {
            this.nbrSpectateur = nbrSpectateur;
        }
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSite_web() {
        return siteWeb;
    }

    public void setSite_web(String site_web) {
        this.siteWeb = site_web;
    }

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    @NonNull
    public Lieu getLieu() {
        return lieu != null ? lieu : new Lieu();
    }

    public void getLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public String getIdLieu() {
        return idLieu;
    }

    public float getPrixParType(String typeSelected) {
        if (typeSelected.equals("gold")) {
            return (float) (prix * 1.5);
        } else if (typeSelected.equals("silver")){
            return (float) (prix * 1.2);
        } else
        { return prix;}

    }
}
