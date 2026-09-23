package com.achat.ui.fournisseur;

import com.achat.model.Fournisseur;
import com.achat.model.Personne;
import com.achat.model.Societe;
import com.achat.service.FournisseurService;
import com.achat.service.PersonneService;
import com.achat.service.SocieteService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Formulaire d'ajout / modification d'un fournisseur.
 *
 * Fonctionnalités :
 * - Gestion PERSONNE / SOCIETE
 * - Validation des champs
 * - Validation email
 * - Validation téléphone
 * - Validation NIF / STAT
 * - Indication visuelle des champs invalides
 * - Ajout et modification
 */
public class FournisseurForm extends JPanel {

    private final FournisseurService fournisseurService;
    private final PersonneService personneService;
    private final SocieteService societeService;

    // ========================= THÈME =========================

    private static final Color HEADER_BG = new Color(24, 24, 24);
    private static final Color SURFACE = Color.WHITE;
    private static final Color TEXT = new Color(25, 25, 25);
    private static final Color MUTED = new Color(105, 105, 105);
    private static final Color BORDER = new Color(228, 228, 228);
    private static final Color PRIMARY = new Color(18, 18, 18);
    private static final Color PRIMARY_HOVER = new Color(40, 40, 40);
    private static final Color LIGHT_GRAY = new Color(242, 242, 242);
    private static final Color DISABLED = new Color(242, 242, 242);

    // Couleur validation
    private static final Color ERROR = new Color(210, 60, 60);

    // ========================= CHAMPS =========================

    private JComboBox<String> comboType;

    private JTextField txtAdresse;
    private JTextField txtEmail;
    private JTextField txtTelephone;

    private JTextField txtNom;
    private JTextField txtPrenom;

    private JTextField txtRaisonSociale;
    private JTextField txtNif;
    private JTextField txtStat;

    private JButton btnEnregistrer;
    private JButton btnAnnuler;

    // ========================= ÉTAT =========================

    private int idModification = -1;

    private JDialog dialog;

    // ========================= CONSTRUCTEURS =========================

    public FournisseurForm() {

        fournisseurService = new FournisseurService();
        personneService = new PersonneService();
        societeService = new SocieteService();

        construireInterface();
    }

    public FournisseurForm(Fournisseur fournisseur) {

        this();

        if (fournisseur != null) {
            chargerFournisseur(fournisseur);
        }
    }

    // ========================= DIALOG =========================

    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    // ========================= INTERFACE =========================

