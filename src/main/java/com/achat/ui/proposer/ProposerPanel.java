package com.achat.ui.proposer;

import com.achat.model.Fournisseur;
import com.achat.model.Personne;
import com.achat.model.Produit;
import com.achat.model.Proposer;
import com.achat.model.Societe;
import com.achat.service.FournisseurService;
import com.achat.service.PersonneService;
import com.achat.service.ProduitService;
import com.achat.service.ProposerService;
import com.achat.service.SocieteService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface de gestion des propositions Fournisseur -> Produit.
 *
 * Fonctionnalités :
 * - Choisir un fournisseur
 * - Choisir un produit
 * - Définir un prix d'achat spécifique
 * - Ajouter une proposition
 * - Modifier une proposition
 * - Supprimer une proposition
 */
public class ProposerPanel extends JPanel {

    private final FournisseurService fournisseurService;
    private final PersonneService personneService;
    private final SocieteService societeService;
    private final ProduitService produitService;
    private final ProposerService proposerService;

    private JComboBox<FournisseurItem> comboFournisseur;
    private JComboBox<ProduitItem> comboProduit;
    private JTextField txtPrix;

    private JTable table;
    private DefaultTableModel tableModel;

    private JButton btnAjouter;
    private JButton btnModifier;
    private JButton btnSupprimer;
    private JButton btnActualiser;

    private JLabel lblPagination;
    private JButton btnPrecedent;
    private JButton btnSuivant;
    private List<Proposer> propositionsCourantes = new ArrayList<>();
    private int pageActuelle = 1;
    private static final int TAILLE_PAGE = 8;

    private static final Color PRIMARY = new Color(25, 25, 25);
    private static final Color WHITE = Color.WHITE;
    private static final Color LIGHT_BG = new Color(248, 249, 251);
    private static final Color BORDER = new Color(225, 228, 232);
    private static final Color TEXT = new Color(35, 38, 42);
    private static final Color GRAY = new Color(110, 115, 120);
    private static final Color RED = new Color(190, 60, 60);

    public ProposerPanel() {

        fournisseurService = new FournisseurService();
        personneService = new PersonneService();
        societeService = new SocieteService();
        produitService = new ProduitService();
        proposerService = new ProposerService();

        setLayout(new BorderLayout());

        setBackground(LIGHT_BG);

        creerInterface();
        chargerFournisseurs();
        chargerProduits();
        chargerPropositions();
    }

    private void creerInterface() {

        JPanel mainPanel = new JPanel(
                new BorderLayout(0, 20)
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
                new MigLayout(
                        "insets 0",
                        "[grow][]"
                )
        );
        headerPanel.setOpaque(false);

        JPanel titresPanel = new JPanel(
                new MigLayout("insets 0", "[grow]")
        );
        titresPanel.setOpaque(false);

        JLabel lblTitre = new JLabel("Propositions fournisseur");
        lblTitre.setFont(
                new Font("SansSerif", Font.BOLD, 28)
        );
        lblTitre.setForeground(TEXT);

        JLabel lblSousTitre = new JLabel(
                "Gestion des prix d'achat spécifiques par fournisseur"
        );
        lblSousTitre.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );
        lblSousTitre.setForeground(GRAY);

        titresPanel.add(lblTitre, "wrap");
        titresPanel.add(lblSousTitre);

        btnActualiser = creerBoutonSecondaire("Actualiser");
        btnActualiser.addActionListener(e -> actualiser());

        headerPanel.add(titresPanel, "growx");
        headerPanel.add(btnActualiser, "right");

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        // ============================================================
        // CONTENU (formulaire + tableau)
        // ============================================================

        JPanel contenuPanel = new JPanel(
                new BorderLayout(0, 15)
        );

        contenuPanel.setOpaque(false);

        // ------------------------------------------------------------
        // FORMULAIRE
        // ------------------------------------------------------------

        JPanel formPanel = new JPanel(
                new MigLayout(
                        "fill, insets 18",
                        "[][grow]20[][grow]20[][grow][]",
                        "[]"
                )
        );

