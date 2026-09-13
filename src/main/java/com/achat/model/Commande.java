package com.achat.model;

import java.time.LocalDate;

public class Commande {

    private int idCommande;
    private LocalDate dateCommande;
    private String etatCommande;
    private int idFournisseur;

    public Commande() {
    }

    public Commande(LocalDate dateCommande,
                    String etatCommande,
                    int idFournisseur) {

        this.dateCommande = dateCommande;
        this.etatCommande = etatCommande;
        this.idFournisseur = idFournisseur;
    }

    public Commande(int idCommande,
                    LocalDate dateCommande,
                    String etatCommande,
                    int idFournisseur) {

        this.idCommande = idCommande;
        this.dateCommande = dateCommande;
        this.etatCommande = etatCommande;
        this.idFournisseur = idFournisseur;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public String getEtatCommande() {
        return etatCommande;
    }

    public void setEtatCommande(String etatCommande) {
        this.etatCommande = etatCommande;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    @Override
    public String toString() {
        return "Commande #" + idCommande;
    }
}