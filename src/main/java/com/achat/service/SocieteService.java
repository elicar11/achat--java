package com.achat.service;

import com.achat.dao.SocieteDAO;
import com.achat.model.Societe;

import java.sql.SQLException;

public class SocieteService {

    private final SocieteDAO societeDAO;

    public SocieteService() {
        societeDAO = new SocieteDAO();
    }

    public void ajouter(Societe societe)
            throws SQLException {

        if (societe == null) {
            throw new IllegalArgumentException(
                    "La société ne peut pas être null."
            );
        }

        if (societe.getIdFournisseur() <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        if (societe.getRaisonSociale() == null
                || societe.getRaisonSociale().isBlank()) {

            throw new IllegalArgumentException(
                    "La raison sociale est obligatoire."
            );
        }

        societe.setRaisonSociale(
                societe.getRaisonSociale().trim()
        );

        if (societe.getNif() != null) {
            societe.setNif(societe.getNif().trim());
        }

        if (societe.getStat() != null) {
            societe.setStat(societe.getStat().trim());
        }

        societeDAO.ajouter(societe);
    }

    public Societe findByFournisseur(
            int idFournisseur) throws SQLException {

        if (idFournisseur <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        return societeDAO.findByFournisseur(idFournisseur);
    }

    public void modifier(Societe societe)
            throws SQLException {

        if (societe == null) {
            throw new IllegalArgumentException(
                    "La société ne peut pas être null."
            );
        }

        if (societe.getIdFournisseur() <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        if (societe.getRaisonSociale() == null
                || societe.getRaisonSociale().isBlank()) {

            throw new IllegalArgumentException(
                    "La raison sociale est obligatoire."
            );
        }

        societe.setRaisonSociale(
                societe.getRaisonSociale().trim()
        );

        if (societe.getNif() != null) {
            societe.setNif(societe.getNif().trim());
        }

        if (societe.getStat() != null) {
            societe.setStat(societe.getStat().trim());
        }

        societeDAO.modifier(societe);
    }

    public void supprimer(int idFournisseur)
            throws SQLException {

        if (idFournisseur <= 0) {
            throw new IllegalArgumentException(
                    "L'identifiant du fournisseur est invalide."
            );
        }

        societeDAO.supprimer(idFournisseur);
    }
}