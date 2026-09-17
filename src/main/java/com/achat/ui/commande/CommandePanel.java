package com.achat.ui.commande;

import com.achat.model.Commande;
import com.achat.model.Fournisseur;
import com.achat.model.LigneCommande;
import com.achat.service.CommandeService;
import com.achat.service.FournisseurService;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface de gestion des commandes.
 */
public class CommandePanel extends JPanel {

    // =========================================================
    // COULEURS
    // =========================================================

    private static final Color BACKGROUND =
            new Color(246, 246, 246);

    private static final Color WHITE =
            Color.WHITE;

    private static final Color BLACK =
            new Color(18, 18, 18);

    private static final Color GRAY =
            new Color(120, 120, 120);

    private static final Color BORDER =
            new Color(225, 225, 225);

    private static final Color LIGHT_GRAY =
            new Color(238, 238, 238);

    private static final Color RED =
            new Color(190, 50, 50);

    // =========================================================
    // SERVICES
    // =========================================================

    private final CommandeService commandeService;
    private final FournisseurService fournisseurService;

    // =========================================================
    // COMPOSANTS
    // =========================================================

    private JTextField txtRecherche;

    private JTable table;

    private DefaultTableModel tableModel;

    // =========================================================
    // CONSTRUCTEUR
    // =========================================================

    public CommandePanel() {

        commandeService =
                new CommandeService();

        fournisseurService =
                new FournisseurService();

        construireInterface();

        chargerCommandes();
    }

    // =========================================================
    // CONSTRUCTION INTERFACE
    // =========================================================

