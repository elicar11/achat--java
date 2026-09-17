package com.achat.dao;

import com.achat.database.DatabaseConnection;
import com.achat.model.Personne;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonneDAO {

    /**
     * Ajouter une personne liée à un fournisseur.
     */
    public void ajouter(Personne personne)
            throws SQLException {

        String sql = """
            INSERT INTO PERSONNE
            (id_fournisseur, nom, prenom)
            VALUES (?, ?, ?)
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    personne.getIdFournisseur()
            );

            statement.setString(
                    2,
                    personne.getNom()
            );

            statement.setString(
                    3,
                    personne.getPrenom()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Version utilisée lorsqu'une transaction
     * est déjà ouverte par le Service.
     */
    public void ajouter(
            Personne personne,
            Connection connection)
            throws SQLException {

        String sql = """
            INSERT INTO PERSONNE
            (id_fournisseur, nom, prenom)
            VALUES (?, ?, ?)
            """;

        try (
            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    personne.getIdFournisseur()
            );

            statement.setString(
                    2,
                    personne.getNom()
            );

            statement.setString(
                    3,
                    personne.getPrenom()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Rechercher une personne par son fournisseur.
     */
    public Personne findByFournisseur(
            int idFournisseur)
            throws SQLException {

        String sql = """
            SELECT *
            FROM PERSONNE
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

                    Personne personne =
                            new Personne();

                    personne.setIdFournisseur(
                            result.getInt(
                                    "id_fournisseur"
                            )
                    );

                    personne.setNom(
                            result.getString("nom")
                    );

                    personne.setPrenom(
                            result.getString("prenom")
                    );

                    return personne;
                }
            }
        }

        return null;
    }

    /**
     * Modifier une personne.
     */
    public void modifier(
            Personne personne)
            throws SQLException {

        String sql = """
            UPDATE PERSONNE
            SET nom = ?,
                prenom = ?
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
                    personne.getNom()
            );

            statement.setString(
                    2,
                    personne.getPrenom()
            );

            statement.setInt(
                    3,
                    personne.getIdFournisseur()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Supprimer une personne.
     */
    public void supprimer(
            int idFournisseur)
            throws SQLException {

        String sql = """
            DELETE FROM PERSONNE
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