package com.achat.model;

public class LigneCommande {

    private int idCommande;
    private int idProduit;
    private double quantiteCommandee;
    private double prixUnitaireAchat;

    public LigneCommande() {
    }

    public LigneCommande(int idCommande,
                          int idProduit,
                          double quantiteCommandee,
                          double prixUnitaireAchat) {

        this.idCommande = idCommande;
        this.idProduit = idProduit;
        this.quantiteCommandee = quantiteCommandee;
        this.prixUnitaireAchat = prixUnitaireAchat;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public double getQuantiteCommandee() {
        return quantiteCommandee;
    }

    public void setQuantiteCommandee(double quantiteCommandee) {
        this.quantiteCommandee = quantiteCommandee;
    }

    public double getPrixUnitaireAchat() {
        return prixUnitaireAchat;
    }

    public void setPrixUnitaireAchat(double prixUnitaireAchat) {
        this.prixUnitaireAchat = prixUnitaireAchat;
    }

    public double getMontantTotal() {
        return quantiteCommandee * prixUnitaireAchat;
    }

    @Override
    public String toString() {
        return "LigneCommande{" +
                "idCommande=" + idCommande +
                ", idProduit=" + idProduit +
                ", quantite=" + quantiteCommandee +
                ", prix=" + prixUnitaireAchat +
                '}';
    }
}