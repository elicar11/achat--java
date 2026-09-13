package com.achat.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        String sqlFournisseur = """
            CREATE TABLE IF NOT EXISTS FOURNISSEUR (
                id_fournisseur INTEGER PRIMARY KEY AUTOINCREMENT,
                type_fournisseur TEXT NOT NULL
                    CHECK (type_fournisseur IN ('PERSONNE', 'SOCIETE')),
                adresse TEXT,
                email TEXT,
                telephone TEXT
            );
            """;

        String sqlPersonne = """
            CREATE TABLE IF NOT EXISTS PERSONNE (
                id_fournisseur INTEGER PRIMARY KEY,
                nom TEXT NOT NULL,
                prenom TEXT NOT NULL,
                FOREIGN KEY (id_fournisseur)
                    REFERENCES FOURNISSEUR(id_fournisseur)
                    ON DELETE CASCADE
            );
            """;

        String sqlSociete = """
            CREATE TABLE IF NOT EXISTS SOCIETE (
                id_fournisseur INTEGER PRIMARY KEY,
                raison_sociale TEXT NOT NULL,
                nif TEXT,
                stat TEXT,
                FOREIGN KEY (id_fournisseur)
                    REFERENCES FOURNISSEUR(id_fournisseur)
                    ON DELETE CASCADE
            );
            """;

        String sqlProduit = """
            CREATE TABLE IF NOT EXISTS PRODUIT (
                id_produit INTEGER PRIMARY KEY AUTOINCREMENT,
                designation TEXT NOT NULL,
                description TEXT,
                stock_actuel REAL DEFAULT 0,
                stock_alerte REAL DEFAULT 0
            );
            """;

        String sqlProposer = """
            CREATE TABLE IF NOT EXISTS PROPOSER (
                id_fournisseur INTEGER NOT NULL,
                id_produit INTEGER NOT NULL,
                prix_achat_specifique REAL NOT NULL,
                PRIMARY KEY (id_fournisseur, id_produit),
                FOREIGN KEY (id_fournisseur)
                    REFERENCES FOURNISSEUR(id_fournisseur)
                    ON DELETE CASCADE,
                FOREIGN KEY (id_produit)
                    REFERENCES PRODUIT(id_produit)
                    ON DELETE CASCADE
            );
            """;

        String sqlCommande = """
            CREATE TABLE IF NOT EXISTS COMMANDE (
                id_commande INTEGER PRIMARY KEY AUTOINCREMENT,
                date_commande TEXT NOT NULL,
                etat_commande TEXT NOT NULL DEFAULT 'EN_ATTENTE',
                id_fournisseur INTEGER NOT NULL,
                FOREIGN KEY (id_fournisseur)
                    REFERENCES FOURNISSEUR(id_fournisseur)
            );
            """;

        String sqlLigneCommande = """
            CREATE TABLE IF NOT EXISTS LIGNE_COMMANDE (
                id_commande INTEGER NOT NULL,
                id_produit INTEGER NOT NULL,
                quantite_commandee REAL NOT NULL,
                prix_unitaire_achat REAL NOT NULL,
                PRIMARY KEY (id_commande, id_produit),
                FOREIGN KEY (id_commande)
                    REFERENCES COMMANDE(id_commande)
                    ON DELETE CASCADE,
                FOREIGN KEY (id_produit)
                    REFERENCES PRODUIT(id_produit)
            );
            """;

        String sqlFacture = """
            CREATE TABLE IF NOT EXISTS FACTURE (
                id_facture INTEGER PRIMARY KEY AUTOINCREMENT,
                num_facture TEXT NOT NULL UNIQUE,
                date_facture TEXT NOT NULL,
                montant_total_ht REAL DEFAULT 0,
                montant_tva REAL DEFAULT 0,
                etat_paiement TEXT NOT NULL DEFAULT 'NON_PAYEE'
            );
            """;

        String sqlReception = """
            CREATE TABLE IF NOT EXISTS RECEPTION (
                id_reception INTEGER PRIMARY KEY AUTOINCREMENT,
                date_reception TEXT NOT NULL,
                num_bon_livraison TEXT,
                id_commande INTEGER NOT NULL,
                id_facture INTEGER,
                FOREIGN KEY (id_commande)
                    REFERENCES COMMANDE(id_commande),
                FOREIGN KEY (id_facture)
                    REFERENCES FACTURE(id_facture)
            );
            """;

        String sqlLigneReception = """
            CREATE TABLE IF NOT EXISTS LIGNE_RECEPTION (
                id_reception INTEGER NOT NULL,
                id_produit INTEGER NOT NULL,
                quantite_recue REAL NOT NULL,
                PRIMARY KEY (id_reception, id_produit),
                FOREIGN KEY (id_reception)
                    REFERENCES RECEPTION(id_reception)
                    ON DELETE CASCADE,
                FOREIGN KEY (id_produit)
                    REFERENCES PRODUIT(id_produit)
            );
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {

            // Activation des clés étrangères SQLite
            statement.execute("PRAGMA foreign_keys = ON");

            statement.execute(sqlFournisseur);
            statement.execute(sqlPersonne);
            statement.execute(sqlSociete);
            statement.execute(sqlProduit);
            statement.execute(sqlProposer);
            statement.execute(sqlCommande);
            statement.execute(sqlLigneCommande);
            statement.execute(sqlFacture);
            statement.execute(sqlReception);
            statement.execute(sqlLigneReception);

            System.out.println("Toutes les tables ont été créées avec succès !");

        } catch (SQLException e) {
            System.out.println("Erreur lors de la création des tables.");
            e.printStackTrace();
        }
    }
}