package com.achat.dao;

import com.achat.database.DatabaseConnection;
import com.achat.model.Commande;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {

    // =========================
    // AJOUTER UNE COMMANDE
    // =========================
    public int ajouter(Commande commande, Connection connection)
            throws SQLException {

        String sql = """
        INSERT INTO COMMANDE
        (date_commande, etat_commande, id_fournisseur)
        VALUES (?, ?, ?)
        """;

        try (PreparedStatement statement
                = connection.prepareStatement(
                        sql,
                        java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(
                    1,
                    commande.getDateCommande().toString()
            );

            statement.setString(
                    2,
                    commande.getEtatCommande()
            );

            statement.setInt(
                    3,
                    commande.getIdFournisseur()
            );

            statement.executeUpdate();

            try (ResultSet result
                    = statement.getGeneratedKeys()) {

                if (result.next()) {

                    int idCommande = result.getInt(1);

                    commande.setIdCommande(idCommande);

                    return idCommande;
                }
            }
        }

        throw new SQLException(
                "Impossible de récupérer l'identifiant de la commande."
        );
    }

    // =========================
    // LISTER TOUTES LES COMMANDES
    // =========================
    public List<Commande> findAll() throws SQLException {

        List<Commande> commandes = new ArrayList<>();

        String sql = """
            SELECT *
            FROM COMMANDE
            ORDER BY id_commande
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Commande commande = new Commande();

                commande.setIdCommande(
                        result.getInt("id_commande")
                );

                String date = result.getString("date_commande");

                commande.setDateCommande(
                        LocalDate.parse(date)
                );

                commande.setEtatCommande(
                        result.getString("etat_commande")
                );

                commande.setIdFournisseur(
                        result.getInt("id_fournisseur")
                );

                commandes.add(commande);
            }
        }

        return commandes;
    }

    // =========================
    // RECHERCHER UNE COMMANDE
    // =========================
    public List<Commande> rechercher(String motCle) throws SQLException {

        List<Commande> commandes = new ArrayList<>();

        String sql = """
            SELECT *
            FROM COMMANDE
            WHERE CAST(id_commande AS TEXT) LIKE ?
               OR date_commande LIKE ?
               OR etat_commande LIKE ?
            ORDER BY id_commande
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            String recherche = "%" + motCle + "%";

            statement.setString(1, recherche);
            statement.setString(2, recherche);
            statement.setString(3, recherche);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Commande commande = new Commande();

                    commande.setIdCommande(
                            result.getInt("id_commande")
                    );

                    commande.setDateCommande(
                            LocalDate.parse(
                                    result.getString("date_commande")
                            )
                    );

                    commande.setEtatCommande(
                            result.getString("etat_commande")
                    );

                    commande.setIdFournisseur(
                            result.getInt("id_fournisseur")
                    );

                    commandes.add(commande);
                }
            }
        }

        return commandes;
    }

    // =========================
    // MODIFIER UNE COMMANDE
    // =========================
    public void modifier(Commande commande) throws SQLException {

        String sql = """
            UPDATE COMMANDE
            SET date_commande = ?,
                etat_commande = ?,
                id_fournisseur = ?
            WHERE id_commande = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    commande.getDateCommande().toString()
            );

            statement.setString(
                    2,
                    commande.getEtatCommande()
            );

            statement.setInt(
                    3,
                    commande.getIdFournisseur()
            );

            statement.setInt(
                    4,
                    commande.getIdCommande()
            );

            statement.executeUpdate();
        }
    }

    // =========================
    // SUPPRIMER UNE COMMANDE
    // =========================
    public void supprimer(int idCommande) throws SQLException {

        String sql = """
            DELETE FROM COMMANDE
            WHERE id_commande = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idCommande);

            statement.executeUpdate();
        }
    }
}
