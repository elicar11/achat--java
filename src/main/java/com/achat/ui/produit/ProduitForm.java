package com.achat.ui.produit;

import com.achat.model.Produit;
import com.achat.service.ProduitService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Formulaire d'ajout / modification d'un produit.
 *
 * Style calqué sur FournisseurForm ("Nouvelle commande") :
 * - Bandeau noir en en-tête avec titre blanc
 * - Formulaire à plat (pas de cartes encadrées)
 * - Séparateur horizontal avant les boutons
 * - Boutons alignés à droite (Annuler / Enregistrer)
 */
public class ProduitForm extends JPanel {

    private final ProduitService produitService;

    // ========================= THÈME =========================
    private static final Color HEADER_BG = new Color(24, 24, 24);
    private static final Color SURFACE = Color.WHITE;
    private static final Color TEXT = new Color(25, 25, 25);
    private static final Color MUTED = new Color(105, 105, 105);
    private static final Color BORDER = new Color(228, 228, 228);
    private static final Color PRIMARY = new Color(18, 18, 18);
    private static final Color PRIMARY_HOVER = new Color(40, 40, 40);
    private static final Color LIGHT_GRAY = new Color(242, 242, 242);

    private JTextField txtDesignation;
    private JTextArea txtDescription;
    private JSpinner spinnerStockActuel;
    private JSpinner spinnerStockAlerte;

    private JButton btnEnregistrer;
    private JButton btnAnnuler;

    // -1 = ajout, autre valeur = modification
    private int idModification = -1;

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

    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    /**
     * Construire l'interface.
     */
    private void construireInterface() {

        setLayout(new BorderLayout());
        setBackground(SURFACE);

        // ========================= BANDEAU NOIR =========================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titre = new JLabel("Informations produit");
        titre.setFont(titre.getFont().deriveFont(Font.BOLD, 22f));
        titre.setForeground(Color.WHITE);

        header.add(titre, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // ========================= CORPS =========================
        JPanel corps = new JPanel(new MigLayout(
                "fill, insets 26 28 20 28",
                "[150][grow]",
                "[]14[grow]14[]14[]"
        ));
        corps.setOpaque(false);

        // ------------------------- DÉSIGNATION -------------------------
        corps.add(creerLabel("Désignation :"));

        txtDesignation = new JTextField();
        styliserChamp(txtDesignation);

        corps.add(txtDesignation, "growx, h 36!, wrap");

        // ------------------------- DESCRIPTION -------------------------
        corps.add(creerLabel("Description :"), "top");

        txtDescription = new JTextArea(5, 30);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setFont(txtDescription.getFont().deriveFont(Font.PLAIN, 13f));
        txtDescription.setForeground(TEXT);
        txtDescription.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scrollDescription = new JScrollPane(txtDescription);
        scrollDescription.setBorder(BorderFactory.createLineBorder(BORDER));

        corps.add(scrollDescription, "grow, wrap");

        // ------------------------- STOCK ACTUEL -------------------------
        corps.add(creerLabel("Stock actuel :"));

        spinnerStockActuel = new JSpinner(
                new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)
        );
        styliserSpinner(spinnerStockActuel);

        corps.add(spinnerStockActuel, "growx, h 36!, wrap");

        // ------------------------- STOCK ALERTE -------------------------
        corps.add(creerLabel("Stock d'alerte :"));

        spinnerStockAlerte = new JSpinner(
                new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)
        );
        styliserSpinner(spinnerStockAlerte);

        corps.add(spinnerStockAlerte, "growx, h 36!");

        add(corps, BorderLayout.CENTER);

        // ========================= PIED (séparateur + boutons) ==========
        JPanel pied = new JPanel(new BorderLayout());
        pied.setOpaque(false);
        pied.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(16, 28, 20, 28)
        ));

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        boutons.setOpaque(false);

        btnAnnuler = creerBoutonSecondaire("Annuler");
        btnEnregistrer = creerBoutonPrincipal("Enregistrer");

        boutons.add(btnAnnuler);
        boutons.add(btnEnregistrer);

        pied.add(boutons, BorderLayout.EAST);

        add(pied, BorderLayout.SOUTH);

        // ========================= ÉVÉNEMENTS =========================
        btnEnregistrer.addActionListener(e -> enregistrer());
        btnAnnuler.addActionListener(e -> fermer());
    }

    private JLabel creerLabel(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        label.setForeground(TEXT);
        return label;
    }

    private void styliserChamp(JTextField field) {
        field.setFont(field.getFont().deriveFont(Font.PLAIN, 13f));
        field.setBackground(SURFACE);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 10, 0, 10)
        ));

        field.putClientProperty("JTextField.showClearButton", true);
    }

    private void styliserSpinner(JSpinner spinner) {
        spinner.setFont(spinner.getFont().deriveFont(Font.PLAIN, 13f));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField field =
                    ((JSpinner.DefaultEditor) editor).getTextField();
            field.setBorder(new EmptyBorder(0, 8, 0, 8));
            field.setBackground(SURFACE);
        }

        spinner.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    private JButton creerBoutonPrincipal(String texte) {
        JButton button = new JButton(texte) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? PRIMARY_HOVER : PRIMARY);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(11, 26, 11, 26));

        return button;
    }

    private JButton creerBoutonSecondaire(String texte) {
        JButton button = new JButton(texte);

        button.setBackground(SURFACE);
        button.setForeground(TEXT);
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 13f));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(11, 22, 11, 22)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(LIGHT_GRAY);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(SURFACE);
            }
        });

        return button;
    }

    /**
     * Charger un produit dans le formulaire.
     */
    private void chargerProduit(Produit produit) {

        idModification = produit.getIdProduit();

        txtDesignation.setText(valeur(produit.getDesignation()));
        txtDescription.setText(valeur(produit.getDescription()));

        spinnerStockActuel.setValue(produit.getStockActuel());
        spinnerStockAlerte.setValue(produit.getStockAlerte());

        btnEnregistrer.setText("Modifier");
    }

    /**
     * Ajouter ou modifier le produit.
     */
    private void enregistrer() {

        try {

            String designation = txtDesignation.getText().trim();
            String description = txtDescription.getText().trim();

            int stockActuel =
                    ((Number) spinnerStockActuel.getValue()).intValue();

            int stockAlerte =
                    ((Number) spinnerStockAlerte.getValue()).intValue();

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

            Produit produit = new Produit();
            produit.setDesignation(designation);
            produit.setDescription(description);
            produit.setStockActuel(stockActuel);
            produit.setStockAlerte(stockAlerte);

            if (idModification == -1) {

                produitService.ajouter(produit);

                JOptionPane.showMessageDialog(
                        this,
                        "Produit ajouté avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                produit.setIdProduit(idModification);

                produitService.modifier(produit);

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
                    "Erreur :\n" + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void fermer() {
        if (dialog != null) {
            dialog.dispose();
        }
    }

    private String valeur(String valeur) {
        return valeur == null ? "" : valeur;
    }
}