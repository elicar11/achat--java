package com.achat;

import com.achat.database.DatabaseConnection;
import com.achat.database.DatabaseInitializer;
import java.sql.Connection;

public class Achat {

    public static void main(String[] args) {

        try {
            Connection connexion = DatabaseConnection.getConnection();

            System.out.println("Connexion SQLite réussie !");

            connexion.close();

            // Création des tables
            DatabaseInitializer.initialize();

        } catch (Exception e) {
            System.out.println("Erreur de connexion à SQLite !");
            e.printStackTrace();
        }
    }
}