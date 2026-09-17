package com.achat.service;

import com.achat.dao.FournisseurDAO;
import com.achat.dao.PersonneDAO;
import com.achat.dao.SocieteDAO;
import com.achat.database.DatabaseConnection;
import com.achat.model.Fournisseur;
import com.achat.model.Personne;
import com.achat.model.Societe;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FournisseurService {

    private final FournisseurDAO fournisseurDAO;
    private final PersonneDAO personneDAO;
    private final SocieteDAO societeDAO;

    public FournisseurService() {

        fournisseurDAO = new FournisseurDAO();
        personneDAO = new PersonneDAO();
        societeDAO = new SocieteDAO();
    }

    // =========================================================
    // AJOUTER UN FOURNISSEUR
    // =========================================================

    public void ajouter(
            Fournisseur fournisseur,
            Personne personne,
            Societe societe) throws SQLException {

        // =====================================================
        // VALIDATION FOURNISSEUR
        // =====================================================

        if (fournisseur == null) {

            throw new IllegalArgumentException(
                    "Le fournisseur ne peut pas être null."
            );
        }

        if (fournisseur.getTypeFournisseur() == null
                || fournisseur.getTypeFournisseur().isBlank()) {

            throw new IllegalArgumentException(
                    "Le type du fournisseur est obligatoire."
            );
        }

        String type =
                fournisseur.getTypeFournisseur()
                        .trim()
                        .toUpperCase();

        if (!type.equals("PERSONNE")
                && !type.equals("SOCIETE")) {

            throw new IllegalArgumentException(
                    "Le type doit être PERSONNE ou SOCIETE."
            );
        }

        fournisseur.setTypeFournisseur(type);

        // =====================================================
        // VALIDATION PERSONNE
        // =====================================================

        if (type.equals("PERSONNE")) {

            if (personne == null) {

                throw new IllegalArgumentException(
                        "Les informations de la personne sont obligatoires."
                );
            }

            if (personne.getNom() == null
                    || personne.getNom().isBlank()) {

                throw new IllegalArgumentException(
                        "Le nom est obligatoire."
                );
            }

            if (personne.getPrenom() == null
                    || personne.getPrenom().isBlank()) {

                throw new IllegalArgumentException(
                        "Le prénom est obligatoire."
                );
            }
        }

        // =====================================================
        // VALIDATION SOCIETE
        // =====================================================

        if (type.equals("SOCIETE")) {

            if (societe == null) {

                throw new IllegalArgumentException(
                        "Les informations de la société sont obligatoires."
                );
            }

            if (societe.getRaisonSociale() == null
                    || societe.getRaisonSociale().isBlank()) {

                throw new IllegalArgumentException(
                        "La raison sociale est obligatoire."
                );
            }
        }

        // =====================================================
        // TRANSACTION
        // =====================================================

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            try {

                connection.setAutoCommit(false);

                // -------------------------------------------------
                // 1. Ajouter FOURNISSEUR
                // -------------------------------------------------

                int idFournisseur =
                        fournisseurDAO.ajouter(
                                fournisseur,
                                connection
                        );

                // -------------------------------------------------
                // 2. Ajouter PERSONNE
                // -------------------------------------------------

                if (type.equals("PERSONNE")) {

                    personne.setIdFournisseur(
                            idFournisseur
                    );

                    // IMPORTANT :
                    // utiliser la même connexion
                    personneDAO.ajouter(
                            personne,
                            connection
                    );
                }

                // -------------------------------------------------
                // 3. Ajouter SOCIETE
                // -------------------------------------------------

                if (type.equals("SOCIETE")) {

                    societe.setIdFournisseur(
                            idFournisseur
                    );

                    // IMPORTANT :
                    // utiliser la même connexion
                    societeDAO.ajouter(
                            societe,
                            connection
                    );
                }

                // -------------------------------------------------
                // 4. Valider
                // -------------------------------------------------

                connection.commit();

            } catch (SQLException | RuntimeException e) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;
            }
        }
    }

    // =========================================================
    // TROUVER UN FOURNISSEUR PAR ID
    // =========================================================

    public Fournisseur findById(int idFournisseur)
            throws SQLException {

        if (idFournisseur <= 0) {

            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        return fournisseurDAO.findById(idFournisseur);
    }

    // =========================================================
    // TROUVER TOUS LES FOURNISSEURS
    // =========================================================

    public List<Fournisseur> findAll()
            throws SQLException {

        return fournisseurDAO.findAll();
    }

    // =========================================================
    // RECHERCHER
    // =========================================================

    public List<Fournisseur> rechercher(
            String motCle)
            throws SQLException {

        if (motCle == null) {
            motCle = "";
        }

        return fournisseurDAO.rechercher(
                motCle.trim()
        );
    }

    // =========================================================
    // MODIFIER
    // =========================================================

    public void modifier(
            Fournisseur fournisseur)
            throws SQLException {

        if (fournisseur == null) {

            throw new IllegalArgumentException(
                    "Le fournisseur ne peut pas être null."
            );
        }

        if (fournisseur.getIdFournisseur() <= 0) {

            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        if (fournisseur.getTypeFournisseur() == null
                || fournisseur.getTypeFournisseur().isBlank()) {

            throw new IllegalArgumentException(
                    "Le type du fournisseur est obligatoire."
            );
        }

        String type =
                fournisseur.getTypeFournisseur()
                        .trim()
                        .toUpperCase();

        if (!type.equals("PERSONNE")
                && !type.equals("SOCIETE")) {

            throw new IllegalArgumentException(
                    "Le type doit être PERSONNE ou SOCIETE."
            );
        }

        fournisseur.setTypeFournisseur(type);

        fournisseurDAO.modifier(
                fournisseur
        );
    }

    // =========================================================
    // SUPPRIMER
    // =========================================================

    public void supprimer(
            int idFournisseur)
            throws SQLException {

        if (idFournisseur <= 0) {

            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        fournisseurDAO.supprimer(
                idFournisseur
        );
    }
}