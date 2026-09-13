package com.achat.model;

public class Personne {

    private int idFournisseur;
    private String nom;
    private String prenom;

    public Personne() {
    }

    public Personne(int idFournisseur, String nom, String prenom) {
        this.idFournisseur = idFournisseur;
        this.nom = nom;
        this.prenom = prenom;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}