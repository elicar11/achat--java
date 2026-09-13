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

    // =========================
    // AJOUTER UN FOURNISSEUR
    // =========================
    public void ajouter(Fournisseur fournisseur) throws SQLException {

        String sql = """
            INSERT INTO FOURNISSEUR
            (type_fournisseur, adresse, email, telephone)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, fournisseur.getTypeFournisseur());
            statement.setString(2, fournisseur.getAdresse());
            statement.setString(3, fournisseur.getEmail());
            statement.setString(4, fournisseur.getTelephone());

            statement.executeUpdate();
        }
    }


    // =========================
    // LISTER LES FOURNISSEURS
    // =========================
    public List<Fournisseur> findAll() throws SQLException {

        List<Fournisseur> fournisseurs = new ArrayList<>();

        String sql = """
            SELECT *
            FROM FOURNISSEUR
            ORDER BY id_fournisseur
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Fournisseur fournisseur = new Fournisseur();

                fournisseur.setIdFournisseur(
                        result.getInt("id_fournisseur"));

                fournisseur.setTypeFournisseur(
                        result.getString("type_fournisseur"));

                fournisseur.setAdresse(
                        result.getString("adresse"));

                fournisseur.setEmail(
                        result.getString("email"));

                fournisseur.setTelephone(
                        result.getString("telephone"));

                fournisseurs.add(fournisseur);
            }
        }

        return fournisseurs;
    }


    // =========================
    // RECHERCHER UN FOURNISSEUR
    // =========================
    public List<Fournisseur> rechercher(String motCle) throws SQLException {

        List<Fournisseur> fournisseurs = new ArrayList<>();

        String sql = """
            SELECT *
            FROM FOURNISSEUR
            WHERE type_fournisseur LIKE ?
               OR adresse LIKE ?
               OR email LIKE ?
               OR telephone LIKE ?
            ORDER BY id_fournisseur
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String recherche = "%" + motCle + "%";

            statement.setString(1, recherche);
            statement.setString(2, recherche);
            statement.setString(3, recherche);
            statement.setString(4, recherche);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Fournisseur fournisseur = new Fournisseur();

                    fournisseur.setIdFournisseur(
                            result.getInt("id_fournisseur"));

                    fournisseur.setTypeFournisseur(
                            result.getString("type_fournisseur"));

                    fournisseur.setAdresse(
                            result.getString("adresse"));

                    fournisseur.setEmail(
                            result.getString("email"));

                    fournisseur.setTelephone(
                            result.getString("telephone"));

                    fournisseurs.add(fournisseur);
                }
            }
        }

        return fournisseurs;
    }


    // =========================
    // MODIFIER UN FOURNISSEUR
    // =========================
    public void modifier(Fournisseur fournisseur) throws SQLException {

        String sql = """
            UPDATE FOURNISSEUR
            SET type_fournisseur = ?,
                adresse = ?,
                email = ?,
                telephone = ?
            WHERE id_fournisseur = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, fournisseur.getTypeFournisseur());
            statement.setString(2, fournisseur.getAdresse());
            statement.setString(3, fournisseur.getEmail());
            statement.setString(4, fournisseur.getTelephone());
            statement.setInt(5, fournisseur.getIdFournisseur());

            statement.executeUpdate();
        }
    }


    // =========================
    // SUPPRIMER UN FOURNISSEUR
    // =========================
    public void supprimer(int idFournisseur) throws SQLException {

        String sql = """
            DELETE FROM FOURNISSEUR
            WHERE id_fournisseur = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idFournisseur);

            statement.executeUpdate();
        }
    }
}