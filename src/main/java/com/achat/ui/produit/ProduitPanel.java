package com.achat.ui.produit;

import com.achat.model.Produit;
import com.achat.service.ProduitService;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Interface de gestion des produits.
 *
 * Structure et style calqués sur FournisseurPanel / FacturePanel :
 * - En-tête (titre + bouton d'ajout)
 * - Barre de recherche en carte arrondie
 * - Tableau en carte arrondie
 */
public class ProduitPanel extends JPanel {

    private final ProduitService produitService;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtRecherche;
    private JButton btnActualiser;
    private JButton btnAjouter;
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

    public ProduitPanel() {

        produitService = new ProduitService();

        setLayout(new BorderLayout());

        setBackground(LIGHT_BG);

        creerInterface();
        chargerProduits();
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

        JLabel lblTitre = new JLabel("Gestion des produits");
        lblTitre.setFont(
                new Font("SansSerif", Font.BOLD, 30)
        );
        lblTitre.setForeground(TEXT);

        headerPanel.add(
                lblTitre,
                BorderLayout.WEST
        );

        btnAjouter = creerBoutonPrincipal(
                "+ Ajouter produit"
        );

        btnAjouter.addActionListener(e ->
                ouvrirAjout()
        );

        headerPanel.add(
                btnAjouter,
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
                "Rechercher par désignation..."
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

        btnActualiser.addActionListener(e -> {
            txtRecherche.setText("");
            chargerProduits();
        });

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
     * Création du tableau des produits.
     */
    private void creerTableau() {

        String[] colonnes = {
                "ID",
                "Désignation",
                "Description",
                "Stock actuel",
                "Stock alerte",
                "État"
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
                new Font("SansSerif", Font.PLAIN, 13)
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
                new Font("SansSerif", Font.BOLD, 12)
        );

        table.getTableHeader().setForeground(GRAY);

        table.getTableHeader().setBackground(
                new Color(248, 249, 251)
        );

        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                setBorder(
                        BorderFactory.createEmptyBorder(0, 12, 0, 12)
                );

                if (!isSelected) {
                    setBackground(WHITE);
                    setForeground(TEXT);
                }

                setFont(getFont().deriveFont(Font.PLAIN));

                if (column == 5 && value != null) {

                    setHorizontalAlignment(SwingConstants.CENTER);

                    switch (value.toString()) {
                        case "RUPTURE":
                            setForeground(RED);
                            setFont(getFont().deriveFont(Font.BOLD));
                            break;
                        case "ALERTE":
                            setForeground(ORANGE);
                            setFont(getFont().deriveFont(Font.BOLD));
                            break;
                        default:
                            setForeground(GREEN);
                            setFont(getFont().deriveFont(Font.BOLD));
                            break;
                    }

                } else if (column == 0 || column == 3 || column == 4) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }

                return c;
            }
        };

        table.setDefaultRenderer(Object.class, renderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2
                        && SwingUtilities.isLeftMouseButton(e)) {
                    modifierSelection();
                }
            }
        });
    }

    // ================================================================
    // CHARGEMENT / RECHERCHE
    // ================================================================

    private void chargerProduits() {
        try {
            List<Produit> produits = produitService.findAll();
            afficherProduits(produits);
        } catch (Exception e) {
            afficherErreur("Erreur lors du chargement des produits.", e);
        }
    }

    private void afficherProduits(List<Produit> produits) {
        tableModel.setRowCount(0);

        for (Produit produit : produits) {

            String etat = obtenirEtat(produit);

            tableModel.addRow(new Object[]{
                    produit.getIdProduit(),
                    valeur(produit.getDesignation()),
                    valeur(produit.getDescription()),
                    produit.getStockActuel(),
                    produit.getStockAlerte(),
                    etat
            });
        }
    }

    /**
     * Déterminer l'état du stock.
     */
    private String obtenirEtat(Produit produit) {

        if (produit.getStockActuel() == 0) {
            return "RUPTURE";
        }

        if (produit.getStockActuel() <= produit.getStockAlerte()) {
            return "ALERTE";
        }

        return "NORMAL";
    }

    private void rechercher() {
        try {
            String motCle = txtRecherche.getText().trim();

            List<Produit> produits =
                    motCle.isEmpty()
                            ? produitService.findAll()
                            : produitService.rechercher(motCle);

            afficherProduits(produits);

        } catch (Exception e) {
            afficherErreur("Erreur lors de la recherche.", e);
        }
    }

    // ================================================================
    // AJOUT / MODIFICATION
    // ================================================================

    private void ouvrirAjout() {
        ProduitForm form = new ProduitForm();
        afficherModal("Ajouter un produit", form);
    }

    private void modifierSelection() {
        int ligne = table.getSelectedRow();

        if (ligne == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez sélectionner un produit.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        try {
            int id = Integer.parseInt(
                    tableModel.getValueAt(ligne, 0).toString()
            );

            Produit produit = produitService.findById(id);

            if (produit == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Produit introuvable.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            ProduitForm form = new ProduitForm(produit);
            afficherModal("Modifier le produit", form);

        } catch (Exception e) {
            afficherErreur("Erreur lors de la modification.", e);
        }
    }

    private void afficherModal(String titre, ProduitForm form) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;

        if (parent instanceof Frame) {
            dialog = new JDialog((Frame) parent, titre, true);
        } else if (parent instanceof Dialog) {
            dialog = new JDialog((Dialog) parent, titre, true);
        } else {
            dialog = new JDialog((Frame) null, titre, true);
        }

        form.setDialog(dialog);
        dialog.setContentPane(form);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 620);
        dialog.setMinimumSize(new Dimension(560, 580));
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);

        chargerProduits();
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
                new Font("SansSerif", Font.BOLD, 13)
        );

        bouton.setForeground(WHITE);

        bouton.setBackground(PRIMARY);

        bouton.setFocusPainted(false);

        bouton.setBorder(
                BorderFactory.createEmptyBorder(11, 18, 11, 18)
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
                new Font("SansSerif", Font.PLAIN, 13)
        );

        bouton.setForeground(TEXT);

        bouton.setBackground(WHITE);

        bouton.setFocusPainted(false);

        bouton.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(BORDER, 1, 8),
                        BorderFactory.createEmptyBorder(8, 14, 8, 14)
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
        JOptionPane.showMessageDialog(
                this,
                message + "\n\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
        e.printStackTrace();
    }

    private String valeur(String valeur) {
        return valeur == null ? "" : valeur;
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
                    new BasicStroke(thickness)
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