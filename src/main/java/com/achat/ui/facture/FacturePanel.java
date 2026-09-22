package com.achat.ui.facture;

import com.achat.model.Commande;
import com.achat.model.Facture;
import com.achat.model.Fournisseur;
import com.achat.model.Personne;
import com.achat.model.Societe;
import com.achat.service.CommandeService;
import com.achat.service.FactureService;
import com.achat.service.FournisseurService;
import com.achat.service.PersonneService;
import com.achat.service.SocieteService;

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
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

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
    private final CommandeService commandeService;
    private final FournisseurService fournisseurService;
    private final PersonneService personneService;
    private final SocieteService societeService;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtRecherche;
    private JButton btnActualiser;
    private JButton btnNouvelle;
    private JButton btnRechercher;

    private JLabel lblPagination;
    private JButton btnPrecedent;
    private JButton btnSuivant;
    private List<Facture> facturesCourantes = new ArrayList<>();
    private int pageActuelle = 1;
    private static final int PAGE_SIZE = 8;

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
        commandeService = new CommandeService();
        fournisseurService = new FournisseurService();
        personneService = new PersonneService();
        societeService = new SocieteService();

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

        JPanel pagination = new JPanel(new BorderLayout(10, 0));
        pagination.setOpaque(false);

        lblPagination = new JLabel();
        lblPagination.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblPagination.setForeground(GRAY);

        JPanel boutonsPagination = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        boutonsPagination.setOpaque(false);

        btnPrecedent = creerBoutonPagination("‹ Précédent");
        btnSuivant = creerBoutonPagination("Suivant ›");
        btnPrecedent.addActionListener(e -> pagePrecedente());
        btnSuivant.addActionListener(e -> pageSuivante());

        boutonsPagination.add(btnPrecedent);
        boutonsPagination.add(btnSuivant);
        pagination.add(lblPagination, BorderLayout.WEST);
        pagination.add(boutonsPagination, BorderLayout.EAST);

        mainPanel.add(pagination, BorderLayout.SOUTH);

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
                "Fournisseur",
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
                return column == 8;
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
                .setPreferredWidth(50);

        table.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(140);

        table.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(170);

        table.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(105);

        table.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(125);

        table.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(95);

        table.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(140);

        table.getColumnModel()
                .getColumn(7)
                .setPreferredWidth(125);

        table.getColumnModel()
                .getColumn(8)
                .setPreferredWidth(95);

        // ============================================================
        // RENDERS
        // ============================================================

        table.getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        new CenterRenderer()
                );

        table.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        new CenterRenderer()
                );

        table.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new MontantRenderer()
                );

        table.getColumnModel()
                .getColumn(5)
                .setCellRenderer(
                        new MontantRenderer()
                );

        table.getColumnModel()
                .getColumn(6)
                .setCellRenderer(
                        new EtatPaiementRenderer()
                );

        table.getColumnModel()
                .getColumn(7)
                .setCellRenderer(
                        new TotalRenderer()
                );

        table.getColumnModel()
                .getColumn(8)
                .setPreferredWidth(120);

        table.getColumnModel()
                .getColumn(8)
                .setCellRenderer(
                        new ActionCellRenderer()
                );

        table.getColumnModel()
                .getColumn(8)
                .setCellEditor(
                        new ActionCellEditor()
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

            facturesCourantes = new ArrayList<>(
                    factureService.findAll()
            );
            pageActuelle = 1;
            afficherPage();

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

        facturesCourantes = new ArrayList<>(factures);
        pageActuelle = 1;
        afficherPage();
    }

    private void afficherPage() {
        tableModel.setRowCount(0);

        int total = facturesCourantes.size();
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
        if (pageActuelle > totalPages) pageActuelle = totalPages;

        String mot = total <= 1 ? "facture" : "factures";
        if (lblPagination != null) {
            lblPagination.setText(
                    "Page " + pageActuelle + " / " + totalPages
                            + "  (" + total + " " + mot + ")"
            );
            btnPrecedent.setEnabled(pageActuelle > 1);
            btnSuivant.setEnabled(pageActuelle < totalPages);
        }

        int debut = (pageActuelle - 1) * PAGE_SIZE;
        int fin = Math.min(debut + PAGE_SIZE, total);

        for (int i = debut; i < fin; i++) {
            Facture facture = facturesCourantes.get(i);

            double montantHt =
                    facture.getMontantTotalHt();

            double tva =
                    facture.getMontantTva();

            double totalTtc =
                    facture.getMontantTtc();

            String nomFournisseur =
                    obtenirNomFournisseurPourCommande(
                            facture.getIdCommande()
                    );

            tableModel.addRow(
                    new Object[]{
                            facture.getIdFacture(),
                            facture.getNumFacture(),
                            nomFournisseur,
                            facture.getDateFacture(),
                            formaterMontant(montantHt),
                            formaterMontant(tva),
                            facture.getEtatPaiement(),
                            formaterMontant(totalTtc),
                            ""
                    }
            );
        }
    }

    /**
     * Résout le nom du fournisseur associé à une commande.
     */
    private String obtenirNomFournisseurPourCommande(
            int idCommande
    ) {

        if (idCommande <= 0) {
            return "-";
        }

        try {

            Commande commande =
                    commandeService.findById(idCommande);

            if (commande == null) {
                return "-";
            }

            Fournisseur fournisseur =
                    fournisseurService.findById(
                            commande.getIdFournisseur()
                    );

            if (fournisseur == null) {
                return "-";
            }

            if ("PERSONNE".equals(fournisseur.getTypeFournisseur())) {

                Personne personne =
                        personneService.findByFournisseur(
                                commande.getIdFournisseur()
                        );

                if (personne != null) {
                    return (personne.getNom() == null ? "" : personne.getNom())
                            + " "
                            + (personne.getPrenom() == null ? "" : personne.getPrenom());
                }

            } else {

                Societe societe =
                        societeService.findByFournisseur(
                                commande.getIdFournisseur()
                        );

                if (societe != null) {
                    return societe.getRaisonSociale() == null
                            ? "-"
                            : societe.getRaisonSociale();
                }
            }

        } catch (Exception e) {
            return "-";
        }

        return "-";
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

    private JButton creerBoutonPagination(String texte) {
        JButton bouton = new JButton(texte);
        bouton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bouton.setForeground(TEXT);
        bouton.setBackground(WHITE);
        bouton.setFocusPainted(false);
        bouton.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 1, 8),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return bouton;
    }

    private void pagePrecedente() {
        if (pageActuelle > 1) { pageActuelle--; afficherPage(); }
    }

    private void pageSuivante() {
        int totalPages = Math.max(1, (int) Math.ceil(facturesCourantes.size() / (double) PAGE_SIZE));
        if (pageActuelle < totalPages) { pageActuelle++; afficherPage(); }
    }

    private void modifierLigne(int ligne) {
        int modelRow = table.convertRowIndexToModel(ligne);
        int id = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
        try {
            Facture facture = factureService.findById(id);
            if (facture != null) afficherModal(new FactureForm(facture), "Modifier la facture");
        } catch (Exception e) {
            afficherErreur("Erreur lors de la modification de la facture.", e);
        }
    }

    private void supprimerLigne(int ligne) {
        int modelRow = table.convertRowIndexToModel(ligne);
        int id = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());
        int confirmation = JOptionPane.showConfirmDialog(
                this, "Voulez-vous vraiment supprimer cette facture ?",
                "Confirmation de suppression", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirmation != JOptionPane.YES_OPTION) return;
        try {
            factureService.supprimer(id);
            chargerFactures();
            JOptionPane.showMessageDialog(this, "Facture supprimée avec succès.",
                    "Suppression", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            afficherErreur("Impossible de supprimer la facture.", e);
        }
    }

    private static class ActionCellRenderer extends JPanel implements TableCellRenderer {
        ActionCellRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 7, 10));
            add(new JLabel(new PencilIcon(new Color(70, 100, 180), 18)));
            add(new JLabel(new TrashIcon(RED, 18)));
        }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
            setBackground(selected ? table.getSelectionBackground() : WHITE);
            return this;
        }
    }

    private class ActionCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 10));
        private int currentRow;
        ActionCellEditor() {
            panel.setBackground(WHITE);
            JButton edit = new JButton(new PencilIcon(new Color(70, 100, 180), 18));
            JButton delete = new JButton(new TrashIcon(RED, 18));
            config(edit); config(delete);
            edit.addActionListener(e -> { stopCellEditing(); modifierLigne(currentRow); });
            delete.addActionListener(e -> { stopCellEditing(); supprimerLigne(currentRow); });
            panel.add(edit); panel.add(delete);
        }
        private void config(JButton b) {
            b.setFocusPainted(false); b.setBorderPainted(false); b.setContentAreaFilled(false);
            b.setOpaque(false); b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.setPreferredSize(new Dimension(28, 28));
        }
        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column) {
            currentRow = row; panel.setBackground(selected ? table.getSelectionBackground() : WHITE); return panel;
        }
        @Override public Object getCellEditorValue() { return ""; }
    }

    private static class PencilIcon implements Icon {
        private final Color color; private final int size;
        PencilIcon(Color color, int size) { this.color=color; this.size=size; }
        public int getIconWidth(){return size;} public int getIconHeight(){return size;}
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x+5,y+13,x+13,y+5); g2.drawLine(x+4,y+14,x+7,y+13); g2.drawLine(x+13,y+5,x+11,y+3);
            g2.dispose();
        }
    }

    private static class TrashIcon implements Icon {
        private final Color color; private final int size;
        TrashIcon(Color color, int size) { this.color=color; this.size=size; }
        public int getIconWidth(){return size;} public int getIconHeight(){return size;}
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x+4,y+5,x+14,y+5); g2.drawLine(x+7,y+3,x+11,y+3); g2.drawRoundRect(x+5,y+6,8,10,2,2);
            g2.dispose();
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
                680,
                800
        );

        dialog.setMinimumSize(
                new Dimension(
                        650,
                        720
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