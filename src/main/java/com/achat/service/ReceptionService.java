package com.achat.service;

import com.achat.dao.LigneReceptionDAO;
import com.achat.dao.ProduitDAO;
import com.achat.dao.ReceptionDAO;
import com.achat.database.DatabaseConnection;
import com.achat.model.LigneReception;
import com.achat.model.Reception;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service de gestion des réceptions.
 *
 * Responsabilités : - Ajouter une réception - Modifier une réception -
 * Supprimer une réception - Gérer les lignes de réception - Mettre à jour
 * automatiquement le stock - Calculer les quantités déjà reçues
 */
public class ReceptionService {

    private final ReceptionDAO receptionDAO;
    private final LigneReceptionDAO ligneReceptionDAO;
    private final ProduitDAO produitDAO;

    /**
     * Constructeur.
     */
    public ReceptionService() {

        receptionDAO
                = new ReceptionDAO();

        ligneReceptionDAO
                = new LigneReceptionDAO();

        produitDAO
                = new ProduitDAO();
    }

    // =============================================================
    // AJOUTER UNE RECEPTION
    // =============================================================
    /**
     * Ajouter une réception avec ses lignes.
     *
     * Lors de l'ajout : - la réception est créée - les lignes sont enregistrées
     * - le stock est augmenté
     *
     * Toutes les opérations sont effectuées dans une seule transaction.
     */
    public void ajouter(
            Reception reception,
            List<LigneReception> lignes)
            throws SQLException {

        // Vérifier la réception
        if (reception == null) {

            throw new IllegalArgumentException(
                    "La réception ne peut pas être null."
            );
        }

        // Vérifier la commande
        if (reception.getIdCommande() <= 0) {

            throw new IllegalArgumentException(
                    "La commande de la réception est obligatoire."
            );
        }

        // Date automatique
        if (reception.getDateReception() == null) {

            reception.setDateReception(
                    LocalDate.now()
            );
        }

        // Vérifier les lignes
        if (lignes == null
                || lignes.isEmpty()) {

            throw new IllegalArgumentException(
                    "Une réception doit contenir au moins un produit."
            );
        }

        // Vérifier chaque ligne
        for (LigneReception ligne : lignes) {

            if (ligne == null) {

                throw new IllegalArgumentException(
                        "Une ligne de réception est invalide."
                );
            }

            if (ligne.getIdProduit() <= 0) {

                throw new IllegalArgumentException(
                        "Le produit de la ligne est obligatoire."
                );
            }

            if (ligne.getQuantiteRecue() <= 0) {

                throw new IllegalArgumentException(
                        "La quantité reçue doit être supérieure à zéro."
                );
            }
        }

        // =========================================================
        // TRANSACTION
        // =========================================================
        try (Connection connection
                = DatabaseConnection.getConnection()) {

            try {

                connection.setAutoCommit(false);

                // -------------------------------------------------
                // 1. Ajouter la réception
                // -------------------------------------------------
                int idReception
                        = receptionDAO.ajouter(
                                reception,
                                connection
                        );

                // -------------------------------------------------
                // 2. Ajouter les lignes
                // -------------------------------------------------
                for (LigneReception ligne : lignes) {

                    ligne.setIdReception(
                            idReception
                    );

                    ligneReceptionDAO.ajouter(
                            ligne,
                            connection
                    );

                    // -------------------------------------------------
                    // 3. Augmenter le stock
                    // -------------------------------------------------
                    augmenterStock(
                            ligne.getIdProduit(),
                            ligne.getQuantiteRecue(),
                            connection
                    );
                }

                // -------------------------------------------------
                // 4. Valider la transaction
                // -------------------------------------------------
                connection.commit();

            } catch (SQLException | RuntimeException e) {

                // Annuler toute la transaction
                connection.rollback();

                throw e;
            }
        }
    }

    // =============================================================
    // AUGMENTER LE STOCK
    // =============================================================
    /**
     * Augmente le stock d'un produit.
     */
    private void augmenterStock(
            int idProduit,
            double quantite,
            Connection connection)
            throws SQLException {

        String sql = """
            UPDATE PRODUIT
            SET stock_actuel = stock_actuel + ?
            WHERE id_produit = ?
            """;

        try (var statement
                = connection.prepareStatement(sql)) {

            statement.setDouble(
                    1,
                    quantite
            );

            statement.setInt(
                    2,
                    idProduit
            );

            int lignesModifiees
                    = statement.executeUpdate();

            if (lignesModifiees == 0) {

                throw new SQLException(
                        "Produit introuvable : "
                        + idProduit
                );
            }
        }
    }

    // =============================================================
    // FIND ALL
    // =============================================================
    /**
     * Récupérer toutes les réceptions.
     */
    public List<Reception> findAll()
            throws SQLException {

        return receptionDAO.findAll();
    }

