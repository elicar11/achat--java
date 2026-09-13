package com.achat.model;

public class Produit {

    private int idProduit;
    private String designation;
    private String description;
    private double stockActuel;
    private double stockAlerte;

    public Produit() {
    }

    public Produit(String designation, String description,
                   double stockActuel, double stockAlerte) {
        this.designation = designation;
        this.description = description;
        this.stockActuel = stockActuel;
        this.stockAlerte = stockAlerte;
    }

    public Produit(int idProduit, String designation,
                   String description, double stockActuel,
                   double stockAlerte) {
        this.idProduit = idProduit;
        this.designation = designation;
        this.description = description;
        this.stockActuel = stockActuel;
        this.stockAlerte = stockAlerte;
    }

    public int getIdProduit() {
        return idProduit;
    }

    public void setIdProduit(int idProduit) {
        this.idProduit = idProduit;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(double stockActuel) {
        this.stockActuel = stockActuel;
    }

    public double getStockAlerte() {
        return stockAlerte;
    }

    public void setStockAlerte(double stockAlerte) {
        this.stockAlerte = stockAlerte;
    }

    @Override
    public String toString() {
        return designation;
    }
}