package com.achat.ui.produit;

import com.achat.model.Produit;
import com.achat.service.ProduitService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProduitForm extends JPanel {

    private final ProduitService produitService;

    private JTextField txtDesignation;
    private JTextArea txtDescription;
    private JSpinner spinnerStockActuel;
    private JSpinner spinnerStockAlerte;

    private JButton btnEnregistrer;
    private JButton btnAnnuler;

    // -1 = ajout
    // autre valeur = modification
    private int idModification = -1;

    // Fenêtre qui contient le formulaire
    private JDialog dialog;

    /**
     * Constructeur pour l'ajout.
     */
    public ProduitForm() {

        produitService = new ProduitService();

        construireInterface();
    }

    /**
     * Constructeur pour la modification.
     */
    public ProduitForm(Produit produit) {

        this();

        if (produit != null) {
            chargerProduit(produit);
        }
    }

    /**
     * Permet au formulaire de connaître
     * la fenêtre qui le contient.
     */
    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    /**
     * Construire l'interface.
     */
    private void construireInterface() {

        setLayout(
                new MigLayout(
                        "fill, insets 25",
                        "[right] [grow]",
                        "[][][grow][][][]"
                )
        );

        setBorder(
                new EmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        // ==========================================
        // TITRE
        // ==========================================

        JLabel titre =
                new JLabel("Gestion du produit");

        titre.setFont(
                titre.getFont().deriveFont(
                        Font.BOLD,
                        22f
                )
        );

        add(
                titre,
                "span 2, center, wrap 20"
        );

        // ==========================================
        // DESIGNATION
        // ==========================================

        add(new JLabel("Désignation :"));

        txtDesignation =
                new JTextField();

        add(
                txtDesignation,
                "growx, wrap"
        );

        // ==========================================
        // DESCRIPTION
        // ==========================================

        add(
                new JLabel("Description :"),
                "top"
        );

        txtDescription =
                new JTextArea(
                        5,
                        30
                );

        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);

        JScrollPane scrollDescription =
                new JScrollPane(
                        txtDescription
                );

        add(
                scrollDescription,
                "grow, wrap"
        );

        // ==========================================
        // STOCK ACTUEL
        // ==========================================

        add(new JLabel("Stock actuel :"));

        spinnerStockActuel =
                new JSpinner(
                        new SpinnerNumberModel(
                                0,
                                0,
                                Integer.MAX_VALUE,
                                1
                        )
                );

        add(
                spinnerStockActuel,
                "growx, wrap"
        );

        // ==========================================
        // STOCK ALERTE
        // ==========================================

        add(new JLabel("Stock d'alerte :"));

        spinnerStockAlerte =
                new JSpinner(
                        new SpinnerNumberModel(
                                0,
                                0,
                                Integer.MAX_VALUE,
                                1
                        )
                );

        add(
                spinnerStockAlerte,
                "growx, wrap"
        );

        // ==========================================
        // BOUTONS
        // ==========================================

        JPanel panelBoutons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        btnAnnuler =
                new JButton("Annuler");

        btnEnregistrer =
                new JButton("Enregistrer");

        panelBoutons.add(
                btnAnnuler
        );

        panelBoutons.add(
                btnEnregistrer
        );

        add(
                panelBoutons,
                "span 2, growx"
        );

        // ==========================================
        // EVENEMENTS
        // ==========================================

        btnEnregistrer.addActionListener(
                e -> enregistrer()
        );

        btnAnnuler.addActionListener(
                e -> fermer()
        );
    }

    /**
     * Charger un produit dans le formulaire.
     */
    private void chargerProduit(
            Produit produit) {

        idModification =
                produit.getIdProduit();

        txtDesignation.setText(
                valeur(produit.getDesignation())
        );

        txtDescription.setText(
                valeur(produit.getDescription())
        );

        spinnerStockActuel.setValue(
                produit.getStockActuel()
        );

        spinnerStockAlerte.setValue(
                produit.getStockAlerte()
        );

        btnEnregistrer.setText(
                "Modifier"
        );
    }

    /**
     * Ajouter ou modifier le produit.
     */
    private void enregistrer() {

        try {

            // ==========================================
            // RECUPERATION DES VALEURS
            // ==========================================

            String designation =
                    txtDesignation
                            .getText()
                            .trim();

            String description =
                    txtDescription
                            .getText()
                            .trim();

            int stockActuel =
                    ((Number) spinnerStockActuel
                            .getValue())
                            .intValue();

            int stockAlerte =
                    ((Number) spinnerStockAlerte
                            .getValue())
                            .intValue();

            // ==========================================
            // VALIDATION
            // ==========================================

            if (designation.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "La désignation est obligatoire.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE
                );

                txtDesignation.requestFocus();

                return;
            }

            if (stockActuel < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Le stock actuel ne peut pas être négatif.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            if (stockAlerte < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Le stock d'alerte ne peut pas être négatif.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // ==========================================
            // CREATION DU PRODUIT
            // ==========================================

            Produit produit =
                    new Produit();

            produit.setDesignation(
                    designation
            );

            produit.setDescription(
                    description
            );

            produit.setStockActuel(
                    stockActuel
            );

            produit.setStockAlerte(
                    stockAlerte
            );

            // ==========================================
            // AJOUT
            // ==========================================

            if (idModification == -1) {

                produitService.ajouter(
                        produit
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Produit ajouté avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                // ======================================
                // MODIFICATION
                // ======================================

                produit.setIdProduit(
                        idModification
                );

                produitService.modifier(
                        produit
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Produit modifié avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            fermer();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erreur :\n"
                            + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Fermer la fenêtre.
     */
    private void fermer() {

        if (dialog != null) {
            dialog.dispose();
        }
    }

    /**
     * Eviter d'afficher null.
     */
    private String valeur(
            String valeur) {

        return valeur == null
                ? ""
                : valeur;
    }
}