    // =============================================================
    // FIND BY ID
    // =============================================================
    /**
     * Récupérer une réception par son identifiant.
     *
     * Cette méthode est utilisée notamment par ReceptionPanel pour la
     * modification.
     */
    public Reception findById(
            int idReception)
            throws SQLException {

        if (idReception <= 0) {

            throw new IllegalArgumentException(
                    "L'identifiant de la réception est invalide."
            );
        }

        List<Reception> receptions
                = receptionDAO.findAll();

        for (Reception reception : receptions) {

            if (reception.getIdReception()
                    == idReception) {

                return reception;
            }
        }

        return null;
    }

    // =============================================================
    // RECHERCHER
    // =============================================================
    /**
     * Rechercher une réception.
     */
    public List<Reception> rechercher(
            String motCle)
            throws SQLException {

        if (motCle == null) {

            motCle = "";
        }

        return receptionDAO.rechercher(
                motCle.trim()
        );
    }

    // =============================================================
    // FIND LIGNES
    // =============================================================
    /**
     * Récupérer les lignes d'une réception.
     */
    public List<LigneReception> findLignes(
            int idReception)
            throws SQLException {

        if (idReception <= 0) {

            throw new IllegalArgumentException(
                    "L'identifiant de la réception est invalide."
            );
        }

        return ligneReceptionDAO.findByReception(
                idReception
        );
    }

    // =============================================================
    // CALCULER QUANTITE RECUE
    // =============================================================
    /**
     * Calculer la quantité déjà reçue d'un produit pour une commande.
     *
     * Retourne un int car les quantités utilisées dans ReceptionForm sont des
     * int.
     */
    public int calculerQuantiteRecue(
            int idCommande,
            int idProduit) throws SQLException {

        if (idCommande <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant de la commande est invalide."
            );
        }

