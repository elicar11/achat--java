package com.achat.model;

import java.time.LocalDate;

public class Facture {

    private int idFacture;
    private String numFacture;
    private LocalDate dateFacture;
    private double montantTotalHt;
    private double montantTva;
    private String etatPaiement;

    public Facture() {
    }

    public Facture(String numFacture,
                   LocalDate dateFacture,
                   double montantTotalHt,
                   double montantTva,
                   String etatPaiement) {

        this.numFacture = numFacture;
        this.dateFacture = dateFacture;
        this.montantTotalHt = montantTotalHt;
        this.montantTva = montantTva;
        this.etatPaiement = etatPaiement;
    }

    public Facture(int idFacture,
                   String numFacture,
                   LocalDate dateFacture,
                   double montantTotalHt,
                   double montantTva,
                   String etatPaiement) {

        this.idFacture = idFacture;
        this.numFacture = numFacture;
        this.dateFacture = dateFacture;
        this.montantTotalHt = montantTotalHt;
        this.montantTva = montantTva;
        this.etatPaiement = etatPaiement;
    }

    public int getIdFacture() {
        return idFacture;
    }

    public void setIdFacture(int idFacture) {
        this.idFacture = idFacture;
    }

    public String getNumFacture() {
        return numFacture;
    }

    public void setNumFacture(String numFacture) {
        this.numFacture = numFacture;
    }

    public LocalDate getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDate dateFacture) {
        this.dateFacture = dateFacture;
    }

    public double getMontantTotalHt() {
        return montantTotalHt;
    }

    public void setMontantTotalHt(double montantTotalHt) {
        this.montantTotalHt = montantTotalHt;
    }

    public double getMontantTva() {
        return montantTva;
    }

    public void setMontantTva(double montantTva) {
        this.montantTva = montantTva;
    }

    public String getEtatPaiement() {
        return etatPaiement;
    }

    public void setEtatPaiement(String etatPaiement) {
        this.etatPaiement = etatPaiement;
    }

    public double getMontantTtc() {
        return montantTotalHt + montantTva;
    }

    @Override
    public String toString() {
        return "Facture " + numFacture;
    }
}