package com.achat.dao;

import com.achat.database.DatabaseConnection;
import com.achat.model.LigneReception;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LigneReceptionDAO {

    // =========================
    // AJOUTER UNE LIGNE
    // =========================
    public void ajouter(LigneReception ligne) throws SQLException {

        String sql = """
            INSERT INTO LIGNE_RECEPTION
            (id_reception, id_produit, quantite_recue)
            VALUES (?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, ligne.getIdReception());
            statement.setInt(2, ligne.getIdProduit());
            statement.setDouble(3, ligne.getQuantiteRecue());

            statement.executeUpdate();
        }
    }

    public int calculerQuantiteRecue(
            int idCommande,
            int idProduit) throws SQLException {

        String sql = """
        SELECT COALESCE(SUM(lr.quantite_recue), 0)
        FROM LIGNE_RECEPTION lr
        JOIN RECEPTION r
            ON lr.id_reception = r.id_reception
        WHERE r.id_commande = ?
          AND lr.id_produit = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idCommande);
            statement.setInt(2, idProduit);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0;
    }

    public void ajouter(
            LigneReception ligne,
            Connection connection)
            throws SQLException {

        String sql = """
        INSERT INTO LIGNE_RECEPTION
        (id_reception, id_produit, quantite_recue)
        VALUES (?, ?, ?)
        """;

        try (var statement
                = connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    ligne.getIdReception()
            );

            statement.setInt(
                    2,
                    ligne.getIdProduit()
            );

            statement.setDouble(
                    3,
                    ligne.getQuantiteRecue()
            );

            statement.executeUpdate();
        }
    }

    // =========================
    // LISTER TOUTES LES LIGNES
    // =========================
    public List<LigneReception> findAll() throws SQLException {

        List<LigneReception> lignes = new ArrayList<>();

        String sql = """
            SELECT *
            FROM LIGNE_RECEPTION
            ORDER BY id_reception, id_produit
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                LigneReception ligne = new LigneReception();

                ligne.setIdReception(
                        result.getInt("id_reception")
                );

                ligne.setIdProduit(
                        result.getInt("id_produit")
                );

                ligne.setQuantiteRecue(
                        result.getInt("quantite_recue")
                );

                lignes.add(ligne);
            }
        }

        return lignes;
    }

    // =========================
    // LISTER LES LIGNES
    // D'UNE RECEPTION
    // =========================
    public List<LigneReception> findByReception(int idReception)
            throws SQLException {

        List<LigneReception> lignes = new ArrayList<>();

        String sql = """
            SELECT *
            FROM LIGNE_RECEPTION
            WHERE id_reception = ?
            ORDER BY id_produit
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idReception);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    LigneReception ligne = new LigneReception();

                    ligne.setIdReception(
                            result.getInt("id_reception")
                    );

                    ligne.setIdProduit(
                            result.getInt("id_produit")
                    );

                    ligne.setQuantiteRecue(
                            result.getInt("quantite_recue")
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
    public List<LigneReception> findByProduit(int idProduit)
            throws SQLException {

        List<LigneReception> lignes = new ArrayList<>();

        String sql = """
            SELECT *
            FROM LIGNE_RECEPTION
            WHERE id_produit = ?
            ORDER BY id_reception
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idProduit);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    LigneReception ligne = new LigneReception();

                    ligne.setIdReception(
                            result.getInt("id_reception")
                    );

                    ligne.setIdProduit(
                            result.getInt("id_produit")
                    );

                    ligne.setQuantiteRecue(
                            result.getInt("quantite_recue")
                    );

                    lignes.add(ligne);
                }
            }
        }

        return lignes;
    }

    public void deleteByReception(
            int idReception,
            Connection connection)
            throws SQLException {

        String sql = """
        DELETE FROM LIGNE_RECEPTION
        WHERE id_reception = ?
        """;

        try (var statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, idReception);
            statement.executeUpdate();
        }
    }

    // =========================
    // MODIFIER UNE LIGNE
    // =========================
    public void modifier(LigneReception ligne)
            throws SQLException {

        String sql = """
            UPDATE LIGNE_RECEPTION
            SET quantite_recue = ?
            WHERE id_reception = ?
              AND id_produit = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(
                    1,
                    ligne.getQuantiteRecue()
            );

            statement.setInt(
                    2,
                    ligne.getIdReception()
            );

            statement.setInt(
                    3,
                    ligne.getIdProduit()
            );

            statement.executeUpdate();
        }
    }

    // =========================
    // SUPPRIMER UNE LIGNE
    // =========================
    public void supprimer(int idReception, int idProduit)
            throws SQLException {

        String sql = """
            DELETE FROM LIGNE_RECEPTION
            WHERE id_reception = ?
              AND id_produit = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idReception);
            statement.setInt(2, idProduit);

            statement.executeUpdate();
        }
    }

    // =========================
    // SUPPRIMER TOUTES LES
    // LIGNES D'UNE RECEPTION
    // =========================
    public void supprimerParReception(int idReception)
            throws SQLException {

        String sql = """
            DELETE FROM LIGNE_RECEPTION
            WHERE id_reception = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idReception);

            statement.executeUpdate();
        }
    }

    // =========================
    // CALCULER LA QUANTITE
    // TOTALE RECUE D'UN PRODUIT
    // =========================
}
