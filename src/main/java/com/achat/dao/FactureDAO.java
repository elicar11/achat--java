package com.achat.dao;

import com.achat.database.DatabaseConnection;
import com.achat.model.Facture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FactureDAO {

    // =========================
    // AJOUTER UNE FACTURE
    // =========================
    public void ajouter(Facture facture) throws SQLException {

        String sql = """
            INSERT INTO FACTURE
            (num_facture, date_facture, montant_total_ht,
             montant_tva, etat_paiement, id_commande)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    facture.getNumFacture()
            );

            statement.setString(
                    2,
                    facture.getDateFacture().toString()
            );

            statement.setDouble(
                    3,
                    facture.getMontantTotalHt()
            );

            statement.setDouble(
                    4,
                    facture.getMontantTva()
            );

            statement.setString(
                    5,
                    facture.getEtatPaiement()
            );

            statement.setInt(
                    6,
                    facture.getIdCommande()
            );

            statement.executeUpdate();
        }
    }

    // =========================
    // LISTER TOUTES LES FACTURES
    // =========================
    public List<Facture> findAll() throws SQLException {

        List<Facture> factures = new ArrayList<>();

        String sql = """
            SELECT *
            FROM FACTURE
            ORDER BY id_facture
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                Facture facture = new Facture();

                facture.setIdFacture(
                        result.getInt("id_facture")
                );

                facture.setNumFacture(
                        result.getString("num_facture")
                );

                facture.setDateFacture(
                        LocalDate.parse(
                                result.getString("date_facture")
                        )
                );

                facture.setMontantTotalHt(
                        result.getDouble("montant_total_ht")
                );

                facture.setMontantTva(
                        result.getDouble("montant_tva")
                );

                facture.setEtatPaiement(
                        result.getString("etat_paiement")
                );

                facture.setIdCommande(
                        result.getInt("id_commande")
                );

                factures.add(facture);
            }
        }

        return factures;
    }

    // =========================
    // RECHERCHER UNE FACTURE
    // =========================
    public List<Facture> rechercher(String motCle)
            throws SQLException {

        List<Facture> factures = new ArrayList<>();

        String sql = """
            SELECT *
            FROM FACTURE
            WHERE num_facture LIKE ?
               OR date_facture LIKE ?
               OR etat_paiement LIKE ?
               OR CAST(id_facture AS TEXT) LIKE ?
            ORDER BY id_facture
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            String recherche = "%" + motCle + "%";

            statement.setString(1, recherche);
            statement.setString(2, recherche);
            statement.setString(3, recherche);
            statement.setString(4, recherche);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {

                    Facture facture = new Facture();

                    facture.setIdFacture(
                            result.getInt("id_facture")
                    );

                    facture.setNumFacture(
                            result.getString("num_facture")
                    );

                    facture.setDateFacture(
                            LocalDate.parse(
                                    result.getString("date_facture")
                            )
                    );

                    facture.setMontantTotalHt(
                            result.getDouble("montant_total_ht")
                    );

                    facture.setMontantTva(
                            result.getDouble("montant_tva")
                    );

                    facture.setEtatPaiement(
                            result.getString("etat_paiement")
                    );

                    facture.setIdCommande(
                            result.getInt("id_commande")
                    );

                    factures.add(facture);
                }
            }
        }

        return factures;
    }

    // =========================
    // MODIFIER UNE FACTURE
    // =========================
    public void modifier(Facture facture)
            throws SQLException {

        String sql = """
            UPDATE FACTURE
            SET num_facture = ?,
                date_facture = ?,
                montant_total_ht = ?,
                montant_tva = ?,
                etat_paiement = ?,
                id_commande = ?
            WHERE id_facture = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    facture.getNumFacture()
            );

            statement.setString(
                    2,
                    facture.getDateFacture().toString()
            );

            statement.setDouble(
                    3,
                    facture.getMontantTotalHt()
            );

            statement.setDouble(
                    4,
                    facture.getMontantTva()
            );

            statement.setString(
                    5,
                    facture.getEtatPaiement()
            );

            statement.setInt(
                    6,
                    facture.getIdCommande()
            );

            statement.setInt(
                    7,
                    facture.getIdFacture()
            );

            statement.executeUpdate();
        }
    }

    // =========================
    // SUPPRIMER UNE FACTURE
    // =========================
    public void supprimer(int idFacture)
            throws SQLException {

        String sql = """
            DELETE FROM FACTURE
            WHERE id_facture = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idFacture);

            statement.executeUpdate();
        }
    }

    // =========================
// RECHERCHER PAR ID
// =========================
    public Facture findById(int idFacture)
            throws SQLException {

        String sql = """
        SELECT *
        FROM FACTURE
        WHERE id_facture = ?
        """;

        try (Connection connection
                = DatabaseConnection.getConnection(); PreparedStatement statement
                = connection.prepareStatement(sql)) {

            statement.setInt(1, idFacture);

            try (ResultSet result
                    = statement.executeQuery()) {

                if (result.next()) {

                    Facture facture
                            = new Facture();

                    facture.setIdFacture(
                            result.getInt("id_facture")
                    );

                    facture.setNumFacture(
                            result.getString("num_facture")
                    );

                    facture.setDateFacture(
                            LocalDate.parse(
                                    result.getString(
                                            "date_facture"
                                    )
                            )
                    );

                    facture.setMontantTotalHt(
                            result.getDouble(
                                    "montant_total_ht"
                            )
                    );

                    facture.setMontantTva(
                            result.getDouble(
                                    "montant_tva"
                            )
                    );

                    facture.setEtatPaiement(
                            result.getString(
                                    "etat_paiement"
                            )
                    );

                    facture.setIdCommande(
                            result.getInt(
                                    "id_commande"
                            )
                    );

                    return facture;
                }
            }
        }

        return null;
    }

    // =========================
    // RECHERCHER PAR NUMERO
    // DE FACTURE
    // =========================
    public Facture findByNumero(String numFacture)
            throws SQLException {

        String sql = """
            SELECT *
            FROM FACTURE
            WHERE num_facture = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, numFacture);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    Facture facture = new Facture();

                    facture.setIdFacture(
                            result.getInt("id_facture")
                    );

                    facture.setNumFacture(
                            result.getString("num_facture")
                    );

                    facture.setDateFacture(
                            LocalDate.parse(
                                    result.getString("date_facture")
                            )
                    );

                    facture.setMontantTotalHt(
                            result.getDouble("montant_total_ht")
                    );

                    facture.setMontantTva(
                            result.getDouble("montant_tva")
                    );

                    facture.setEtatPaiement(
                            result.getString("etat_paiement")
                    );

                    facture.setIdCommande(
                            result.getInt("id_commande")
                    );

                    return facture;
                }
            }
        }

        return null;
    }
}