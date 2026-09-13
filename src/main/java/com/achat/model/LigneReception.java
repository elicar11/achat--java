package com.achat.model;

public class LigneReception {

    private int idReception;
    private int idProduit;
    private double quantiteRecue;

    public LigneReception() {
    }

    public LigneReception(int idReception,
                          int idProduit,
                          double quantiteRecue) {

        this.idReception = idReception;
        this.idProduit = idProduit;
        this.quantiteRecue = quantiteRecue;
    }

    public int getIdReception() {
        return idReception;
    }

    public void setIdReception(int idReception) {
        this.idReception = idReception;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public double getQuantiteRecue() {
        return quantiteRecue;
    }

    public void setQuantiteRecue(double quantiteRecue) {
        this.quantiteRecue = quantiteRecue;
    }

    @Override
    public String toString() {
        return "LigneReception{" +
                "idReception=" + idReception +
                ", idProduit=" + idProduit +
                ", quantiteRecue=" + quantiteRecue +
                '}';
    }
}