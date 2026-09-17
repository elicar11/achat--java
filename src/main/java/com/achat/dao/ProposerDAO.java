package com.achat.dao;

import com.achat.database.DatabaseConnection;
import com.achat.model.Proposer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProposerDAO {

    /**
     * Ajouter une relation fournisseur-produit.
     */
    public void ajouter(
            int idFournisseur,
            int idProduit,
            double prixAchatSpecifique)
            throws SQLException {

        String sql = """
            INSERT INTO PROPOSER
            (id_fournisseur, id_produit, prix_achat_specifique)
            VALUES (?, ?, ?)
            """;

        try (Connection connection
                = DatabaseConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, idFournisseur);
            statement.setInt(2, idProduit);
            statement.setDouble(3, prixAchatSpecifique);

            statement.executeUpdate();
        }
    }

    /**
     * Récupérer toutes les relations fournisseur-produit.
     */
    public List<Proposer> findAll() throws SQLException {

        List<Proposer> propositions = new ArrayList<>();

        String sql = """
            SELECT
                id_fournisseur,
                id_produit,
                prix_achat_specifique
            FROM PROPOSER
            ORDER BY id_fournisseur, id_produit
            """;

        try (Connection connection
                = DatabaseConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(sql); ResultSet result
                = statement.executeQuery()) {

            while (result.next()) {

                Proposer proposition = new Proposer();

                proposition.setIdFournisseur(
                        result.getInt("id_fournisseur")
                );

                proposition.setIdProduit(
                        result.getInt("id_produit")
                );

                proposition.setPrixAchatSpecifique(
                        result.getDouble(
                                "prix_achat_specifique"
                        )
                );

                propositions.add(proposition);
            }
        }

        return propositions;
    }

    /**
     * Rechercher les produits proposés par un fournisseur.
     */
    public List<Proposer> findByFournisseur(
            int idFournisseur)
            throws SQLException {

        List<Proposer> propositions = new ArrayList<>();

        String sql = """
            SELECT
                id_fournisseur,
                id_produit,
                prix_achat_specifique
            FROM PROPOSER
            WHERE id_fournisseur = ?
            ORDER BY id_produit
            """;

        try (Connection connection
                = DatabaseConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, idFournisseur);

            try (ResultSet result
                    = statement.executeQuery()) {

                while (result.next()) {

                    Proposer proposition = new Proposer();

                    proposition.setIdFournisseur(
                            result.getInt("id_fournisseur")
                    );

                    proposition.setIdProduit(
                            result.getInt("id_produit")
                    );

                    proposition.setPrixAchatSpecifique(
                            result.getDouble(
                                    "prix_achat_specifique"
                            )
                    );

                    propositions.add(proposition);
                }
            }
        }

        return propositions;
    }

    /**
     * Rechercher les fournisseurs proposant un produit.
     */
    public List<Proposer> findByProduit(
            int idProduit)
            throws SQLException {

        List<Proposer> propositions = new ArrayList<>();

        String sql = """
            SELECT
                id_fournisseur,
                id_produit,
                prix_achat_specifique
            FROM PROPOSER
            WHERE id_produit = ?
            ORDER BY id_fournisseur
            """;

        try (Connection connection
                = DatabaseConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, idProduit);

            try (ResultSet result
                    = statement.executeQuery()) {

                while (result.next()) {

                    Proposer proposition = new Proposer();

                    proposition.setIdFournisseur(
                            result.getInt("id_fournisseur")
                    );

                    proposition.setIdProduit(
                            result.getInt("id_produit")
                    );

                    proposition.setPrixAchatSpecifique(
                            result.getDouble(
                                    "prix_achat_specifique"
                            )
                    );

                    propositions.add(proposition);
                }
            }
        }

        return propositions;
    }

    /**
     * Vérifier si un fournisseur propose un produit.
     */
    public boolean existe(
            int idFournisseur,
            int idProduit)
            throws SQLException {

        String sql = """
            SELECT 1
            FROM PROPOSER
            WHERE id_fournisseur = ?
              AND id_produit = ?
            LIMIT 1
            """;

        try (Connection connection
                = DatabaseConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, idFournisseur);
            statement.setInt(2, idProduit);

            try (ResultSet result
                    = statement.executeQuery()) {

                return result.next();
            }
        }
    }

    /**
     * Récupérer le prix proposé par un fournisseur pour un produit.
     */
    public double getPrix(
            int idFournisseur,
            int idProduit,
            Connection connection)
            throws SQLException {

        String sql = """
        SELECT prix_achat_specifique
        FROM PROPOSER
        WHERE id_fournisseur = ?
          AND id_produit = ?
        """;

        try (PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, idFournisseur);
            statement.setInt(2, idProduit);

            try (ResultSet result
                    = statement.executeQuery()) {

                if (result.next()) {
                    return result.getDouble(
                            "prix_achat_specifique"
                    );
                }
            }
        }

        return -1;
    }

    public double getPrix(
            int idFournisseur,
            int idProduit)
            throws SQLException {

        String sql = """
            SELECT prix_achat_specifique
            FROM PROPOSER
            WHERE id_fournisseur = ?
              AND id_produit = ?
            """;

        try (Connection connection
                = DatabaseConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, idFournisseur);
            statement.setInt(2, idProduit);

            try (ResultSet result
                    = statement.executeQuery()) {

                if (result.next()) {
                    return result.getDouble(
                            "prix_achat_specifique"
                    );
                }
            }
        }

        return -1;
    }

    /**
     * Modifier le prix proposé.
     */
    public void modifier(
            int idFournisseur,
            int idProduit,
            double nouveauPrix)
            throws SQLException {

        String sql = """
            UPDATE PROPOSER
            SET prix_achat_specifique = ?
            WHERE id_fournisseur = ?
              AND id_produit = ?
            """;

        try (Connection connection
                = DatabaseConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setDouble(1, nouveauPrix);
            statement.setInt(2, idFournisseur);
            statement.setInt(3, idProduit);

            statement.executeUpdate();
        }
    }

    /**
     * Supprimer une relation fournisseur-produit.
     */
    public void supprimer(
            int idFournisseur,
            int idProduit)
            throws SQLException {

        String sql = """
            DELETE FROM PROPOSER
            WHERE id_fournisseur = ?
              AND id_produit = ?
            """;

        try (Connection connection
                = DatabaseConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, idFournisseur);
            statement.setInt(2, idProduit);

            statement.executeUpdate();
        }
    }
}
