package com.achat.ui.reception;

import com.achat.model.Reception;
import com.achat.service.ReceptionService;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class ReceptionPanel extends JPanel {

    // =========================================================
    // SERVICE
    // =========================================================

    private final ReceptionService receptionService =
            new ReceptionService();

    // =========================================================
    // COMPOSANTS
    // =========================================================

    private JTable table;

    private DefaultTableModel tableModel;

    private JTextField txtRecherche;

    private JLabel lblPageInfo;

    private JButton btnPagePrecedente;

    private JButton btnPageSuivante;

    // =========================================================
    // DONNÉES
    // =========================================================

    private List<Reception> receptionsCourantes =
            new ArrayList<>();

    private int pageActuelle = 0;

    private static final int PAGE_SIZE = 10;

    // =========================================================
    // COULEURS
    // =========================================================

    private static final Color PRIMARY =
            new Color(25, 25, 25);

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

    private static final Color BLEU =
            new Color(60, 110, 210);

    private static final Color ROUGE =
            new Color(200, 60, 60);

    // =========================================================
    // CONSTRUCTEUR
    // =========================================================

    public ReceptionPanel() {

        setLayout(new BorderLayout());

        setBackground(LIGHT_BG);

        creerInterface();

        chargerReceptions();
    }

    // =========================================================
    // INTERFACE
    // =========================================================

    private void creerInterface() {

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(0, 18)
                );

        mainPanel.setOpaque(false);

        mainPanel.setBorder(
                new EmptyBorder(
                        30,
                        30,
                        30,
                        30
                )
        );

        // =====================================================
        // HEADER
        // =====================================================

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setOpaque(false);

        JLabel titre =
                new JLabel(
                        "Gestion des réceptions"
                );

        titre.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        30
                )
        );

        titre.setForeground(TEXT);

        header.add(
                titre,
                BorderLayout.WEST
        );

        JButton btnAjouter =
                creerBoutonPrincipal(
                        "+ Ajouter réception"
                );

        btnAjouter.addActionListener(
                e -> ouvrirFormulaire()
        );

        header.add(
                btnAjouter,
                BorderLayout.EAST
        );

        mainPanel.add(
                header,
                BorderLayout.NORTH
        );

        // =====================================================
        // CARD
        // =====================================================

        JPanel card =
                new JPanel(
                        new BorderLayout(0, 12)
                );

        card.setOpaque(false);

        // =====================================================
        // RECHERCHE
        // =====================================================

        JPanel recherche =
                new JPanel(
                        new BorderLayout(8, 0)
                );

        recherche.setBackground(WHITE);

        recherche.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(
                                BORDER,
                                1,
                                12
                        ),
                        BorderFactory.createEmptyBorder(
                                6,
                                12,
                                6,
                                12
                        )
                )
        );

        txtRecherche =
                new JTextField();

        txtRecherche.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        txtRecherche.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        8,
                        8,
                        8
                )
        );

        txtRecherche.putClientProperty(
                "JTextField.placeholderText",
                "Bon de livraison ou numéro de commande..."
        );

        recherche.add(
                txtRecherche,
                BorderLayout.CENTER
        );

        JPanel boutonsRecherche =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        boutonsRecherche.setOpaque(false);

        JButton btnRechercher =
                creerBoutonSecondaire(
                        "Rechercher"
                );

        JButton btnActualiser =
                creerBoutonSecondaire(
                        "Actualiser"
                );

        btnRechercher.addActionListener(
                e -> rechercher()
        );

        btnActualiser.addActionListener(
                e -> {

                    txtRecherche.setText("");

                    chargerReceptions();
                }
        );

        txtRecherche.addActionListener(
                e -> rechercher()
        );

        boutonsRecherche.add(
                btnRechercher
        );

        boutonsRecherche.add(
                btnActualiser
        );

        recherche.add(
                boutonsRecherche,
                BorderLayout.EAST
        );

        card.add(
                recherche,
                BorderLayout.NORTH
        );

        // =====================================================
        // TABLEAU
        // =====================================================

        creerTableau();

        JScrollPane scrollPane =
                new JScrollPane(table);

        scrollPane.setBorder(
                new RoundedBorder(
                        BORDER,
                        1,
                        14
                )
        );

        scrollPane.setBackground(WHITE);

        scrollPane.getViewport()
                .setBackground(WHITE);

        card.add(
                scrollPane,
                BorderLayout.CENTER
        );

        // =====================================================
        // PAGINATION
        // =====================================================

        card.add(
                creerBarrePagination(),
                BorderLayout.SOUTH
        );

        mainPanel.add(
                card,
                BorderLayout.CENTER
        );

        add(
                mainPanel,
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // TABLEAU
    // =========================================================

    private void creerTableau() {

        String[] colonnes = {
            "ID",
            "Date",
            "Bon de livraison",
            "Commande",
            "Actions"
        };

        tableModel =
                new DefaultTableModel(
                        colonnes,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return column == 4;
                    }
                };

        table =
                new JTable(tableModel);

        // -----------------------------------------------------
        // STYLE
        // -----------------------------------------------------

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
                new Color(
                        238,
                        240,
                        243
                )
        );

        table.setShowVerticalLines(false);

        table.setShowHorizontalLines(true);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.setSelectionBackground(
                new Color(
                        242,
                        244,
                        247
                )
        );

        table.setSelectionForeground(TEXT);

        // -----------------------------------------------------
        // HEADER
        // -----------------------------------------------------

        table.getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                44
                        )
                );

        table.getTableHeader()
                .setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                12
                        )
                );

        table.getTableHeader()
                .setForeground(GRAY);

        table.getTableHeader()
                .setBackground(
                        new Color(
                                248,
                                249,
                                251
                        )
                );

        table.getTableHeader()
                .setReorderingAllowed(false);

        // -----------------------------------------------------
        // RENDERER
        // -----------------------------------------------------

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component
                    getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean selected,
                            boolean focus,
                            int row,
                            int column
                    ) {

                        Component c =
                                super.getTableCellRendererComponent(
                                        table,
                                        value,
                                        selected,
                                        focus,
                                        row,
                                        column
                                );

                        setBorder(
                                BorderFactory.createEmptyBorder(
                                        0,
                                        12,
                                        0,
                                        12
                                )
                        );

                        if (!selected) {

                            setBackground(WHITE);

                            setForeground(TEXT);
                        }

                        if (column == 0) {

                            setHorizontalAlignment(
                                    SwingConstants.CENTER
                            );

                        } else {

                            setHorizontalAlignment(
                                    SwingConstants.LEFT
                            );
                        }

                        return c;
                    }
                };

        table.setDefaultRenderer(
                Object.class,
                renderer
        );

        // -----------------------------------------------------
        // LARGEURS
        // -----------------------------------------------------

        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(50);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(140);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(280);

        table.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(150);

        table.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(110);

        // -----------------------------------------------------
        // ACTIONS
        // -----------------------------------------------------

        table.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new ActionCellRenderer()
                );

        table.getColumnModel()
                .getColumn(4)
                .setCellEditor(
                        new ActionCellEditor()
                );

        // -----------------------------------------------------
        // DOUBLE CLIC
        // -----------------------------------------------------

        table.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        if (
                                e.getClickCount() == 2
                                && SwingUtilities.isLeftMouseButton(e)
                        ) {

                            int colonne =
                                    table.columnAtPoint(
                                            e.getPoint()
                                    );

                            if (colonne != 4) {

                                modifierSelection();
                            }
                        }
                    }
                }
        );
    }

    // =========================================================
    // PAGINATION
    // =========================================================

    private JPanel creerBarrePagination() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setOpaque(false);

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        4,
                        0,
                        4
                )
        );

        lblPageInfo =
                new JLabel(
                        "Page 1 / 1"
                );

        lblPageInfo.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        lblPageInfo.setForeground(GRAY);

        panel.add(
                lblPageInfo,
                BorderLayout.WEST
        );

        JPanel boutons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        boutons.setOpaque(false);

        btnPagePrecedente =
                creerBoutonSecondaire(
                        "‹ Précédent"
                );

        btnPageSuivante =
                creerBoutonSecondaire(
                        "Suivant ›"
                );

        btnPagePrecedente.addActionListener(
                e -> {

                    if (pageActuelle > 0) {

                        pageActuelle--;

                        afficherPage();
                    }
                }
        );

        btnPageSuivante.addActionListener(
                e -> {

                    if (
                            (pageActuelle + 1)
                            * PAGE_SIZE
                            < receptionsCourantes.size()
                    ) {

                        pageActuelle++;

                        afficherPage();
                    }
                }
        );

        boutons.add(
                btnPagePrecedente
        );

        boutons.add(
                btnPageSuivante
        );

        panel.add(
                boutons,
                BorderLayout.EAST
        );

        return panel;
    }

    // =========================================================
    // CHARGEMENT
    // =========================================================

    private void chargerReceptions() {

        try {

            receptionsCourantes =
                    new ArrayList<>(
                            receptionService.findAll()
                    );

            pageActuelle = 0;

            afficherPage();

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors du chargement des réceptions.",
                    e
            );
        }
    }

    // =========================================================
    // RECHERCHE
    // =========================================================

    private void rechercher() {

        try {

            String texte =
                    txtRecherche
                            .getText()
                            .trim();

            if (texte.isEmpty()) {

                receptionsCourantes =
                        new ArrayList<>(
                                receptionService.findAll()
                        );

            } else {

                receptionsCourantes =
                        new ArrayList<>(
                                receptionService.rechercher(
                                        texte
                                )
                        );
            }

            pageActuelle = 0;

            afficherPage();

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la recherche.",
                    e
            );
        }
    }

    // =========================================================
    // AFFICHAGE PAGE
    // =========================================================

    private void afficherPage() {

        tableModel.setRowCount(0);

        int total =
                receptionsCourantes.size();

        int debut =
                pageActuelle * PAGE_SIZE;

        int fin =
                Math.min(
                        debut + PAGE_SIZE,
                        total
                );

        for (
                int i = debut;
                i < fin;
                i++
        ) {

            Reception reception =
                    receptionsCourantes.get(i);

            tableModel.addRow(
                    new Object[]{
                        reception.getIdReception(),
                        reception.getDateReception(),
                        reception.getNumBonLivraison(),
                        reception.getIdCommande(),
                        ""
                    }
            );
        }

        int nombrePages =
                Math.max(
                        1,
                        (int) Math.ceil(
                                total / (double) PAGE_SIZE
                        )
                );

        lblPageInfo.setText(
                "Page "
                + (pageActuelle + 1)
                + " / "
                + nombrePages
                + "  ("
                + total
                + " réception"
                + (total > 1 ? "s" : "")
                + ")"
        );

        btnPagePrecedente.setEnabled(
                pageActuelle > 0
        );

        btnPageSuivante.setEnabled(
                fin < total
        );
    }

    // =========================================================
    // AJOUT
    // =========================================================

    private void ouvrirFormulaire() {

        afficherModal(
                "Ajouter une réception",
                new ReceptionForm()
        );
    }

    // =========================================================
    // MODIFICATION
    // =========================================================

    private void modifierSelection() {

        int ligne =
                table.getSelectedRow();

        if (ligne < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez sélectionner une réception.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        modifierLigne(ligne);
    }

    private void modifierLigne(
            int ligne
    ) {

        int ligneModele =
                table.convertRowIndexToModel(
                        ligne
                );

        int id =
                Integer.parseInt(
                        tableModel
                                .getValueAt(
                                        ligneModele,
                                        0
                                )
                                .toString()
                );

        try {

            Reception reception =
                    receptionService.findById(id);

            if (reception == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Réception introuvable.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            afficherModal(
                    "Modifier la réception",
                    new ReceptionForm(reception)
            );

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la modification.",
                    e
            );
        }
    }

    // =========================================================
    // SUPPRESSION
    // =========================================================

    private void supprimerLigne(
            int ligne
    ) {

        int ligneModele =
                table.convertRowIndexToModel(
                        ligne
                );

        int id =
                Integer.parseInt(
                        tableModel
                                .getValueAt(
                                        ligneModele,
                                        0
                                )
                                .toString()
                );

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Voulez-vous vraiment supprimer cette réception ?\n"
                        + "Cette action est irréversible.",
                        "Confirmer la suppression",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (
                confirmation
                != JOptionPane.YES_OPTION
        ) {

            return;
        }

        try {

            receptionService.supprimer(id);

            chargerReceptions();

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la suppression de la réception.",
                    e
            );
        }
    }

    // =========================================================
    // MODAL
    // =========================================================

    private void afficherModal(
            String titre,
            ReceptionForm form
    ) {

        Window parent =
                SwingUtilities.getWindowAncestor(
                        this
                );

        JDialog dialog;

        if (parent instanceof Frame) {

            dialog =
                    new JDialog(
                            (Frame) parent,
                            titre,
                            true
                    );

        } else if (parent instanceof Dialog) {

            dialog =
                    new JDialog(
                            (Dialog) parent,
                            titre,
                            true
                    );

        } else {

            dialog =
                    new JDialog(
                            (Frame) null,
                            titre,
                            true
                    );
        }

        form.setDialog(dialog);

        dialog.setContentPane(form);

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        dialog.setSize(
                820,
                720
        );

        dialog.setMinimumSize(
                new Dimension(
                        760,
                        650
                )
        );

        dialog.setLocationRelativeTo(this);

        dialog.setResizable(false);

        dialog.setVisible(true);

        chargerReceptions();
    }

    // =========================================================
    // BOUTON PRINCIPAL
    // =========================================================

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

    // =========================================================
    // BOUTON SECONDAIRE
    // =========================================================

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
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return bouton;
    }

    // =========================================================
    // ERREUR
    // =========================================================

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

        e.printStackTrace();
    }

    // =========================================================
    // RENDERER ACTIONS
    // =========================================================

    private class ActionCellRenderer
            extends JPanel
            implements TableCellRenderer {

        public ActionCellRenderer() {

            setOpaque(true);

            setLayout(
                    new FlowLayout(
                            FlowLayout.CENTER,
                            7,
                            10
                    )
            );

            add(
                    new JLabel(
                            new PencilIcon(
                                    BLEU,
                                    18
                            )
                    )
            );

            add(
                    new JLabel(
                            new TrashIcon(
                                    ROUGE,
                                    18
                            )
                    )
            );
        }

        @Override
        public Component
        getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focus,
                int row,
                int column
        ) {

            setBackground(
                    selected
                    ? table.getSelectionBackground()
                    : WHITE
            );

            return this;
        }
    }

    // =========================================================
    // EDITOR ACTIONS
    // =========================================================

    private class ActionCellEditor
            extends AbstractCellEditor
            implements TableCellEditor {

        private final JPanel panel;

        private int ligneCourante;

        public ActionCellEditor() {

            panel =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.CENTER,
                                    7,
                                    10
                            )
                    );

            panel.setBackground(WHITE);

            JButton modifier =
                    new JButton(
                            new PencilIcon(
                                    BLEU,
                                    18
                            )
                    );

            JButton supprimer =
                    new JButton(
                            new TrashIcon(
                                    ROUGE,
                                    18
                            )
                    );

            configurerBoutonAction(
                    modifier
            );

            configurerBoutonAction(
                    supprimer
            );

            modifier.addActionListener(
                    e -> {

                        fireEditingStopped();

                        table.setRowSelectionInterval(
                                ligneCourante,
                                ligneCourante
                        );

                        modifierLigne(
                                ligneCourante
                        );
                    }
            );

            supprimer.addActionListener(
                    e -> {

                        fireEditingStopped();

                        supprimerLigne(
                                ligneCourante
                        );
                    }
            );

            panel.add(modifier);

            panel.add(supprimer);
        }

        private void configurerBoutonAction(
                JButton bouton
        ) {

            bouton.setFocusPainted(false);

            bouton.setBorderPainted(false);

            bouton.setContentAreaFilled(false);

            bouton.setOpaque(false);

            bouton.setCursor(
                    new Cursor(
                            Cursor.HAND_CURSOR
                    )
            );

            bouton.setPreferredSize(
                    new Dimension(
                            28,
                            28
                    )
            );
        }

        @Override
        public Component
        getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean selected,
                int row,
                int column
        ) {

            ligneCourante = row;

            panel.setBackground(
                    selected
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

    // =========================================================
    // ICÔNE CRAYON
    // =========================================================

    private static class PencilIcon
            implements Icon {

        private final Color color;

        private final int size;

        public PencilIcon(
                Color color,
                int size
        ) {

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
                Component component,
                Graphics graphics,
                int x,
                int y
        ) {

            Graphics2D g =
                    (Graphics2D)
                            graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g.setColor(color);

            g.setStroke(
                    new BasicStroke(
                            2.2f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            g.drawLine(
                    x + 5,
                    y + 13,
                    x + 13,
                    y + 5
            );

            g.drawLine(
                    x + 4,
                    y + 14,
                    x + 7,
                    y + 13
            );

            g.drawLine(
                    x + 13,
                    y + 5,
                    x + 11,
                    y + 3
            );

            g.dispose();
        }
    }

    // =========================================================
    // ICÔNE POUBELLE
    // =========================================================

    private static class TrashIcon
            implements Icon {

        private final Color color;

        private final int size;

        public TrashIcon(
                Color color,
                int size
        ) {

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
                Component component,
                Graphics graphics,
                int x,
                int y
        ) {

            Graphics2D g =
                    (Graphics2D)
                            graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g.setColor(color);

            g.setStroke(
                    new BasicStroke(
                            2f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            g.drawLine(
                    x + 4,
                    y + 5,
                    x + 14,
                    y + 5
            );

            g.drawLine(
                    x + 7,
                    y + 3,
                    x + 11,
                    y + 3
            );

            g.drawRoundRect(
                    x + 5,
                    y + 6,
                    8,
                    10,
                    2,
                    2
            );

            g.dispose();
        }
    }

    // =========================================================
    // BORDURE ARRONDIE
    // =========================================================

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

            Graphics2D g =
                    (Graphics2D)
                            graphics.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g.setColor(color);

            g.setStroke(
                    new BasicStroke(
                            thickness
                    )
            );

            g.draw(
                    new RoundRectangle2D.Double(
                            x + thickness / 2.0,
                            y + thickness / 2.0,
                            width - thickness,
                            height - thickness,
                            radius,
                            radius
                    )
            );

            g.dispose();
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