package com.achat.ui.produit;

import com.achat.model.Produit;
import com.achat.service.ProduitService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ProduitPanel extends JPanel {

    private final ProduitService produitService;

    private JTextField txtRecherche;

    private JTable table;

    private DefaultTableModel tableModel;

    public ProduitPanel() {

        produitService =
                new ProduitService();

        construireInterface();

        chargerProduits();
    }

    /**
     * Construction de l'interface.
     */
    private void construireInterface() {

        setLayout(
                new MigLayout(
                        "fill, insets 25",
                        "[grow]",
                        "[][grow]"
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
        // HEADER
        // ==========================================

        JPanel header =
                new JPanel(
                        new MigLayout(
                                "insets 0",
                                "[grow][]"
                        )
                );

        header.setOpaque(false);

        JLabel titre =
                new JLabel("Gestion des produits");

        titre.setFont(
                titre.getFont().deriveFont(
                        Font.BOLD,
                        26f
                )
        );

        header.add(
                titre,
                "growx"
        );

        JButton btnAjouter =
                new JButton(
                        "+ Ajouter produit"
                );

        btnAjouter.addActionListener(
                e -> ouvrirAjout()
        );

        header.add(
                btnAjouter
        );

        add(
                header,
                "growx, wrap"
        );

        // ==========================================
        // RECHERCHE
        // ==========================================

        JPanel recherchePanel =
                new JPanel(
                        new MigLayout(
                                "insets 0",
                                "[][grow][]"
                        )
                );

        recherchePanel.setOpaque(false);

        recherchePanel.add(
                new JLabel("Rechercher :")
        );

        txtRecherche =
                new JTextField();

        recherchePanel.add(
                txtRecherche,
                "growx"
        );

        JButton btnRechercher =
                new JButton("Rechercher");

        recherchePanel.add(
                btnRechercher
        );

        JButton btnActualiser =
                new JButton("Actualiser");

        recherchePanel.add(
                btnActualiser
        );

        add(
                recherchePanel,
                "growx, wrap 15"
        );

        // ==========================================
        // TABLE
        // ==========================================

        String[] colonnes = {
                "ID",
                "Désignation",
                "Description",
                "Stock actuel",
                "Stock alerte",
                "État"
        };

        tableModel =
                new DefaultTableModel(
                        colonnes,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        table =
                new JTable(tableModel);

        table.setRowHeight(35);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.getTableHeader()
                .setReorderingAllowed(false);

        // ==========================================
        // LARGEUR DES COLONNES
        // ==========================================

        table.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(60);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(180);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(300);

        table.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(100);

        table.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(100);

        table.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(120);

        JScrollPane scrollPane =
                new JScrollPane(table);

        add(
                scrollPane,
                "grow"
        );

        // ==========================================
        // EVENEMENTS
        // ==========================================

        btnRechercher.addActionListener(
                e -> rechercher()
        );

        btnActualiser.addActionListener(
                e -> {

                    txtRecherche.setText("");

                    chargerProduits();
                }
        );

        txtRecherche.addActionListener(
                e -> rechercher()
        );

        // Double clic = modification
        table.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        if (e.getClickCount() == 2
                                && SwingUtilities
                                .isLeftMouseButton(e)) {

                            modifierSelection();
                        }
                    }
                }
        );
    }

    /**
     * Charger tous les produits.
     */
    private void chargerProduits() {

        try {

            List<Produit> produits =
                    produitService.findAll();

            afficherProduits(
                    produits
            );

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors du chargement des produits.",
                    e
            );
        }
    }

    /**
     * Afficher les produits dans la table.
     */
    private void afficherProduits(
            List<Produit> produits) {

        tableModel.setRowCount(0);

        for (Produit produit : produits) {

            String etat =
                    obtenirEtat(produit);

            tableModel.addRow(
                    new Object[]{
                            produit.getIdProduit(),

                            valeur(
                                    produit.getDesignation()
                            ),

                            valeur(
                                    produit.getDescription()
                            ),

                            produit.getStockActuel(),

                            produit.getStockAlerte(),

                            etat
                    }
            );
        }
    }

    /**
     * Déterminer l'état du stock.
     */
    private String obtenirEtat(
            Produit produit) {

        if (produit.getStockActuel() == 0) {

            return "RUPTURE";
        }

        if (produit.getStockActuel()
                <= produit.getStockAlerte()) {

            return "ALERTE";
        }

        return "NORMAL";
    }

    /**
     * Rechercher un produit.
     */
    private void rechercher() {

        try {

            String motCle =
                    txtRecherche
                            .getText()
                            .trim();

            List<Produit> produits;

            if (motCle.isEmpty()) {

                produits =
                        produitService.findAll();

            } else {

                produits =
                        produitService.rechercher(
                                motCle
                        );
            }

            afficherProduits(
                    produits
            );

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la recherche.",
                    e
            );
        }
    }

    /**
     * Ouvrir le formulaire d'ajout.
     */
    private void ouvrirAjout() {

        ProduitForm form =
                new ProduitForm();

        afficherModal(
                "Ajouter un produit",
                form
        );
    }

    /**
     * Modifier le produit sélectionné.
     */
    private void modifierSelection() {

        int ligne =
                table.getSelectedRow();

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

            int id =
                    Integer.parseInt(
                            tableModel
                                    .getValueAt(
                                            ligne,
                                            0
                                    )
                                    .toString()
                    );

            Produit produit =
                    produitService.findById(id);

            if (produit == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Produit introuvable.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            ProduitForm form =
                    new ProduitForm(
                            produit
                    );

            afficherModal(
                    "Modifier le produit",
                    form
            );

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la modification.",
                    e
            );
        }
    }

    /**
     * Afficher le formulaire dans un JDialog.
     */
    private void afficherModal(
            String titre,
            ProduitForm form) {

        Window parent =
                SwingUtilities
                        .getWindowAncestor(this);

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
                600,
                500
        );

        dialog.setLocationRelativeTo(this);

        dialog.setResizable(false);

        dialog.setVisible(true);

        // Actualiser après fermeture
        chargerProduits();
    }

    /**
     * Afficher une erreur.
     */
    private void afficherErreur(
            String message,
            Exception e) {

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

    /**
     * Eviter null.
     */
    private String valeur(
            String valeur) {

        return valeur == null
                ? ""
                : valeur;
    }
}