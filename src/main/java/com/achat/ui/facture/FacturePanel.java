package com.achat.ui.facture;

import com.achat.model.Facture;
import com.achat.service.FactureService;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface de gestion des factures.
 *
 * Fonctionnalités :
 * - Affichage de la liste des factures
 * - Recherche par numéro de facture
 * - Actualisation
 * - Création d'une facture
 * - Modification d'une facture par double-clic
 * - Affichage du montant HT, TVA et TTC
 */
public class FacturePanel extends JPanel {

    private final FactureService factureService;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtRecherche;
    private JButton btnActualiser;
    private JButton btnNouvelle;
    private JButton btnRechercher;

    private static final Color PRIMARY = new Color(25, 25, 25);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_BG = new Color(248, 249, 251);
    private static final Color BORDER = new Color(225, 228, 232);
    private static final Color TEXT = new Color(35, 38, 42);
    private static final Color GRAY = new Color(110, 115, 120);
    private static final Color GREEN = new Color(34, 139, 94);
    private static final Color RED = new Color(210, 70, 70);
    private static final Color ORANGE = new Color(210, 135, 45);

    private final DecimalFormat decimalFormat =
            new DecimalFormat("#,##0.00");

    public FacturePanel() {

        factureService = new FactureService();

        setLayout(new BorderLayout());

        setBackground(LIGHT_BG);

        creerInterface();
        chargerFactures();
    }

