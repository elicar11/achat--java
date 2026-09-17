package com.achat.dao;

import com.achat.database.DatabaseConnection;
import com.achat.model.LigneCommande;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LigneCommandeDAO {

    // =========================
    // AJOUTER UNE LIGNE
    // =========================
    public void ajouter(
            LigneCommande ligne,
            Connection connection) throws SQLException {

        String sql = """
        INSERT INTO LIGNE_COMMANDE
        (id_commande, id_produit,
         quantite_commandee, prix_unitaire_achat)
        VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    ligne.getIdCommande()
            );

            statement.setInt(
                    2,
                    ligne.getIdProduit()
            );

            statement.setDouble(
                    3,
                    ligne.getQuantiteCommandee()
            );

            statement.setDouble(
                    4,
                    ligne.getPrixUnitaireAchat()
            );

            statement.executeUpdate();
        }
    }

    // =========================
    // LISTER TOUTES LES LIGNES
    // =========================
    public List<LigneCommande> findAll() throws SQLException {

        List<LigneCommande> lignes = new ArrayList<>();

        String sql = """
            SELECT *
            FROM LIGNE_COMMANDE
            ORDER BY id_commande, id_produit
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                LigneCommande ligne = new LigneCommande();

                ligne.setIdCommande(
                        result.getInt("id_commande")
                );

                ligne.setIdProduit(
                        result.getInt("id_produit")
                );

                ligne.setQuantiteCommandee(
                        result.getDouble("quantite_commandee")
                );

                ligne.setPrixUnitaireAchat(
                        result.getDouble("prix_unitaire_achat")
                );

                lignes.add(ligne);
            }
        }

        return lignes;
    }

    // =========================
    // LISTER LES LIGNES
    // D'UNE COMMANDE
    // =========================
    public List<LigneCommande> findByCommande(int idCommande)
            throws SQLException {

        List<LigneCommande> lignes = new ArrayList<>();

        String sql = """
            SELECT *
            FROM LIGNE_COMMANDE
            WHERE id_commande = ?
            ORDER BY id_produit
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idCommande);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    LigneCommande ligne = new LigneCommande();

                    ligne.setIdCommande(
                            result.getInt("id_commande")
                    );

                    ligne.setIdProduit(
                            result.getInt("id_produit")
                    );

                    ligne.setQuantiteCommandee(
                            result.getDouble("quantite_commandee")
                    );

                    ligne.setPrixUnitaireAchat(
                            result.getDouble("prix_unitaire_achat")
                    );

                    lignes.add(ligne);
                }
            }
        }

        return lignes;
    }

    // =========================
    // RECHERCHER PAR PRODUIT
    // =========================
    public List<LigneCommande> findByProduit(int idProduit)
            throws SQLException {

        List<LigneCommande> lignes = new ArrayList<>();

        String sql = """
            SELECT *
            FROM LIGNE_COMMANDE
            WHERE id_produit = ?
            ORDER BY id_commande
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idProduit);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    LigneCommande ligne = new LigneCommande();

                    ligne.setIdCommande(
                            result.getInt("id_commande")
                    );

                    ligne.setIdProduit(
                            result.getInt("id_produit")
                    );

                    ligne.setQuantiteCommandee(
                            result.getDouble("quantite_commandee")
                    );

                    ligne.setPrixUnitaireAchat(
                            result.getDouble("prix_unitaire_achat")
                    );

                    lignes.add(ligne);
                }
            }
        }

        return lignes;
    }

    // =========================
    // MODIFIER UNE LIGNE
    // =========================
    public void modifier(LigneCommande ligne) throws SQLException {

        String sql = """
            UPDATE LIGNE_COMMANDE
            SET quantite_commandee = ?,
                prix_unitaire_achat = ?
            WHERE id_commande = ?
              AND id_produit = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(
                    1,
                    ligne.getQuantiteCommandee()
            );

            statement.setDouble(
                    2,
                    ligne.getPrixUnitaireAchat()
            );

            statement.setInt(
                    3,
                    ligne.getIdCommande()
            );

            statement.setInt(
                    4,
                    ligne.getIdProduit()
            );

            statement.executeUpdate();
        }
    }

    // =========================
    // SUPPRIMER UNE LIGNE
    // =========================
    public void supprimer(int idCommande, int idProduit)
            throws SQLException {

        String sql = """
            DELETE FROM LIGNE_COMMANDE
            WHERE id_commande = ?
              AND id_produit = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idCommande);
            statement.setInt(2, idProduit);

            statement.executeUpdate();
        }
    }

    // =========================
    // SUPPRIMER TOUTES LES
    // LIGNES D'UNE COMMANDE
    // =========================
    public void supprimerParCommande(int idCommande)
            throws SQLException {

        String sql = """
            DELETE FROM LIGNE_COMMANDE
            WHERE id_commande = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idCommande);

            statement.executeUpdate();
        }
    }

    // =========================
    // CALCULER LE TOTAL
    // D'UNE COMMANDE
    // =========================
    public double calculerTotalCommande(int idCommande)
            throws SQLException {

        String sql = """
            SELECT SUM(
                quantite_commandee * prix_unitaire_achat
            ) AS total
            FROM LIGNE_COMMANDE
            WHERE id_commande = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idCommande);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return result.getDouble("total");
                }
            }
        }

        return 0;
    }
}
