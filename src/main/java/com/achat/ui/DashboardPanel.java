package com.achat.ui;

import com.achat.database.DatabaseConnection;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

/**
 * Tableau de bord de l'application Achat.
 *
 * Les statistiques sont récupérées directement depuis SQLite afin
 * d'afficher les données réellement présentes dans la base.
 */
public class DashboardPanel extends JPanel {

    private JLabel lblFournisseurs;
    private JLabel lblProduits;
    private JLabel lblAlertes;
    private JLabel lblCommandes;
    private JLabel lblReceptions;
    private JLabel lblMontantAchats;

    private JLabel lblDateMaj;

    private static final Color BACKGROUND = new Color(248, 249, 251);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT = new Color(28, 30, 33);
    private static final Color SECONDARY_TEXT = new Color(105, 110, 116);
    private static final Color BORDER = new Color(225, 228, 232);
    private static final Color DANGER = new Color(190, 60, 60);

    public DashboardPanel() {

        setBackground(BACKGROUND);

        setLayout(new BorderLayout());

        creerInterface();
        chargerStatistiques();
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
        // EN-TÊTE (titre + date de mise à jour + bouton actualiser)
        // ============================================================

        JPanel headerBloc = new JPanel(
                new BorderLayout(0, 15)
        );

        headerBloc.setOpaque(false);

        JPanel header = new JPanel(
                new MigLayout(
                        "insets 0",
                        "[grow][]"
                )
        );

        header.setOpaque(false);

        JPanel titres = new JPanel(
                new MigLayout(
                        "insets 0",
                        "[grow]"
                )
        );

        titres.setOpaque(false);

        JLabel title = new JLabel("Tableau de bord");

        title.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        28
                )
        );

        title.setForeground(TEXT);

        JLabel subtitle = new JLabel(
                "Vue d'ensemble de la gestion des achats"
        );

        subtitle.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        subtitle.setForeground(SECONDARY_TEXT);

        titres.add(title, "wrap");
        titres.add(subtitle);

        lblDateMaj = new JLabel();
        lblDateMaj.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );
        lblDateMaj.setForeground(SECONDARY_TEXT);

        header.add(titres, "growx");
        header.add(lblDateMaj, "right");

        headerBloc.add(
                header,
                BorderLayout.NORTH
        );

        // ============================================================
        // BOUTON ACTUALISER
        // ============================================================

        JPanel actionPanel = new JPanel(
                new MigLayout(
                        "insets 0",
                        "[grow][]"
                )
        );

        actionPanel.setOpaque(false);

        JLabel info = new JLabel(
                "Statistiques calculées à partir des données enregistrées"
        );

        info.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        info.setForeground(SECONDARY_TEXT);

        JButton btnActualiser =
                creerBoutonActualiser("Actualiser");

        btnActualiser.addActionListener(
                e -> chargerStatistiques()
        );

        actionPanel.add(info, "growx");
        actionPanel.add(btnActualiser);

        headerBloc.add(
                actionPanel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                headerBloc,
                BorderLayout.NORTH
        );

        // ============================================================
        // CARTES
        // ============================================================

        JPanel cards = new JPanel(
                new MigLayout(
                        "fill, wrap 3",
                        "[grow][grow][grow]",
                        "[145][145]"
                )
        );

        cards.setOpaque(false);

        lblFournisseurs = new JLabel("0");
        lblProduits = new JLabel("0");
        lblAlertes = new JLabel("0");
        lblCommandes = new JLabel("0");
        lblReceptions = new JLabel("0");
        lblMontantAchats = new JLabel("0 Ar");

        cards.add(
                creerCard(
                        "Nombre de fournisseurs",
                        lblFournisseurs,
                        "Fournisseurs enregistrés"
                ),
                "grow"
        );

        cards.add(
                creerCard(
                        "Nombre de produits",
                        lblProduits,
                        "Produits dans le catalogue"
                ),
                "grow"
        );

        cards.add(
                creerCard(
                        "Produits en alerte",
                        lblAlertes,
                        "Stock inférieur ou égal au seuil",
                        true
                ),
                "grow"
        );

        cards.add(
                creerCard(
                        "Commandes en cours",
                        lblCommandes,
                        "Commandes non soldées"
                ),
                "grow"
        );

        cards.add(
                creerCard(
                        "Réceptions récentes",
                        lblReceptions,
                        "Réceptions des 30 derniers jours"
                ),
                "grow"
        );

        cards.add(
                creerCard(
                        "Montant des achats",
                        lblMontantAchats,
                        "Total des lignes de commande"
                ),
                "grow"
        );

        mainPanel.add(
                cards,
                BorderLayout.CENTER
        );

        add(
                mainPanel,
                BorderLayout.CENTER
        );
    }

    // ================================================================
    // CARTES
    // ================================================================

    private JPanel creerCard(
            String titre,
            JLabel valeur,
            String description
    ) {
        return creerCard(
                titre,
                valeur,
                description,
                false
        );
    }

    private JPanel creerCard(
            String titre,
            JLabel valeur,
            String description,
            boolean alerte
    ) {

        JPanel card = new JPanel(
                new MigLayout(
                        "fill, insets 20",
                        "[grow]",
                        "[][grow][]"
                )
        );

        card.setBackground(CARD_BACKGROUND);

        card.setBorder(
                new RoundedBorder(
                        alerte
                                ? new Color(235, 205, 205)
                                : BORDER,
                        1,
                        12
                )
        );

        JLabel lblTitre = new JLabel(titre);

        lblTitre.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        lblTitre.setForeground(
                alerte
                        ? DANGER
                        : SECONDARY_TEXT
        );

        valeur.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        30
                )
        );

        valeur.setForeground(
                alerte
                        ? DANGER
                        : TEXT
        );

        JLabel lblDescription =
                new JLabel(description);

        lblDescription.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        12
                )
        );

        lblDescription.setForeground(
                SECONDARY_TEXT
        );

        card.add(
                lblTitre,
                "growx, wrap"
        );

        card.add(
                valeur,
                "growx, push"
        );

        card.add(
                lblDescription,
                "growx"
        );

        return card;
    }

    // ================================================================
    // STATISTIQUES
    // ================================================================

    private void chargerStatistiques() {

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            int fournisseurs =
                    compter(
                            connection,
                            "SELECT COUNT(*) FROM FOURNISSEUR"
                    );

            int produits =
                    compter(
                            connection,
                            "SELECT COUNT(*) FROM PRODUIT"
                    );

            int alertes =
                    compter(
                            connection,
                            """
                            SELECT COUNT(*)
                            FROM PRODUIT
                            WHERE stock_actuel <= stock_alerte
                            """
                    );

            int commandes =
                    compter(
                            connection,
                            """
                            SELECT COUNT(*)
                            FROM COMMANDE
                            WHERE UPPER(TRIM(etat_commande)) = 'EN COURS'
                            """
                    );

            int receptions =
                    compter(
                            connection,
                            """
                            SELECT COUNT(*)
                            FROM RECEPTION
                            WHERE date_reception >= date('now', '-30 day')
                            """
                    );

            double montantAchats =
                    calculerMontantAchats(connection);

            lblFournisseurs.setText(
                    formatNombre(fournisseurs)
            );

            lblProduits.setText(
                    formatNombre(produits)
            );

            lblAlertes.setText(
                    formatNombre(alertes)
            );

            lblCommandes.setText(
                    formatNombre(commandes)
            );

            lblReceptions.setText(
                    formatNombre(receptions)
            );

            lblMontantAchats.setText(
                    formatMontant(montantAchats)
            );

            lblDateMaj.setText(
                    "Mis à jour le "
                            + LocalDate.now()
            );

        } catch (SQLException e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Impossible de charger les statistiques.\n\n"
                            + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private int compter(
            Connection connection,
            String sql
    ) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result =
                     statement.executeQuery()) {

            if (result.next()) {
                return result.getInt(1);
            }

            return 0;
        }
    }

    /**
     * Montant total des achats = somme de :
     * quantité commandée × prix unitaire d'achat.
     */
    private double calculerMontantAchats(
            Connection connection
    ) throws SQLException {

        String sql = """
                SELECT COALESCE(
                    SUM(
                        quantite_commandee
                        * prix_unitaire_achat
                    ),
                    0
                )
                FROM LIGNE_COMMANDE
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result =
                     statement.executeQuery()) {

            if (result.next()) {
                return result.getDouble(1);
            }

            return 0.0;
        }
    }

    // ================================================================
    // FORMATAGE
    // ================================================================

    private String formatNombre(int nombre) {

        return NumberFormat
                .getIntegerInstance(
                        Locale.FRANCE
                )
                .format(nombre);
    }

    private String formatMontant(double montant) {

        NumberFormat format =
                NumberFormat.getNumberInstance(
                        Locale.FRANCE
                );

        format.setMaximumFractionDigits(0);
        format.setMinimumFractionDigits(0);

        return format.format(montant) + " Ar";
    }

    // ================================================================
    // BOUTON
    // ================================================================

    private JButton creerBoutonActualiser(
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
        bouton.setBackground(CARD_BACKGROUND);
        bouton.setFocusPainted(false);

        bouton.setBorder(
                BorderFactory.createCompoundBorder(
                        new RoundedBorder(
                                BORDER,
                                1,
                                8
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                16,
                                9,
                                16
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