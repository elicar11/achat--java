package com.achat.ui.facture;

import com.achat.model.Facture;
import com.achat.service.FactureService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.text.DecimalFormat;

/**
 * Formulaire de création et de modification d'une facture.
 */
public class FactureForm extends JPanel {

    private final FactureService factureService;

    private JTextField txtNumeroFacture;
    private JTextField txtDate;
    private JTextField txtMontantHt;
    private JTextField txtTva;
    private JTextField txtTotalTtc;

    private JComboBox<String> comboEtatPaiement;

    private JButton btnEnregistrer;
    private JButton btnAnnuler;

    private int idModification = -1;

    private JDialog dialog;

    private static final Color WHITE =
            Color.WHITE;

    private static final Color LIGHT_BG =
            new Color(248, 249, 251);

    private static final Color BORDER =
            new Color(225, 228, 232);

    private static final Color TEXT =
            new Color(35, 38, 42);

    private static final Color GRAY =
            new Color(110, 115, 120);

    private static final Color PRIMARY =
            new Color(25, 25, 25);

    private static final Color RED =
            new Color(210, 70, 70);

    private static final Color GREEN =
            new Color(34, 139, 94);

    private final DecimalFormat decimalFormat =
            new DecimalFormat("#,##0.00");

    // ================================================================
    // CONSTRUCTEUR - NOUVELLE FACTURE
    // ================================================================

    public FactureForm() {

        factureService =
                new FactureService();

        construireInterface();

        remplirValeursParDefaut();

        ajouterCalculAutomatique();
    }

    // ================================================================
    // CONSTRUCTEUR - MODIFICATION
    // ================================================================

    public FactureForm(Facture facture) {

        this();

        if (facture == null) {
            return;
        }

        idModification =
                facture.getIdFacture();

        chargerFacture(facture);
    }

    // ================================================================
    // INTERFACE
    // ================================================================

