package com.achat.service;

import com.achat.dao.ProposerDAO;
import com.achat.model.Proposer;

import java.sql.SQLException;
import java.util.List;

public class ProposerService {

    private final ProposerDAO proposerDAO;

    public ProposerService() {
        proposerDAO = new ProposerDAO();
    }

    /**
     * Ajouter une proposition
     */
    public void ajouter(Proposer proposer)
            throws SQLException {

        if (proposer == null) {
            throw new IllegalArgumentException(
                    "La proposition ne peut pas être null."
            );
        }

        // Vérifier le fournisseur
        if (proposer.getIdFournisseur() <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        // Vérifier le produit
        if (proposer.getIdProduit() <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du produit est invalide."
            );
        }

        // Vérifier le prix
        if (proposer.getPrixAchatSpecifique() < 0) {
            throw new IllegalArgumentException(
                    "Le prix d'achat ne peut pas être négatif."
            );
        }

        // Vérifier si la proposition existe déjà
        if (proposerDAO.existe(
                proposer.getIdFournisseur(),
                proposer.getIdProduit())) {

            throw new IllegalArgumentException(
                    "Ce fournisseur propose déjà ce produit."
            );
        }

        // Ajouter dans la base
        proposerDAO.ajouter(
                proposer.getIdFournisseur(),
                proposer.getIdProduit(),
                proposer.getPrixAchatSpecifique()
        );
    }

    /**
     * Récupérer toutes les propositions
     */
    public List<Proposer> findAll()
            throws SQLException {

        return proposerDAO.findAll();
    }

    /**
     * Récupérer les produits proposés
     * par un fournisseur
     */
    public List<Proposer> findByFournisseur(
            int idFournisseur)
            throws SQLException {

        if (idFournisseur <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        return proposerDAO.findByFournisseur(
                idFournisseur
        );
    }

    /**
     * Récupérer les fournisseurs qui proposent
     * un produit
     */
    public List<Proposer> findByProduit(
            int idProduit)
            throws SQLException {

        if (idProduit <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du produit est invalide."
            );
        }

        return proposerDAO.findByProduit(
                idProduit
        );
    }

    /**
     * Vérifier si un fournisseur propose
     * un produit
     */
    public boolean existe(
            int idFournisseur,
            int idProduit)
            throws SQLException {

        if (idFournisseur <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        if (idProduit <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du produit est invalide."
            );
        }

        return proposerDAO.existe(
                idFournisseur,
                idProduit
        );
    }

    /**
     * Récupérer le prix d'achat spécifique
     */
    public double getPrix(
            int idFournisseur,
            int idProduit)
            throws SQLException {

        if (idFournisseur <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        if (idProduit <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du produit est invalide."
            );
        }

        double prix = proposerDAO.getPrix(
                idFournisseur,
                idProduit
        );

        if (prix < 0) {
            throw new IllegalArgumentException(
                    "Ce fournisseur ne propose pas ce produit."
            );
        }

        return prix;
    }

    /**
     * Modifier le prix d'une proposition
     */
    public void modifier(
            int idFournisseur,
            int idProduit,
            double nouveauPrix)
            throws SQLException {

        if (idFournisseur <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        if (idProduit <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du produit est invalide."
            );
        }

        if (nouveauPrix < 0) {
            throw new IllegalArgumentException(
                    "Le prix d'achat ne peut pas être négatif."
            );
        }

        // Vérifier que la proposition existe
        if (!proposerDAO.existe(
                idFournisseur,
                idProduit)) {

            throw new IllegalArgumentException(
                    "Cette proposition n'existe pas."
            );
        }

        proposerDAO.modifier(
                idFournisseur,
                idProduit,
                nouveauPrix
        );
    }

    /**
     * Supprimer une proposition
     */
    public void supprimer(
            int idFournisseur,
            int idProduit)
            throws SQLException {

        if (idFournisseur <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        if (idProduit <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du produit est invalide."
            );
        }

        // Vérifier que la proposition existe
        if (!proposerDAO.existe(
                idFournisseur,
                idProduit)) {

            throw new IllegalArgumentException(
                    "Cette proposition n'existe pas."
            );
        }

        proposerDAO.supprimer(
                idFournisseur,
                idProduit
        );
    }
}