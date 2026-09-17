package com.achat.dao;

import com.achat.database.DatabaseConnection;
import com.achat.model.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProduitDAO {

    /**
     * Ajouter un produit.
     */
    public void ajouter(Produit produit)
            throws SQLException {

        String sql = """
            INSERT INTO PRODUIT
            (designation, description, stock_actuel, stock_alerte)
            VALUES (?, ?, ?, ?)
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    produit.getDesignation()
            );

            statement.setString(
                    2,
                    produit.getDescription()
            );

            statement.setDouble(
                    3,
                    produit.getStockActuel()
            );

            statement.setDouble(
                    4,
                    produit.getStockAlerte()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Rechercher un produit par son identifiant.
     */
    public Produit findById(int idProduit)
            throws SQLException {

        String sql = """
            SELECT
                id_produit,
                designation,
                description,
                stock_actuel,
                stock_alerte
            FROM PRODUIT
            WHERE id_produit = ?
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    idProduit
            );

            try (
                ResultSet result =
                        statement.executeQuery()
            ) {

                if (result.next()) {

                    Produit produit =
                            new Produit();

                    produit.setIdProduit(
                            result.getInt(
                                    "id_produit"
                            )
                    );

                    produit.setDesignation(
                            result.getString(
                                    "designation"
                            )
                    );

                    produit.setDescription(
                            result.getString(
                                    "description"
                            )
                    );

                    produit.setStockActuel(
                            result.getDouble(
                                    "stock_actuel"
                            )
                    );

                    produit.setStockAlerte(
                            result.getDouble(
                                    "stock_alerte"
                            )
                    );

                    return produit;
                }
            }
        }

        return null;
    }

    /**
     * Récupérer tous les produits.
     */
    public List<Produit> findAll()
            throws SQLException {

        List<Produit> produits =
                new ArrayList<>();

        String sql = """
            SELECT
                id_produit,
                designation,
                description,
                stock_actuel,
                stock_alerte
            FROM PRODUIT
            ORDER BY id_produit
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

                Produit produit =
                        new Produit();

                produit.setIdProduit(
                        result.getInt(
                                "id_produit"
                        )
                );

                produit.setDesignation(
                        result.getString(
                                "designation"
                        )
                );

                produit.setDescription(
                        result.getString(
                                "description"
                        )
                );

                produit.setStockActuel(
                        result.getDouble(
                                "stock_actuel"
                        )
                );

                produit.setStockAlerte(
                        result.getDouble(
                                "stock_alerte"
                        )
                );

                produits.add(produit);
            }
        }

        return produits;
    }

    /**
     * Rechercher un produit par désignation
     * ou description.
     */
    public List<Produit> rechercher(
            String motCle)
            throws SQLException {

        List<Produit> produits =
                new ArrayList<>();

        String sql = """
            SELECT
                id_produit,
                designation,
                description,
                stock_actuel,
                stock_alerte
            FROM PRODUIT
            WHERE designation LIKE ?
               OR description LIKE ?
            ORDER BY id_produit
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

            try (
                ResultSet result =
                        statement.executeQuery()
            ) {

                while (result.next()) {

                    Produit produit =
                            new Produit();

                    produit.setIdProduit(
                            result.getInt(
                                    "id_produit"
                            )
                    );

                    produit.setDesignation(
                            result.getString(
                                    "designation"
                            )
                    );

                    produit.setDescription(
                            result.getString(
                                    "description"
                            )
                    );

                    produit.setStockActuel(
                            result.getDouble(
                                    "stock_actuel"
                            )
                    );

                    produit.setStockAlerte(
                            result.getDouble(
                                    "stock_alerte"
                            )
                    );

                    produits.add(produit);
                }
            }
        }

        return produits;
    }

    /**
     * Modifier un produit.
     */
    public void modifier(
            Produit produit)
            throws SQLException {

        String sql = """
            UPDATE PRODUIT
            SET
                designation = ?,
                description = ?,
                stock_actuel = ?,
                stock_alerte = ?
            WHERE id_produit = ?
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    produit.getDesignation()
            );

            statement.setString(
                    2,
                    produit.getDescription()
            );

            statement.setDouble(
                    3,
                    produit.getStockActuel()
            );

            statement.setDouble(
                    4,
                    produit.getStockAlerte()
            );

            statement.setInt(
                    5,
                    produit.getIdProduit()
            );

            statement.executeUpdate();
        }
    }

    /**
     * Supprimer un produit.
     */
    public void supprimer(
            int idProduit)
            throws SQLException {

        String sql = """
            DELETE FROM PRODUIT
            WHERE id_produit = ?
            """;

        try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    idProduit
            );

            statement.executeUpdate();
        }
    }
}