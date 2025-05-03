package com.example.layali.models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Reservation implements Serializable {
    private int idRes;
    private int idUser;
    private int idSpec;
    private Date dateRes;
    private int nbrPlace;
    private double prixTotal;
    private ReservationStatus status;
    private String nomUser;
    private String prenomUser;
    private String emailUser;
    private String titreSpec;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

    // Constructors
    public Reservation() {
        this.status = ReservationStatus.PENDING;
    }

    public Reservation(int idRes, int idUser, int idSpec, Date dateRes, int nbrPlace,
            double prixTotal, ReservationStatus status) {
        this.idRes = idRes;
        this.idUser = idUser;
        this.idSpec = idSpec;
        this.dateRes = dateRes;
        this.nbrPlace = nbrPlace;
        this.prixTotal = prixTotal;
        this.status = status != null ? status : ReservationStatus.PENDING;
    }

    // Validation methods
    public boolean isValid() {
        return idUser > 0 &&
                idSpec > 0 &&
                dateRes != null &&
                nbrPlace > 0 &&
                prixTotal >= 0 &&
                status != null;
    }

    // Date handling methods
    public void setDateFromString(String dateStr) throws ParseException {
        this.dateRes = DATE_FORMAT.parse(dateStr);
    }

    public String getDateAsString() {
        return dateRes != null ? DATE_FORMAT.format(dateRes) : null;
    }

    // Status management
    public boolean canBeCancelled() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED;
    }

    public boolean canBeConfirmed() {
        return status == ReservationStatus.PENDING;
    }

    // Getters and Setters
    public int getIdRes() {
        return idRes;
    }

    public void setIdRes(int idRes) {
        this.idRes = idRes;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        if (idUser > 0) {
            this.idUser = idUser;
        }
    }

    public int getIdSpec() {
        return idSpec;
    }

    public void setIdSpec(int idSpec) {
        if (idSpec > 0) {
            this.idSpec = idSpec;
        }
    }

    public Date getDateRes() {
        return dateRes;
    }

    public void setDateRes(Date dateRes) {
        this.dateRes = dateRes;
    }

    public int getNbrPlace() {
        return nbrPlace;
    }

    public void setNbrPlace(int nbrPlace) {
        if (nbrPlace > 0) {
            this.nbrPlace = nbrPlace;
        }
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        if (prixTotal >= 0) {
            this.prixTotal = prixTotal;
        }
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    public String getNomUser() {
        return nomUser;
    }

    public void setNomUser(String nomUser) {
        this.nomUser = nomUser;
    }

    public String getPrenomUser() {
        return prenomUser;
    }

    public void setPrenomUser(String prenomUser) {
        this.prenomUser = prenomUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getTitreSpec() {
        return titreSpec;
    }

    public void setTitreSpec(String titreSpec) {
        this.titreSpec = titreSpec;
    }
}
