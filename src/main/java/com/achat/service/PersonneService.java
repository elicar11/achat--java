package com.achat.service;

import com.achat.dao.PersonneDAO;
import com.achat.model.Personne;

import java.sql.SQLException;

public class PersonneService {

    private final PersonneDAO personneDAO;

    public PersonneService() {
        personneDAO = new PersonneDAO();
    }

    public void ajouter(Personne personne) throws SQLException {

        if (personne == null) {
            throw new IllegalArgumentException(
                    "La personne ne peut pas être null."
            );
        }

        if (personne.getIdFournisseur() <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
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

        personne.setNom(personne.getNom().trim());
        personne.setPrenom(personne.getPrenom().trim());

        personneDAO.ajouter(personne);
    }

    public Personne findByFournisseur(
            int idFournisseur) throws SQLException {

        if (idFournisseur <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        return personneDAO.findByFournisseur(idFournisseur);
    }

    public void modifier(Personne personne)
            throws SQLException {

        if (personne == null) {
            throw new IllegalArgumentException(
                    "La personne ne peut pas être null."
            );
        }

        if (personne.getIdFournisseur() <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
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

        personne.setNom(personne.getNom().trim());
        personne.setPrenom(personne.getPrenom().trim());

        personneDAO.modifier(personne);
    }

    public void supprimer(int idFournisseur)
            throws SQLException {

        if (idFournisseur <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        personneDAO.supprimer(idFournisseur);
    }
}