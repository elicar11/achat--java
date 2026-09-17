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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        setLayout(
                new MigLayout(
                        "fill, insets 28",
                        "[grow]",
                        "[][18][][][18][grow][18][]"
                )
        );

        setBackground(BACKGROUND);

        // =========================================================
        // TITRE
        // =========================================================

        JLabel titre =
                new JLabel(
                        idModification == -1
                                ? "Nouvelle réception"
                                : "Modifier la réception"
                );

        titre.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        25
                )
        );

        titre.setForeground(TEXT);

        add(
                titre,
                "wrap"
        );

        JLabel sousTitre =
                new JLabel(
                        "Enregistrez les marchandises réellement reçues."
                );

        sousTitre.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        sousTitre.setForeground(MUTED);

        add(
                sousTitre,
                "wrap"
        );

        // =========================================================
        // INFORMATIONS GENERALES
        // =========================================================

        JPanel informations =
                creerCarte();

        informations.setLayout(
                new MigLayout(
                        "fill, insets 20",
                        "[right] 14 [grow]",
                        "[][][18][][]"
                )
        );

        JLabel section =
                creerTitreSection(
                        "Informations générales"
                );

        informations.add(
                section,
                "span 2, wrap"
        );

        // Commande
        informations.add(
                creerLabel("Commande :")
        );

        comboCommande =
                new JComboBox<>();

        comboCommande.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        comboCommande.setBackground(WHITE);
        comboCommande.setForeground(TEXT);

        informations.add(
                comboCommande,
                "growx, wrap"
        );

        // Date
        informations.add(
                creerLabel("Date réception :")
        );

        txtDate =
                creerChamp();

        txtDate.setText(
                LocalDate.now().toString()
        );

        informations.add(
                txtDate,
                "growx, wrap"
        );

        // Bon livraison
        informations.add(
                creerLabel("Bon de livraison :")
        );

        txtBonLivraison =
                creerChamp();

        informations.add(
                txtBonLivraison,
                "growx"
        );

        add(
                informations,
                "growx, wrap"
        );

        // =========================================================
        // PRODUITS
        // =========================================================

        JLabel produitsTitre =
                creerTitreSection(
                        "Produits reçus"
                );

        add(
                produitsTitre,
                "wrap"
        );

        JLabel produitsInfo =
                new JLabel(
                        "Saisissez uniquement les quantités réellement reçues."
                );

        produitsInfo.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        produitsInfo.setForeground(MUTED);

        add(
                produitsInfo,
                "wrap"
        );

        JPanel produitsCard =
                creerCarte();

        produitsCard.setLayout(
                new BorderLayout()
        );

        creerTableProduits();

        JScrollPane scroll =
                new JScrollPane(
                        tableProduits
                );

        scroll.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        8,
                        8,
                        8
                )
        );

        scroll.getViewport()
                .setBackground(WHITE);

        produitsCard.add(
                scroll,
                BorderLayout.CENTER
        );

        add(
                produitsCard,
                "grow, push, wrap"
        );

        // =========================================================
        // BOUTONS
        // =========================================================

        JPanel boutons =
                new JPanel(
                        new MigLayout(
                                "insets 0",
                                "[grow][][]",
                                "[]"
                        )
                );

        boutons.setOpaque(false);

        btnAnnuler =
                creerBoutonSecondaire(
                        "Annuler"
                );

        btnEnregistrer =
                creerBoutonPrincipal(
                        idModification == -1
                                ? "Enregistrer"
                                : "Modifier"
                );

        boutons.add(
                new JPanel(),
                "growx"
        );

        boutons.add(
                btnAnnuler,
                "w 120!"
        );

        boutons.add(
                btnEnregistrer,
                "w 145!"
        );

        add(
                boutons,
                "growx"
        );

        // Actions
        comboCommande.addActionListener(
                e -> chargerProduitsCommande()
        );

        btnEnregistrer.addActionListener(
                e -> enregistrer()
        );

        btnAnnuler.addActionListener(
                e -> fermer()
        );
    }

    /**
     * Crée le tableau des produits.
     */
    private void creerTableProduits() {

        String[] colonnes = {
            "ID PRODUIT",
            "PRODUIT",
            "QUANTITÉ COMMANDÉE",
            "DÉJÀ REÇUE",
            "QUANTITÉ À RECEVOIR",
            "QUANTITÉ REÇUE"
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

                        // Seule la dernière colonne
                        // est modifiable.
                        return column == 5;
                    }
                };

        tableProduits =
                new JTable(tableModel);

        tableProduits.setRowHeight(44);

        tableProduits.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        tableProduits.setBackground(WHITE);

        tableProduits.setForeground(TEXT);

        tableProduits.setSelectionBackground(
                new Color(238, 238, 238)
        );

        tableProduits.setSelectionForeground(TEXT);

        tableProduits.setShowGrid(false);

        tableProduits.setIntercellSpacing(
                new Dimension(0, 0)
        );

        tableProduits.getTableHeader()
                .setPreferredSize(
                        new Dimension(0, 42)
                );

        tableProduits.getTableHeader()
                .setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                11
                        )
                );

        tableProduits.getTableHeader()
                .setBackground(BLACK);

        tableProduits.getTableHeader()
                .setForeground(Color.WHITE);

        tableProduits.getTableHeader()
                .setReorderingAllowed(false);

        // Largeurs
        tableProduits
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(70);

        tableProduits
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(180);

        tableProduits
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(130);

        tableProduits
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(110);

        tableProduits
                .getColumnModel()
                .getColumn(4)
                .setPreferredWidth(140);

        tableProduits
                .getColumnModel()
                .getColumn(5)
                .setPreferredWidth(130);
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
                        Integer.parseInt(
                                tableModel
                                        .getValueAt(i, 0)
                                        .toString()
                        );

                int quantiteCommandee =
                        Integer.parseInt(
                                tableModel
                                        .getValueAt(i, 2)
                                        .toString()
                        );

                int dejaRecu =
                        Integer.parseInt(
                                tableModel
                                        .getValueAt(i, 3)
                                        .toString()
                        );

                int quantiteMax =
                        Integer.parseInt(
                                tableModel
                                        .getValueAt(i, 4)
                                        .toString()
                        );

                Object valeur =
                        tableModel
                                .getValueAt(i, 5);

                int quantiteRecue;

                try {

                    quantiteRecue =
                            Integer.parseInt(
                                    valeur
                                            .toString()
                                            .trim()
                            );

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