package com.achat.service;

import com.achat.dao.FactureDAO;
import com.achat.model.Facture;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class FactureService {

    private final FactureDAO factureDAO;

    public FactureService() {
        factureDAO = new FactureDAO();
    }

    /**
     * Ajouter une facture
     */
    public void ajouter(Facture facture)
            throws SQLException {

        if (facture == null) {
            throw new IllegalArgumentException(
                    "La facture ne peut pas être null."
            );
        }

        // Numéro de facture obligatoire
        if (facture.getNumFacture() == null
                || facture.getNumFacture().isBlank()) {

            throw new IllegalArgumentException(
                    "Le numéro de facture est obligatoire."
            );
        }

        facture.setNumFacture(
                facture.getNumFacture().trim()
        );

        // Date automatique si elle n'est pas renseignée
        if (facture.getDateFacture() == null) {
            facture.setDateFacture(LocalDate.now());
        }

        // Vérifier le montant HT
        if (facture.getMontantTotalHt() < 0) {
            throw new IllegalArgumentException(
                    "Le montant HT ne peut pas être négatif."
            );
        }

        // Vérifier la TVA
        if (facture.getMontantTva() < 0) {
            throw new IllegalArgumentException(
                    "Le montant de TVA ne peut pas être négatif."
            );
        }

        // État de paiement par défaut
        if (facture.getEtatPaiement() == null
                || facture.getEtatPaiement().isBlank()) {

            facture.setEtatPaiement("NON_PAYEE");
        }

        facture.setEtatPaiement(
                facture.getEtatPaiement()
                        .trim()
                        .toUpperCase()
        );

        // Vérifier l'état
        verifierEtatPaiement(
                facture.getEtatPaiement()
        );

        // La commande facturée est obligatoire
        if (facture.getIdCommande() <= 0) {
            throw new IllegalArgumentException(
                    "Veuillez sélectionner la commande à facturer."
            );
        }

        // Ajouter dans la base
        factureDAO.ajouter(facture);
    }

    /**
     * Récupérer toutes les factures
     */
    public List<Facture> findAll()
            throws SQLException {

        return factureDAO.findAll();
    }

    /**
     * Récupérer une facture par son identifiant
     */
    public Facture findById(int idFacture)
            throws SQLException {

        if (idFacture <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant de la facture est invalide."
            );
        }

        return factureDAO.findById(idFacture);
    }

    /**
     * Rechercher une facture
     */
    public List<Facture> rechercher(String motCle)
            throws SQLException {

        if (motCle == null) {
            motCle = "";
        }

        return factureDAO.rechercher(
                motCle.trim()
        );
    }

    /**
     * Rechercher une facture par son numéro
     */
    public Facture findByNumero(
            String numFacture)
            throws SQLException {

        if (numFacture == null
                || numFacture.isBlank()) {

            throw new IllegalArgumentException(
                    "Le numéro de facture est obligatoire."
            );
        }

        return factureDAO.findByNumero(
                numFacture.trim()
        );
    }

    /**
     * Modifier une facture
     */
    public void modifier(Facture facture)
            throws SQLException {

        if (facture == null) {
            throw new IllegalArgumentException(
                    "La facture ne peut pas être null."
            );
        }

        // Vérifier l'identifiant
        if (facture.getIdFacture() <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant de la facture est invalide."
            );
        }

        // Vérifier le numéro
        if (facture.getNumFacture() == null
                || facture.getNumFacture().isBlank()) {

            throw new IllegalArgumentException(
                    "Le numéro de facture est obligatoire."
            );
        }

        facture.setNumFacture(
                facture.getNumFacture().trim()
        );

        // Vérifier la date
        if (facture.getDateFacture() == null) {
            throw new IllegalArgumentException(
                    "La date de facture est obligatoire."
            );
        }

        // Vérifier les montants
        if (facture.getMontantTotalHt() < 0) {
            throw new IllegalArgumentException(
                    "Le montant HT ne peut pas être négatif."
            );
        }

        if (facture.getMontantTva() < 0) {
            throw new IllegalArgumentException(
                    "Le montant de TVA ne peut pas être négatif."
            );
        }

        // Vérifier l'état
        if (facture.getEtatPaiement() == null
                || facture.getEtatPaiement().isBlank()) {

            throw new IllegalArgumentException(
                    "L'état de paiement est obligatoire."
            );
        }

        facture.setEtatPaiement(
                facture.getEtatPaiement()
                        .trim()
                        .toUpperCase()
        );

        verifierEtatPaiement(
                facture.getEtatPaiement()
        );

        // La commande facturée est obligatoire
        if (facture.getIdCommande() <= 0) {
            throw new IllegalArgumentException(
                    "Veuillez sélectionner la commande à facturer."
            );
        }

        factureDAO.modifier(facture);
    }

    /**
     * Supprimer une facture
     */
    public void supprimer(int idFacture)
            throws SQLException {

        if (idFacture <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant de la facture est invalide."
            );
        }

        factureDAO.supprimer(idFacture);
    }

    /**
     * Calculer le montant TTC
     */
    public double calculerTtc(Facture facture) {

        if (facture == null) {
            throw new IllegalArgumentException(
                    "La facture ne peut pas être null."
            );
        }

        return facture.getMontantTotalHt()
                + facture.getMontantTva();
    }

    /**
     * Vérifier l'état de paiement
     */
    private void verifierEtatPaiement(
            String etatPaiement) {

        if (!etatPaiement.equals("NON_PAYEE")
                && !etatPaiement.equals("PAYEE")
                && !etatPaiement.equals("PARTIELLEMENT_PAYEE")) {

            throw new IllegalArgumentException(
                    "État de paiement invalide. "
                    + "Valeurs acceptées : "
                    + "NON_PAYEE, PAYEE, PARTIELLEMENT_PAYEE."
            );
        }
    }
}