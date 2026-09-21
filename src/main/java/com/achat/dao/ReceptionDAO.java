package com.achat.dao;

import com.achat.database.DatabaseConnection;
import com.achat.model.Reception;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReceptionDAO {

    // =========================
    // AJOUTER UNE RECEPTION
    // =========================
    public int ajouter(
            Reception reception,
            Connection connection)
            throws SQLException {

        String sql = """
        INSERT INTO RECEPTION
        (date_reception, num_bon_livraison,
         id_commande, id_facture)
        VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement statement
                = connection.prepareStatement(
                        sql,
                        java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(
                    1,
                    reception.getDateReception().toString()
            );

            statement.setString(
                    2,
                    reception.getNumBonLivraison()
            );

            statement.setInt(
                    3,
                    reception.getIdCommande()
            );

            // CORRECTION : Vérifier d'abord si l'ID n'est pas null avant de le comparer
            if (reception.getIdFacture() != null && reception.getIdFacture() > 0) {
                statement.setInt(
                        4,
                        reception.getIdFacture()
                );
            } else {
                statement.setNull(
                        4,
                        java.sql.Types.INTEGER
                );
            }

            statement.executeUpdate();

            try (ResultSet result
                    = statement.getGeneratedKeys()) {

                if (result.next()) {

                    int idReception
                            = result.getInt(1);

                    reception.setIdReception(
                            idReception
                    );

                    return idReception;
                }
            }
        }

        throw new SQLException(
                "Impossible de récupérer l'identifiant de la réception."
        );
    }

    // =========================
    // LISTER TOUTES LES RECEPTIONS
    // =========================
    public List<Reception> findAll() throws SQLException {

        List<Reception> receptions = new ArrayList<>();

        String sql = """
            SELECT *
            FROM RECEPTION
            ORDER BY id_reception
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Reception reception = new Reception();

                reception.setIdReception(
                        result.getInt("id_reception")
                );

                reception.setDateReception(
                        LocalDate.parse(
                                result.getString("date_reception")
                        )
                );

                reception.setNumBonLivraison(
                        result.getString("num_bon_livraison")
                );

                reception.setIdCommande(
                        result.getInt("id_commande")
                );

                // Gestion du NULL
                int idFacture = result.getInt("id_facture");

                if (result.wasNull()) {
                    reception.setIdFacture(null);
                } else {
                    reception.setIdFacture(idFacture);
                }

                receptions.add(reception);
            }
        }

        return receptions;
    }

    // =========================
    // RECHERCHER PAR
    // NUMERO DE BON DE LIVRAISON
    // =========================
    public List<Reception> rechercher(String motCle)
            throws SQLException {

        List<Reception> receptions = new ArrayList<>();

        String sql = """
            SELECT *
            FROM RECEPTION
            WHERE num_bon_livraison LIKE ?
               OR date_reception LIKE ?
               OR CAST(id_reception AS TEXT) LIKE ?
            ORDER BY id_reception
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            String recherche = "%" + motCle + "%";

            statement.setString(1, recherche);
            statement.setString(2, recherche);
            statement.setString(3, recherche);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Reception reception = new Reception();

                    reception.setIdReception(
                            result.getInt("id_reception")
                    );

                    reception.setDateReception(
                            LocalDate.parse(
                                    result.getString("date_reception")
                            )
                    );

                    reception.setNumBonLivraison(
                            result.getString("num_bon_livraison")
                    );

                    reception.setIdCommande(
                            result.getInt("id_commande")
                    );

                    int idFacture = result.getInt("id_facture");

                    if (result.wasNull()) {
                        reception.setIdFacture(null);
                    } else {
                        reception.setIdFacture(idFacture);
                    }

                    receptions.add(reception);
                }
            }
        }

        return receptions;
    }

    // =========================
    // RECHERCHER LES RECEPTIONS
    // D'UNE COMMANDE
    // =========================
    public List<Reception> findByCommande(int idCommande)
            throws SQLException {

        List<Reception> receptions = new ArrayList<>();

        String sql = """
            SELECT *
            FROM RECEPTION
            WHERE id_commande = ?
            ORDER BY date_reception
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idCommande);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Reception reception = new Reception();

                    reception.setIdReception(
                            result.getInt("id_reception")
                    );

                    reception.setDateReception(
                            LocalDate.parse(
                                    result.getString("date_reception")
                            )
                    );

                    reception.setNumBonLivraison(
                            result.getString("num_bon_livraison")
                    );

                    reception.setIdCommande(
                            result.getInt("id_commande")
                    );

                    int idFacture = result.getInt("id_facture");

                    if (result.wasNull()) {
                        reception.setIdFacture(null);
                    } else {
                        reception.setIdFacture(idFacture);
                    }

                    receptions.add(reception);
                }
            }
        }

        return receptions;
    }

    // =========================
    // MODIFIER UNE RECEPTION
    // =========================
    public void modifier(Reception reception) throws SQLException {

        String sql = """
            UPDATE RECEPTION
            SET date_reception = ?,
                num_bon_livraison = ?,
                id_commande = ?,
                id_facture = ?
            WHERE id_reception = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    reception.getDateReception().toString()
            );

            statement.setString(
                    2,
                    reception.getNumBonLivraison()
            );

            statement.setInt(
                    3,
                    reception.getIdCommande()
            );

            if (reception.getIdFacture() != null) {
                statement.setInt(
                        4,
                        reception.getIdFacture()
                );
            } else {
                statement.setNull(
                        4,
                        java.sql.Types.INTEGER
                );
            }

            statement.setInt(
                    5,
                    reception.getIdReception()
            );

            statement.executeUpdate();
        }
    }

    // =========================
    // SUPPRIMER UNE RECEPTION
    // =========================
    public void supprimer(
            int idReception,
            Connection connection)
            throws SQLException {

        String sql = """
        DELETE FROM RECEPTION
        WHERE id_reception = ?
        """;

        try (PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, idReception);

            statement.executeUpdate();
        }
    }
}