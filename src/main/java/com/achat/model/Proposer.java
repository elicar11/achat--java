package com.achat.model;

public class Proposer {

    private int idFournisseur;
    private int idProduit;
    private double prixAchatSpecifique;

    public Proposer() {
    }

    public Proposer(
            int idFournisseur,
            int idProduit,
            double prixAchatSpecifique) {

        this.idFournisseur = idFournisseur;
        this.idProduit = idProduit;
        this.prixAchatSpecifique = prixAchatSpecifique;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    /**
     *
     * @param idFournisseur
     */
    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public double getPrixAchatSpecifique() {
        return prixAchatSpecifique;
    }

    public void setPrixAchatSpecifique(
            double prixAchatSpecifique) {

        this.prixAchatSpecifique =
                prixAchatSpecifique;
    }

    @Override
    public String toString() {

        return "Proposer{" +
                "idFournisseur=" + idFournisseur +
                ", idProduit=" + idProduit +
                ", prixAchatSpecifique=" +
                prixAchatSpecifique +
                '}';
    }

  
}