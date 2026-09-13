package com.achat.model;

public class Fournisseur {

    private int idFournisseur;
    private String typeFournisseur;
    private String adresse;
    private String email;
    private String telephone;

    public Fournisseur() {
    }

    public Fournisseur(String typeFournisseur, String adresse,
                       String email, String telephone) {
        this.typeFournisseur = typeFournisseur;
        this.adresse = adresse;
        this.email = email;
        this.telephone = telephone;
    }

    public Fournisseur(int idFournisseur, String typeFournisseur,
                       String adresse, String email, String telephone) {
        this.idFournisseur = idFournisseur;
        this.typeFournisseur = typeFournisseur;
        this.adresse = adresse;
        this.email = email;
        this.telephone = telephone;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public String getTypeFournisseur() {
        return typeFournisseur;
    }

    public void setTypeFournisseur(String typeFournisseur) {
        this.typeFournisseur = typeFournisseur;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "Fournisseur{" +
                "idFournisseur=" + idFournisseur +
                ", typeFournisseur='" + typeFournisseur + '\'' +
                ", adresse='" + adresse + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}