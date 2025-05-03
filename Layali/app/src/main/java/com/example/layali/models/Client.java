package com.example.layali.models;

public class Client {

    private int idClt;
    private String nomClt;
    private String prenomClt;
    private String tel;
    private String email;



    private String password;

    public Client() {
    }

    public Client(String nomClt, String email) {
        this.nomClt = nomClt;
        this.email = email;

    }

    public Client(int idClt, String nomClt, String prenomClt, String tel, String email, String password) {
        this.idClt = idClt;
        this.nomClt = nomClt;
        this.prenomClt = prenomClt;
        this.tel = tel;
        this.email = email;
        this.password = password;
    }

    public int getidClt() {
        return idClt;
    }

    public void setidClt(int idClt) {
        this.idClt = idClt;
    }

    public String getnomClt() {
        return nomClt;
    }

    public void setnomClt(String nomClt) {
        this.nomClt = nomClt;
    }

    public String getPrenomClt() {
        return prenomClt;
    }

    public void setPrenomClt(String prenomClt) {
        this.prenomClt = prenomClt;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