        formPanel.setBackground(WHITE);
        formPanel.setBorder(
                new RoundedBorder(BORDER, 1, 12)
        );

        JLabel lblFournisseur = new JLabel("Fournisseur");
        lblFournisseur.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        lblFournisseur.setForeground(TEXT);

        comboFournisseur = new JComboBox<>();
        comboFournisseur.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        comboFournisseur.setPreferredSize(
                new Dimension(220, 40)
        );

        JLabel lblProduit = new JLabel("Produit");
        lblProduit.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        lblProduit.setForeground(TEXT);

        comboProduit = new JComboBox<>();
        comboProduit.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        comboProduit.setPreferredSize(
                new Dimension(220, 40)
        );

        JLabel lblPrix = new JLabel("Prix d'achat");
        lblPrix.setFont(
                new Font("SansSerif", Font.BOLD, 13)
        );
        lblPrix.setForeground(TEXT);

        txtPrix = new JTextField();
        txtPrix.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        txtPrix.setPreferredSize(
                new Dimension(150, 40)
        );
        txtPrix.putClientProperty(
                "JTextField.placeholderText",
                "Ex. 25 000"
        );

        btnAjouter = creerBoutonPrincipal("+ Ajouter");
        btnAjouter.addActionListener(e -> ajouterProposition());

        formPanel.add(lblFournisseur);
        formPanel.add(comboFournisseur, "growx");
        formPanel.add(lblProduit);
        formPanel.add(comboProduit, "growx");
        formPanel.add(lblPrix);
        formPanel.add(txtPrix, "growx");
        formPanel.add(btnAjouter, "h 40!");

        contenuPanel.add(
                formPanel,
                BorderLayout.NORTH
        );

        // ------------------------------------------------------------
        // TABLEAU
        // ------------------------------------------------------------

