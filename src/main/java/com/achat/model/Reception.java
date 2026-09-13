package com.achat.model;

import java.time.LocalDate;

public class Reception {

    private int idReception;
    private LocalDate dateReception;
    private String numBonLivraison;
    private int idCommande;
    private Integer idFacture;

    public Reception() {
    }

    public Reception(LocalDate dateReception,
                     String numBonLivraison,
                     int idCommande,
                     Integer idFacture) {

        this.dateReception = dateReception;
        this.numBonLivraison = numBonLivraison;
        this.idCommande = idCommande;
        this.idFacture = idFacture;
    }

    public Reception(int idReception,
                     LocalDate dateReception,
                     String numBonLivraison,
                     int idCommande,
                     Integer idFacture) {

        this.idReception = idReception;
        this.dateReception = dateReception;
        this.numBonLivraison = numBonLivraison;
        this.idCommande = idCommande;
        this.idFacture = idFacture;
    }

    public int getIdReception() {
        return idReception;
    }

    public void setIdReception(int idReception) {
        this.idReception = idReception;
    }

    public LocalDate getDateReception() {
        return dateReception;
    }

    public void setDateReception(LocalDate dateReception) {
        this.dateReception = dateReception;
    }

    public String getNumBonLivraison() {
        return numBonLivraison;
    }

    public void setNumBonLivraison(String numBonLivraison) {
        this.numBonLivraison = numBonLivraison;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public Integer getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(Integer idFacture) {
        this.idFacture = idFacture;
    }

    @Override
    public String toString() {
        return "Réception #" + idReception;
    }
}