    private void construireInterface() {

        /*
         * BorderLayout est utilisé pour que le contenu
         * occupe réellement toute la largeur et la hauteur.
         */
        setLayout(new BorderLayout());

        setBackground(BACKGROUND);

        JPanel mainPanel =
                new JPanel(new BorderLayout(0, 18));

        mainPanel.setOpaque(false);

        mainPanel.setBorder(
                new EmptyBorder(
                        28,
                        28,
                        28,
                        28
                )
        );

        // =====================================================
        // HEADER
        // =====================================================

        JPanel header =
                new JPanel(new BorderLayout());

        header.setOpaque(false);

        JLabel titre =
                new JLabel("Gestion des commandes");

        titre.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        30
                )
        );

        titre.setForeground(BLACK);

        header.add(
                titre,
                BorderLayout.WEST
        );

        JButton btnAjouter =
                createBlackButton(
                        "+ Ajouter commande"
                );

        btnAjouter.addActionListener(
                e -> ouvrirAjout()
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
        // CARTE PRINCIPALE
        // =====================================================

        JPanel tableCard =
                new JPanel(
                        new BorderLayout(0, 12)
                );

        tableCard.setBackground(WHITE);

        tableCard.setBorder(
                new EmptyBorder(
                        22,
                        22,
                        22,
                        22
                )
        );

        // =====================================================
        // RECHERCHE
        // =====================================================

        JPanel recherchePanel =
                new JPanel(
                        new BorderLayout(8, 0)
                );

        recherchePanel.setOpaque(false);

        txtRecherche =
                new JTextField();

        txtRecherche.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        txtRecherche.setPreferredSize(
                new Dimension(
                        0,
                        48
                )
        );

        txtRecherche.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                0,
                                12,
                                0,
                                12
                        )
                )
        );

        recherchePanel.add(
                txtRecherche,
                BorderLayout.CENTER
        );

        JPanel boutonsRecherche =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                0,
                                0
                        )
                );

        boutonsRecherche.setOpaque(false);

        JButton btnRechercher =
                createWhiteButton(
                        "Rechercher",
                        125
                );

        JButton btnActualiser =
                createWhiteButton(
                        "Actualiser",
                        115
                );

        btnRechercher.addActionListener(
                e -> rechercher()
        );

        btnActualiser.addActionListener(
                e -> {

                    txtRecherche.setText("");

                    chargerCommandes();
                }
        );

        boutonsRecherche.add(
                btnRechercher
        );

        boutonsRecherche.add(
                Box.createHorizontalStrut(8)
        );

        boutonsRecherche.add(
                btnActualiser
        );

        recherchePanel.add(
                boutonsRecherche,
                BorderLayout.EAST
        );

        tableCard.add(
                recherchePanel,
                BorderLayout.NORTH
        );

        // =====================================================
        // TABLEAU
        // =====================================================

        String[] colonnes = {
                "ID",
                "Date",
                "Fournisseur",
                "État",
                "Total",
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
                        return false;
                    }
                };

        table =
                new JTable(tableModel);

        table.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        table.setRowHeight(48);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.setShowVerticalLines(false);

        table.setShowHorizontalLines(true);

        table.setGridColor(
                new Color(
                        235,
                        235,
                        235
                )
        );

        table.setIntercellSpacing(
                new Dimension(
                        0,
                        0
                )
        );

        table.setSelectionBackground(
                LIGHT_GRAY
        );

        table.setSelectionForeground(
                BLACK
        );

        // =====================================================
        // HEADER TABLE
        // =====================================================

        table.getTableHeader()
                .setReorderingAllowed(false);

        table.getTableHeader()
                .setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                13
                        )
                );

        table.getTableHeader()
                .setForeground(BLACK);

        table.getTableHeader()
                .setBackground(
                        new Color(
                                248,
                                248,
                                248
                        )
                );

        table.getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                45
                        )
                );

        // =====================================================
        // LARGEURS COLONNES
        // =====================================================

        table.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(70);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(160);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(300);

        table.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(180);

        table.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(180);

        table.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(150);

        // =====================================================
        // CENTRAGE
        // =====================================================

        DefaultTableCellRenderer center =
                new DefaultTableCellRenderer();

        center.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        table.getColumnModel()
                .getColumn(0)
                .setCellRenderer(center);

        table.getColumnModel()
                .getColumn(1)
                .setCellRenderer(center);

        table.getColumnModel()
                .getColumn(3)
                .setCellRenderer(center);

        table.getColumnModel()
                .getColumn(4)
                .setCellRenderer(center);

        table.getColumnModel()
                .getColumn(5)
                .setCellRenderer(center);

        // =====================================================
        // SCROLLPANE
        // =====================================================

        JScrollPane scrollPane =
                new JScrollPane(table);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );

        scrollPane.setBackground(WHITE);

        scrollPane.getViewport()
                .setBackground(WHITE);

        /*
         * IMPORTANT :
         * BorderLayout.CENTER permet au tableau
         * de prendre toute la place disponible.
         */
        tableCard.add(
                scrollPane,
                BorderLayout.CENTER
        );

        // =====================================================
        // DOUBLE CLIC
        // =====================================================

        table.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e
                    ) {

                        if (e.getClickCount() == 2
                                && SwingUtilities
                                .isLeftMouseButton(e)) {

                            modifierSelection();
                        }
                    }
                }
        );

        // =====================================================
        // MAIN PANEL
        // =====================================================

        mainPanel.add(
                tableCard,
                BorderLayout.CENTER
        );

        add(
                mainPanel,
                BorderLayout.CENTER
        );

        // =====================================================
        // ENTER DANS RECHERCHE
        // =====================================================

        txtRecherche.addActionListener(
                e -> rechercher()
        );
    }

    // =========================================================
    // CHARGER COMMANDES
    // =========================================================

    private void chargerCommandes() {

        try {

            List<Commande> commandes =
                    commandeService.findAll();

            afficherCommandes(
                    commandes
            );

        } catch (Exception e) {

            afficherErreur(
                    "Impossible de charger les commandes.",
                    e
            );
        }
    }

    // =========================================================
    // AFFICHER COMMANDES
    // =========================================================

    private void afficherCommandes(
            List<Commande> commandes
    ) {

        tableModel.setRowCount(0);

        for (Commande commande :
                commandes) {

            String fournisseur =
                    "Fournisseur #"
                            + commande
                            .getIdFournisseur();

            String total =
                    calculerTotal(
                            commande
                                    .getIdCommande()
                    );

            tableModel.addRow(
                    new Object[]{
                            commande
                                    .getIdCommande(),

                            commande
                                    .getDateCommande(),

                            fournisseur,

                            commande
                                    .getEtatCommande(),

                            total,

                            "Modifier"
                    }
            );
        }
    }

    // =========================================================
    // CALCUL TOTAL
    // =========================================================

    private String calculerTotal(
            int idCommande
    ) {

        try {

            double total =
                    commandeService
                            .calculerTotal(
                                    idCommande
                            );

            return String.format(
                    "%.2f Ar",
                    total
            );

        } catch (Exception e) {

            return "0.00 Ar";
        }
    }

    // =========================================================
    // RECHERCHE
    // =========================================================

    private void rechercher() {

        try {

            String motCle =
                    txtRecherche
                            .getText()
                            .trim();

            if (motCle.isEmpty()) {

                chargerCommandes();

                return;
            }

            List<Commande> commandes =
                    commandeService
                            .rechercher(
                                    motCle
                            );

            afficherCommandes(
                    commandes
            );

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la recherche.",
                    e
            );
        }
    }

    // =========================================================
    // AJOUT
    // =========================================================

    private void ouvrirAjout() {

        CommandeForm form =
                new CommandeForm();

        afficherModal(
                "Ajouter une commande",
                form
        );
    }

    // =========================================================
    // MODIFICATION
    // =========================================================

    private void modifierSelection() {

        int ligne =
                table.getSelectedRow();

        if (ligne < 0) {

            return;
        }

        int modelRow =
                table.convertRowIndexToModel(
                        ligne
                );

        int id =
                Integer.parseInt(
                        tableModel
                                .getValueAt(
                                        modelRow,
                                        0
                                )
                                .toString()
                );

        try {

            Commande commande =
                    trouverCommande(id);

            if (commande == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Commande introuvable.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            CommandeForm form =
                    new CommandeForm(
                            commande
                    );

            afficherModal(
                    "Modifier la commande",
                    form
            );

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la modification.",
                    e
            );
        }
    }

    // =========================================================
    // TROUVER COMMANDE
    // =========================================================

    private Commande trouverCommande(
            int id
    ) throws Exception {

        List<Commande> commandes =
                commandeService.findAll();

        for (Commande commande :
                commandes) {

            if (commande.getIdCommande()
                    == id) {

                return commande;
            }
        }

        return null;
    }

    // =========================================================
    // MODAL
    // =========================================================

    private void afficherModal(
            String titre,
            CommandeForm form
    ) {

        Window parent =
                SwingUtilities
                        .getWindowAncestor(
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
                760,
                680
        );

        dialog.setMinimumSize(
                new Dimension(
                        700,
                        620
                )
        );

        dialog.setLocationRelativeTo(
                this
        );

        dialog.setResizable(false);

        dialog.setVisible(true);

        chargerCommandes();
    }

    // =========================================================
    // BOUTON NOIR
    // =========================================================

    private JButton createBlackButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(WHITE);

        button.setBackground(BLACK);

        button.setFocusPainted(false);

        button.setBorder(
                new EmptyBorder(
                        0,
                        20,
                        0,
                        20
                )
        );

        button.setPreferredSize(
                new Dimension(
                        175,
                        48
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    // =========================================================
    // BOUTON BLANC
    // =========================================================

    private JButton createWhiteButton(
            String text,
            int width
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        button.setForeground(BLACK);

        button.setBackground(WHITE);

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                0,
                                15,
                                0,
                                15
                        )
                )
        );

        button.setPreferredSize(
                new Dimension(
                        width,
                        48
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
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
}