package com.achat.ui.facture;

import com.achat.model.Commande;
import com.achat.model.Facture;
import com.achat.model.Fournisseur;
import com.achat.model.LigneCommande;
import com.achat.model.Personne;
import com.achat.model.Produit;
import com.achat.model.Societe;
import com.achat.service.CommandeService;
import com.achat.service.FactureService;
import com.achat.service.FournisseurService;
import com.achat.service.PersonneService;
import com.achat.service.ProduitService;
import com.achat.service.SocieteService;
import net.miginfocom.swing.MigLayout;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.List;

/**
 * Formulaire de création et de modification d'une facture.
 *
 * La facture référence désormais la commande qu'elle facture ;
 * le fournisseur est déduit automatiquement de cette commande.
 * Un export PDF de la facture est également disponible.
 */
public class FactureForm extends JPanel {

    private final FactureService factureService;
    private final CommandeService commandeService;
    private final FournisseurService fournisseurService;
    private final PersonneService personneService;
    private final SocieteService societeService;
    private final ProduitService produitService;

    private JComboBox<CommandeItem> comboCommande;
    private JTextField txtFournisseur;

    private JTextField txtNumeroFacture;
    private JTextField txtDate;
    private JTextField txtMontantHt;
    private JTextField txtTva;
    private JTextField txtTotalTtc;

    private JComboBox<String> comboEtatPaiement;

    private JButton btnEnregistrer;
    private JButton btnAnnuler;
    private JButton btnExporterPdf;

    private int idModification = -1;

    private static final double TAUX_TVA = 0.20;

    private JDialog dialog;

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

    private static final Color PRIMARY =
            new Color(25, 25, 25);

    private static final Color RED =
            new Color(210, 70, 70);

    private static final Color GREEN =
            new Color(34, 139, 94);

    private final DecimalFormat decimalFormat =
            creerFormatMontant();

    /**
     * Crée un format numérique avec un espace ASCII normal comme
     * séparateur de milliers (au lieu de l'espace fine insécable
     * "\u202F" utilisée par défaut par le format français, qui
     * n'est pas supportée par la police du PDF et s'affichait
     * comme un point d'interrogation "?" à l'export).
     */
    private static DecimalFormat creerFormatMontant() {

        DecimalFormatSymbols symboles =
                new DecimalFormatSymbols(Locale.FRANCE);

        symboles.setGroupingSeparator(' ');
        symboles.setDecimalSeparator(',');

        return new DecimalFormat("#,##0.00", symboles);
    }

    // ================================================================
    // CONSTRUCTEUR - NOUVELLE FACTURE
    // ================================================================

    public FactureForm() {

        factureService =
                new FactureService();

        commandeService =
                new CommandeService();

        fournisseurService =
                new FournisseurService();

        personneService =
                new PersonneService();

        societeService =
                new SocieteService();

        produitService =
                new ProduitService();

        construireInterface();

        chargerCommandes();

        remplirValeursParDefaut();

        ajouterCalculAutomatique();
    }

    // ================================================================
    // CONSTRUCTEUR - MODIFICATION
    // ================================================================

    public FactureForm(Facture facture) {

        this();

        if (facture == null) {
            return;
        }

        idModification =
                facture.getIdFacture();

        chargerFacture(facture);
    }

    // ================================================================
    // INTERFACE
    // ================================================================

