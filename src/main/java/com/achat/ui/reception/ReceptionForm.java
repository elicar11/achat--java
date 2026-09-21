package com.achat.ui.reception;

import com.achat.model.Commande;
import com.achat.model.LigneCommande;
import com.achat.model.LigneReception;
import com.achat.model.Produit;
import com.achat.model.Reception;

import com.achat.service.CommandeService;
import com.achat.service.ProduitService;
import com.achat.service.ReceptionService;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import static java.awt.Color.GRAY;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulaire d'ajout / modification d'une réception.
 */
public class ReceptionForm extends JPanel {

    private final ReceptionService receptionService;
    private final CommandeService commandeService;
    private final ProduitService produitService;

    private JComboBox<CommandeItem> comboCommande;

    private JTextField txtDate;
    private JTextField txtBonLivraison;

    private JTable tableProduits;
    private DefaultTableModel tableModel;

    private JButton btnEnregistrer;
    private JButton btnAnnuler;
    private JLabel lblTitre;

    private int idModification = -1;

    private JDialog dialog;

    private static final Color BACKGROUND =
            new Color(247, 247, 247);

    private static final Color WHITE =
            Color.WHITE;

    private static final Color BLACK =
            new Color(20, 20, 20);

    private static final Color TEXT =
            new Color(35, 35, 35);

    private static final Color MUTED =
            new Color(120, 120, 120);

    private static final Color BORDER =
            new Color(225, 225, 225);

    public ReceptionForm() {

        receptionService =
                new ReceptionService();

        commandeService =
                new CommandeService();

        produitService =
                new ProduitService();

        construireInterface();

        chargerCommandes();
    }

    /**
     * Constructeur pour modification.
     */
    public ReceptionForm(
            Reception reception
    ) {

        this();

        if (reception != null) {

            chargerReception(reception);
        }
    }

    /**
     * Permet au panel de fournir le JDialog.
     */
    public void setDialog(
            JDialog dialog
    ) {

        this.dialog = dialog;
    }

    /**
     * Construction de l'interface.
     */
    private void construireInterface() {

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        // =====================================================
        // HEADER
        // =====================================================

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BLACK);
        header.setBorder(new javax.swing.border.EmptyBorder(18, 22, 18, 22));

        lblTitre = new JLabel("Nouvelle réception");
        lblTitre.setForeground(WHITE);
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(lblTitre, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // =====================================================
        // CONTENU
        // =====================================================

        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(WHITE);
        content.setBorder(new javax.swing.border.EmptyBorder(22, 25, 22, 25));

        // =====================================================
        // INFORMATIONS GÉNÉRALES
        // =====================================================

        JPanel informations = new JPanel(new MigLayout(
                "fillx, insets 0",
                "[120!][grow]",
                "[][12][][12][]"
        ));
        informations.setOpaque(false);

        JLabel titreInfos = createLabelSection("Informations de réception");
        informations.add(titreInfos, "span 2, wrap");

        informations.add(creerLabel("Commande"));
        comboCommande = new JComboBox<>();
        comboCommande.setFont(new Font("SansSerif", Font.PLAIN, 14));
        comboCommande.setPreferredSize(new Dimension(0, 42));
        informations.add(comboCommande, "growx, wrap");

        informations.add(creerLabel("Date réception"));
        txtDate = creerChamp();
        txtDate.setText(LocalDate.now().toString());
        txtDate.setPreferredSize(new Dimension(0, 42));
        informations.add(txtDate, "growx, wrap");

        informations.add(creerLabel("Bon de livraison"));
        txtBonLivraison = creerChamp();
        txtBonLivraison.setPreferredSize(new Dimension(0, 42));
        informations.add(txtBonLivraison, "growx, wrap");

        content.add(informations, BorderLayout.NORTH);

        // =====================================================
        // PRODUITS
        // =====================================================

        JPanel produitsPanel = new JPanel(new BorderLayout(0, 10));
        produitsPanel.setOpaque(false);

        JPanel produitsHeader = new JPanel(new BorderLayout());
        produitsHeader.setOpaque(false);

        JLabel titreProduits = createLabelSection("Produits reçus");
        produitsHeader.add(titreProduits, BorderLayout.WEST);

        JLabel info = new JLabel("Saisissez les quantités réellement reçues");
        info.setFont(new Font("SansSerif", Font.PLAIN, 12));
        info.setForeground(MUTED);
        produitsHeader.add(info, BorderLayout.EAST);

        produitsPanel.add(produitsHeader, BorderLayout.NORTH);

        creerTableProduits();
        JScrollPane scroll = new JScrollPane(tableProduits);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);
        scroll.setBackground(WHITE);
        produitsPanel.add(scroll, BorderLayout.CENTER);

