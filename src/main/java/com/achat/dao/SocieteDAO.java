package com.achat.dao;

import com.achat.database.DatabaseConnection;
import com.achat.model.Societe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SocieteDAO {

    /**
     * Ajouter une société liée à un fournisseur.
     */
    public void ajouter(Societe societe)
            throws SQLException {

        String sql = """
            INSERT INTO SOCIETE
            (id_fournisseur, raison_sociale, nif, stat)
            VALUES (?, ?, ?, ?)
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    societe.getIdFournisseur()
            );

            statement.setString(
                    2,
                    societe.getRaisonSociale()
            );

            statement.setString(
                    3,
                    societe.getNif()
            );

            statement.setString(
                    4,
                    societe.getStat()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Version utilisée lorsqu'une transaction
     * est déjà ouverte par le Service.
     */
    public void ajouter(
            Societe societe,
            Connection connection)
            throws SQLException {

        String sql = """
            INSERT INTO SOCIETE
            (id_fournisseur, raison_sociale, nif, stat)
            VALUES (?, ?, ?, ?)
            """;

        try (
            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    societe.getIdFournisseur()
            );

            statement.setString(
                    2,
                    societe.getRaisonSociale()
            );

            statement.setString(
                    3,
                    societe.getNif()
            );

            statement.setString(
                    4,
                    societe.getStat()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Rechercher une société par son fournisseur.
     */
    public Societe findByFournisseur(
            int idFournisseur)
            throws SQLException {

        String sql = """
            SELECT *
            FROM SOCIETE
            WHERE id_fournisseur = ?
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    idFournisseur
            );

            try (
                ResultSet result =
                        statement.executeQuery()
            ) {

                if (result.next()) {

                    Societe societe =
                            new Societe();

                    societe.setIdFournisseur(
                            result.getInt(
                                    "id_fournisseur"
                            )
                    );

                    societe.setRaisonSociale(
                            result.getString(
                                    "raison_sociale"
                            )
                    );

                    societe.setNif(
                            result.getString("nif")
                    );

                    societe.setStat(
                            result.getString("stat")
                    );

                    return societe;
                }
            }
        }

        return null;
    }

    /**
     * Modifier une société.
     */
    public void modifier(
            Societe societe)
            throws SQLException {

        String sql = """
            UPDATE SOCIETE
            SET raison_sociale = ?,
                nif = ?,
                stat = ?
            WHERE id_fournisseur = ?
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    societe.getRaisonSociale()
            );

            statement.setString(
                    2,
                    societe.getNif()
            );

            statement.setString(
                    3,
                    societe.getStat()
            );

            statement.setInt(
                    4,
                    societe.getIdFournisseur()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Supprimer une société.
     */
    public void supprimer(
            int idFournisseur)
            throws SQLException {

        String sql = """
            DELETE FROM SOCIETE
            WHERE id_fournisseur = ?
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    idFournisseur
            );

            statement.executeUpdate();
        }
    }
}