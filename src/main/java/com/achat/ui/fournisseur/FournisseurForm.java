package com.achat.ui.fournisseur;

import com.achat.model.Fournisseur;
import com.achat.model.Personne;
import com.achat.model.Societe;
import com.achat.service.FournisseurService;
import com.achat.service.PersonneService;
import com.achat.service.SocieteService;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FournisseurForm extends JPanel {

    private final FournisseurService fournisseurService;
    private final PersonneService personneService;
    private final SocieteService societeService;

    // =========================================================
    // COULEURS
    // =========================================================

    private static final Color BLACK =
            new Color(18, 18, 18);

    private static final Color WHITE =
            Color.WHITE;

    private static final Color BACKGROUND =
            new Color(248, 248, 248);

    private static final Color BORDER =
            new Color(225, 225, 225);

    private static final Color GRAY =
            new Color(105, 105, 105);

    // =========================================================
    // CHAMPS
    // =========================================================

    private JComboBox<String> comboType;

    private JTextField txtAdresse;
    private JTextField txtEmail;
    private JTextField txtTelephone;

    // PERSONNE
    private JTextField txtNom;
    private JTextField txtPrenom;

    // SOCIETE
    private JTextField txtRaisonSociale;
    private JTextField txtNif;
    private JTextField txtStat;

    private JButton btnEnregistrer;
    private JButton btnAnnuler;

    private int idModification = -1;

    private JDialog dialog;

    // =========================================================
    // CONSTRUCTEUR AJOUT
    // =========================================================

    public FournisseurForm() {

        fournisseurService =
                new FournisseurService();

        personneService =
                new PersonneService();

        societeService =
                new SocieteService();

        construireInterface();
    }

    // =========================================================
    // CONSTRUCTEUR MODIFICATION
    // =========================================================

    public FournisseurForm(
            Fournisseur fournisseur) {

        this();

        if (fournisseur != null) {

            chargerFournisseur(
                    fournisseur
            );
        }
    }

    // =========================================================
    // DIALOG
    // =========================================================

    public void setDialog(
            JDialog dialog) {

        this.dialog = dialog;
    }

    // =========================================================
    // CONSTRUCTION
    // =========================================================

    private void construireInterface() {

        setLayout(
                new MigLayout(
                        "fill, insets 25",
                        "[grow]",
                        "[][][grow][]"
                )
        );

        setBackground(BACKGROUND);

        // =====================================================
        // TITRE
        // =====================================================

        JPanel header =
                new JPanel(
                        new MigLayout(
                                "insets 0",
                                "[]"
                        )
                );

        header.setOpaque(false);

        JLabel titre =
                new JLabel(
                        "Informations fournisseur"
                );

        titre.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        22
                )
        );

        titre.setForeground(BLACK);

        JLabel sousTitre =
                new JLabel(
                        "Renseignez les informations du fournisseur"
                );

        sousTitre.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        sousTitre.setForeground(GRAY);

        header.setLayout(
                new BoxLayout(
                        header,
                        BoxLayout.Y_AXIS
                )
        );

        header.add(titre);

        header.add(
                Box.createVerticalStrut(4)
        );

        header.add(sousTitre);

        add(
                header,
                "growx, wrap 18"
        );

        // =====================================================
        // TYPE
        // =====================================================

        JPanel typePanel =
                new RoundedPanel(14);

        typePanel.setLayout(
                new MigLayout(
                        "fillx, insets 14",
                        "[][grow]",
                        "[]"
                )
        );

        JLabel typeLabel =
                new JLabel("Type de fournisseur");

        typeLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        typePanel.add(typeLabel);

        comboType =
                new JComboBox<>(
                        new String[]{
                                "PERSONNE",
                                "SOCIETE"
                        }
                );

        styliserComboBox(
                comboType
        );

        typePanel.add(
                comboType,
                "growx, h 38!"
        );

        add(
                typePanel,
                "growx, wrap 12"
        );

        // =====================================================
        // FORMULAIRE
        // =====================================================

        JPanel formPanel =
                new RoundedPanel(14);

        formPanel.setLayout(
                new MigLayout(
                        "fillx, insets 18",
                        "[150][grow]",
                        "[][][][][][][][]"
                )
        );

        // -----------------------------------------------------
        // PERSONNE
        // -----------------------------------------------------

        txtNom =
                creerChamp();

        txtPrenom =
                creerChamp();

        ajouterLigne(
                formPanel,
                "Nom :",
                txtNom
        );

        ajouterLigne(
                formPanel,
                "Prénom :",
                txtPrenom
        );

        // -----------------------------------------------------
        // SOCIETE
        // -----------------------------------------------------

        txtRaisonSociale =
                creerChamp();

        txtNif =
                creerChamp();

        txtStat =
                creerChamp();

        ajouterLigne(
                formPanel,
                "Raison sociale :",
                txtRaisonSociale
        );

        ajouterLigne(
                formPanel,
                "NIF :",
                txtNif
        );

        ajouterLigne(
                formPanel,
                "STAT :",
                txtStat
        );

        // -----------------------------------------------------
        // INFORMATIONS COMMUNES
        // -----------------------------------------------------

        txtAdresse =
                creerChamp();

        txtEmail =
                creerChamp();

        txtTelephone =
                creerChamp();

        ajouterLigne(
                formPanel,
                "Adresse :",
                txtAdresse
        );

        ajouterLigne(
                formPanel,
                "Email :",
                txtEmail
        );

        ajouterLigne(
                formPanel,
                "Téléphone :",
                txtTelephone
        );

        add(
                formPanel,
                "grow, wrap 15"
        );

        // =====================================================
        // BOUTONS
        // =====================================================

        JPanel boutons =
                new JPanel(
                        new MigLayout(
                                "insets 0",
                                "[grow][]10[]"
                        )
                );

        boutons.setOpaque(false);

        btnAnnuler =
                new JButton(
                        "Annuler"
                );

        btnEnregistrer =
                new JButton(
                        "Enregistrer"
                );

        styliserBoutonAnnuler(
                btnAnnuler
        );

        styliserBoutonPrincipal(
                btnEnregistrer
        );

        boutons.add(
                new JLabel(),
                "growx"
        );

        boutons.add(
                btnAnnuler,
                "h 40!"
        );

        boutons.add(
                btnEnregistrer,
                "h 40!"
        );

        add(
                boutons,
                "growx"
        );

        // =====================================================
        // EVENEMENTS
        // =====================================================

        comboType.addActionListener(
                this::changerType
        );

        btnEnregistrer.addActionListener(
                e -> enregistrer()
        );

        btnAnnuler.addActionListener(
                e -> fermer()
        );

        changerType(null);
    }

    // =========================================================
    // AJOUTER UNE LIGNE
    // =========================================================

    private void ajouterLigne(
            JPanel panel,
            String label,
            JTextField field) {

        JLabel lbl =
                new JLabel(label);

        lbl.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        lbl.setForeground(
                new Color(55, 55, 55)
        );

        panel.add(
                lbl
        );

        panel.add(
                field,
                "growx, h 38!, wrap 9"
        );
    }

    // =========================================================
    // CREER CHAMP
    // =========================================================

    private JTextField creerChamp() {

        JTextField field =
                new JTextField();

        field.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        field.setBackground(WHITE);

        field.setForeground(BLACK);

        field.setCaretColor(BLACK);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                0,
                                10,
                                0,
                                10
                        )
                )
        );

        return field;
    }

    // =========================================================
    // COMBOBOX
    // =========================================================

    private void styliserComboBox(
            JComboBox<String> combo) {

        combo.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        combo.setBackground(WHITE);
        combo.setForeground(BLACK);

        combo.setFocusable(false);
    }

    // =========================================================
    // BOUTON PRINCIPAL
    // =========================================================

    private void styliserBoutonPrincipal(
            JButton button) {

        button.setBackground(BLACK);
        button.setForeground(WHITE);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        20,
                        0,
                        20
                )
        );
    }

    // =========================================================
    // BOUTON ANNULER
    // =========================================================

    private void styliserBoutonAnnuler(
            JButton button) {

        button.setBackground(WHITE);
        button.setForeground(BLACK);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                0,
                                18,
                                0,
                                18
                        )
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
    }

    // =========================================================
    // CHANGEMENT DE TYPE
    // =========================================================

    private void changerType(
            ActionEvent event) {

        boolean personne =
                "PERSONNE".equals(
                        comboType.getSelectedItem()
                );

        // PERSONNE

        txtNom.setEnabled(personne);
        txtPrenom.setEnabled(personne);

        // SOCIETE

        txtRaisonSociale.setEnabled(
                !personne
        );

        txtNif.setEnabled(
                !personne
        );

        txtStat.setEnabled(
                !personne
        );

        if (personne) {

            txtNom.setBackground(WHITE);
            txtPrenom.setBackground(WHITE);

            txtRaisonSociale.setBackground(
                    new Color(235, 235, 235)
            );

            txtNif.setBackground(
                    new Color(235, 235, 235)
            );

            txtStat.setBackground(
                    new Color(235, 235, 235)
            );

        } else {

            txtNom.setBackground(
                    new Color(235, 235, 235)
            );

            txtPrenom.setBackground(
                    new Color(235, 235, 235)
            );

            txtRaisonSociale.setBackground(
                    WHITE
            );

            txtNif.setBackground(
                    WHITE
            );

            txtStat.setBackground(
                    WHITE
            );
        }
    }

    // =========================================================
    // CHARGER FOURNISSEUR
    // =========================================================

    private void chargerFournisseur(
            Fournisseur fournisseur) {

        idModification =
                fournisseur.getIdFournisseur();

        comboType.setSelectedItem(
                fournisseur.getTypeFournisseur()
        );

        txtAdresse.setText(
                valeur(
                        fournisseur.getAdresse()
                )
        );

        txtEmail.setText(
                valeur(
                        fournisseur.getEmail()
                )
        );

        txtTelephone.setText(
                valeur(
                        fournisseur.getTelephone()
                )
        );

        try {

            if ("PERSONNE".equals(
                    fournisseur.getTypeFournisseur())) {

                Personne personne =
                        personneService
                                .findByFournisseur(
                                        fournisseur
                                                .getIdFournisseur()
                                );

                if (personne != null) {

                    txtNom.setText(
                            valeur(
                                    personne.getNom()
                            )
                    );

                    txtPrenom.setText(
                            valeur(
                                    personne.getPrenom()
                            )
                    );
                }

            } else {

                Societe societe =
                        societeService
                                .findByFournisseur(
                                        fournisseur
                                                .getIdFournisseur()
                                );

                if (societe != null) {

                    txtRaisonSociale.setText(
                            valeur(
                                    societe
                                            .getRaisonSociale()
                            )
                    );

                    txtNif.setText(
                            valeur(
                                    societe.getNif()
                            )
                    );

                    txtStat.setText(
                            valeur(
                                    societe.getStat()
                            )
                    );
                }
            }

            changerType(null);

            btnEnregistrer.setText(
                    "Modifier"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erreur lors du chargement :\n"
                            + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ENREGISTRER
    // =========================================================

    private void enregistrer() {

        try {

            String type =
                    comboType
                            .getSelectedItem()
                            .toString();

            String adresse =
                    txtAdresse
                            .getText()
                            .trim();

            String email =
                    txtEmail
                            .getText()
                            .trim();

            String telephone =
                    txtTelephone
                            .getText()
                            .trim();

            Fournisseur fournisseur =
                    new Fournisseur();

            fournisseur.setTypeFournisseur(
                    type
            );

            fournisseur.setAdresse(
                    adresse
            );

            fournisseur.setEmail(
                    email
            );

            fournisseur.setTelephone(
                    telephone
            );

            // =================================================
            // PERSONNE
            // =================================================

            if ("PERSONNE".equals(type)) {

                String nom =
                        txtNom
                                .getText()
                                .trim();

                String prenom =
                        txtPrenom
                                .getText()
                                .trim();

                if (nom.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Le nom est obligatoire.",
                            "Validation",
                            JOptionPane.WARNING_MESSAGE
                    );

                    txtNom.requestFocus();

                    return;
                }

                if (prenom.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Le prénom est obligatoire.",
                            "Validation",
                            JOptionPane.WARNING_MESSAGE
                    );

                    txtPrenom.requestFocus();

                    return;
                }

                Personne personne =
                        new Personne();

                personne.setNom(nom);
                personne.setPrenom(prenom);

                if (idModification == -1) {

                    fournisseurService.ajouter(
                            fournisseur,
                            personne,
                            null
                    );

                    afficherSucces(
                            "Fournisseur ajouté avec succès."
                    );

                } else {

                    fournisseur.setIdFournisseur(
                            idModification
                    );

                    fournisseurService.modifier(
                            fournisseur
                    );

                    personne.setIdFournisseur(
                            idModification
                    );

                    personneService.modifier(
                            personne
                    );

                    afficherSucces(
                            "Fournisseur modifié avec succès."
                    );
                }

            // =================================================
            // SOCIETE
            // =================================================

            } else {

                String raisonSociale =
                        txtRaisonSociale
                                .getText()
                                .trim();

                String nif =
                        txtNif
                                .getText()
                                .trim();

                String stat =
                        txtStat
                                .getText()
                                .trim();

                if (raisonSociale.isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "La raison sociale est obligatoire.",
                            "Validation",
                            JOptionPane.WARNING_MESSAGE
                    );

                    txtRaisonSociale.requestFocus();

                    return;
                }

                Societe societe =
                        new Societe();

                societe.setRaisonSociale(
                        raisonSociale
                );

                societe.setNif(nif);
                societe.setStat(stat);

                if (idModification == -1) {

                    fournisseurService.ajouter(
                            fournisseur,
                            null,
                            societe
                    );

                    afficherSucces(
                            "Fournisseur ajouté avec succès."
                    );

                } else {

                    fournisseur.setIdFournisseur(
                            idModification
                    );

                    fournisseurService.modifier(
                            fournisseur
                    );

                    societe.setIdFournisseur(
                            idModification
                    );

                    societeService.modifier(
                            societe
                    );

                    afficherSucces(
                            "Fournisseur modifié avec succès."
                    );
                }
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

    // =========================================================
    // MESSAGE SUCCES
    // =========================================================

    private void afficherSucces(
            String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Succès",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =========================================================
    // FERMER
    // =========================================================

    private void fermer() {

        if (dialog != null) {

            dialog.dispose();
        }
    }

    // =========================================================
    // VALEUR
    // =========================================================

    private String valeur(
            String valeur) {

        return valeur == null
                ? ""
                : valeur;
    }

    // =========================================================
    // PANEL ARRONDI
    // =========================================================

    private static class RoundedPanel
            extends JPanel {

        private final int radius;

        public RoundedPanel(
                int radius) {

            this.radius = radius;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(WHITE);

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    radius,
                    radius
            );

            g2.setColor(BORDER);

            g2.drawRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 1,
                    radius,
                    radius
            );

            g2.dispose();

            super.paintComponent(g);
        }
    }
}