    private void construireInterface() {

        setLayout(
                new MigLayout(
                        "fill, insets 25",
                        "[grow]",
                        "[]15[]15[]15[]15[]15[]15[]"
                )
        );

        setBackground(LIGHT_BG);

        // ============================================================
        // TITRE
        // ============================================================

        JLabel lblTitre =
                new JLabel("Facture");

        lblTitre.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        24
                )
        );

        lblTitre.setForeground(TEXT);

        add(
                lblTitre,
                "wrap"
        );

        JLabel lblSousTitre =
                new JLabel(
                        idModification == -1
                                ? "Créer une nouvelle facture"
                                : "Modifier les informations de la facture"
                );

        lblSousTitre.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        lblSousTitre.setForeground(GRAY);

        add(
                lblSousTitre,
                "wrap"
        );

        // ============================================================
        // NUMÉRO FACTURE
        // ============================================================

        JLabel lblNumero =
                creerLabel(
                        "Numéro facture"
                );

        txtNumeroFacture =
                creerTextField();

        add(
                lblNumero,
                "split 2, growx"
        );

        add(
                txtNumeroFacture,
                "growx, wrap"
        );

        // ============================================================
        // DATE
        // ============================================================

        JLabel lblDate =
                creerLabel(
                        "Date"
                );

        txtDate =
                creerTextField();

        add(
                lblDate,
                "split 2, growx"
        );

        add(
                txtDate,
                "growx, wrap"
        );

        // ============================================================
        // MONTANT HT
        // ============================================================

        JLabel lblMontantHt =
                creerLabel(
                        "Montant HT"
                );

        txtMontantHt =
                creerTextField();

        txtMontantHt.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        add(
                lblMontantHt,
                "split 2, growx"
        );

        add(
                txtMontantHt,
                "growx, wrap"
        );

        // ============================================================
        // TVA
        // ============================================================

        JLabel lblTva =
                creerLabel(
                        "TVA"
                );

        txtTva =
                creerTextField();

        txtTva.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        add(
                lblTva,
                "split 2, growx"
        );

        add(
                txtTva,
                "growx, wrap"
        );

        // ============================================================
        // TOTAL TTC
        // ============================================================

        JLabel lblTotalTtc =
                creerLabel(
                        "Total TTC"
                );

        txtTotalTtc =
                creerTextField();

        txtTotalTtc.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        txtTotalTtc.setEditable(false);

        txtTotalTtc.setBackground(
                new Color(240, 242, 245)
        );

        txtTotalTtc.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        add(
                lblTotalTtc,
                "split 2, growx"
        );

        add(
                txtTotalTtc,
                "growx, wrap"
        );

        // ============================================================
        // ETAT PAIEMENT
        // ============================================================

        JLabel lblEtat =
                creerLabel(
                        "État du paiement"
                );

        comboEtatPaiement =
                new JComboBox<>(
                        new String[]{
                                "Non payé",
                                "Partiel",
                                "Payé"
                        }
                );

        comboEtatPaiement.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        comboEtatPaiement.setBackground(
                WHITE
        );

        add(
                lblEtat,
                "split 2, growx"
        );

        add(
                comboEtatPaiement,
                "growx, wrap"
        );

        // ============================================================
        // ESPACE
        // ============================================================

        add(
                new JLabel(),
                "growy, wrap"
        );

        // ============================================================
        // BOUTONS
        // ============================================================

        JPanel boutonsPanel =
                new JPanel(
                        new MigLayout(
                                "insets 0",
                                "[grow][]10[]"
                        )
                );

        boutonsPanel.setOpaque(false);

        btnAnnuler =
                creerBoutonSecondaire(
                        "Annuler"
                );

        btnAnnuler.addActionListener(
                e -> fermer()
        );

        btnEnregistrer =
                creerBoutonPrincipal(
                        idModification == -1
                                ? "Enregistrer"
                                : "Modifier"
                );

        btnEnregistrer.addActionListener(
                e -> enregistrer()
        );

        boutonsPanel.add(
                new JLabel(),
                "growx"
        );

        boutonsPanel.add(
                btnAnnuler
        );

        boutonsPanel.add(
                btnEnregistrer
        );

        add(
                boutonsPanel,
                "growx"
        );
    }

    // ================================================================
    // VALEURS PAR DÉFAUT
    // ================================================================

    private void remplirValeursParDefaut() {

        txtDate.setText(
                LocalDate.now().toString()
        );

        txtMontantHt.setText("0");

        txtTva.setText("0");

        txtTotalTtc.setText(
                "0,00 Ar"
        );

        comboEtatPaiement.setSelectedItem(
                "Non payé"
        );
    }

    // ================================================================
    // CHARGER FACTURE
    // ================================================================

    private void chargerFacture(
            Facture facture
    ) {

        txtNumeroFacture.setText(
                facture.getNumFacture()
        );

        txtDate.setText(
                facture.getDateFacture()
                        .toString()
        );

        txtMontantHt.setText(
                String.valueOf(
                        facture.getMontantTotalHt()
                )
        );

        txtTva.setText(
                String.valueOf(
                        facture.getMontantTva()
                )
        );

        comboEtatPaiement.setSelectedItem(
                facture.getEtatPaiement()
        );

        calculerTotalTtc();
    }

    // ================================================================
    // CALCUL TTC
    // ================================================================

    private void ajouterCalculAutomatique() {

        DocumentListener listener =
                new DocumentListener() {

                    @Override
                    public void insertUpdate(
                            DocumentEvent e
                    ) {
                        calculerTotalTtc();
                    }

                    @Override
                    public void removeUpdate(
                            DocumentEvent e
                    ) {
                        calculerTotalTtc();
                    }

                    @Override
                    public void changedUpdate(
                            DocumentEvent e
                    ) {
                        calculerTotalTtc();
                    }
                };

        txtMontantHt
                .getDocument()
                .addDocumentListener(listener);

        txtTva
                .getDocument()
                .addDocumentListener(listener);
    }

    private void calculerTotalTtc() {

        try {

            double montantHt =
                    lireMontant(
                            txtMontantHt.getText()
                    );

            double tva =
                    lireMontant(
                            txtTva.getText()
                    );

            double total =
                    montantHt + tva;

            txtTotalTtc.setText(
                    decimalFormat.format(total)
                            + " Ar"
            );

        } catch (Exception e) {

            txtTotalTtc.setText(
                    "0,00 Ar"
            );
        }
    }

    // ================================================================
    // ENREGISTREMENT
    // ================================================================

    private void enregistrer() {

        try {

            // --------------------------------------------------------
            // NUMÉRO
            // --------------------------------------------------------

            String numero =
                    txtNumeroFacture
                            .getText()
                            .trim();

            if (numero.isEmpty()) {

                afficherAvertissement(
                        "Veuillez saisir le numéro de facture."
                );

                txtNumeroFacture.requestFocus();

                return;
            }

            // --------------------------------------------------------
            // DATE
            // --------------------------------------------------------

            String dateTexte =
                    txtDate
                            .getText()
                            .trim();

            if (dateTexte.isEmpty()) {

                afficherAvertissement(
                        "Veuillez saisir la date de la facture."
                );

                txtDate.requestFocus();

                return;
            }

            LocalDate dateFacture;

            try {

                dateFacture =
                        LocalDate.parse(
                                dateTexte
                        );

            } catch (DateTimeParseException e) {

                afficherAvertissement(
                        "La date doit être au format : AAAA-MM-JJ."
                );

                txtDate.requestFocus();

                return;
            }

            // --------------------------------------------------------
            // MONTANT HT
            // --------------------------------------------------------

            double montantHt;

            try {

                montantHt =
                        lireMontant(
                                txtMontantHt
                                        .getText()
                        );

            } catch (NumberFormatException e) {

                afficherAvertissement(
                        "Le montant HT est invalide."
                );

                txtMontantHt.requestFocus();

                return;
            }

            if (montantHt < 0) {

                afficherAvertissement(
                        "Le montant HT ne peut pas être négatif."
                );

                txtMontantHt.requestFocus();

                return;
            }

            // --------------------------------------------------------
            // TVA
            // --------------------------------------------------------

            double montantTva;

            try {

                montantTva =
                        lireMontant(
                                txtTva
                                        .getText()
                        );

            } catch (NumberFormatException e) {

                afficherAvertissement(
                        "Le montant de TVA est invalide."
                );

                txtTva.requestFocus();

                return;
            }

            if (montantTva < 0) {

                afficherAvertissement(
                        "La TVA ne peut pas être négative."
                );

                txtTva.requestFocus();

                return;
            }

            // --------------------------------------------------------
            // ETAT
            // --------------------------------------------------------

            String etatPaiement =
                    (String)
                            comboEtatPaiement
                                    .getSelectedItem();

            if (etatPaiement == null
                    || etatPaiement.trim().isEmpty()) {

                afficherAvertissement(
                        "Veuillez sélectionner l'état du paiement."
                );

                return;
            }

            // --------------------------------------------------------
            // CREATION OBJET FACTURE
            // --------------------------------------------------------

            Facture facture =
                    new Facture();

            facture.setNumFacture(
                    numero
            );

            facture.setDateFacture(
                    dateFacture
            );

            facture.setMontantTotalHt(
                    montantHt
            );

            facture.setMontantTva(
                    montantTva
            );

            facture.setEtatPaiement(
                    etatPaiement
            );

            // --------------------------------------------------------
            // AJOUT
            // --------------------------------------------------------

            if (idModification == -1) {

                factureService.ajouter(
                        facture
                );

                JOptionPane.showMessageDialog(
                        this,
                        "La facture a été enregistrée avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );

            }

            // --------------------------------------------------------
            // MODIFICATION
            // --------------------------------------------------------

            else {

                facture.setIdFacture(
                        idModification
                );

                factureService.modifier(
                        facture
                );

                JOptionPane.showMessageDialog(
                        this,
                        "La facture a été modifiée avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            fermer();

        } catch (Exception e) {

            e.printStackTrace();

            afficherErreur(
                    "Impossible d'enregistrer la facture.",
                    e
            );
        }
    }

    // ================================================================
    // LECTURE MONTANT
    // ================================================================

    private double lireMontant(
            String texte
    ) {

        if (texte == null
                || texte.trim().isEmpty()) {

            return 0;
        }

        String valeur =
                texte
                        .trim()
                        .replace("Ar", "")
                        .replace(" ", "")
                        .replace(",", ".");

        return Double.parseDouble(
                valeur
        );
    }

    // ================================================================
    // FERMER
    // ================================================================

    private void fermer() {

        if (dialog != null) {

            dialog.dispose();

        } else {

            Window window =
                    SwingUtilities
                            .getWindowAncestor(
                                    this
                            );

            if (window != null) {
                window.dispose();
            }
        }
    }

    // ================================================================
    // DIALOG
    // ================================================================

    public void setDialog(
            JDialog dialog
    ) {

        this.dialog = dialog;
    }

    // ================================================================
    // LABEL
    // ================================================================

    private JLabel creerLabel(
            String texte
    ) {

        JLabel label =
                new JLabel(texte);

        label.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        label.setForeground(TEXT);

        return label;
    }

    // ================================================================
    // TEXTFIELD
    // ================================================================

    private JTextField creerTextField() {

        JTextField field =
                new JTextField();

        field.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        field.setForeground(TEXT);

        field.setBackground(WHITE);

        field.setPreferredSize(
                new Dimension(
                        0,
                        42
                )
        );

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(
                                BORDER,
                                1,
                                8
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                12,
                                8,
                                12
                        )
                )
        );

        return field;
    }

    // ================================================================
    // BOUTON PRINCIPAL
    // ================================================================

    private JButton creerBoutonPrincipal(
            String texte
    ) {

        JButton bouton =
                new JButton(texte);

        bouton.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        bouton.setForeground(WHITE);

        bouton.setBackground(PRIMARY);

        bouton.setFocusPainted(false);

        bouton.setBorder(
                BorderFactory.createEmptyBorder(
                        11,
                        20,
                        11,
                        20
                )
        );

        bouton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        bouton.putClientProperty(
                "JButton.buttonType",
                "roundRect"
        );

        return bouton;
    }

    // ================================================================
    // BOUTON SECONDAIRE
    // ================================================================

    private JButton creerBoutonSecondaire(
            String texte
    ) {

        JButton bouton =
                new JButton(texte);

        bouton.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        bouton.setForeground(TEXT);

        bouton.setBackground(WHITE);

        bouton.setFocusPainted(false);

        bouton.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(
                                BORDER,
                                1,
                                8
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                18,
                                10,
                                18
                        )
                )
        );

        bouton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return bouton;
    }

    // ================================================================
    // AVERTISSEMENT
    // ================================================================

    private void afficherAvertissement(
            String message
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Attention",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // ================================================================
    // ERREUR
    // ================================================================

    private void afficherErreur(
            String message,
            Exception e
    ) {

        JOptionPane.showMessageDialog(
                this,
                message
                        + "\n\n"
                        + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ================================================================
    // BORDURE ARRONDIE
    // ================================================================

    private static class RoundedBorder
            extends AbstractBorder {

        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundedBorder(
                Color color,
                int thickness,
                int radius
        ) {

            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(
                Component component,
                Graphics graphics,
                int x,
                int y,
                int width,
                int height
        ) {

            Graphics2D g2 =
                    (Graphics2D)
                            graphics.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);

            g2.setStroke(
                    new BasicStroke(
                            thickness
                    )
            );

            Shape shape =
                    new RoundRectangle2D.Double(
                            x + thickness / 2.0,
                            y + thickness / 2.0,
                            width - thickness,
                            height - thickness,
                            radius,
                            radius
                    );

            g2.draw(shape);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(
                Component component
        ) {

            return new Insets(
                    thickness + 4,
                    thickness + 4,
                    thickness + 4,
                    thickness + 4
            );
        }

        @Override
        public Insets getBorderInsets(
                Component component,
                Insets insets
        ) {

            insets.top =
                    thickness + 4;

            insets.left =
                    thickness + 4;

            insets.bottom =
                    thickness + 4;

            insets.right =
                    thickness + 4;

            return insets;
        }
    }
}