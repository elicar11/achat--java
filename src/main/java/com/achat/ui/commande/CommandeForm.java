package com.achat.ui.commande;

import com.achat.model.Commande;
import com.achat.model.Fournisseur;
import com.achat.model.LigneCommande;
import com.achat.model.Produit;

import com.achat.service.CommandeService;
import com.achat.service.FournisseurService;
import com.achat.service.ProduitService;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulaire d'ajout et de modification d'une commande.
 */
public class CommandeForm extends JPanel {

    // =========================================================
    // COULEURS
    // =========================================================

    private static final Color WHITE =
            Color.WHITE;

    private static final Color BLACK =
            new Color(18, 18, 18);

    private static final Color BACKGROUND =
            new Color(246, 246, 246);

    private static final Color BORDER =
            new Color(220, 220, 220);

    private static final Color GRAY =
            new Color(120, 120, 120);

    private static final Color RED =
            new Color(190, 50, 50);

    // =========================================================
    // SERVICES
    // =========================================================

    private final CommandeService commandeService;

    private final FournisseurService fournisseurService;

    private final ProduitService produitService;

    // =========================================================
    // COMPOSANTS
    // =========================================================

    private JComboBox<FournisseurItem>
            comboFournisseur;

    private JTextField txtDate;

    private JComboBox<String>
            comboEtat;

    private JTable tableLignes;

    private DefaultTableModel
            ligneTableModel;

    private JLabel lblTotal;

    private JDialog dialog;

    // =========================================================
    // DONNEES
    // =========================================================

    private final List<LigneCommande>
            lignes;

    private int idModification = -1;

    // =========================================================
    // CONSTRUCTEUR AJOUT
    // =========================================================

    public CommandeForm() {

        commandeService =
                new CommandeService();

        fournisseurService =
                new FournisseurService();

        produitService =
                new ProduitService();

        lignes =
                new ArrayList<>();

        construireInterface();

        chargerFournisseurs();

        txtDate.setText(
                LocalDate.now().toString()
        );

        comboEtat.setSelectedItem(
                "En cours"
        );
    }

    // =========================================================
    // CONSTRUCTEUR MODIFICATION
    // =========================================================

    public CommandeForm(
            Commande commande
    ) {

        this();

        chargerCommande(
                commande
        );
    }

    // =========================================================
    // DIALOG
    // =========================================================

    public void setDialog(
            JDialog dialog
    ) {

        this.dialog = dialog;
    }

    // =========================================================
    // CONSTRUCTION
    // =========================================================