        if (idProduit <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du produit est invalide."
            );
        }

        return ligneReceptionDAO.calculerQuantiteRecue(
                idCommande,
                idProduit
        );
    }

    // =============================================================
    // CALCULER QUANTITE TOTALE
    // =============================================================
    /**
     * Calculer la quantité totale reçue pour une réception.
     */
    public double calculerQuantiteTotale(
            int idReception)
            throws SQLException {

        if (idReception <= 0) {

            throw new IllegalArgumentException(
                    "L'identifiant de la réception est invalide."
            );
        }

        List<LigneReception> lignes
                = ligneReceptionDAO.findByReception(
                        idReception
                );

        double total = 0;

        for (LigneReception ligne : lignes) {

            total += ligne.getQuantiteRecue();
        }

        return total;
    }

    // =============================================================
    // FIND BY COMMANDE
    // =============================================================
    /**
     * Récupérer les réceptions d'une commande.
     */
    public List<Reception> findByCommande(
            int idCommande)
            throws SQLException {

        if (idCommande <= 0) {

            throw new IllegalArgumentException(
                    "L'identifiant de la commande est invalide."
            );
        }

        return receptionDAO.findByCommande(
                idCommande
        );
    }

    // =============================================================
    // MODIFIER
    // =============================================================
    /**
     * Modifier les informations générales d'une réception.
     *
     * Les lignes sont modifiées séparément avec remplacerLignes().
     */
    public void modifier(
            Reception reception)
            throws SQLException {

        if (reception == null) {

            throw new IllegalArgumentException(
                    "La réception ne peut pas être null."
            );
        }

        if (reception.getIdReception() <= 0) {

            throw new IllegalArgumentException(
                    "L'identifiant de la réception est invalide."
            );
        }

        if (reception.getIdCommande() <= 0) {

            throw new IllegalArgumentException(
                    "La commande est obligatoire."
            );
        }

        if (reception.getDateReception() == null) {

            throw new IllegalArgumentException(
                    "La date de réception est obligatoire."
            );
        }

        receptionDAO.modifier(
                reception
        );
    }

    // =============================================================
    // REMPLACER LES LIGNES
    // =============================================================
    /**
     * Remplacer toutes les lignes d'une réception.
     *
     * Lors de la modification :
     *
     * 1. Récupérer les anciennes lignes 2. Retirer leurs quantités du stock 3.
     * Supprimer les anciennes lignes 4. Ajouter les nouvelles lignes 5. Ajouter
     * les nouvelles quantités au stock
     *
     * Toutes les opérations sont effectuées dans une seule transaction.
     */
    public void remplacerLignes(
            int idReception,
            List<LigneReception> nouvellesLignes)
            throws SQLException {

        if (idReception <= 0) {

            throw new IllegalArgumentException(
                    "L'identifiant de la réception est invalide."
            );
        }

        if (nouvellesLignes == null
                || nouvellesLignes.isEmpty()) {

            throw new IllegalArgumentException(
                    "La réception doit contenir au moins un produit."
            );
        }

        // Vérifier les nouvelles lignes
        for (LigneReception ligne
                : nouvellesLignes) {

            if (ligne == null) {

                throw new IllegalArgumentException(
                        "Une ligne de réception est invalide."
                );
            }

            if (ligne.getIdProduit() <= 0) {

                throw new IllegalArgumentException(
                        "Le produit est obligatoire."
                );
            }

            if (ligne.getQuantiteRecue() <= 0) {

                throw new IllegalArgumentException(
                        "La quantité reçue doit être supérieure à zéro."
                );
            }
        }

        // =========================================================
        // TRANSACTION
        // =========================================================
        try (Connection connection
                = DatabaseConnection.getConnection()) {

            try {

                connection.setAutoCommit(false);

                // -------------------------------------------------
                // 1. Récupérer les anciennes lignes
                // -------------------------------------------------
                List<LigneReception> anciennesLignes
                        = ligneReceptionDAO.findByReception(
                                idReception
                        );

                // -------------------------------------------------
                // 2. Retirer les anciennes quantités
                // -------------------------------------------------
                for (LigneReception ligne
                        : anciennesLignes) {

                    diminuerStockSansBloquer(
                            ligne.getIdProduit(),
                            ligne.getQuantiteRecue(),
                            connection
                    );
                }

                // -------------------------------------------------
                // 3. Supprimer les anciennes lignes
                // -------------------------------------------------
                ligneReceptionDAO.deleteByReception(
                        idReception,
                        connection
                );

                // -------------------------------------------------
                // 4. Ajouter les nouvelles lignes
                // -------------------------------------------------
                for (LigneReception ligne
                        : nouvellesLignes) {

                    ligne.setIdReception(
                            idReception
                    );

                    ligneReceptionDAO.ajouter(
                            ligne,
                            connection
                    );

                    // -------------------------------------------------
                    // 5. Ajouter les nouvelles quantités au stock
                    // -------------------------------------------------
                    augmenterStock(
                            ligne.getIdProduit(),
                            ligne.getQuantiteRecue(),
                            connection
                    );
                }

                // -------------------------------------------------
                // 6. Valider
                // -------------------------------------------------
                connection.commit();

            } catch (SQLException | RuntimeException e) {

                connection.rollback();

                throw e;
            }
        }
    }

    // =============================================================
    // SUPPRIMER
    // =============================================================
    /**
     * Supprimer une réception.
     *
     * Avant la suppression : les quantités reçues sont retirées du stock.
     */
    public void supprimer(
            int idReception)
            throws SQLException {

        if (idReception <= 0) {

            throw new IllegalArgumentException(
                    "L'identifiant de la réception est invalide."
            );
        }

        /*
         * Récupérer les lignes avant suppression
         * pour connaître les quantités à retirer
         * du stock.
         */
        List<LigneReception> lignes
                = ligneReceptionDAO.findByReception(
                        idReception
                );

        // =========================================================
        // TRANSACTION
        // =========================================================
        try (Connection connection
                = DatabaseConnection.getConnection()) {

            try {

                connection.setAutoCommit(false);

                // -------------------------------------------------
                // 1. Diminuer le stock
                // -------------------------------------------------
                for (LigneReception ligne
                        : lignes) {

                    diminuerStock(
                            ligne.getIdProduit(),
                            ligne.getQuantiteRecue(),
                            connection
                    );
                }

                // -------------------------------------------------
                // 2. Supprimer la réception
                // -------------------------------------------------
                receptionDAO.supprimer(
                        idReception,
                        connection
                );

                // -------------------------------------------------
                // 3. Valider
                // -------------------------------------------------
                connection.commit();

            } catch (SQLException | RuntimeException e) {

                connection.rollback();

                throw e;
            }
        }
    }

    // =============================================================
    // DIMINUER LE STOCK
    // =============================================================
    /**
     * Diminue le stock d'un produit.
     *
     * Utilisé lors de la suppression d'une réception.
     */
    private void diminuerStock(
            int idProduit,
            double quantite,
            Connection connection)
            throws SQLException {

        String sql = """
            UPDATE PRODUIT
            SET stock_actuel = stock_actuel - ?
            WHERE id_produit = ?
              AND stock_actuel >= ?
            """;

        try (var statement
                = connection.prepareStatement(sql)) {

            statement.setDouble(
                    1,
                    quantite
            );

            statement.setInt(
                    2,
                    idProduit
            );

            statement.setDouble(
                    3,
                    quantite
            );

            int lignesModifiees
                    = statement.executeUpdate();

            if (lignesModifiees == 0) {

                throw new SQLException(
                        "Stock insuffisant pour le produit "
                        + idProduit
                );
            }
        }
    }

    // =============================================================
    // DIMINUER LE STOCK SANS BLOQUER
    // =============================================================
    /**
     * Diminue le stock sans vérifier que le stock est supérieur à la quantité.
     *
     * Cette méthode est utilisée lors de la modification d'une réception.
     *
     * On retire d'abord les anciennes quantités, puis on ajoute les nouvelles.
     */
    private void diminuerStockSansBloquer(
            int idProduit,
            double quantite,
            Connection connection)
            throws SQLException {

        String sql = """
            UPDATE PRODUIT
            SET stock_actuel = stock_actuel - ?
            WHERE id_produit = ?
            """;

        try (var statement
                = connection.prepareStatement(sql)) {

            statement.setDouble(
                    1,
                    quantite
            );

            statement.setInt(
                    2,
                    idProduit
            );

            int lignesModifiees
                    = statement.executeUpdate();

            if (lignesModifiees == 0) {

                throw new SQLException(
                        "Produit introuvable : "
                        + idProduit
                );
            }
        }
    }
}
