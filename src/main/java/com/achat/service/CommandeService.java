package com.achat.service;

import com.achat.dao.CommandeDAO;
import com.achat.dao.LigneCommandeDAO;
import com.achat.dao.ProposerDAO;
import com.achat.database.DatabaseConnection;
import com.achat.model.Commande;
import com.achat.model.LigneCommande;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandeService {

    private final CommandeDAO commandeDAO;
    private final LigneCommandeDAO ligneCommandeDAO;
    private final ProposerDAO proposerDAO;

    public CommandeService() {
        commandeDAO = new CommandeDAO();
        ligneCommandeDAO = new LigneCommandeDAO();
        proposerDAO = new ProposerDAO();
    }

    /**
     * Ajouter une commande avec ses lignes.
     *
     * Le prix d'achat est récupéré automatiquement
     * depuis la table PROPOSER.
     */
    public void ajouter(
            Commande commande,
            List<LigneCommande> lignes)
            throws SQLException {

        // -------------------------------------------------
        // 1. Vérifier la commande
        // -------------------------------------------------

        if (commande == null) {
            throw new IllegalArgumentException(
                    "La commande ne peut pas être null."
            );
        }

        if (commande.getIdFournisseur() <= 0) {
            throw new IllegalArgumentException(
                    "Le fournisseur de la commande est obligatoire."
            );
        }

        if (commande.getDateCommande() == null) {
            commande.setDateCommande(LocalDate.now());
        }

        if (commande.getEtatCommande() == null
                || commande.getEtatCommande().isBlank()) {

            commande.setEtatCommande("EN_ATTENTE");
        }

        commande.setEtatCommande(
                commande.getEtatCommande()
                        .trim()
                        .toUpperCase()
        );

        // -------------------------------------------------
        // 2. Vérifier les lignes
        // -------------------------------------------------

        if (lignes == null || lignes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Une commande doit contenir au moins un produit."
            );
        }

        /*
         * Permet de détecter deux fois le même produit
         * dans une même commande.
         */
        Set<Integer> produitsDejaAjoutes =
                new HashSet<>();

        for (LigneCommande ligne : lignes) {

            if (ligne == null) {
                throw new IllegalArgumentException(
                        "Une ligne de commande est invalide."
                );
            }

            if (ligne.getIdProduit() <= 0) {
                throw new IllegalArgumentException(
                        "Le produit de la ligne est obligatoire."
                );
            }

            if (ligne.getQuantiteCommandee() <= 0) {
                throw new IllegalArgumentException(
                        "La quantité commandée doit être "
                        + "supérieure à zéro."
                );
            }

            /*
             * Le prix n'est plus vérifié ici.
             *
             * Il sera récupéré automatiquement
             * depuis PROPOSER.
             */

            if (!produitsDejaAjoutes.add(
                    ligne.getIdProduit())) {

                throw new IllegalArgumentException(
                        "Le produit "
                        + ligne.getIdProduit()
                        + " apparaît plusieurs fois "
                        + "dans la commande."
                );
            }
        }

        // -------------------------------------------------
        // 3. Transaction
        // -------------------------------------------------

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            try {

                connection.setAutoCommit(false);

                // -----------------------------------------
                // 4. Ajouter la commande
                // -----------------------------------------

                int idCommande =
                        commandeDAO.ajouter(
                                commande,
                                connection
                        );

                // -----------------------------------------
                // 5. Ajouter les lignes
                // -----------------------------------------

                for (LigneCommande ligne : lignes) {

                    int idProduit =
                            ligne.getIdProduit();

                    /*
                     * Récupérer automatiquement le prix
                     * proposé par le fournisseur.
                     */
                    double prix =
                            proposerDAO.getPrix(
                                    commande.getIdFournisseur(),
                                    idProduit,
                                    connection
                            );

                    // -------------------------------------
                    // Vérifier que le fournisseur propose
                    // bien le produit
                    // -------------------------------------

                    if (prix < 0) {

                        throw new IllegalArgumentException(
                                "Le fournisseur "
                                + commande.getIdFournisseur()
                                + " ne propose pas le produit "
                                + idProduit
                                + "."
                        );
                    }

                    /*
                     * On sauvegarde le prix trouvé
                     * dans la ligne de commande.
                     *
                     * Le prix devient donc le prix
                     * historique de cette commande.
                     */
                    ligne.setPrixUnitaireAchat(prix);

                    ligne.setIdCommande(idCommande);

                    ligneCommandeDAO.ajouter(
                            ligne,
                            connection
                    );
                }

                // -----------------------------------------
                // 6. Valider la transaction
                // -----------------------------------------

                connection.commit();

            } catch (SQLException | RuntimeException e) {

                // Annuler toutes les opérations
                connection.rollback();

                throw e;
            }
        }
    }

    /**
     * Récupérer toutes les commandes.
     */
    public List<Commande> findAll()
            throws SQLException {

        return commandeDAO.findAll();
    }

    /**
     * Rechercher une commande.
     */
    public List<Commande> rechercher(
            String motCle)
            throws SQLException {

        if (motCle == null) {
            motCle = "";
        }

        return commandeDAO.rechercher(
                motCle.trim()
        );
    }

    /**
     * Récupérer les lignes d'une commande.
     */
    public List<LigneCommande> findLignes(
            int idCommande)
            throws SQLException {

        if (idCommande <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant de la commande est invalide."
            );
        }

        return ligneCommandeDAO.findByCommande(
                idCommande
        );
    }

    /**
     * Calculer le total d'une commande.
     */
    public double calculerTotal(
            int idCommande)
            throws SQLException {

        if (idCommande <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant de la commande est invalide."
            );
        }

        return ligneCommandeDAO.calculerTotalCommande(
                idCommande
        );
    }

    /**
     * Modifier une commande.
     */
    public void modifier(
            Commande commande)
            throws SQLException {

        if (commande == null) {
            throw new IllegalArgumentException(
                    "La commande ne peut pas être null."
            );
        }

        if (commande.getIdCommande() <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant de la commande est invalide."
            );
        }

        if (commande.getIdFournisseur() <= 0) {
            throw new IllegalArgumentException(
                    "Le fournisseur est obligatoire."
            );
        }

        if (commande.getDateCommande() == null) {
            throw new IllegalArgumentException(
                    "La date de commande est obligatoire."
            );
        }

        if (commande.getEtatCommande() == null
                || commande.getEtatCommande().isBlank()) {

            throw new IllegalArgumentException(
                    "L'état de la commande est obligatoire."
            );
        }

        commande.setEtatCommande(
                commande.getEtatCommande()
                        .trim()
                        .toUpperCase()
        );

        commandeDAO.modifier(commande);
    }

    /**
     * Supprimer une commande.
     */
    public void supprimer(
            int idCommande)
            throws SQLException {

        if (idCommande <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant de la commande est invalide."
            );
        }

        commandeDAO.supprimer(idCommande);
    }
}