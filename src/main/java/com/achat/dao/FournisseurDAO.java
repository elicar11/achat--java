package com.achat.dao;

import com.achat.database.DatabaseConnection;
import com.achat.model.Fournisseur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FournisseurDAO {

    /**
     * Ajouter un fournisseur dans une connexion existante.
     *
     * Cette méthode est utilisée par FournisseurService
     * pour permettre une transaction avec PERSONNE ou SOCIETE.
     */
    public int ajouter(
            Fournisseur fournisseur,
            Connection connection)
            throws SQLException {

        String sql = """
            INSERT INTO FOURNISSEUR
            (type_fournisseur, adresse, email, telephone)
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(
                    1,
                    fournisseur.getTypeFournisseur()
            );

            statement.setString(
                    2,
                    fournisseur.getAdresse()
            );

            statement.setString(
                    3,
                    fournisseur.getEmail()
            );

            statement.setString(
                    4,
                    fournisseur.getTelephone()
            );

            statement.executeUpdate();

            // Récupération de l'identifiant généré
            try (ResultSet result =
                         statement.getGeneratedKeys()) {

                if (result.next()) {

                    int id = result.getInt(1);

                    fournisseur.setIdFournisseur(id);

                    return id;
                }
            }
        }

        throw new SQLException(
                "Impossible de récupérer l'identifiant du fournisseur."
        );
    }

    /**
     * Rechercher un fournisseur par son identifiant.
     */
    public Fournisseur findById(int id)
            throws SQLException {

        String sql = """
            SELECT
                id_fournisseur,
                type_fournisseur,
                adresse,
                email,
                telephone
            FROM FOURNISSEUR
            WHERE id_fournisseur = ?
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            try (
                ResultSet result =
                        statement.executeQuery()
            ) {

                if (result.next()) {

                    Fournisseur fournisseur =
                            new Fournisseur();

                    fournisseur.setIdFournisseur(
                            result.getInt(
                                    "id_fournisseur"
                            )
                    );

                    fournisseur.setTypeFournisseur(
                            result.getString(
                                    "type_fournisseur"
                            )
                    );

                    fournisseur.setAdresse(
                            result.getString(
                                    "adresse"
                            )
                    );

                    fournisseur.setEmail(
                            result.getString(
                                    "email"
                            )
                    );

                    fournisseur.setTelephone(
                            result.getString(
                                    "telephone"
                            )
                    );

                    return fournisseur;
                }
            }
        }

        return null;
    }

    /**
     * Récupérer tous les fournisseurs.
     */
    public List<Fournisseur> findAll()
            throws SQLException {

        List<Fournisseur> fournisseurs =
                new ArrayList<>();

        String sql = """
            SELECT
                id_fournisseur,
                type_fournisseur,
                adresse,
                email,
                telephone
            FROM FOURNISSEUR
            ORDER BY id_fournisseur
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet result =
                    statement.executeQuery()
        ) {

            while (result.next()) {

                Fournisseur fournisseur =
                        new Fournisseur();

                fournisseur.setIdFournisseur(
                        result.getInt(
                                "id_fournisseur"
                        )
                );

                fournisseur.setTypeFournisseur(
                        result.getString(
                                "type_fournisseur"
                        )
                );

                fournisseur.setAdresse(
                        result.getString(
                                "adresse"
                        )
                );

                fournisseur.setEmail(
                        result.getString(
                                "email"
                        )
                );

                fournisseur.setTelephone(
                        result.getString(
                                "telephone"
                        )
                );

                fournisseurs.add(fournisseur);
            }
        }

        return fournisseurs;
    }

    /**
     * Rechercher des fournisseurs
     * à partir d'un mot-clé.
     */
    public List<Fournisseur> rechercher(
            String motCle)
            throws SQLException {

        List<Fournisseur> fournisseurs =
                new ArrayList<>();

        String sql = """
            SELECT
                id_fournisseur,
                type_fournisseur,
                adresse,
                email,
                telephone
            FROM FOURNISSEUR
            WHERE type_fournisseur LIKE ?
               OR adresse LIKE ?
               OR email LIKE ?
               OR telephone LIKE ?
            ORDER BY id_fournisseur
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            String recherche =
                    "%" + motCle + "%";

            statement.setString(
                    1,
                    recherche
            );

            statement.setString(
                    2,
                    recherche
            );

            statement.setString(
                    3,
                    recherche
            );

            statement.setString(
                    4,
                    recherche
            );

            try (
                ResultSet result =
                        statement.executeQuery()
            ) {

                while (result.next()) {

                    Fournisseur fournisseur =
                            new Fournisseur();

                    fournisseur.setIdFournisseur(
                            result.getInt(
                                    "id_fournisseur"
                            )
                    );

                    fournisseur.setTypeFournisseur(
                            result.getString(
                                    "type_fournisseur"
                            )
                    );

                    fournisseur.setAdresse(
                            result.getString(
                                    "adresse"
                            )
                    );

                    fournisseur.setEmail(
                            result.getString(
                                    "email"
                            )
                    );

                    fournisseur.setTelephone(
                            result.getString(
                                    "telephone"
                            )
                    );

                    fournisseurs.add(fournisseur);
                }
            }
        }

        return fournisseurs;
    }

    /**
     * Modifier un fournisseur.
     */
    public void modifier(
            Fournisseur fournisseur)
            throws SQLException {

        String sql = """
            UPDATE FOURNISSEUR
            SET
                type_fournisseur = ?,
                adresse = ?,
                email = ?,
                telephone = ?
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
                    fournisseur.getTypeFournisseur()
            );

            statement.setString(
                    2,
                    fournisseur.getAdresse()
            );

            statement.setString(
                    3,
                    fournisseur.getEmail()
            );

            statement.setString(
                    4,
                    fournisseur.getTelephone()
            );

            statement.setInt(
                    5,
                    fournisseur.getIdFournisseur()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Supprimer un fournisseur.
     */
    public void supprimer(
            int idFournisseur)
            throws SQLException {

        String sql = """
            DELETE FROM FOURNISSEUR
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