    private void construireInterface() {

        setLayout(new BorderLayout());
        setBackground(SURFACE);

        // ========================= BANDEAU NOIR =========================

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(HEADER_BG);
        header.setBorder(
                new EmptyBorder(
                        24,
                        28,
                        24,
                        28
                )
        );

        JLabel titre = new JLabel(
                "Informations fournisseur"
        );

        titre.setFont(
                titre.getFont().deriveFont(
                        Font.BOLD,
                        22f
                )
        );

        titre.setForeground(Color.WHITE);

        header.add(
                titre,
                BorderLayout.WEST
        );

        add(
                header,
                BorderLayout.NORTH
        );

        // ========================= CORPS =========================

        JPanel corps = new JPanel(
                new MigLayout(
                        "fill, insets 26 28 20 28",
                        "[grow]",
                        "[]18[grow]"
                )
        );

        corps.setOpaque(false);

        // ========================= TYPE =========================

        JPanel typeLigne = new JPanel(
                new MigLayout(
                        "insets 0",
                        "[][300]",
                        "[]"
                )
        );

        typeLigne.setOpaque(false);

        JLabel typeLabel = creerLabel(
                "Type de fournisseur"
        );

        typeLigne.add(
                typeLabel,
                "gapright 16"
        );

        comboType = new JComboBox<>(
                new String[]{
                        "PERSONNE",
                        "SOCIETE"
                }
        );

        styliserComboBox(comboType);

        typeLigne.add(
                comboType,
                "growx, h 34!"
        );

        corps.add(
                typeLigne,
                "growx, wrap"
        );

        // ========================= CHAMPS =========================

        JPanel formCard = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[150][grow]",
                        "[]14[]14[]14[]14[]14[]14[]14[]"
                )
        );

        formCard.setOpaque(false);

        txtNom = creerChamp();
        txtPrenom = creerChamp();

        txtRaisonSociale = creerChamp();

        txtNif = creerChamp();
        txtNif.putClientProperty(
                "JTextField.placeholderText",
                "10 chiffres"
        );

        txtStat = creerChamp();
        txtStat.putClientProperty(
                "JTextField.placeholderText",
                "17 chiffres"
        );

        txtAdresse = creerChamp();
        txtEmail = creerChamp();
        txtEmail.putClientProperty(
                "JTextField.placeholderText",
                "exemple@domaine.com"
        );

        txtTelephone = creerChamp();
        txtTelephone.putClientProperty(
                "JTextField.placeholderText",
                "10 chiffres, ex. 0341234567"
        );

        ajouterLigne(
                formCard,
                "Nom :",
                txtNom
        );

        ajouterLigne(
                formCard,
                "Prénom :",
                txtPrenom
        );

        ajouterLigne(
                formCard,
                "Raison sociale :",
                txtRaisonSociale
        );

        ajouterLigne(
                formCard,
                "NIF :",
                txtNif
        );

        ajouterLigne(
                formCard,
                "STAT :",
                txtStat
        );

        ajouterLigne(
                formCard,
                "Adresse :",
                txtAdresse
        );

        ajouterLigne(
                formCard,
                "Email :",
                txtEmail
        );

        ajouterLigne(
                formCard,
                "Téléphone :",
                txtTelephone
        );

        corps.add(
                formCard,
                "grow, top"
        );

        add(
                corps,
                BorderLayout.CENTER
        );

        // ========================= PIED =========================

        JPanel pied = new JPanel(
                new BorderLayout()
        );

        pied.setOpaque(false);

        pied.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1,
                                0,
                                0,
                                0,
                                BORDER
                        ),
                        new EmptyBorder(
                                16,
                                28,
                                20,
                                28
                        )
                )
        );

        JPanel boutons = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        0
                )
        );

        boutons.setOpaque(false);

        btnAnnuler = creerBoutonSecondaire(
                "Annuler"
        );

        btnEnregistrer = creerBoutonPrincipal(
                "Enregistrer"
        );

        boutons.add(btnAnnuler);
        boutons.add(btnEnregistrer);

        pied.add(
                boutons,
                BorderLayout.EAST
        );

        add(
                pied,
                BorderLayout.SOUTH
        );

        // ========================= ÉVÉNEMENTS =========================

        comboType.addActionListener(
                this::changerType
        );

        btnEnregistrer.addActionListener(
                e -> enregistrer()
        );

        btnAnnuler.addActionListener(
                e -> fermer()
        );

        // Retirer automatiquement l'erreur lorsque
        // l'utilisateur recommence à modifier le champ.

        ajouterEcouteValidation(txtNom);
        ajouterEcouteValidation(txtPrenom);
        ajouterEcouteValidation(txtRaisonSociale);
        ajouterEcouteValidation(txtNif);
        ajouterEcouteValidation(txtStat);
        ajouterEcouteValidation(txtAdresse);
        ajouterEcouteValidation(txtEmail);
        ajouterEcouteValidation(txtTelephone);

        changerType(null);
    }

    // ========================= LABEL =========================

    private JLabel creerLabel(String texte) {

        JLabel label = new JLabel(texte);

        label.setFont(
                label.getFont().deriveFont(
                        Font.BOLD,
                        13f
                )
        );

        label.setForeground(TEXT);

        return label;
    }

    // ========================= LIGNE =========================

    private void ajouterLigne(
            JPanel panel,
            String label,
            JTextField field) {

        panel.add(
                creerLabel(label)
        );

        panel.add(
                field,
                "growx, h 36!, wrap"
        );
    }

    // ========================= CHAMP =========================

    private JTextField creerChamp() {

        JTextField field = new JTextField();

        field.setFont(
                field.getFont().deriveFont(
                        Font.PLAIN,
                        13f
                )
        );

        field.setBackground(SURFACE);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);

        appliquerBordureNormale(field);

        field.putClientProperty(
                "JTextField.showClearButton",
                true
        );

        return field;
    }

    // ========================= COMBOBOX =========================

    private void styliserComboBox(
            JComboBox<String> combo) {

        combo.setFont(
                combo.getFont().deriveFont(
                        Font.PLAIN,
                        13f
                )
        );

        combo.setBackground(SURFACE);
        combo.setForeground(TEXT);
        combo.setFocusable(false);
    }

    // ========================= BOUTON PRINCIPAL =========================

    private JButton creerBoutonPrincipal(
            String texte) {

        JButton button = new JButton(texte) {

            @Override
            protected void paintComponent(
                    Graphics g) {

                Graphics2D g2 =
                        (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(
                        getModel().isRollover()
                                ? PRIMARY_HOVER
                                : PRIMARY
                );

                g2.fillRect(
                        0,
                        0,
                        getWidth(),
                        getHeight()
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);

        button.setFont(
                button.getFont().deriveFont(
                        Font.BOLD,
                        13f
                )
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setBorder(
                new EmptyBorder(
                        11,
                        26,
                        11,
                        26
                )
        );

        return button;
    }

    // ========================= BOUTON SECONDAIRE =========================

    private JButton creerBoutonSecondaire(
            String texte) {

        JButton button = new JButton(texte);

        button.setBackground(SURFACE);
        button.setForeground(TEXT);

        button.setFont(
                button.getFont().deriveFont(
                        Font.PLAIN,
                        13f
                )
        );

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                11,
                                22,
                                11,
                                22
                        )
                )
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e) {

                        button.setBackground(
                                LIGHT_GRAY
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e) {

                        button.setBackground(
                                SURFACE
                        );
                    }
                }
        );

        return button;
    }

    // ========================= CHANGEMENT TYPE =========================

    private void changerType(ActionEvent event) {

        boolean personne =
                "PERSONNE".equals(
                        comboType.getSelectedItem()
                );

        txtNom.setEnabled(personne);
        txtPrenom.setEnabled(personne);

        txtRaisonSociale.setEnabled(!personne);
        txtNif.setEnabled(!personne);
        txtStat.setEnabled(!personne);

        if (personne) {

            activerChamp(txtNom);
            activerChamp(txtPrenom);

            desactiverChamp(
                    txtRaisonSociale
            );

            desactiverChamp(txtNif);
            desactiverChamp(txtStat);

        } else {

            desactiverChamp(txtNom);
            desactiverChamp(txtPrenom);

            activerChamp(
                    txtRaisonSociale
            );

            activerChamp(txtNif);
            activerChamp(txtStat);
        }
    }

    // ========================= CHAMP ACTIF =========================

    private void activerChamp(
            JTextField field) {

        field.setBackground(SURFACE);
        field.setForeground(TEXT);
    }

    // ========================= CHAMP DÉSACTIVÉ =========================

    private void desactiverChamp(
            JTextField field) {

        field.setBackground(DISABLED);
        field.setForeground(MUTED);

        appliquerBordureNormale(field);
    }

    // ========================= CHARGEMENT =========================

    private void chargerFournisseur(
            Fournisseur fournisseur) {

        idModification =
                fournisseur.getIdFournisseur();

        comboType.setSelectedItem(
                fournisseur.getTypeFournisseur()
        );

        txtAdresse.setText(
                valeur(fournisseur.getAdresse())
        );

        txtEmail.setText(
                valeur(fournisseur.getEmail())
        );

        txtTelephone.setText(
                valeur(fournisseur.getTelephone())
        );

        try {

            if ("PERSONNE".equals(
                    fournisseur.getTypeFournisseur())) {

                Personne personne =
                        personneService.findByFournisseur(
                                fournisseur.getIdFournisseur()
                        );

                if (personne != null) {

                    txtNom.setText(
                            valeur(personne.getNom())
                    );

                    txtPrenom.setText(
                            valeur(personne.getPrenom())
                    );
                }

            } else {

                Societe societe =
                        societeService.findByFournisseur(
                                fournisseur.getIdFournisseur()
                        );

                if (societe != null) {

                    txtRaisonSociale.setText(
                            valeur(
                                    societe.getRaisonSociale()
                            )
                    );

                    txtNif.setText(
                            valeur(societe.getNif())
                    );

                    txtStat.setText(
                            valeur(societe.getStat())
                    );
                }
            }

            changerType(null);

            btnEnregistrer.setText(
                    "Modifier"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erreur lors du chargement :\n"
                    + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================= ENREGISTREMENT =========================

    private void enregistrer() {

        try {

            String type =
                    comboType.getSelectedItem() == null
                            ? ""
                            : comboType
                                    .getSelectedItem()
                                    .toString();

            // =========================
            // RÉCUPÉRATION
            // =========================

            String adresse =
                    nettoyerTexte(
                            txtAdresse.getText()
                    );

            String email =
                    nettoyerTexte(
                            txtEmail.getText()
                    );

            String telephone =
                    nettoyerTexte(
                            txtTelephone.getText()
                    );

            // =========================
            // VALIDATION TYPE
            // =========================

            if (!"PERSONNE".equals(type)
                    && !"SOCIETE".equals(type)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Veuillez sélectionner un type de fournisseur.",
                        "Validation",
                        JOptionPane.WARNING_MESSAGE
                );

                comboType.requestFocus();

                return;
            }

            // =========================
            // VALIDATION EMAIL
            // =========================

            if (!email.isEmpty()
                    && !estEmailValide(email)) {

                afficherValidation(
                        "L'adresse email n'est pas valide.\n"
                        + "Exemple : contact@example.com",
                        txtEmail
                );

                return;
            }

            // =========================
            // VALIDATION TELEPHONE
            // =========================

            if (!telephone.isEmpty()
                    && !estTelephoneValide(telephone)) {

                afficherValidation(
                        "Le numéro de téléphone doit contenir "
                        + "exactement 10 chiffres (uniquement des "
                        + "chiffres, sans espace ni symbole).\n"
                        + "Exemple : 0341234567",
                        txtTelephone
                );

                return;
            }

            // =========================
            // OBJET FOURNISSEUR
            // =========================

            Fournisseur fournisseur =
                    new Fournisseur();

            fournisseur.setTypeFournisseur(type);
            fournisseur.setAdresse(adresse);
            fournisseur.setEmail(email);
            fournisseur.setTelephone(telephone);

            // =========================================================
            // PERSONNE
            // =========================================================

            if ("PERSONNE".equals(type)) {

                String nom =
                        nettoyerTexte(
                                txtNom.getText()
                        );

                String prenom =
                        nettoyerTexte(
                                txtPrenom.getText()
                        );

                // -------------------------
                // NOM
                // -------------------------

                if (nom.isEmpty()) {

                    afficherValidation(
                            "Le nom est obligatoire.",
                            txtNom
                    );

                    return;
                }

                if (!estTexteValide(nom)) {

                    afficherValidation(
                            "Le nom ne doit contenir que des lettres "
                            + "(pas de chiffres ni de caractères spéciaux).",
                            txtNom
                    );

                    return;
                }

                // -------------------------
                // PRÉNOM
                // -------------------------

                if (prenom.isEmpty()) {

                    afficherValidation(
                            "Le prénom est obligatoire.",
                            txtPrenom
                    );

                    return;
                }

                if (!estTexteValide(prenom)) {

                    afficherValidation(
                            "Le prénom ne doit contenir que des lettres "
                            + "(pas de chiffres ni de caractères spéciaux).",
                            txtPrenom
                    );

                    return;
                }

                // -------------------------
                // PERSONNE
                // -------------------------

                Personne personne =
                        new Personne();

                personne.setNom(nom);
                personne.setPrenom(prenom);

                // =========================
                // AJOUT
                // =========================

                if (idModification == -1) {

                    fournisseurService.ajouter(
                            fournisseur,
                            personne,
                            null
                    );

                    afficherSucces(
                            "Fournisseur ajouté avec succès."
                    );

                } else {

                    // =========================
                    // MODIFICATION
                    // =========================

                    fournisseur.setIdFournisseur(
                            idModification
                    );

                    fournisseurService.modifier(
                            fournisseur
                    );

                    personne.setIdFournisseur(
                            idModification
                    );

                    personneService.modifier(
                            personne
                    );

                    afficherSucces(
                            "Fournisseur modifié avec succès."
                    );
                }

            // =========================================================
            // SOCIETE
            // =========================================================

            } else {

                String raisonSociale =
                        nettoyerTexte(
                                txtRaisonSociale.getText()
                        );

                String nif =
                        nettoyerTexte(
                                txtNif.getText()
                        );

                String stat =
                        nettoyerTexte(
                                txtStat.getText()
                        );

                // -------------------------
                // RAISON SOCIALE
                // -------------------------

                if (raisonSociale.isEmpty()) {

                    afficherValidation(
                            "La raison sociale est obligatoire.",
                            txtRaisonSociale
                    );

                    return;
                }

                // -------------------------
                // NIF
                // -------------------------

                if (nif.isEmpty()) {

                    afficherValidation(
                            "Le NIF est obligatoire.",
                            txtNif
                    );

                    return;
                }

                if (!estNifValide(nif)) {

                    afficherValidation(
                            "Le NIF doit contenir exactement "
                            + "10 chiffres (uniquement des chiffres).",
                            txtNif
                    );

                    return;
                }

                // -------------------------
                // STAT
                // -------------------------

                if (stat.isEmpty()) {

                    afficherValidation(
                            "Le STAT est obligatoire.",
                            txtStat
                    );

                    return;
                }

                if (!estStatValide(stat)) {

                    afficherValidation(
                            "Le STAT doit contenir exactement "
                            + "17 chiffres (uniquement des chiffres).",
                            txtStat
                    );

                    return;
                }

                // -------------------------
                // SOCIETE
                // -------------------------

                Societe societe =
                        new Societe();

                societe.setRaisonSociale(
                        raisonSociale
                );

                societe.setNif(nif);
                societe.setStat(stat);

                // =========================
                // AJOUT
                // =========================

                if (idModification == -1) {

                    fournisseurService.ajouter(
                            fournisseur,
                            null,
                            societe
                    );

                    afficherSucces(
                            "Fournisseur ajouté avec succès."
                    );

                } else {

                    // =========================
                    // MODIFICATION
                    // =========================

                    fournisseur.setIdFournisseur(
                            idModification
                    );

                    fournisseurService.modifier(
                            fournisseur
                    );

                    societe.setIdFournisseur(
                            idModification
                    );

                    societeService.modifier(
                            societe
                    );

                    afficherSucces(
                            "Fournisseur modifié avec succès."
                    );
                }
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
        }
    }

    // ========================= VALIDATION =========================

    /**
     * Vérifie le format d'un email.
     */
    private boolean estEmailValide(
            String email) {

        return email.matches(
                "^[A-Za-z0-9._%+-]+"
                + "@"
                + "[A-Za-z0-9.-]+"
                + "\\."
                + "[A-Za-z]{2,}$"
        );
    }

    /**
     * Vérifie le numéro de téléphone.
     *
     * Doit être strictement numérique et contenir
     * exactement 10 chiffres (ex. 0341234567).
     */
    private boolean estTelephoneValide(
            String telephone) {

        return telephone.matches(
                "^[0-9]{10}$"
        );
    }

    /**
     * Vérifie un nom ou prénom.
     *
     * Autorise :
     * lettres
     * accents
     * espaces
     * apostrophes
     * tirets
     */
    private boolean estTexteValide(
            String texte) {

        return texte.matches(
                "^[\\p{L}À-ÿ' -]+$"
        );
    }

    /**
     * Vérifie le NIF.
     *
     * Doit être strictement numérique et contenir
     * exactement 10 chiffres.
     */
    private boolean estNifValide(
            String nif) {

        return nif.matches(
                "^[0-9]{10}$"
        );
    }

    /**
     * Vérifie le STAT.
     *
     * Doit être strictement numérique et contenir
     * exactement 17 chiffres.
     */
    private boolean estStatValide(
            String stat) {

        return stat.matches(
                "^[0-9]{17}$"
        );
    }

    /**
     * Nettoie les espaces inutiles.
     */
    private String nettoyerTexte(
            String texte) {

        if (texte == null) {
            return "";
        }

        return texte
                .trim()
                .replaceAll("\\s+", " ");
    }

    // ========================= VALIDATION VISUELLE =========================

    /**
     * Affiche un message de validation et
     * marque le champ concerné en rouge.
     */
    private void afficherValidation(
            String message,
            JTextField field) {

        marquerErreur(field);

        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation",
                JOptionPane.WARNING_MESSAGE
        );

        field.requestFocus();
        field.selectAll();
    }

    /**
     * Bordure rouge pour un champ invalide.
     */
    private void marquerErreur(
            JTextField field) {

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                ERROR,
                                1
                        ),
                        new EmptyBorder(
                                0,
                                10,
                                0,
                                10
                        )
                )
        );
    }

    /**
     * Bordure normale.
     */
    private void appliquerBordureNormale(
            JTextField field) {

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        new EmptyBorder(
                                0,
                                10,
                                0,
                                10
                        )
                )
        );
    }

    /**
     * Quand l'utilisateur recommence à écrire,
     * on retire la bordure rouge.
     */
    private void ajouterEcouteValidation(
            JTextField field) {

        field.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyTyped(
                            KeyEvent e) {

                        appliquerBordureNormale(
                                field
                        );
                    }

                    @Override
                    public void keyPressed(
                            KeyEvent e) {

                        appliquerBordureNormale(
                                field
                        );
                    }
                }
        );
    }

    // ========================= SUCCÈS =========================

    private void afficherSucces(
            String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Succès",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ========================= FERMETURE =========================

    private void fermer() {

        if (dialog != null) {
            dialog.dispose();
        }
    }

    // ========================= VALEUR =========================

    private String valeur(
            String valeur) {

        return valeur == null
                ? ""
                : valeur;
    }
}