    private void construireInterface() {

        setLayout(
                new MigLayout(
                        "fill, insets 20",
                        "[150][grow]",
                        "[]10[]10[]10[]10[]10[]10[]10[]10[]"
                )
        );

        setBackground(LIGHT_BG);

        // ============================================================
        // TITRE
        // ============================================================

        JLabel lblTitre =
                new JLabel("Facture");

        lblTitre.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        20
                )
        );

        lblTitre.setForeground(TEXT);

        add(
                lblTitre,
                "span 2, wrap"
        );

        JLabel lblSousTitre =
                new JLabel(
                        idModification == -1
                                ? "Créer une nouvelle facture"
                                : "Modifier les informations de la facture"
                );

        lblSousTitre.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        lblSousTitre.setForeground(GRAY);

        add(
                lblSousTitre,
                "span 2, wrap"
        );

        // ============================================================
        // COMMANDE À FACTURER
        // ============================================================

        JLabel lblCommande =
                creerLabel(
                        "Commande à facturer"
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

        comboCommande.setPreferredSize(
                new Dimension(0, 34)
        );

        comboCommande.addActionListener(
                e -> mettreAJourFournisseur()
        );

        add(
                lblCommande,
                "top"
        );

        add(
                comboCommande,
                "growx, wrap"
        );

        // ============================================================
        // FOURNISSEUR (déduit automatiquement de la commande)
        // ============================================================

        JLabel lblFournisseur =
                creerLabel(
                        "Fournisseur"
                );

        txtFournisseur =
                creerTextField();

        txtFournisseur.setEditable(false);

        txtFournisseur.setBackground(
                new Color(240, 242, 245)
        );

        add(
                lblFournisseur,
                "top"
        );

        add(
                txtFournisseur,
                "growx, wrap"
        );

        // ============================================================
        // NUMÉRO FACTURE
        // ============================================================

        JLabel lblNumero =
                creerLabel(
                        "Numéro facture"
                );

        txtNumeroFacture =
                creerTextField();

        add(
                lblNumero,
                "top"
        );

        add(
                txtNumeroFacture,
                "growx, wrap"
        );

        // ============================================================
        // DATE
        // ============================================================

        JLabel lblDate =
                creerLabel(
                        "Date"
                );

        txtDate =
                creerTextField();

        add(
                lblDate,
                "top"
        );

        add(
                txtDate,
                "growx, wrap"
        );

        // ============================================================
        // MONTANT HT
        // ============================================================

        JLabel lblMontantHt =
                creerLabel(
                        "Montant HT"
                );

        txtMontantHt =
                creerTextField();

        txtMontantHt.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        add(
                lblMontantHt,
                "top"
        );

        add(
                txtMontantHt,
                "growx, wrap"
        );

        // ============================================================
        // TVA
        // ============================================================

        JLabel lblTva =
                creerLabel(
                        "TVA (20%)"
                );

        txtTva =
                creerTextField();

        txtTva.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        txtTva.setEditable(false);

        txtTva.setBackground(
                new Color(240, 242, 245)
        );

        add(
                lblTva,
                "top"
        );

        add(
                txtTva,
                "growx, wrap"
        );

        // ============================================================
        // TOTAL TTC
        // ============================================================

        JLabel lblTotalTtc =
                creerLabel(
                        "Total TTC"
                );

        txtTotalTtc =
                creerTextField();

        txtTotalTtc.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        txtTotalTtc.setEditable(false);

        txtTotalTtc.setBackground(
                new Color(240, 242, 245)
        );

        txtTotalTtc.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        add(
                lblTotalTtc,
                "top"
        );

        add(
                txtTotalTtc,
                "growx, wrap"
        );

        // ============================================================
        // ETAT PAIEMENT
        // ============================================================

        JLabel lblEtat =
                creerLabel(
                        "État du paiement"
                );

        comboEtatPaiement =
                new JComboBox<>(
                        new String[]{
                                "Non payé",
                                "Partiel",
                                "Payé"
                        }
                );

        comboEtatPaiement.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        comboEtatPaiement.setBackground(
                WHITE
        );

        add(
                lblEtat,
                "top"
        );

        add(
                comboEtatPaiement,
                "growx, wrap"
        );

        // ============================================================
        // ESPACE
        // ============================================================

        add(
                new JLabel(),
                "span 2, growy, wrap"
        );

        // ============================================================
        // BOUTONS
        // ============================================================

        JPanel boutonsPanel =
                new JPanel(
                        new MigLayout(
                                "insets 0",
                                "[][grow][]10[]"
                        )
                );

        boutonsPanel.setOpaque(false);

        btnExporterPdf =
                creerBoutonSecondaire(
                        "Exporter en PDF"
                );

        btnExporterPdf.addActionListener(
                e -> exporterPdf()
        );

        btnAnnuler =
                creerBoutonSecondaire(
                        "Annuler"
                );

        btnAnnuler.addActionListener(
                e -> fermer()
        );

        btnEnregistrer =
                creerBoutonPrincipal(
                        idModification == -1
                                ? "Enregistrer"
                                : "Modifier"
                );

        btnEnregistrer.addActionListener(
                e -> enregistrer()
        );

        boutonsPanel.add(
                btnExporterPdf
        );

        boutonsPanel.add(
                new JLabel(),
                "growx"
        );

        boutonsPanel.add(
                btnAnnuler
        );

        boutonsPanel.add(
                btnEnregistrer
        );

        add(
                boutonsPanel,
                "span 2, growx"
        );
    }

    // ================================================================
    // VALEURS PAR DÉFAUT
    // ================================================================

    private void remplirValeursParDefaut() {

        txtDate.setText(
                LocalDate.now().toString()
        );

        txtMontantHt.setText("0");

        comboEtatPaiement.setSelectedItem(
                "Non payé"
        );

        calculerTotalTtc();
    }

    // ================================================================
    // CHARGER FACTURE
    // ================================================================

    private void chargerFacture(
            Facture facture
    ) {

        txtNumeroFacture.setText(
                facture.getNumFacture()
        );

        txtDate.setText(
                facture.getDateFacture()
                        .toString()
        );

        txtMontantHt.setText(
                String.valueOf(
                        facture.getMontantTotalHt()
                )
        );

        // Sélection de la commande facturée
        for (int i = 0;
                i < comboCommande.getItemCount();
                i++) {

            CommandeItem item =
                    comboCommande.getItemAt(i);

            if (item.getCommande().getIdCommande()
                    == facture.getIdCommande()) {

                comboCommande.setSelectedIndex(i);
                break;
            }
        }

        mettreAJourFournisseur();

        // CORRECTION : Conversion du code système vers le libellé UI
        String etatSys = facture.getEtatPaiement();
        if ("PARTIELLEMENT_PAYEE".equalsIgnoreCase(etatSys) || "Partiel".equalsIgnoreCase(etatSys)) {
            comboEtatPaiement.setSelectedItem("Partiel");
        } else if ("PAYEE".equalsIgnoreCase(etatSys) || "Payé".equalsIgnoreCase(etatSys)) {
            comboEtatPaiement.setSelectedItem("Payé");
        } else {
            comboEtatPaiement.setSelectedItem("Non payé");
        }

        calculerTotalTtc();
    }

    // ================================================================
    // COMMANDES / FOURNISSEUR
    // ================================================================

    /**
     * Charge la liste des commandes dans le ComboBox.
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
     * Met à jour le champ Fournisseur en fonction
     * de la commande sélectionnée.
     */
    private void mettreAJourFournisseur() {

        CommandeItem item =
                (CommandeItem) comboCommande.getSelectedItem();

        if (item == null) {
            txtFournisseur.setText("");
            return;
        }

        try {

            String nom =
                    obtenirNomFournisseur(
                            item.getCommande().getIdFournisseur()
                    );

            txtFournisseur.setText(nom);

        } catch (Exception e) {

            txtFournisseur.setText("");
        }
    }

    /**
     * Résout le nom d'affichage d'un fournisseur
     * (personne ou société) à partir de son identifiant.
     */
    private String obtenirNomFournisseur(int idFournisseur) {

        try {

            Fournisseur fournisseur =
                    fournisseurService.findById(idFournisseur);

            if (fournisseur == null) {
                return "Fournisseur introuvable";
            }

            if ("PERSONNE".equals(fournisseur.getTypeFournisseur())) {

                Personne personne =
                        personneService.findByFournisseur(idFournisseur);

                if (personne != null) {
                    return (personne.getNom() == null ? "" : personne.getNom())
                            + " "
                            + (personne.getPrenom() == null ? "" : personne.getPrenom());
                }

            } else {

                Societe societe =
                        societeService.findByFournisseur(idFournisseur);

                if (societe != null) {
                    return societe.getRaisonSociale() == null
                            ? ""
                            : societe.getRaisonSociale();
                }
            }

        } catch (Exception e) {
            return "Erreur lors de la récupération du fournisseur";
        }

        return "";
    }

    // ================================================================
    // CALCUL TTC
    // ================================================================

    private void ajouterCalculAutomatique() {

        DocumentListener listener =
                new DocumentListener() {

                    @Override
                    public void insertUpdate(
                            DocumentEvent e
                    ) {
                        calculerTotalTtc();
                    }

                    @Override
                    public void removeUpdate(
                            DocumentEvent e
                    ) {
                        calculerTotalTtc();
                    }

                    @Override
                    public void changedUpdate(
                            DocumentEvent e
                    ) {
                        calculerTotalTtc();
                    }
                };

        txtMontantHt
                .getDocument()
                .addDocumentListener(listener);
    }

    private void calculerTotalTtc() {

        try {

            double montantHt =
                    lireMontant(
                            txtMontantHt.getText()
                    );

            double tva =
                    montantHt * TAUX_TVA;

            double total =
                    montantHt + tva;

            txtTva.setText(
                    decimalFormat.format(tva)
            );

            txtTotalTtc.setText(
                    decimalFormat.format(total)
                            + " Ar"
            );

        } catch (Exception e) {

            txtTva.setText("0,00");

            txtTotalTtc.setText(
                    "0,00 Ar"
            );
        }
    }

    // ================================================================
    // ENREGISTREMENT
    // ================================================================

    private void enregistrer() {

        try {

            // --------------------------------------------------------
            // COMMANDE À FACTURER
            // --------------------------------------------------------

            CommandeItem itemCommande =
                    (CommandeItem) comboCommande.getSelectedItem();

            if (itemCommande == null) {

                afficherAvertissement(
                        "Veuillez sélectionner la commande à facturer."
                );

                return;
            }

            // --------------------------------------------------------
            // NUMÉRO
            // --------------------------------------------------------

            String numero =
                    txtNumeroFacture
                            .getText()
                            .trim();

            if (numero.isEmpty()) {

                afficherAvertissement(
                        "Veuillez saisir le numéro de facture."
                );

                txtNumeroFacture.requestFocus();

                return;
            }

            // --------------------------------------------------------
            // DATE
            // --------------------------------------------------------

            String dateTexte =
                    txtDate
                            .getText()
                            .trim();

            if (dateTexte.isEmpty()) {

                afficherAvertissement(
                        "Veuillez saisir la date de la facture."
                );

                txtDate.requestFocus();

                return;
            }

            LocalDate dateFacture;

            try {

                dateFacture =
                        LocalDate.parse(
                                dateTexte
                        );

            } catch (DateTimeParseException e) {

                afficherAvertissement(
                        "La date doit être au format : AAAA-MM-JJ."
                );

                txtDate.requestFocus();

                return;
            }

            // --------------------------------------------------------
            // MONTANT HT
            // --------------------------------------------------------

            double montantHt;

            try {

                montantHt =
                        lireMontant(
                                txtMontantHt
                                        .getText()
                        );

            } catch (NumberFormatException e) {

                afficherAvertissement(
                        "Le montant HT est invalide."
                );

                txtMontantHt.requestFocus();

                return;
            }

            if (montantHt < 0) {

                afficherAvertissement(
                        "Le montant HT ne peut pas être négatif."
                );

                txtMontantHt.requestFocus();

                return;
            }

            // --------------------------------------------------------
            // TVA (calculée automatiquement, 20% du montant HT)
            // --------------------------------------------------------

            double montantTva = montantHt * TAUX_TVA;

            // --------------------------------------------------------
            // ETAT (CORRECTION DE LA CONVERSION VERS LE FORMAT ATTENDU)
            // --------------------------------------------------------

            String etatSelectionne =
                    (String) comboEtatPaiement.getSelectedItem();

            if (etatSelectionne == null
                    || etatSelectionne.trim().isEmpty()) {

                afficherAvertissement(
                        "Veuillez sélectionner l'état du paiement."
                );

                return;
            }

            String etatPaiement;
            if ("Partiel".equalsIgnoreCase(etatSelectionne)) {
                etatPaiement = "PARTIELLEMENT_PAYEE";
            } else if ("Payé".equalsIgnoreCase(etatSelectionne)) {
                etatPaiement = "PAYEE";
            } else {
                etatPaiement = "NON_PAYEE";
            }

            // --------------------------------------------------------
            // CREATION OBJET FACTURE
            // --------------------------------------------------------

            Facture facture =
                    new Facture();

            facture.setNumFacture(
                    numero
            );

            facture.setDateFacture(
                    dateFacture
            );

            facture.setMontantTotalHt(
                    montantHt
            );

            facture.setMontantTva(
                    montantTva
            );

            facture.setEtatPaiement(
                    etatPaiement
            );

            facture.setIdCommande(
                    itemCommande.getCommande().getIdCommande()
            );

            // --------------------------------------------------------
            // AJOUT
            // --------------------------------------------------------

            if (idModification == -1) {

                factureService.ajouter(
                        facture
                );

                JOptionPane.showMessageDialog(
                        this,
                        "La facture a été enregistrée avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );

            }

            // --------------------------------------------------------
            // MODIFICATION
            // --------------------------------------------------------

            else {

                facture.setIdFacture(
                        idModification
                );

                factureService.modifier(
                        facture
                );

                JOptionPane.showMessageDialog(
                        this,
                        "La facture a été modifiée avec succès.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            fermer();

        } catch (Exception e) {

            e.printStackTrace();

            afficherErreur(
                    "Impossible d'enregistrer la facture.",
                    e
            );
        }
    }

    // ================================================================
    // LECTURE MONTANT
    // ================================================================

    private double lireMontant(
            String texte
    ) {

        if (texte == null
                || texte.trim().isEmpty()) {

            return 0;
        }

        String valeur =
                texte
                        .trim()
                        .replace("Ar", "")
                        .replace(" ", "")
                        .replace(",", ".");

        return Double.parseDouble(
                valeur
        );
    }

    // ================================================================
    // EXPORT PDF
    // ================================================================

    private void exporterPdf() {

        CommandeItem item =
                (CommandeItem) comboCommande.getSelectedItem();

        if (item == null) {

            afficherAvertissement(
                    "Veuillez sélectionner la commande à facturer "
                            + "avant d'exporter la facture."
            );

            return;
        }

        String numero =
                txtNumeroFacture.getText().trim();

        if (numero.isEmpty()) {

            afficherAvertissement(
                    "Veuillez saisir le numéro de facture "
                            + "avant d'exporter."
            );

            txtNumeroFacture.requestFocus();

            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Exporter la facture en PDF");
        chooser.setSelectedFile(
                new File("Facture-" + numero.replace("/", "-") + ".pdf")
        );

        int choix = chooser.showSaveDialog(this);

        if (choix != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fichier = chooser.getSelectedFile();

        if (!fichier.getName().toLowerCase().endsWith(".pdf")) {
            fichier = new File(fichier.getParentFile(), fichier.getName() + ".pdf");
        }

        try {

            genererPdf(fichier, item.getCommande());

            JOptionPane.showMessageDialog(
                    this,
                    "Facture exportée avec succès :\n"
                            + fichier.getAbsolutePath(),
                    "Export PDF",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {

            afficherErreur(
                    "Impossible de générer le PDF de la facture.",
                    e
            );
        }
    }

    /**
     * Génère le document PDF de la facture à l'aide d'Apache PDFBox.
     */
    private void genererPdf(
            File fichier,
            Commande commande
    ) throws IOException {

        String numero = txtNumeroFacture.getText().trim();
        String dateTexte = txtDate.getText().trim();
        String nomFournisseur = txtFournisseur.getText().trim();
        String etat = (String) comboEtatPaiement.getSelectedItem();

        double montantHt = lireMontant(txtMontantHt.getText());
        double montantTva = lireMontant(txtTva.getText());
        double totalTtc = montantHt + montantTva;

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float margeGauche = 55;
            float largeurPage = page.getMediaBox().getWidth();
            float y = page.getMediaBox().getHeight() - 60;

            try (PDPageContentStream cs =
                    new PDPageContentStream(document, page)) {

                // ---------------------------------------------------
                // EN-TÊTE
                // ---------------------------------------------------

                cs.setFont(PDType1Font.HELVETICA_BOLD, 22);
                ecrireTexte(cs, margeGauche, y, "FACTURE");

                y -= 30;

                cs.setFont(PDType1Font.HELVETICA, 11);
                ecrireTexte(cs, margeGauche, y, "Numero : " + numero);

                y -= 16;

                ecrireTexte(cs, margeGauche, y, "Date : " + dateTexte);

                y -= 16;

                ecrireTexte(cs, margeGauche, y,
                        "Etat du paiement : " + (etat != null ? etat : ""));

                y -= 34;

                // ---------------------------------------------------
                // FOURNISSEUR / COMMANDE
                // ---------------------------------------------------

                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                ecrireTexte(cs, margeGauche, y, "Fournisseur");

                y -= 18;

                cs.setFont(PDType1Font.HELVETICA, 11);
                ecrireTexte(cs, margeGauche, y,
                        nomFournisseur.isEmpty() ? "-" : nomFournisseur);

                y -= 30;

                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                ecrireTexte(cs, margeGauche, y, "Commande facturee");

                y -= 18;

                cs.setFont(PDType1Font.HELVETICA, 11);
                ecrireTexte(cs, margeGauche, y,
                        "CMD-" + commande.getIdCommande()
                                + "   -   Date : " + commande.getDateCommande()
                                + "   -   Etat : " + commande.getEtatCommande());

                y -= 30;

                // ---------------------------------------------------
                // PRODUITS DE LA COMMANDE
                // ---------------------------------------------------

                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                ecrireTexte(cs, margeGauche, y, "Produits commandes");

                y -= 20;

                float colProduit = margeGauche;
                float colQuantite = margeGauche + 260;
                float colPrix = margeGauche + 350;
                float colTotal = margeGauche + 440;

                cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
                ecrireTexte(cs, colProduit, y, "Produit");
                ecrireTexte(cs, colQuantite, y, "Qte");
                ecrireTexte(cs, colPrix, y, "P.U.");
                ecrireTexte(cs, colTotal, y, "Total");

                y -= 6;

                cs.moveTo(margeGauche, y);
                cs.lineTo(largeurPage - margeGauche, y);
                cs.stroke();

                y -= 16;

                cs.setFont(PDType1Font.HELVETICA, 10);

                List<LigneCommande> lignes;

                try {
                    lignes = commandeService.findLignes(
                            commande.getIdCommande()
                    );
                } catch (Exception e) {
                    lignes = java.util.Collections.emptyList();
                }

                for (LigneCommande ligne : lignes) {

                    if (y < 90) {
                        break;
                    }

                    String designation;
                    double prixUnitaire;
                    double totalLigne;

                    try {

                        Produit produit =
                                produitService.findById(
                                        ligne.getIdProduit()
                                );

                        designation =
                                produit != null
                                        ? produit.getDesignation()
                                        : "Produit #" + ligne.getIdProduit();

                        prixUnitaire = ligne.getPrixUnitaireAchat();

                        totalLigne =
                                ligne.getQuantiteCommandee() * prixUnitaire;

                    } catch (Exception e) {

                        // Ligne illisible : on l'ignore et on passe
                        // à la suivante sans jamais toucher au flux PDF.
                        continue;
                    }

                    ecrireTexte(cs, colProduit, y, tronquer(designation, 32));

                    ecrireTexte(cs, colQuantite, y,
                            decimalFormat.format(ligne.getQuantiteCommandee()));

                    ecrireTexte(cs, colPrix, y,
                            decimalFormat.format(prixUnitaire));

                    ecrireTexte(cs, colTotal, y,
                            decimalFormat.format(totalLigne));

                    y -= 18;
                }

                y -= 20;

                cs.moveTo(margeGauche, y);
                cs.lineTo(largeurPage - margeGauche, y);
                cs.stroke();

                y -= 26;

                // ---------------------------------------------------
                // TOTAUX
                // ---------------------------------------------------

                float colLabel = largeurPage - margeGauche - 200;
                float colValeur = largeurPage - margeGauche - 90;

                cs.setFont(PDType1Font.HELVETICA, 11);

                ecrireTexte(cs, colLabel, y, "Montant HT :");
                ecrireTexte(cs, colValeur, y,
                        decimalFormat.format(montantHt) + " Ar");

                y -= 18;

                ecrireTexte(cs, colLabel, y, "TVA :");
                ecrireTexte(cs, colValeur, y,
                        decimalFormat.format(montantTva) + " Ar");

                y -= 22;

                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);

                ecrireTexte(cs, colLabel, y, "TOTAL TTC :");
                ecrireTexte(cs, colValeur, y,
                        decimalFormat.format(totalTtc) + " Ar");
            }

            document.save(fichier);
        }
    }

    /**
     * Écrit une ligne de texte dans le flux PDF.
     *
     * Le bloc de texte (beginText/endText) est TOUJOURS refermé,
     * même en cas d'erreur (caractère non supporté par la police,
     * par exemple) — cela évite de laisser le flux PDF dans un état
     * incohérent ("moveTo is not allowed within a text block").
     */
    private void ecrireTexte(
            PDPageContentStream cs,
            float x,
            float y,
            String texte
    ) throws IOException {

        cs.beginText();

        try {

            cs.newLineAtOffset(x, y);
            cs.showText(nettoyerTexte(texte));

        } catch (Exception e) {

            // Si le texte contient un caractère non supporté par
            // la police, on retente avec une version nettoyée.
            try {
                cs.showText("?");
            } catch (Exception ignored) {
                // Rien de plus à tenter.
            }

        } finally {

            cs.endText();
        }
    }

    /**
     * Remplace les caractères non supportés par WinAnsiEncoding
     * (accents compris, normalement pris en charge, mais on
     * neutralise ici les caractères "exotiques" éventuels comme
     * les puces • ou les guillemets typographiques) par leur
     * équivalent ASCII le plus proche.
     */
    private String nettoyerTexte(String texte) {

        if (texte == null) {
            return "";
        }

        StringBuilder resultat = new StringBuilder(texte.length());

        for (int i = 0; i < texte.length(); i++) {

            char c = texte.charAt(i);

            if (c <= 0xFF) {
                resultat.append(c);
            } else {
                resultat.append('?');
            }
        }

        return resultat.toString();
    }

    private String tronquer(String texte, int max) {

        if (texte == null) {
            return "";
        }

        return texte.length() <= max
                ? texte
                : texte.substring(0, max - 1) + "…";
    }

    // ================================================================
    // FERMER
    // ================================================================

    private void fermer() {

        if (dialog != null) {

            dialog.dispose();

        } else {

            Window window =
                    SwingUtilities
                            .getWindowAncestor(
                                    this
                            );

            if (window != null) {
                window.dispose();
            }
        }
    }

    // ================================================================
    // DIALOG
    // ================================================================

    public void setDialog(
            JDialog dialog
    ) {

        this.dialog = dialog;
    }

    // ================================================================
    // LABEL
    // ================================================================

    private JLabel creerLabel(
            String texte
    ) {

        JLabel label =
                new JLabel(texte);

        label.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        label.setForeground(TEXT);

        return label;
    }

    // ================================================================
    // TEXTFIELD
    // ================================================================

    private JTextField creerTextField() {

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

        field.setPreferredSize(
                new Dimension(
                        0,
                        34
                )
        );

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(
                                BORDER,
                                1,
                                8
                        ),
                        BorderFactory.createEmptyBorder(
                                6,
                                12,
                                6,
                                12
                        )
                )
        );

        return field;
    }

    // ================================================================
    // BOUTON PRINCIPAL
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
                        9,
                        20,
                        9,
                        20
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

    // ================================================================
    // BOUTON SECONDAIRE
    // ================================================================

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
                                18,
                                8,
                                18
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

    // ================================================================
    // AVERTISSEMENT
    // ================================================================

    private void afficherAvertissement(
            String message
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Attention",
                JOptionPane.WARNING_MESSAGE
        );
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
                message
                        + "\n\n"
                        + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ================================================================
    // ITEM COMBO COMMANDE
    // ================================================================

    private static class CommandeItem {

        private final Commande commande;

        public CommandeItem(Commande commande) {
            this.commande = commande;
        }

        public Commande getCommande() {
            return commande;
        }

        @Override
        public String toString() {
            return "CMD-" + commande.getIdCommande()
                    + "  •  " + commande.getDateCommande()
                    + "  •  " + commande.getEtatCommande();
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
                    (Graphics2D)
                            graphics.create();

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