        content.add(produitsPanel, BorderLayout.CENTER);

        // =====================================================
        // BOUTONS
        // =====================================================

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);

        btnAnnuler = creerBoutonSecondaire("Annuler");
        btnEnregistrer = creerBoutonPrincipal("Enregistrer");
        btnAnnuler.setPreferredSize(new Dimension(120, 46));
        btnEnregistrer.setPreferredSize(new Dimension(145, 46));

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        boutons.setOpaque(false);
        boutons.add(btnAnnuler);
        boutons.add(btnEnregistrer);
        bottom.add(boutons, BorderLayout.EAST);

        content.add(bottom, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);

        comboCommande.addActionListener(e -> chargerProduitsCommande());
        btnEnregistrer.addActionListener(e -> enregistrer());
        btnAnnuler.addActionListener(e -> fermer());
    }

    /**
     * Crée le tableau des produits.
     */
    private void creerTableProduits() {

        String[] colonnes = {
                "ID PRODUIT",
                "PRODUIT",
                "COMMANDÉ",
                "DÉJÀ REÇU",
                "À RECEVOIR",
                "QUANTITÉ REÇUE"
        };

        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        tableProduits = new JTable(tableModel);
        tableProduits.setRowHeight(42);
        tableProduits.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tableProduits.setBackground(WHITE);
        tableProduits.setForeground(TEXT);
        tableProduits.setSelectionBackground(new Color(238, 238, 238));
        tableProduits.setSelectionForeground(TEXT);
        tableProduits.setShowVerticalLines(false);
        tableProduits.setShowHorizontalLines(true);
        tableProduits.setGridColor(new Color(235, 235, 235));
        tableProduits.setIntercellSpacing(new Dimension(0, 0));

        tableProduits.getTableHeader().setPreferredSize(new Dimension(0, 44));
        tableProduits.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tableProduits.getTableHeader().setBackground(new Color(248, 248, 248));
        tableProduits.getTableHeader().setForeground(GRAY);
        tableProduits.getTableHeader().setReorderingAllowed(false);

        int[] widths = {75, 230, 100, 105, 105, 125};
        for (int i = 0; i < widths.length; i++) {
            tableProduits.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tableProduits.getColumnModel().getColumn(0).setCellRenderer(center);
        tableProduits.getColumnModel().getColumn(2).setCellRenderer(center);
        tableProduits.getColumnModel().getColumn(3).setCellRenderer(center);
        tableProduits.getColumnModel().getColumn(4).setCellRenderer(center);
        tableProduits.getColumnModel().getColumn(5).setCellRenderer(center);
    }

    /**
     * Charge les commandes dans le ComboBox.
     */
    private void chargerCommandes() {

        try {

            comboCommande.removeAllItems();

            List<Commande> commandes =
                    commandeService.findAll();

            for (Commande commande : commandes) {

                comboCommande.addItem(
                        new CommandeItem(commande)
                );
            }

        } catch (Exception e) {

            afficherErreur(
                    "Impossible de charger les commandes.",
                    e
            );
        }
    }

    /**
     * Charge les produits de la commande sélectionnée.
     */
    private void chargerProduitsCommande() {

        CommandeItem item =
                (CommandeItem)
                        comboCommande.getSelectedItem();

        if (item == null) {
            return;
        }

        try {

            tableModel.setRowCount(0);

            Commande commande =
                    item.getCommande();

            /*
             * Récupération des lignes de commande.
             */
            List<LigneCommande> lignes =
                    commandeService.findLignes(
                            commande.getIdCommande()
                    );

            for (LigneCommande ligne : lignes) {

                Produit produit =
                        produitService.findById(
                                ligne.getIdProduit()
                        );

                int dejaRecu = 0;

                try {

                    dejaRecu =
                            receptionService
                                    .calculerQuantiteRecue(
                                            commande.getIdCommande(),
                                            ligne.getIdProduit()
                                    );

                } catch (Exception ignored) {
                    // Si aucune réception précédente
                    // n'existe, on considère 0.
                }

                int reste =
                        (int) (ligne.getQuantiteCommandee()
                                - dejaRecu);

                if (reste < 0) {
                    reste = 0;
                }

                tableModel.addRow(
                        new Object[]{
                            ligne.getIdProduit(),
                            produit != null
                                    ? produit.getDesignation()
                                    : "Produit #" +
                                      ligne.getIdProduit(),
                            ligne.getQuantiteCommandee(),
                            dejaRecu,
                            reste,
                            reste
                        }
                );
            }

        } catch (Exception e) {

            afficherErreur(
                    "Impossible de charger les produits de la commande.",
                    e
            );
        }
    }

    /**
     * Charge une réception existante.
     */
    private void chargerReception(
            Reception reception
    ) {

        idModification =
                reception.getIdReception();

        if (lblTitre != null) {
            lblTitre.setText("Modifier la réception");
        }
        btnEnregistrer.setText("Modifier");

        txtDate.setText(
                reception.getDateReception() != null
                        ? reception.getDateReception().toString()
                        : ""
        );

        txtBonLivraison.setText(
                reception.getNumBonLivraison() != null
                        ? reception.getNumBonLivraison()
                        : ""
        );

        // Sélection de la commande
        for (int i = 0;
                i < comboCommande.getItemCount();
                i++) {

            CommandeItem item =
                    comboCommande.getItemAt(i);

            if (item.getCommande()
                    .getIdCommande()
                    ==
                    reception.getIdCommande()) {

                comboCommande.setSelectedIndex(i);

                break;
            }
        }

        /*
         * Les lignes existantes.
         */
        try {

            tableModel.setRowCount(0);

            List<LigneCommande> lignesCommande =
                    commandeService.findLignes(
                            reception.getIdCommande()
                    );

            List<LigneReception> lignesReception =
                    receptionService.findLignes(
                            reception.getIdReception()
                    );

            for (LigneCommande ligneCommande :
                    lignesCommande) {

                Produit produit =
                        produitService.findById(
                                ligneCommande.getIdProduit()
                        );

                int quantiteExistante = 0;

                for (LigneReception ligneReception :
                        lignesReception) {

                    if (ligneReception.getIdProduit()
                            ==
                            ligneCommande.getIdProduit()) {

                        quantiteExistante =
                                ligneReception
                                        .getQuantiteRecue();

                        break;
                    }
                }

                int dejaRecuAvant = 0;

                try {

                    dejaRecuAvant =
                            receptionService
                                    .calculerQuantiteRecue(
                                            reception.getIdCommande(),
                                            ligneCommande
                                                    .getIdProduit()
                                    );

                    /*
                     * La quantité de la réception
                     * actuelle est déjà comprise.
                     * On la retire pour calculer
                     * correctement le reste.
                     */
                    dejaRecuAvant -=
                            quantiteExistante;

                    if (dejaRecuAvant < 0) {
                        dejaRecuAvant = 0;
                    }

                } catch (Exception ignored) {
                }

                int reste =
                        (int) (ligneCommande
                                .getQuantiteCommandee()
                                - dejaRecuAvant);

                if (reste < 0) {
                    reste = 0;
                }

                tableModel.addRow(
                        new Object[]{
                            ligneCommande.getIdProduit(),
                            produit != null
                                    ? produit.getDesignation()
                                    : "Produit #" +
                                      ligneCommande
                                              .getIdProduit(),
                            ligneCommande
                                    .getQuantiteCommandee(),
                            dejaRecuAvant,
                            reste,
                            quantiteExistante
                        }
                );
            }

            btnEnregistrer.setText("Modifier");

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors du chargement de la réception.",
                    e
            );
        }
    }

    /**
     * Enregistre la réception.
     */
    private void enregistrer() {

        try {

            CommandeItem item =
                    (CommandeItem)
                            comboCommande.getSelectedItem();

            if (item == null) {

                afficherAvertissement(
                        "Veuillez sélectionner une commande."
                );

                return;
            }

            String dateTexte =
                    txtDate.getText().trim();

            if (dateTexte.isEmpty()) {

                afficherAvertissement(
                        "La date de réception est obligatoire."
                );

                txtDate.requestFocus();

                return;
            }

            LocalDate dateReception;

            try {

                dateReception =
                        LocalDate.parse(dateTexte);

            } catch (Exception e) {

                afficherAvertissement(
                        "La date doit être au format AAAA-MM-JJ."
                );

                txtDate.requestFocus();

                return;
            }

            String bonLivraison =
                    txtBonLivraison
                            .getText()
                            .trim();

            if (bonLivraison.isEmpty()) {

                afficherAvertissement(
                        "Le numéro du bon de livraison est obligatoire."
                );

                txtBonLivraison.requestFocus();

                return;
            }

            if (tableModel.getRowCount() == 0) {

                afficherAvertissement(
                        "La commande ne contient aucun produit."
                );

                return;
            }

            // =====================================================
            // Création de la réception
            // =====================================================

            Reception reception =
                    new Reception();

            reception.setDateReception(
                    dateReception
            );

            reception.setNumBonLivraison(
                    bonLivraison
            );

            reception.setIdCommande(
                    item.getCommande()
                            .getIdCommande()
            );

            List<LigneReception>
                    lignes =
                    new ArrayList<>();

            // =====================================================
            // Lecture des quantités
            // =====================================================

            for (int i = 0;
                    i < tableModel.getRowCount();
                    i++) {

                int idProduit =
                        lireEntier(tableModel.getValueAt(i, 0));

                int quantiteCommandee =
                        lireEntier(tableModel.getValueAt(i, 2));

                int dejaRecu =
                        lireEntier(tableModel.getValueAt(i, 3));

                int quantiteMax =
                        lireEntier(tableModel.getValueAt(i, 4));

                Object valeur =
                        tableModel
                                .getValueAt(i, 5);

                int quantiteRecue;

                try {

                    quantiteRecue = lireEntier(valeur);

                } catch (Exception e) {

                    afficherAvertissement(
                            "Quantité invalide pour le produit : "
                                    + tableModel
                                            .getValueAt(i, 1)
                    );

                    return;
                }

                if (quantiteRecue < 0) {

                    afficherAvertissement(
                            "La quantité reçue ne peut pas être négative."
                    );

                    return;
                }

                /*
                 * En ajout :
                 * quantité reçue <= reste.
                 *
                 * En modification :
                 * la colonne "reste" a déjà
                 * exclu la quantité actuelle.
                 */
                if (quantiteRecue > quantiteMax) {

                    afficherAvertissement(
                            "La quantité reçue pour "
                                    + tableModel
                                            .getValueAt(i, 1)
                                    + " dépasse la quantité autorisée."
                    );

                    return;
                }

                /*
                 * On ne crée pas de ligne avec
                 * une quantité nulle.
                 */
                if (quantiteRecue > 0) {

                    LigneReception ligne =
                            new LigneReception();

                    ligne.setIdProduit(
                            idProduit
                    );

                    ligne.setQuantiteRecue(
                            quantiteRecue
                    );

                    lignes.add(ligne);
                }
            }

            if (lignes.isEmpty()) {

                afficherAvertissement(
                        "Veuillez saisir au moins une quantité reçue."
                );

                return;
            }

            // =====================================================
            // AJOUT / MODIFICATION
            // =====================================================

            if (idModification == -1) {

                receptionService.ajouter(
                        reception,
                        lignes
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Réception enregistrée avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                reception.setIdReception(
                        idModification
                );

                receptionService.modifier(
                        reception
                );

                /*
                 * Pour la modification des lignes,
                 * on supprime les anciennes lignes
                 * puis on recrée les nouvelles.
                 */
                receptionService
                        .remplacerLignes(
                                idModification,
                                lignes
                        );

                JOptionPane.showMessageDialog(
                        this,
                        "Réception modifiée avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            fermer();

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de l'enregistrement.",
                    e
            );
        }
    }

    /**
     * Ferme le formulaire.
     */
    private void fermer() {

        if (dialog != null) {

            dialog.dispose();

        } else {

            Window window =
                    SwingUtilities
                            .getWindowAncestor(this);

            if (window != null) {

                window.dispose();
            }
        }
    }

    // =============================================================
    // COMPOSANTS
    // =============================================================

    private JPanel creerCarte() {

        JPanel panel =
                new JPanel();

        panel.setBackground(WHITE);

        panel.setBorder(
                new RoundedBorder(
                        BORDER,
                        15,
                        1
                )
        );

        return panel;
    }

    private JLabel creerTitreSection(
            String texte
    ) {

        JLabel label =
                new JLabel(texte);

        label.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        15
                )
        );

        label.setForeground(TEXT);

        return label;
    }

    private JLabel createLabelSection(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(TEXT);
        return label;
    }

    private JLabel creerLabel(
            String texte
    ) {

        JLabel label =
                new JLabel(texte);

        label.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        12
                )
        );

        label.setForeground(TEXT);

        return label;
    }

    private JTextField creerChamp() {

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

        field.setCaretColor(BLACK);

        field.setBorder(
                new RoundedBorder(
                        BORDER,
                        9,
                        1
                )
        );

        return field;
    }

    private JButton creerBoutonPrincipal(
            String texte
    ) {

        JButton button =
                new JButton(
                        texte,
                        new SaveIcon()
                );

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(Color.WHITE);

        button.setBackground(BLACK);

        button.setFocusPainted(false);

        button.setBorder(
                new RoundedBorder(
                        BLACK,
                        10,
                        1
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    private JButton creerBoutonSecondaire(
            String texte
    ) {

        JButton button =
                new JButton(
                        texte,
                        new CloseIcon()
                );

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(TEXT);

        button.setBackground(WHITE);

        button.setFocusPainted(false);

        button.setBorder(
                new RoundedBorder(
                        BORDER,
                        10,
                        1
                )
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return button;
    }

    /**
     * Convertit une quantité en entier.
     * La JTable peut fournir 5.0 sous forme de Double alors que
     * la quantité métier est entière. On accepte donc 5 et 5.0,
     * mais pas une vraie valeur décimale comme 5.5.
     */
    private int lireEntier(Object valeur) {
        if (valeur == null) {
            throw new IllegalArgumentException("Valeur vide");
        }

        double nombre;

        if (valeur instanceof Number) {
            nombre = ((Number) valeur).doubleValue();
        } else {
            String texte = valeur.toString().trim().replace(',', '.');
            if (texte.isEmpty()) {
                throw new IllegalArgumentException("Valeur vide");
            }
            nombre = Double.parseDouble(texte);
        }

        if (Double.isNaN(nombre)
                || Double.isInfinite(nombre)
                || nombre != Math.rint(nombre)
                || nombre > Integer.MAX_VALUE
                || nombre < Integer.MIN_VALUE) {
            throw new IllegalArgumentException(
                    "La quantité doit être un nombre entier.");
        }

        return (int) nombre;
    }

    private void afficherAvertissement(
            String message
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation",
                JOptionPane.WARNING_MESSAGE
        );
    }

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

    // =============================================================
    // ITEM COMBO COMMANDE
    // =============================================================

    private static class CommandeItem {

        private final Commande commande;

        public CommandeItem(
                Commande commande
        ) {

            this.commande = commande;
        }

        public Commande getCommande() {

            return commande;
        }

        @Override
        public String toString() {

            return "CMD-"
                    + commande.getIdCommande()
                    + "  •  "
                    + commande.getDateCommande()
                    + "  •  "
                    + commande.getEtatCommande();
        }
    }

    // =============================================================
    // BORDURE ARRONDIE
    // =============================================================

    private static class RoundedBorder
            extends AbstractBorder {

        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedBorder(
                Color color,
                int radius,
                int thickness
        ) {

            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(
                Component c
        ) {

            return new Insets(
                    9,
                    12,
                    9,
                    12
            );
        }

        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x,
                int y,
                int width,
                int height
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

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

            g2.drawRoundRect(
                    x + 1,
                    y + 1,
                    width - 2,
                    height - 2,
                    radius,
                    radius
            );

            g2.dispose();
        }
    }

    // =============================================================
    // ICÔNE ENREGISTRER
    // =============================================================

    private static class SaveIcon
            implements Icon {

        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(Color.WHITE);

            g2.setStroke(
                    new BasicStroke(
                            1.8f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            g2.drawRoundRect(
                    x + 3,
                    y + 2,
                    12,
                    14,
                    2,
                    2
            );

            g2.drawLine(
                    x + 6,
                    y + 2,
                    x + 6,
                    y + 7
            );

            g2.drawLine(
                    x + 12,
                    y + 2,
                    x + 12,
                    y + 7
            );

            g2.drawLine(
                    x + 6,
                    y + 11,
                    x + 12,
                    y + 11
            );

            g2.dispose();
        }
    }

    // =============================================================
    // ICÔNE FERMER
    // =============================================================

    private static class CloseIcon
            implements Icon {

        @Override
        public int getIconWidth() {
            return 18;
        }

        @Override
        public int getIconHeight() {
            return 18;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    c != null
                            ? c.getForeground()
                            : BLACK
            );

            g2.setStroke(
                    new BasicStroke(
                            2,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            g2.drawLine(
                    x + 4,
                    y + 4,
                    x + 14,
                    y + 14
            );

            g2.drawLine(
                    x + 14,
                    y + 4,
                    x + 4,
                    y + 14
            );

            g2.dispose();
        }
    }
}