    /**
     * Création de l'interface.
     */
    private void creerInterface() {

        JPanel mainPanel = new JPanel(
                new BorderLayout(0, 18)
        );

        mainPanel.setOpaque(false);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 30, 30, 30
                )
        );

        // ============================================================
        // EN-TÊTE
        // ============================================================

        JPanel headerPanel = new JPanel(
                new BorderLayout()
        );

        headerPanel.setOpaque(false);

        JLabel lblTitre = new JLabel("Gestion des factures");
        lblTitre.setFont(
                new Font("SansSerif", Font.BOLD, 30)
        );
        lblTitre.setForeground(TEXT);

        headerPanel.add(
                lblTitre,
                BorderLayout.WEST
        );

        btnNouvelle = creerBoutonPrincipal(
                "+ Ajouter facture"
        );

        btnNouvelle.addActionListener(e ->
                ouvrirFormulaire()
        );

        headerPanel.add(
                btnNouvelle,
                BorderLayout.EAST
        );

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        // ============================================================
        // CARTE PRINCIPALE (recherche + tableau)
        // ============================================================

        JPanel tableCard = new JPanel(
                new BorderLayout(0, 12)
        );

        tableCard.setOpaque(false);

        // ------------------------------------------------------------
        // BARRE DE RECHERCHE
        // ------------------------------------------------------------

        JPanel recherchePanel = new JPanel(
                new BorderLayout(8, 0)
        );

        recherchePanel.setBackground(WHITE);

        recherchePanel.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(
                                BORDER,
                                1,
                                12
                        ),
                        BorderFactory.createEmptyBorder(
                                6, 12, 6, 12
                        )
                )
        );

        txtRecherche = new JTextField();

        txtRecherche.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        txtRecherche.setBorder(
                BorderFactory.createEmptyBorder(
                        8, 8, 8, 8
                )
        );

        txtRecherche.putClientProperty(
                "JTextField.placeholderText",
                "Rechercher par numéro de facture..."
        );

        recherchePanel.add(
                txtRecherche,
                BorderLayout.CENTER
        );

        JPanel boutonsRecherche = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT, 8, 0
                )
        );

        boutonsRecherche.setOpaque(false);

        btnRechercher = creerBoutonSecondaire(
                "Rechercher"
        );

        btnRechercher.addActionListener(e ->
                rechercher()
        );

        btnActualiser = creerBoutonSecondaire(
                "Actualiser"
        );

        btnActualiser.addActionListener(e ->
                chargerFactures()
        );

        boutonsRecherche.add(btnRechercher);
        boutonsRecherche.add(btnActualiser);

        recherchePanel.add(
                boutonsRecherche,
                BorderLayout.EAST
        );

        // Recherche avec ENTER
        txtRecherche.addActionListener(e ->
                rechercher()
        );

        tableCard.add(
                recherchePanel,
                BorderLayout.NORTH
        );

        // ------------------------------------------------------------
        // TABLEAU
        // ------------------------------------------------------------

        creerTableau();

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(
                new RoundedBorder(
                        BORDER,
                        1,
                        14
                )
        );

        scrollPane.getViewport().setBackground(WHITE);

        scrollPane.setBackground(WHITE);

        tableCard.add(
                scrollPane,
                BorderLayout.CENTER
        );

        mainPanel.add(
                tableCard,
                BorderLayout.CENTER
        );

        add(
                mainPanel,
                BorderLayout.CENTER
        );
    }

    /**
     * Création du tableau des factures.
     */
    private void creerTableau() {

        String[] colonnes = {
                "ID",
                "Numéro",
                "Date",
                "Montant HT",
                "TVA",
                "État",
                "Total TTC",
                "Actions"
        };

        tableModel = new DefaultTableModel(
                colonnes,
                0
        ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.setRowHeight(48);

        table.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        table.setForeground(TEXT);

        table.setBackground(WHITE);

        table.setGridColor(
                new Color(238, 240, 243)
        );

        table.setShowVerticalLines(false);

        table.setShowHorizontalLines(true);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.setSelectionBackground(
                new Color(242, 244, 247)
        );

        table.setSelectionForeground(TEXT);

        // ============================================================
        // EN-TÊTE DU TABLEAU
        // ============================================================

        table.getTableHeader().setPreferredSize(
                new Dimension(0, 44)
        );

        table.getTableHeader().setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        table.getTableHeader().setForeground(GRAY);

        table.getTableHeader().setBackground(
                new Color(248, 249, 251)
        );

        table.getTableHeader().setReorderingAllowed(false);

        // ============================================================
        // LARGEURS
        // ============================================================

        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(55);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(170);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(115);

        table.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(135);

        table.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(100);

        table.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(145);

        table.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(135);

        table.getColumnModel()
                .getColumn(7)
                .setPreferredWidth(100);

        // ============================================================
        // RENDERS
        // ============================================================

        table.getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        new CenterRenderer()
                );

        table.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        new CenterRenderer()
                );

        table.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        new MontantRenderer()
                );

        table.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new MontantRenderer()
                );

        table.getColumnModel()
                .getColumn(5)
                .setCellRenderer(
                        new EtatPaiementRenderer()
                );

        table.getColumnModel()
                .getColumn(6)
                .setCellRenderer(
                        new TotalRenderer()
                );

        table.getColumnModel()
                .getColumn(7)
                .setCellRenderer(
                        new ActionRenderer()
                );

        // ============================================================
        // DOUBLE-CLIC
        // ============================================================

        table.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        if (e.getClickCount() == 2
                                && SwingUtilities.isLeftMouseButton(e)) {

                            modifierSelection();
                        }
                    }
                }
        );
    }

    // ================================================================
    // CHARGEMENT DES FACTURES
    // ================================================================

    private void chargerFactures() {

        try {

            List<Facture> factures =
                    factureService.findAll();

            remplirTableau(factures);

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors du chargement des factures.",
                    e
            );
        }
    }

    /**
     * Remplit le tableau avec une liste de factures.
     */
    private void remplirTableau(
            List<Facture> factures
    ) {

        tableModel.setRowCount(0);

        for (Facture facture : factures) {

            double montantHt =
                    facture.getMontantTotalHt();

            double tva =
                    facture.getMontantTva();

            double totalTtc =
                    facture.getMontantTtc();

            tableModel.addRow(
                    new Object[]{
                            facture.getIdFacture(),
                            facture.getNumFacture(),
                            facture.getDateFacture(),
                            formaterMontant(montantHt),
                            formaterMontant(tva),
                            facture.getEtatPaiement(),
                            formaterMontant(totalTtc),
                            "Modifier"
                    }
            );
        }
    }

    // ================================================================
    // RECHERCHE
    // ================================================================

    private void rechercher() {

        String recherche =
                txtRecherche.getText().trim();

        if (recherche.isEmpty()) {

            chargerFactures();
            return;
        }

        try {

            List<Facture> factures =
                    factureService.rechercher(
                            recherche
                    );

            remplirTableau(factures);

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la recherche.",
                    e
            );
        }
    }

    // ================================================================
    // NOUVELLE FACTURE
    // ================================================================

    private void ouvrirFormulaire() {

        FactureForm form =
                new FactureForm();

        afficherModal(
                form,
                "Nouvelle facture"
        );
    }

    // ================================================================
    // MODIFICATION
    // ================================================================

    private void modifierSelection() {

        int ligne =
                table.getSelectedRow();

        if (ligne < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez sélectionner une facture.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int idFacture =
                (int) tableModel.getValueAt(
                        ligne,
                        0
                );

        try {

            Facture facture =
                    factureService.findById(
                            idFacture
                    );

            if (facture == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Facture introuvable.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            FactureForm form =
                    new FactureForm(facture);

            afficherModal(
                    form,
                    "Modifier la facture"
            );

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la récupération de la facture.",
                    e
            );
        }
    }

    // ================================================================
    // MODAL
    // ================================================================

    private void afficherModal(
            FactureForm form,
            String titre
    ) {

        Window parent =
                SwingUtilities.getWindowAncestor(
                        this
                );

        JDialog dialog =
                new JDialog(
                        parent,
                        titre,
                        Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        dialog.setLayout(
                new BorderLayout()
        );

        dialog.add(
                form,
                BorderLayout.CENTER
        );

        form.setDialog(dialog);

        dialog.setSize(
                650,
                650
        );

        dialog.setMinimumSize(
                new Dimension(
                        600,
                        550
                )
        );

        dialog.setLocationRelativeTo(this);

        dialog.setVisible(true);

        // Actualiser après fermeture
        chargerFactures();
    }

    // ================================================================
    // FORMATAGE
    // ================================================================

    private String formaterMontant(
            double montant
    ) {

        return decimalFormat.format(montant)
                + " Ar";
    }

    // ================================================================
    // BOUTONS
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
                        18,
                        11,
                        18
                )
        );

        bouton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        bouton.putClientProperty(
                "JButton.buttonType",
                "roundRect"
        );

        return bouton;
    }

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
                                8,
                                14,
                                8,
                                14
                        )
                )
        );

        bouton.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        return bouton;
    }

    // ================================================================
    // ERREUR
    // ================================================================

    private void afficherErreur(
            String message,
            Exception e
    ) {

        e.printStackTrace();

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
    // RENDERER CENTRÉ
    // ================================================================

    private static class CenterRenderer
            extends DefaultTableCellRenderer {

        public CenterRenderer() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );
        }
    }

    // ================================================================
    // RENDERER MONTANT
    // ================================================================

    private static class MontantRenderer
            extends DefaultTableCellRenderer {

        public MontantRenderer() {

            setHorizontalAlignment(
                    SwingConstants.RIGHT
            );

            setForeground(TEXT);
        }
    }

    // ================================================================
    // RENDERER TOTAL TTC
    // ================================================================

    private static class TotalRenderer
            extends DefaultTableCellRenderer {

        public TotalRenderer() {

            setHorizontalAlignment(
                    SwingConstants.RIGHT
            );

            setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            13
                    )
            );

            setForeground(PRIMARY);
        }
    }

    // ================================================================
    // RENDERER ÉTAT PAIEMENT
    // ================================================================

    private static class EtatPaiementRenderer
            extends DefaultTableCellRenderer {

        public EtatPaiementRenderer() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            12
                    )
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            Component component =
                    super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            if (!isSelected && value != null) {

                String etat =
                        value.toString()
                                .trim()
                                .toLowerCase();

                if (etat.contains("payé")
                        || etat.contains("paye")
                        || etat.equals("payé")
                        || etat.equals("paye")) {

                    component.setForeground(
                            GREEN
                    );

                } else if (
                        etat.contains("partiel")
                                || etat.contains("attente")
                ) {

                    component.setForeground(
                            ORANGE
                    );

                } else if (
                        etat.contains("non")
                                || etat.contains("impay")
                ) {

                    component.setForeground(
                            RED
                    );

                } else {

                    component.setForeground(
                            TEXT
                    );
                }
            }

            return component;
        }
    }

    // ================================================================
    // RENDERER ACTION
    // ================================================================

    private static class ActionRenderer
            extends DefaultTableCellRenderer {

        public ActionRenderer() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            setForeground(
                    new Color(70, 100, 180)
            );

            setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            12
                    )
            );
        }
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
                    (Graphics2D) graphics.create();

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

            insets.top = thickness + 4;
            insets.left = thickness + 4;
            insets.bottom = thickness + 4;
            insets.right = thickness + 4;

            return insets;
        }
    }
}