    private void construireInterface() {

        setLayout(
                new BorderLayout()
        );

        setBackground(
                BACKGROUND
        );

        // =====================================================
        // HEADER
        // =====================================================

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setBackground(
                BLACK
        );

        header.setBorder(
                new EmptyBorder(
                        18,
                        22,
                        18,
                        22
                )
        );

        JLabel titre =
                new JLabel(
                        "Nouvelle commande"
                );

        titre.setForeground(
                WHITE
        );

        titre.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        20
                )
        );

        header.add(
                titre,
                BorderLayout.WEST
        );

        add(
                header,
                BorderLayout.NORTH
        );

        // =====================================================
        // CONTENU
        // =====================================================

        JPanel content =
                new JPanel(
                        new BorderLayout(
                                0,
                                15
                        )
                );

        content.setBackground(
                WHITE
        );

        content.setBorder(
                new EmptyBorder(
                        22,
                        25,
                        22,
                        25
                )
        );

        // =====================================================
        // INFORMATIONS COMMANDE
        // =====================================================

        JPanel informations =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[120!][grow][120!][grow]",
                                "[][12][]"
                        )
                );

        informations.setOpaque(
                false
        );

        // -----------------------------------------------------
        // FOURNISSEUR
        // -----------------------------------------------------

        JLabel lblFournisseur =
                createLabel(
                        "Fournisseur"
                );

        informations.add(
                lblFournisseur
        );

        comboFournisseur =
                new JComboBox<>();

        comboFournisseur.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        comboFournisseur.setPreferredSize(
                new Dimension(
                        0,
                        42
                )
        );

        informations.add(
                comboFournisseur,
                "growx"
        );

        // -----------------------------------------------------
        // DATE
        // -----------------------------------------------------

        JLabel lblDate =
                createLabel(
                        "Date"
                );

        informations.add(
                lblDate
        );

        txtDate =
                new JTextField();

        txtDate.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        txtDate.setPreferredSize(
                new Dimension(
                        0,
                        42
                )
        );

        informations.add(
                txtDate,
                "growx, wrap"
        );

        // -----------------------------------------------------
        // ETAT
        // -----------------------------------------------------

        JLabel lblEtat =
                createLabel(
                        "État"
                );

        informations.add(
                lblEtat
        );

        comboEtat =
                new JComboBox<>(
                        new String[]{
                                "En cours",
                                "Soldée"
                        }
                );

        comboEtat.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        comboEtat.setPreferredSize(
                new Dimension(
                        0,
                        42
                )
        );

        informations.add(
                comboEtat,
                "growx"
        );

        informations.add(
                new JLabel()
        );

        informations.add(
                new JLabel(),
                "growx"
        );

        content.add(
                informations,
                BorderLayout.NORTH
        );

        // =====================================================
        // LIGNES DE COMMANDE
        // =====================================================

        JPanel lignesPanel =
                new JPanel(
                        new BorderLayout(
                                0,
                                10
                        )
                );

        lignesPanel.setOpaque(
                false
        );

        // -----------------------------------------------------
        // TITRE
        // -----------------------------------------------------

        JPanel lignesHeader =
                new JPanel(
                        new BorderLayout()
                );

        lignesHeader.setOpaque(
                false
        );

        JLabel titreLignes =
                new JLabel(
                        "Produits commandés"
                );

        titreLignes.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                )
        );

        titreLignes.setForeground(
                BLACK
        );

        lignesHeader.add(
                titreLignes,
                BorderLayout.WEST
        );

        // -----------------------------------------------------
        // BOUTONS LIGNES
        // -----------------------------------------------------

        JPanel boutonsLignes =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                6,
                                0
                        )
                );

        boutonsLignes.setOpaque(
                false
        );

        JButton btnAjouterLigne =
                createBlackSmallButton(
                        "+ Ajouter produit"
                );

        JButton btnSupprimerLigne =
                createWhiteSmallButton(
                        "Supprimer"
                );

        btnAjouterLigne.addActionListener(
                e -> ajouterLigne()
        );

        btnSupprimerLigne.addActionListener(
                e -> supprimerLigne()
        );

        boutonsLignes.add(
                btnAjouterLigne
        );

        boutonsLignes.add(
                btnSupprimerLigne
        );

        lignesHeader.add(
                boutonsLignes,
                BorderLayout.EAST
        );

        lignesPanel.add(
                lignesHeader,
                BorderLayout.NORTH
        );

        // =====================================================
        // TABLE LIGNES
        // =====================================================

        String[] colonnes = {
                "Produit",
                "Quantité",
                "Prix unitaire",
                "Total"
        };

        ligneTableModel =
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

        tableLignes =
                new JTable(
                        ligneTableModel
                );

        tableLignes.setRowHeight(
                42
        );

        tableLignes.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        tableLignes.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tableLignes.setShowVerticalLines(
                false
        );

        tableLignes.setShowHorizontalLines(
                true
        );

        tableLignes.setGridColor(
                new Color(
                        235,
                        235,
                        235
                )
        );

        tableLignes
                .getTableHeader()
                .setReorderingAllowed(
                        false
                );

        tableLignes
                .getTableHeader()
                .setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                13
                        )
                );

        tableLignes
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                42
                        )
                );

        // -----------------------------------------------------
        // LARGEURS
        // -----------------------------------------------------

        tableLignes
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(
                        350
                );

        tableLignes
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(
                        120
                );

        tableLignes
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(
                        160
                );

        tableLignes
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(
                        160
                );

        // -----------------------------------------------------
        // CENTRAGE
        // -----------------------------------------------------

        DefaultTableCellRenderer center =
                new DefaultTableCellRenderer();

        center.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        tableLignes
                .getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        center
                );

        tableLignes
                .getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        center
                );

        tableLignes
                .getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        center
                );

        // -----------------------------------------------------
        // SCROLLPANE
        // -----------------------------------------------------

        JScrollPane scrollPane =
                new JScrollPane(
                        tableLignes
                );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );

        scrollPane
                .getViewport()
                .setBackground(
                        WHITE
                );

        lignesPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        content.add(
                lignesPanel,
                BorderLayout.CENTER
        );

        // =====================================================
        // BAS
        // =====================================================

        JPanel bottom =
                new JPanel(
                        new BorderLayout()
                );

        bottom.setOpaque(
                false
        );

        // -----------------------------------------------------
        // TOTAL
        // -----------------------------------------------------

        lblTotal =
                new JLabel(
                        "Total : 0.00 Ar"
                );

        lblTotal.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        18
                )
        );

        lblTotal.setForeground(
                BLACK
        );

        bottom.add(
                lblTotal,
                BorderLayout.WEST
        );

        // -----------------------------------------------------
        // BOUTONS
        // -----------------------------------------------------

        JPanel boutons =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        boutons.setOpaque(
                false
        );

        JButton btnAnnuler =
                createWhiteButton(
                        "Annuler",
                        110
                );

        JButton btnEnregistrer =
                createBlackButton(
                        "Enregistrer"
                );

        btnEnregistrer.setPreferredSize(
                new Dimension(
                        140,
                        46
                )
        );

        btnAnnuler.addActionListener(
                e -> fermer()
        );

        btnEnregistrer.addActionListener(
                e -> enregistrer()
        );

        boutons.add(
                btnAnnuler
        );

        boutons.add(
                btnEnregistrer
        );

        bottom.add(
                boutons,
                BorderLayout.EAST
        );

        content.add(
                bottom,
                BorderLayout.SOUTH
        );

        add(
                content,
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // CHARGER FOURNISSEURS
    // =========================================================

    private void chargerFournisseurs() {

        try {

            List<Fournisseur>
                    fournisseurs =
                    fournisseurService
                            .findAll();

            comboFournisseur
                    .removeAllItems();

            for (Fournisseur fournisseur :
                    fournisseurs) {

                comboFournisseur.addItem(
                        new FournisseurItem(
                                fournisseur
                        )
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Impossible de charger les fournisseurs.\n"
                            + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // CHARGER COMMANDE
    // =========================================================

    private void chargerCommande(
            Commande commande
    ) {

        if (commande == null) {
            return;
        }

        idModification =
                commande.getIdCommande();

        /*
         * Date.
         */
        if (commande.getDateCommande()
                != null) {

            txtDate.setText(
                    commande
                            .getDateCommande()
                            .toString()
            );
        }

        /*
         * Etat.
         */
        if (commande.getEtatCommande()
                != null) {

            comboEtat.setSelectedItem(
                    commande.getEtatCommande()
            );
        }

        /*
         * Fournisseur.
         */
        for (int i = 0;
             i < comboFournisseur
                     .getItemCount();
             i++) {

            FournisseurItem item =
                    comboFournisseur
                            .getItemAt(i);

            if (item.getFournisseur()
                    .getIdFournisseur()
                    ==
                    commande
                            .getIdFournisseur()) {

                comboFournisseur
                        .setSelectedIndex(i);

                break;
            }
        }

        /*
         * Charger les lignes existantes.
         */
        try {

            List<LigneCommande>
                    anciennesLignes =
                    commandeService
                            .findLignes(
                                    idModification
                            );

            lignes.clear();

            lignes.addAll(
                    anciennesLignes
            );

            afficherLignes();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Impossible de charger les lignes de la commande.\n"
                            + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        /*
         * Pour une modification, on change le titre.
         */
        Component header =
                getComponent(
                        0
                );

        if (header instanceof JPanel) {

            JPanel headerPanel =
                    (JPanel) header;

            Component[] composants =
                    headerPanel
                            .getComponents();

            if (composants.length > 0
                    && composants[0]
                    instanceof JLabel) {

                ((JLabel) composants[0])
                        .setText(
                                "Modifier la commande"
                        );
            }
        }
    }

    // =========================================================
    // AJOUTER UNE LIGNE
    // =========================================================

    private void ajouterLigne() {

        /*
         * On récupère tous les produits.
         */
        try {

            List<Produit> produits =
                    produitService.findAll();

            if (produits.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Aucun produit disponible.\n"
                                + "Ajoutez d'abord un produit.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            LigneCommandeDialog dialog =
                    new LigneCommandeDialog(
                            produits
                    );

            dialog.setVisible(true);

            if (dialog.isValide()) {

                Produit produit =
                        dialog.getProduit();

                int quantite =
                        dialog.getQuantite();

                /*
                 * Vérifier que le produit
                 * n'est pas déjà présent.
                 */
                for (LigneCommande ligne :
                        lignes) {

                    if (ligne
                            .getIdProduit()
                            == produit
                            .getIdProduit()) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Ce produit est déjà présent dans la commande.",
                                "Information",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        return;
                    }
                }

                LigneCommande ligne =
                        new LigneCommande();

                /*
                 * En ajout, le prix sera récupéré
                 * automatiquement par CommandeService
                 * depuis PROPOSER.
                 */
                ligne.setIdProduit(
                        produit.getIdProduit()
                );

                ligne.setQuantiteCommandee(
                        quantite
                );

                ligne.setPrixUnitaireAchat(
                        0
                );

                lignes.add(
                        ligne
                );

                afficherLignes();
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erreur lors de l'ajout du produit.\n"
                            + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // SUPPRIMER UNE LIGNE
    // =========================================================

    private void supprimerLigne() {

        int row =
                tableLignes
                        .getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Sélectionnez une ligne à supprimer.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Supprimer cette ligne ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmation
                != JOptionPane.YES_OPTION) {

            return;
        }

        lignes.remove(
                row
        );

        afficherLignes();
    }

    // =========================================================
    // AFFICHER LES LIGNES
    // =========================================================

    private void afficherLignes() {

        ligneTableModel
                .setRowCount(0);

        double total = 0;

        for (LigneCommande ligne :
                lignes) {

            String nomProduit =
                    obtenirNomProduit(
                            ligne.getIdProduit()
                    );

            double prix =
                    ligne
                            .getPrixUnitaireAchat();

            double montant =
                    ligne
                            .getMontantTotal();

            total += montant;

            /*
             * Si le prix est encore 0,
             * on affiche "Automatique".
             */
            String prixAffichage;

            String totalAffichage;

            if (prix <= 0) {

                prixAffichage =
                        "Automatique";

                totalAffichage =
                        "Automatique";

            } else {

                prixAffichage =
                        String.format(
                                "%.2f Ar",
                                prix
                        );

                totalAffichage =
                        String.format(
                                "%.2f Ar",
                                montant
                        );
            }

            ligneTableModel.addRow(
                    new Object[]{
                            nomProduit,
                            ligne
                                    .getQuantiteCommandee(),
                            prixAffichage,
                            totalAffichage
                    }
            );
        }

        /*
         * En mode ajout, le prix est encore inconnu.
         */
        if (!lignes.isEmpty()
                && lignes.stream()
                .anyMatch(
                        l -> l
                                .getPrixUnitaireAchat()
                                <= 0
                )) {

            lblTotal.setText(
                    "Total : calculé à l'enregistrement"
            );

        } else {

            lblTotal.setText(
                    String.format(
                            "Total : %.2f Ar",
                            total
                    )
            );
        }
    }

    // =========================================================
    // NOM PRODUIT
    // =========================================================

    private String obtenirNomProduit(
            int idProduit
    ) {

        try {

            List<Produit> produits =
                    produitService.findAll();

            for (Produit produit :
                    produits) {

                if (produit
                        .getIdProduit()
                        == idProduit) {

                    return produit
                            .getDesignation();
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "Erreur produit : "
                            + e.getMessage()
            );
        }

        return "Produit #"
                + idProduit;
    }

    // =========================================================
    // ENREGISTRER
    // =========================================================

    private void enregistrer() {

        try {

            // =================================================
            // FOURNISSEUR
            // =================================================

            FournisseurItem item =
                    (FournisseurItem)
                            comboFournisseur
                                    .getSelectedItem();

            if (item == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Veuillez sélectionner un fournisseur.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // =================================================
            // DATE
            // =================================================

            String dateText =
                    txtDate
                            .getText()
                            .trim();

            if (dateText.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "La date est obligatoire.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE
                );

                txtDate.requestFocus();

                return;
            }

            LocalDate date;

            try {

                date =
                        LocalDate.parse(
                                dateText
                        );

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Format de date invalide.\n"
                                + "Utilisez : AAAA-MM-JJ",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE
                );

                txtDate.requestFocus();

                return;
            }

            // =================================================
            // ETAT
            // =================================================

            String etat =
                    comboEtat
                            .getSelectedItem()
                            .toString();

            // =================================================
            // LIGNES
            // =================================================

            if (lignes.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Ajoutez au moins un produit à la commande.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // =================================================
            // COMMANDE
            // =================================================

            Commande commande =
                    new Commande();

            commande.setDateCommande(
                    date
            );

            commande.setEtatCommande(
                    etat
            );

            commande.setIdFournisseur(
                    item.getFournisseur()
                            .getIdFournisseur()
            );

            // =================================================
            // AJOUT
            // =================================================

            if (idModification == -1) {

                commandeService.ajouter(
                        commande,
                        lignes
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Commande ajoutée avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                // =============================================
                // MODIFICATION
                // =============================================

                commande.setIdCommande(
                        idModification
                );

                commandeService.modifier(
                        commande
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Commande modifiée avec succès.",
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

            e.printStackTrace();
        }
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
    // LABEL
    // =========================================================

    private JLabel createLabel(
            String text
    ) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        label.setForeground(
                BLACK
        );

        return label;
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

        button.setForeground(
                WHITE
        );

        button.setBackground(
                BLACK
        );

        button.setFocusPainted(
                false
        );

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
                        150,
                        46
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
    // PETIT BOUTON NOIR
    // =========================================================

    private JButton createBlackSmallButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        button.setForeground(
                WHITE
        );

        button.setBackground(
                BLACK
        );

        button.setFocusPainted(
                false
        );

        button.setPreferredSize(
                new Dimension(
                        145,
                        40
                )
        );

        return button;
    }

    // =========================================================
    // PETIT BOUTON BLANC
    // =========================================================

    private JButton createWhiteSmallButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        button.setForeground(
                BLACK
        );

        button.setBackground(
                WHITE
        );

        button.setFocusPainted(
                false
        );

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
                        100,
                        40
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

        button.setForeground(
                BLACK
        );

        button.setBackground(
                WHITE
        );

        button.setFocusPainted(
                false
        );

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
                        46
                )
        );

        return button;
    }

    // =========================================================
    // FOURNISSEUR ITEM
    // =========================================================

    private static class FournisseurItem {

        private final Fournisseur fournisseur;

        public FournisseurItem(
                Fournisseur fournisseur
        ) {

            this.fournisseur =
                    fournisseur;
        }

        public Fournisseur getFournisseur() {

            return fournisseur;
        }

        @Override
        public String toString() {

            if (fournisseur == null) {

                return "";
            }

            return "Fournisseur #"
                    + fournisseur
                    .getIdFournisseur()
                    + "  •  "
                    + fournisseur
                    .getTypeFournisseur();
        }
    }

    // =========================================================
    // DIALOG AJOUT LIGNE
    // =========================================================

    private static class LigneCommandeDialog
            extends JDialog {

        private JComboBox<Produit>
                comboProduit;

        private JSpinner
                spinnerQuantite;

        private boolean valide =
                false;

        public LigneCommandeDialog(
                List<Produit> produits
        ) {

            setTitle(
                    "Ajouter un produit"
            );

            setModal(true);

            setSize(
                    480,
                    300
            );

            setResizable(false);

            setLocationRelativeTo(null);

            setLayout(
                    new BorderLayout()
            );

            // =================================================
            // HEADER
            // =================================================

            JPanel header =
                    new JPanel(
                            new BorderLayout()
                    );

            header.setBackground(
                    BLACK
            );

            header.setBorder(
                    new EmptyBorder(
                            15,
                            20,
                            15,
                            20
                    )
            );

            JLabel titre =
                    new JLabel(
                            "Ajouter un produit"
                    );

            titre.setForeground(
                    WHITE
            );

            titre.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            17
                    )
            );

            header.add(
                    titre,
                    BorderLayout.WEST
            );

            add(
                    header,
                    BorderLayout.NORTH
            );

            // =================================================
            // FORMULAIRE
            // =================================================

            JPanel form =
                    new JPanel(
                            new MigLayout(
                                    "fillx, insets 22",
                                    "[110!][grow]",
                                    "[][15][]"
                            )
                    );

            form.setBackground(
                    WHITE
            );

            JLabel lblProduit =
                    new JLabel(
                            "Produit"
                    );

            comboProduit =
                    new JComboBox<>();

            comboProduit.setFont(
                    new Font(
                            "SansSerif",
                            Font.PLAIN,
                            13
                    )
            );

            for (Produit produit :
                    produits) {

                comboProduit
                        .addItem(
                                produit
                        );
            }

            form.add(
                    lblProduit
            );

            form.add(
                    comboProduit,
                    "growx, wrap"
            );

            JLabel lblQuantite =
                    new JLabel(
                            "Quantité"
                    );

            spinnerQuantite =
                    new JSpinner(
                            new SpinnerNumberModel(
                                    1,
                                    1,
                                    1000000,
                                    1
                            )
                    );

            spinnerQuantite
                    .setPreferredSize(
                            new Dimension(
                                    0,
                                    40
                            )
                    );

            form.add(
                    lblQuantite
            );

            form.add(
                    spinnerQuantite,
                    "growx"
            );

            add(
                    form,
                    BorderLayout.CENTER
            );

            // =================================================
            // BOUTONS
            // =================================================

            JPanel buttons =
                    new JPanel(
                            new FlowLayout(
                                    FlowLayout.RIGHT,
                                    10,
                                    10
                            )
                    );

            buttons.setBackground(
                    WHITE
            );

            JButton btnAnnuler =
                    new JButton(
                            "Annuler"
                    );

            JButton btnAjouter =
                    new JButton(
                            "Ajouter"
                    );

            btnAnnuler.setFocusPainted(
                    false
            );

            btnAjouter.setFocusPainted(
                    false
            );

            btnAjouter.setForeground(
                    WHITE
            );

            btnAjouter.setBackground(
                    BLACK
            );

            btnAnnuler.addActionListener(
                    e -> dispose()
            );

            btnAjouter.addActionListener(
                    e -> {

                        if (comboProduit
                                .getSelectedItem()
                                == null) {

                            return;
                        }

                        valide = true;

                        dispose();
                    }
            );

            buttons.add(
                    btnAnnuler
            );

            buttons.add(
                    btnAjouter
            );

            add(
                    buttons,
                    BorderLayout.SOUTH
            );
        }

        public boolean isValide() {

            return valide;
        }

        public Produit getProduit() {

            return (Produit)
                    comboProduit
                            .getSelectedItem();
        }

        public int getQuantite() {

            return (Integer)
                    spinnerQuantite
                            .getValue();
        }
    }
}