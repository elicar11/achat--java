package com.achat.model;

public class Societe {

    private int idFournisseur;
    private String raisonSociale;
    private String nif;
    private String stat;

    public Societe() {
    }

    public Societe(int idFournisseur, String raisonSociale,
                   String nif, String stat) {
        this.idFournisseur = idFournisseur;
        this.raisonSociale = raisonSociale;
        this.nif = nif;
        this.stat = stat;
    }

    public int getIdFournisseur() {
        return idFournisseur;
    }

    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

    public String getRaisonSociale() {
        return raisonSociale;
    }

    public void setRaisonSociale(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    @Override
    public String toString() {
        return raisonSociale;
    }
}