        creerTableau();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                new RoundedBorder(BORDER, 1, 14)
        );
        scrollPane.getViewport().setBackground(WHITE);
        scrollPane.setBackground(WHITE);

        JPanel tablePanel = new JPanel(
                new MigLayout(
                        "fill, insets 0",
                        "[grow]",
                        "[grow]15[]"
                )
        );
        tablePanel.setOpaque(false);

        tablePanel.add(scrollPane, "grow, push, wrap");

        JPanel pagination = new JPanel(new BorderLayout());
        pagination.setOpaque(false);
        pagination.setBorder(
                BorderFactory.createEmptyBorder(12, 4, 0, 4)
        );

        lblPagination = new JLabel("Page 1 / 1");
        lblPagination.setFont(
                new Font("SansSerif", Font.PLAIN, 13)
        );
        lblPagination.setForeground(GRAY);

        JPanel boutonsPagination = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        boutonsPagination.setOpaque(false);

        btnPrecedent = creerBoutonPagination("‹ Précédent");
        btnSuivant = creerBoutonPagination("Suivant ›");

        btnPrecedent.addActionListener(e -> pagePrecedente());
        btnSuivant.addActionListener(e -> pageSuivante());

        boutonsPagination.add(btnPrecedent);
        boutonsPagination.add(btnSuivant);

        pagination.add(lblPagination, BorderLayout.WEST);
        pagination.add(boutonsPagination, BorderLayout.EAST);

        contenuPanel.add(pagination, BorderLayout.SOUTH);

        contenuPanel.add(
                tablePanel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                contenuPanel,
                BorderLayout.CENTER
        );

        add(
                mainPanel,
                BorderLayout.CENTER
        );
    }

    // ================================================================
    // TABLEAU
    // ================================================================

    private void creerTableau() {

        String[] colonnes = {
                "FOURNISSEUR",
                "PRODUIT",
                "PRIX D'ACHAT SPÉCIFIQUE",
                "ACTIONS"
        };

        tableModel = new DefaultTableModel(colonnes, 0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return column == 3;
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

        table.getColumnModel().getColumn(0)
                .setPreferredWidth(220);

        table.getColumnModel().getColumn(1)
                .setPreferredWidth(300);

        table.getColumnModel().getColumn(2)
                .setPreferredWidth(220);

        table.getColumnModel().getColumn(3)
                .setPreferredWidth(110);

        table.getColumnModel().getColumn(2)
                .setCellRenderer(new PrixRenderer());

        table.getColumnModel().getColumn(3)
                .setPreferredWidth(125);

        table.getColumnModel().getColumn(3)
                .setCellRenderer(new ActionsRenderer());

        table.getColumnModel().getColumn(3)
                .setCellEditor(new ActionsEditor());

        // Double-clic = modification
        table.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
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
    // CHARGEMENT
    // ================================================================

    private void chargerFournisseurs() {

        try {

            comboFournisseur.removeAllItems();

            List<Fournisseur> fournisseurs =
                    fournisseurService.findAll();

            for (Fournisseur fournisseur : fournisseurs) {

                comboFournisseur.addItem(
                        new FournisseurItem(
                                fournisseur,
                                obtenirNomFournisseur(fournisseur)
                        )
                );
            }

        } catch (Exception e) {
            afficherErreur(
                    "Erreur lors du chargement des fournisseurs.",
                    e
            );
        }
    }

    /**
     * Résout le nom d'affichage d'un fournisseur
     * (nom + prénom pour une personne, raison sociale
     * pour une société) au lieu de son type brut.
     */
    private String obtenirNomFournisseur(Fournisseur fournisseur) {

        if (fournisseur == null) {
            return "";
        }

        try {

            int id = fournisseur.getIdFournisseur();

            if ("PERSONNE".equals(fournisseur.getTypeFournisseur())) {

                Personne personne =
                        personneService.findByFournisseur(id);

                if (personne != null) {
                    return (personne.getNom() == null ? "" : personne.getNom())
                            + " "
                            + (personne.getPrenom() == null ? "" : personne.getPrenom());
                }

            } else {

                Societe societe =
                        societeService.findByFournisseur(id);

                if (societe != null) {
                    return societe.getRaisonSociale() == null
                            ? ""
                            : societe.getRaisonSociale();
                }
            }

        } catch (Exception e) {
            System.err.println(
                    "Erreur récupération nom fournisseur : "
                            + e.getMessage()
            );
        }

        return "Fournisseur #" + fournisseur.getIdFournisseur();
    }

    private void chargerProduits() {

        try {

            comboProduit.removeAllItems();

            List<Produit> produits =
                    produitService.findAll();

            for (Produit produit : produits) {

                comboProduit.addItem(
                        new ProduitItem(produit)
                );
            }

        } catch (Exception e) {
            afficherErreur(
                    "Erreur lors du chargement des produits.",
                    e
            );
        }
    }

    private void chargerPropositions() {

        try {
            propositionsCourantes = new ArrayList<>(
                    proposerService.findAll()
            );
            pageActuelle = 1;
            afficherPage();

        } catch (Exception e) {
            afficherErreur(
                    "Erreur lors du chargement des propositions.",
                    e
            );
        }
    }

    private void afficherPage() {

        tableModel.setRowCount(0);

        int total = propositionsCourantes.size();
        int totalPages = Math.max(
                1,
                (int) Math.ceil(total / (double) TAILLE_PAGE)
        );

        if (pageActuelle > totalPages) {
            pageActuelle = totalPages;
        }

        int debut = (pageActuelle - 1) * TAILLE_PAGE;
        int fin = Math.min(
                debut + TAILLE_PAGE,
                total
        );

        for (int i = debut; i < fin; i++) {

            Proposer proposition = propositionsCourantes.get(i);

            Fournisseur fournisseur = null;
            Produit produit = null;

            try {
                fournisseur = fournisseurService.findById(
                        proposition.getIdFournisseur()
                );

                produit = produitService.findById(
                        proposition.getIdProduit()
                );
            } catch (java.sql.SQLException e) {
                // Si un fournisseur ou produit ne peut pas être chargé,
                // on conserve au moins son identifiant dans le tableau.
                System.err.println(
                        "Erreur lors du chargement de la proposition : "
                                + e.getMessage()
                );
            }

            String fournisseurTexte =
                    fournisseur == null
                            ? "#" + proposition.getIdFournisseur()
                            : "#" + fournisseur.getIdFournisseur()
                            + " - "
                            + obtenirNomFournisseur(fournisseur);

            String produitTexte =
                    produit == null
                            ? "#" + proposition.getIdProduit()
                            : "#" + produit.getIdProduit()
                            + " - "
                            + produit.getDesignation();

            tableModel.addRow(new Object[]{
                    fournisseurTexte,
                    produitTexte,
                    proposition.getPrixAchatSpecifique(),
                    ""
            });
        }

        String mot = total <= 1 ? "proposition" : "propositions";
        lblPagination.setText(
                "Page " + pageActuelle
                        + " / " + totalPages
                        + " (" + total + " " + mot + ")"
        );

        btnPrecedent.setEnabled(pageActuelle > 1);
        btnSuivant.setEnabled(pageActuelle < totalPages);
    }

    private void pagePrecedente() {
        if (pageActuelle > 1) {
            pageActuelle--;
            afficherPage();
        }
    }

    private void pageSuivante() {
        int totalPages = Math.max(
                1,
                (int) Math.ceil(
                        propositionsCourantes.size()
                                / (double) TAILLE_PAGE
                )
        );

        if (pageActuelle < totalPages) {
            pageActuelle++;
            afficherPage();
        }
    }

    // ================================================================
    // AJOUT
    // ================================================================

    private void ajouterProposition() {

        FournisseurItem fournisseur =
                (FournisseurItem) comboFournisseur.getSelectedItem();

        ProduitItem produit =
                (ProduitItem) comboProduit.getSelectedItem();

        if (fournisseur == null) {
            afficherInformation(
                    "Veuillez choisir un fournisseur."
            );
            return;
        }

        if (produit == null) {
            afficherInformation(
                    "Veuillez choisir un produit."
            );
            return;
        }

        Double prix = lirePrix();

        if (prix == null) {
            return;
        }

        try {

            if (proposerService.existe(
                    fournisseur.id,
                    produit.id
            )) {

                afficherInformation(
                        "Cette proposition existe déjà."
                );
                return;
            }

            Proposer proposition =
                    new Proposer(
                            fournisseur.id,
                            produit.id,
                            prix
                    );

            proposerService.ajouter(proposition);

            afficherInformation(
                    "La proposition a été ajoutée avec succès."
            );

            txtPrix.setText("");
            chargerPropositions();

        } catch (Exception e) {
            afficherErreur(
                    "Erreur lors de l'ajout de la proposition.",
                    e
            );
        }
    }

    // ================================================================
    // MODIFICATION
    // ================================================================

    private void modifierSelection() {

        int ligne = table.getSelectedRow();

        if (ligne < 0) {
            afficherInformation(
                    "Veuillez sélectionner une proposition."
            );
            return;
        }

        Proposer proposition = obtenirProposition(ligne);

        if (proposition == null) {
            return;
        }

        JTextField champPrix = new JTextField(
                String.valueOf(
                        proposition.getPrixAchatSpecifique()
                )
        );

        champPrix.selectAll();

        JPanel panel = new JPanel(
                new MigLayout(
                        "fill, insets 5",
                        "[][grow]"
                )
        );

        panel.add(
                new JLabel("Nouveau prix :")
        );

        panel.add(
                champPrix,
                "growx"
        );

        int choix = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Modifier le prix d'achat",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choix != JOptionPane.OK_OPTION) {
            return;
        }

        String texte = champPrix.getText()
                .trim()
                .replace(" ", "")
                .replace(",", ".");

        try {

            double nouveauPrix =
                    Double.parseDouble(texte);

            if (nouveauPrix <= 0) {
                throw new NumberFormatException();
            }

            proposerService.modifier(
                    proposition.getIdFournisseur(),
                    proposition.getIdProduit(),
                    nouveauPrix
            );

            afficherInformation(
                    "La proposition a été modifiée avec succès."
            );

            chargerPropositions();

        } catch (NumberFormatException e) {

            afficherInformation(
                    "Veuillez saisir un prix valide supérieur à 0."
            );

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la modification.",
                    e
            );
        }
    }

    // ================================================================
    // SUPPRESSION
    // ================================================================

    private void supprimerSelection() {

        int ligne = table.getSelectedRow();

        if (ligne < 0) {
            afficherInformation(
                    "Veuillez sélectionner une proposition."
            );
            return;
        }

        Proposer proposition = obtenirProposition(ligne);

        if (proposition == null) {
            return;
        }

        int choix = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous vraiment supprimer cette proposition ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choix != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            proposerService.supprimer(
                    proposition.getIdFournisseur(),
                    proposition.getIdProduit()
            );

            afficherInformation(
                    "La proposition a été supprimée."
            );

            chargerPropositions();

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la suppression.",
                    e
            );
        }
    }

    private Proposer obtenirProposition(int ligne) {

        try {

            String fournisseurTexte =
                    tableModel.getValueAt(ligne, 0).toString();

            String produitTexte =
                    tableModel.getValueAt(ligne, 1).toString();

            int idFournisseur =
                    Integer.parseInt(
                            fournisseurTexte
                                    .substring(
                                            1,
                                            fournisseurTexte.indexOf(" - ")
                                    )
                    );

            int idProduit =
                    Integer.parseInt(
                            produitTexte
                                    .substring(
                                            1,
                                            produitTexte.indexOf(" - ")
                                    )
                    );

            Object prixObject =
                    tableModel.getValueAt(ligne, 2);

            double prix =
                    prixObject instanceof Number
                            ? ((Number) prixObject).doubleValue()
                            : Double.parseDouble(
                                    prixObject.toString()
                            );

            return new Proposer(
                    idFournisseur,
                    idProduit,
                    prix
            );

        } catch (Exception e) {

            afficherErreur(
                    "Impossible de récupérer la proposition sélectionnée.",
                    e
            );

            return null;
        }
    }

    // ================================================================
    // ACTUALISATION
    // ================================================================

    private void actualiser() {

        chargerFournisseurs();
        chargerProduits();
        chargerPropositions();
    }

    // ================================================================
    // LECTURE DU PRIX
    // ================================================================

    private Double lirePrix() {

        String texte = txtPrix.getText()
                .trim()
                .replace(" ", "")
                .replace(",", ".");

        if (texte.isEmpty()) {

            afficherInformation(
                    "Veuillez définir un prix d'achat."
            );

            return null;
        }

        try {

            double prix =
                    Double.parseDouble(texte);

            if (prix <= 0) {
                throw new NumberFormatException();
            }

            return prix;

        } catch (NumberFormatException e) {

            afficherInformation(
                    "Veuillez saisir un prix valide supérieur à 0."
            );

            txtPrix.requestFocus();

            return null;
        }
    }

    // ================================================================
    // BOUTONS
    // ================================================================

    private JButton creerBoutonPrincipal(String texte) {

        JButton bouton = new JButton(texte);

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
                        10,
                        18,
                        10,
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

    private JButton creerBoutonSecondaire(String texte) {

        JButton bouton = new JButton(texte);

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

    private JButton creerBoutonSupprimer(String texte) {

        JButton bouton = creerBoutonSecondaire(texte);

        bouton.setForeground(RED);

        return bouton;
    }

    private JButton creerBoutonPagination(String texte) {
        JButton bouton = new JButton(texte);
        bouton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bouton.setForeground(TEXT);
        bouton.setBackground(WHITE);
        bouton.setFocusPainted(false);
        bouton.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(BORDER, 1, 8),
                        BorderFactory.createEmptyBorder(7, 12, 7, 12)
                )
        );
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return bouton;
    }

    // ================================================================
    // ICÔNES D'ACTION
    // ================================================================

    private static class PencilIcon implements Icon {
        private final Color color;
        private final int size;
        PencilIcon(Color color, int size) { this.color = color; this.size = size; }
        public int getIconWidth() { return size; }
        public int getIconHeight() { return size; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x + 5, y + 13, x + 13, y + 5);
            g2.drawLine(x + 4, y + 14, x + 7, y + 13);
            g2.drawLine(x + 13, y + 5, x + 11, y + 3);
            g2.dispose();
        }
    }

    private static class TrashIcon implements Icon {
        private final Color color;
        private final int size;
        TrashIcon(Color color, int size) { this.color = color; this.size = size; }
        public int getIconWidth() { return size; }
        public int getIconHeight() { return size; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x + 4, y + 5, x + 14, y + 5);
            g2.drawLine(x + 7, y + 3, x + 11, y + 3);
            g2.drawRoundRect(x + 5, y + 6, 8, 10, 2, 2);
            g2.dispose();
        }
    }

    private class ActionsRenderer extends JPanel implements TableCellRenderer {
        ActionsRenderer() {
            super(new FlowLayout(FlowLayout.CENTER, 7, 10));
            setOpaque(true);
            add(new JLabel(new PencilIcon(new Color(70, 110, 210), 18)));
            add(new JLabel(new TrashIcon(RED, 18)));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            setBackground(selected ? new Color(242, 244, 247) : WHITE);
            return this;
        }
    }

    private class ActionsEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 10));
        private int ligneCourante = -1;

        ActionsEditor() {
            panel.setOpaque(true);
            JButton modifier = new JButton(new PencilIcon(new Color(70, 110, 210), 18));
            JButton supprimer = new JButton(new TrashIcon(RED, 18));
            configurerBoutonAction(modifier);
            configurerBoutonAction(supprimer);

            modifier.addActionListener(e -> {
                fireEditingStopped();
                table.setRowSelectionInterval(ligneCourante, ligneCourante);
                modifierSelection();
            });
            supprimer.addActionListener(e -> {
                fireEditingStopped();
                table.setRowSelectionInterval(ligneCourante, ligneCourante);
                supprimerSelection();
            });
            panel.add(modifier);
            panel.add(supprimer);
        }

        private void configurerBoutonAction(JButton bouton) {
            bouton.setFocusPainted(false);
            bouton.setBorderPainted(false);
            bouton.setContentAreaFilled(false);
            bouton.setOpaque(false);
            bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            bouton.setPreferredSize(new Dimension(28, 28));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column) {
            ligneCourante = row;
            panel.setBackground(selected ? new Color(242, 244, 247) : WHITE);
            return panel;
        }

        @Override
        public Object getCellEditorValue() { return ""; }
    }

    // ================================================================
    // MESSAGES
    // ================================================================

    private void afficherInformation(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void afficherErreur(
            String message,
            Exception e
    ) {

        e.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                message + "\n\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ================================================================
    // OBJETS COMBOBOX
    // ================================================================

    private static class FournisseurItem {

        private final int id;
        private final String texte;

        public FournisseurItem(
                Fournisseur fournisseur,
                String nomAffiche
        ) {

            this.id = fournisseur.getIdFournisseur();

            this.texte =
                    "#" + fournisseur.getIdFournisseur()
                            + " - "
                            + nomAffiche;
        }

        @Override
        public String toString() {
            return texte;
        }
    }

    private static class ProduitItem {

        private final int id;
        private final String texte;

        public ProduitItem(Produit produit) {

            this.id = produit.getIdProduit();

            this.texte =
                    "#" + produit.getIdProduit()
                            + " - "
                            + produit.getDesignation();
        }

        @Override
        public String toString() {
            return texte;
        }
    }

    // ================================================================
    // RENDERER PRIX
    // ================================================================

    private static class PrixRenderer
            extends DefaultTableCellRenderer {

        public PrixRenderer() {

            setHorizontalAlignment(
                    SwingConstants.RIGHT
            );
        }

        @Override
        protected void setValue(Object value) {

            if (value instanceof Number) {

                double prix =
                        ((Number) value).doubleValue();

                setText(
                        String.format(
                                "%,.2f Ar",
                                prix
                        )
                );

            } else {

                setText(
                        value == null
                                ? ""
                                : value.toString()
                );
            }
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