package com.achat.ui.fournisseur;

import com.achat.model.Fournisseur;
import com.achat.model.Personne;
import com.achat.model.Societe;
import com.achat.service.FournisseurService;
import com.achat.service.PersonneService;
import com.achat.service.SocieteService;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface de gestion des fournisseurs.
 *
 * Structure et style calqués sur FacturePanel :
 * - En-tête (titre + bouton d'ajout)
 * - Barre de recherche en carte arrondie
 * - Tableau en carte arrondie
 */
public class FournisseurPanel extends JPanel {

    private final FournisseurService fournisseurService;
    private final PersonneService personneService;
    private final SocieteService societeService;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtRecherche;
    private JButton btnActualiser;
    private JButton btnAjouter;
    private JButton btnRechercher;

    // Pagination
    private List<Fournisseur> fournisseursComplets = new ArrayList<>();
    private int pageActuelle = 0;
    private static final int TAILLE_PAGE = 10;
    private JLabel lblPageInfo;
    private JButton btnPagePrecedente;
    private JButton btnPageSuivante;

    private static final Color PRIMARY = new Color(25, 25, 25);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_BG = new Color(248, 249, 251);
    private static final Color BORDER = new Color(225, 228, 232);
    private static final Color TEXT = new Color(35, 38, 42);
    private static final Color GRAY = new Color(110, 115, 120);
    private static final Color BLEU = new Color(60, 110, 210);
    private static final Color ROUGE = new Color(200, 60, 60);

    public FournisseurPanel() {

        fournisseurService = new FournisseurService();
        personneService = new PersonneService();
        societeService = new SocieteService();

        setLayout(new BorderLayout());

        setBackground(LIGHT_BG);

        creerInterface();
        chargerFournisseurs();
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

        JLabel lblTitre = new JLabel("Gestion des fournisseurs");
        lblTitre.setFont(
                new Font("SansSerif", Font.BOLD, 30)
        );
        lblTitre.setForeground(TEXT);

        headerPanel.add(
                lblTitre,
                BorderLayout.WEST
        );

        btnAjouter = creerBoutonPrincipal(
                "+ Ajouter fournisseur"
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
                "Nom, société, email ou téléphone..."
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
            chargerFournisseurs();
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

        tableCard.add(
                creerBarrePagination(),
                BorderLayout.SOUTH
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
     * Crée la barre de pagination affichée sous le tableau.
     */
    private JPanel creerBarrePagination() {

        JPanel pagination = new JPanel(
                new BorderLayout()
        );

        pagination.setOpaque(false);

        pagination.setBorder(
                BorderFactory.createEmptyBorder(12, 4, 0, 4)
        );

        lblPageInfo = new JLabel("Page 1 / 1");
        lblPageInfo.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        lblPageInfo.setForeground(GRAY);

        pagination.add(lblPageInfo, BorderLayout.WEST);

        JPanel boutonsPagination = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        boutonsPagination.setOpaque(false);

        btnPagePrecedente = creerBoutonSecondaire("‹ Précédent");
        btnPagePrecedente.addActionListener(e -> {
            if (pageActuelle > 0) {
                pageActuelle--;
                afficherPage();
            }
        });

        btnPageSuivante = creerBoutonSecondaire("Suivant ›");
        btnPageSuivante.addActionListener(e -> {
            if ((pageActuelle + 1) * TAILLE_PAGE < fournisseursComplets.size()) {
                pageActuelle++;
                afficherPage();
            }
        });

        boutonsPagination.add(btnPagePrecedente);
        boutonsPagination.add(btnPageSuivante);

        pagination.add(boutonsPagination, BorderLayout.EAST);

        return pagination;
    }

    /**
     * Création du tableau des fournisseurs.
     */
    private void creerTableau() {

        String[] colonnes = {
                "ID",
                "Type",
                "Nom / Société",
                "Adresse",
                "Email",
                "Téléphone",
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
                return column == 6;
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

                setHorizontalAlignment(
                        column == 0
                                ? SwingConstants.CENTER
                                : SwingConstants.LEFT
                );

                return c;
            }
        };

        table.setDefaultRenderer(Object.class, renderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(190);
        table.getColumnModel().getColumn(5).setPreferredWidth(130);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);

        table.getColumnModel().getColumn(6)
                .setCellRenderer(new ActionCellRenderer());

        table.getColumnModel().getColumn(6)
                .setCellEditor(new ActionCellEditor());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2
                        && SwingUtilities.isLeftMouseButton(e)) {

                    int colonne = table.columnAtPoint(e.getPoint());

                    // Le double-clic ouvre la modification, sauf
                    // sur la colonne Actions qui gère ses propres
                    // boutons.
                    if (colonne != 6) {
                        modifierSelection();
                    }
                }
            }
        });
    }

    // ================================================================
    // CHARGEMENT / RECHERCHE
    // ================================================================

    private void chargerFournisseurs() {
        try {
            fournisseursComplets = fournisseurService.findAll();
            pageActuelle = 0;
            afficherPage();
        } catch (Exception e) {
            afficherErreur("Erreur lors du chargement des fournisseurs.", e);
        }
    }

    /**
     * Affiche la page courante à partir de la liste complète
     * actuellement chargée (résultat de findAll() ou rechercher()).
     */
    private void afficherPage() {

        tableModel.setRowCount(0);

        int total = fournisseursComplets.size();

        int debut = pageActuelle * TAILLE_PAGE;
        int fin = Math.min(debut + TAILLE_PAGE, total);

        for (int i = debut; i < fin; i++) {

            Fournisseur fournisseur = fournisseursComplets.get(i);
            String nomSociete = obtenirNomOuSociete(fournisseur);

            tableModel.addRow(new Object[]{
                    fournisseur.getIdFournisseur(),
                    fournisseur.getTypeFournisseur(),
                    nomSociete,
                    valeur(fournisseur.getAdresse()),
                    valeur(fournisseur.getEmail()),
                    valeur(fournisseur.getTelephone()),
                    ""
            });
        }

        int totalPages = Math.max(
                1,
                (int) Math.ceil(total / (double) TAILLE_PAGE)
        );

        lblPageInfo.setText(
                "Page " + (pageActuelle + 1) + " / " + totalPages
                        + "  (" + total + " fournisseur"
                        + (total > 1 ? "s" : "") + ")"
        );

        btnPagePrecedente.setEnabled(pageActuelle > 0);
        btnPageSuivante.setEnabled(fin < total);
    }

    private String obtenirNomOuSociete(Fournisseur fournisseur) {
        try {
            int id = fournisseur.getIdFournisseur();

            if ("PERSONNE".equals(fournisseur.getTypeFournisseur())) {
                Personne personne = personneService.findByFournisseur(id);

                if (personne != null) {
                    return valeur(personne.getNom()) + " "
                            + valeur(personne.getPrenom());
                }
            } else {
                Societe societe = societeService.findByFournisseur(id);

                if (societe != null) {
                    return valeur(societe.getRaisonSociale());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur récupération nom fournisseur : "
                    + e.getMessage());
        }

        return "";
    }

    private void rechercher() {
        try {
            String motCle = txtRecherche.getText().trim();

            fournisseursComplets =
                    motCle.isEmpty()
                            ? fournisseurService.findAll()
                            : fournisseurService.rechercher(motCle);

            pageActuelle = 0;
            afficherPage();

        } catch (Exception e) {
            afficherErreur("Erreur lors de la recherche.", e);
        }
    }

    // ================================================================
    // AJOUT / MODIFICATION
    // ================================================================

    private void ouvrirAjout() {
        FournisseurForm form = new FournisseurForm();
        afficherModal("Ajouter un fournisseur", form);
    }

    private void modifierSelection() {
        int ligne = table.getSelectedRow();

        if (ligne == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez sélectionner un fournisseur.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        try {
            int id = Integer.parseInt(
                    tableModel.getValueAt(ligne, 0).toString()
            );

            Fournisseur fournisseur = fournisseurService.findById(id);

            if (fournisseur == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Fournisseur introuvable.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            FournisseurForm form = new FournisseurForm(fournisseur);
            afficherModal("Modifier le fournisseur", form);

        } catch (Exception e) {
            afficherErreur("Erreur lors de la modification.", e);
        }
    }

    private void supprimerSelection(int id) {

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vraiment supprimer ce fournisseur ?\n"
                        + "Cette action est irréversible.",
                "Confirmer la suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            fournisseurService.supprimer(id);
            chargerFournisseurs();

        } catch (Exception e) {
            afficherErreur(
                    "Erreur lors de la suppression du fournisseur.",
                    e
            );
        }
    }

    private void afficherModal(String titre, FournisseurForm form) {
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
        dialog.setSize(600, 650);
        dialog.setMinimumSize(new Dimension(560, 600));
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);

        chargerFournisseurs();
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
    // CELLULE ACTIONS — même icônes que ProduitPanel
    // ================================================================

    private class ActionCellRenderer
            extends JPanel
            implements TableCellRenderer {

        ActionCellRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(
                    FlowLayout.CENTER,
                    7,
                    10
            ));

            add(new JLabel(new PencilIcon(BLEU, 18)));
            add(new JLabel(new TrashIcon(ROUGE, 18)));
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
            setBackground(
                    isSelected
                            ? table.getSelectionBackground()
                            : WHITE
            );
            return this;
        }
    }

    private class ActionCellEditor
            extends AbstractCellEditor
            implements TableCellEditor {

        private final JPanel panel;
        private int ligneCourante;

        ActionCellEditor() {
            panel = new JPanel(new FlowLayout(
                    FlowLayout.CENTER,
                    7,
                    10
            ));
            panel.setBackground(WHITE);

            JButton btnModifier =
                    new JButton(new PencilIcon(BLEU, 18));

            JButton btnSupprimer =
                    new JButton(new TrashIcon(ROUGE, 18));

            configurerBoutonAction(btnModifier);
            configurerBoutonAction(btnSupprimer);

            btnModifier.addActionListener(e -> {
                fireEditingStopped();
                table.setRowSelectionInterval(
                        ligneCourante,
                        ligneCourante
                );
                modifierSelection();
            });

            btnSupprimer.addActionListener(e -> {
                fireEditingStopped();

                int id = Integer.parseInt(
                        tableModel.getValueAt(
                                ligneCourante,
                                0
                        ).toString()
                );

                supprimerSelection(id);
            });

            panel.add(btnModifier);
            panel.add(btnSupprimer);
        }

        private void configurerBoutonAction(JButton bouton) {
            bouton.setFocusPainted(false);
            bouton.setBorderPainted(false);
            bouton.setContentAreaFilled(false);
            bouton.setOpaque(false);
            bouton.setCursor(
                    new Cursor(Cursor.HAND_CURSOR)
            );
            bouton.setPreferredSize(
                    new Dimension(28, 28)
            );
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column
        ) {
            ligneCourante = row;

            panel.setBackground(
                    isSelected
                            ? table.getSelectionBackground()
                            : WHITE
            );

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    // ================================================================
    // ICÔNES VECTORIELLES — identiques à ProduitPanel
    // ================================================================

    private static class PencilIcon implements Icon {

        private final Color color;
        private final int size;

        PencilIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y
        ) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);

            g2.setStroke(
                    new BasicStroke(
                            2.2f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            g2.drawLine(
                    x + 5, y + 13,
                    x + 13, y + 5
            );

            g2.drawLine(
                    x + 4, y + 14,
                    x + 7, y + 13
            );

            g2.drawLine(
                    x + 13, y + 5,
                    x + 11, y + 3
            );

            g2.dispose();
        }
    }

    private static class TrashIcon implements Icon {

        private final Color color;
        private final int size;

        TrashIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y
        ) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);

            g2.setStroke(
                    new BasicStroke(
                            2f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            g2.drawLine(
                    x + 4, y + 5,
                    x + 14, y + 5
            );

            g2.drawLine(
                    x + 7, y + 3,
                    x + 11, y + 3
            );

            g2.drawRoundRect(
                    x + 5, y + 6,
                    8, 10,
                    2, 2
            );

            g2.dispose();
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