package com.achat.service;

import com.achat.dao.ProduitDAO;
import com.achat.model.Produit;

import java.sql.SQLException;
import java.util.List;

public class ProduitService {

    private final ProduitDAO produitDAO;

    public ProduitService() {
        produitDAO = new ProduitDAO();
    }

    /**
     * Ajouter un produit
     */
    public void ajouter(Produit produit)
            throws SQLException {

        // Vérifier que le produit existe
        if (produit == null) {
            throw new IllegalArgumentException(
                    "Le produit ne peut pas être null."
            );
        }

        // Vérifier la désignation
        if (produit.getDesignation() == null
                || produit.getDesignation().isBlank()) {

            throw new IllegalArgumentException(
                    "La désignation du produit est obligatoire."
            );
        }

        // Nettoyer la désignation
        produit.setDesignation(
                produit.getDesignation().trim()
        );

        // Vérifier le stock actuel
        if (produit.getStockActuel() < 0) {
            throw new IllegalArgumentException(
                    "Le stock actuel ne peut pas être négatif."
            );
        }

        // Vérifier le stock d'alerte
        if (produit.getStockAlerte() < 0) {
            throw new IllegalArgumentException(
                    "Le stock d'alerte ne peut pas être négatif."
            );
        }

        // Ajouter dans la base
        produitDAO.ajouter(produit);
    }

    /**
     * Récupérer tous les produits
     */
    public List<Produit> findAll()
            throws SQLException {

        return produitDAO.findAll();
    }

    /**
     * Rechercher un produit par son identifiant.
     */
    public Produit findById(int idProduit)
            throws SQLException {

        if (idProduit <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du produit est invalide."
            );
        }

        return produitDAO.findById(idProduit);
    }

    /**
     * Rechercher un produit
     */
    public List<Produit> rechercher(String motCle)
            throws SQLException {

        if (motCle == null) {
            motCle = "";
        }

        return produitDAO.rechercher(
                motCle.trim()
        );
    }

    /**
     * Modifier un produit
     */
    public void modifier(Produit produit)
            throws SQLException {

        if (produit == null) {
            throw new IllegalArgumentException(
                    "Le produit ne peut pas être null."
            );
        }

        // Vérifier l'identifiant
        if (produit.getIdProduit() <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du produit est invalide."
            );
        }

        // Vérifier la désignation
        if (produit.getDesignation() == null
                || produit.getDesignation().isBlank()) {

            throw new IllegalArgumentException(
                    "La désignation du produit est obligatoire."
            );
        }

        produit.setDesignation(
                produit.getDesignation().trim()
        );

        // Vérifier le stock actuel
        if (produit.getStockActuel() < 0) {
            throw new IllegalArgumentException(
                    "Le stock actuel ne peut pas être négatif."
            );
        }

        // Vérifier le stock d'alerte
        if (produit.getStockAlerte() < 0) {
            throw new IllegalArgumentException(
                    "Le stock d'alerte ne peut pas être négatif."
            );
        }

        produitDAO.modifier(produit);
    }

    /**
     * Supprimer un produit
     */
    public void supprimer(int idProduit)
            throws SQLException {

        if (idProduit <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du produit est invalide."
            );
        }

        produitDAO.supprimer(idProduit);
    }

    /**
     * Vérifier si le stock est inférieur ou égal au seuil d'alerte
     */
    public boolean stockEnAlerte(Produit produit) {

        if (produit == null) {
            throw new IllegalArgumentException(
                    "Le produit ne peut pas être null."
            );
        }

        return produit.getStockActuel()
                <= produit.getStockAlerte();
    }

    /**
     * Vérifier si un produit est en rupture de stock
     */
    public boolean stockRupture(Produit produit) {

        if (produit == null) {
            throw new IllegalArgumentException(
                    "Le produit ne peut pas être null."
            );
        }

        return produit.getStockActuel() <= 0;
    }
}
