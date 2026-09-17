package com.achat.ui;

import com.achat.ui.commande.CommandePanel;
import com.achat.ui.fournisseur.FournisseurPanel;
import com.achat.ui.produit.ProduitPanel;
import com.achat.ui.reception.ReceptionPanel;
import com.achat.ui.facture.FacturePanel;
import com.achat.ui.proposer.ProposerPanel;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Fenêtre principale de l'application Achat.
 *
 * Design : - Noir / blanc - Sidebar fixe - Navigation avec CardLayout - Coins
 * arrondis - Boutons minimiser / fermer personnalisés - Pas de barre de
 * recherche dans MainFrame
 */
public class MainFrame extends JFrame {

    // =========================================================
    // DIMENSIONS
    // =========================================================
    private static final int WIDTH = 1450;
    private static final int HEIGHT = 850;

    private static final int MIN_WIDTH = 1100;
    private static final int MIN_HEIGHT = 650;

    private static final int SIDEBAR_WIDTH = 235;

    private static final int RADIUS = 28;

    // =========================================================
    // COULEURS
    // =========================================================
    private static final Color BLACK
            = new Color(18, 18, 18);

    private static final Color BLACK_HOVER
            = new Color(40, 40, 40);

    private static final Color WHITE
            = new Color(255, 255, 255);

    private static final Color LIGHT_GRAY
            = new Color(242, 242, 242);

    private static final Color BORDER
            = new Color(228, 228, 228);

    private static final Color TEXT
            = new Color(25, 25, 25);

    private static final Color GRAY
            = new Color(165, 165, 165);

    private static final Color DARK_GRAY
            = new Color(105, 105, 105);

    private static final Color RED_HOVER
            = new Color(225, 55, 55);

    // =========================================================
    // VARIABLES
    // =========================================================
    private JPanel contentPanel;

    private JButton activeButton;

    private Point mouseLocation;

    // =========================================================
    // CONSTRUCTEUR
    // =========================================================
    public MainFrame() {

        // -----------------------------------------------------
        // CONFIGURATION DE LA FENÊTRE
        // -----------------------------------------------------
        setTitle("Achat - Gestion des achats");

        setSize(
                WIDTH,
                HEIGHT
        );

        setMinimumSize(
                new Dimension(
                        MIN_WIDTH,
                        MIN_HEIGHT
                )
        );

        setLocationRelativeTo(null);

        setUndecorated(true);

        setBackground(
                new Color(
                        0,
                        0,
                        0,
                        0
                )
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        // -----------------------------------------------------
        // PANEL PRINCIPAL
        // -----------------------------------------------------
        RoundedPanel mainPanel
                = new RoundedPanel(RADIUS);

        mainPanel.setLayout(
                new BorderLayout()
        );

        mainPanel.setBackground(
                WHITE
        );

        // -----------------------------------------------------
        // SIDEBAR
        // -----------------------------------------------------
        JPanel sidebar
                = createSidebar();

        // -----------------------------------------------------
        // PARTIE DROITE
        // -----------------------------------------------------
        JPanel rightPanel
                = new JPanel(
                        new BorderLayout()
                );

        rightPanel.setBackground(
                WHITE
        );

        // -----------------------------------------------------
        // BARRE SUPÉRIEURE
        // -----------------------------------------------------
        JPanel topBar
                = createTopBar();

        // -----------------------------------------------------
        // CONTENT PANEL
        // -----------------------------------------------------
        contentPanel
                = new JPanel(
                        new CardLayout()
                );

        contentPanel.setBackground(
                WHITE
        );

        contentPanel.setOpaque(
                true
        );

        // -----------------------------------------------------
        // DASHBOARD
        // -----------------------------------------------------
        contentPanel.add(
                new DashboardPanel(),
                "dashboard"
        );

        // -----------------------------------------------------
        // FOURNISSEURS
        // -----------------------------------------------------
        contentPanel.add(
                new FournisseurPanel(),
                "fournisseur"
        );

        // -----------------------------------------------------
        // PRODUITS
        // -----------------------------------------------------
        contentPanel.add(
                new ProduitPanel(),
                "produit"
        );

        // -----------------------------------------------------
        // COMMANDES
        // -----------------------------------------------------
        contentPanel.add(
                new CommandePanel(),
                "commande"
        );

        contentPanel.add(
                new ReceptionPanel(),
                "reception"
        );

        // -----------------------------------------------------
        // FACTURES
        // -----------------------------------------------------
        contentPanel.add(
                new FacturePanel(),
                "facture"
        );

        // -----------------------------------------------------
        // PROPOSITIONS FOURNISSEUR / PRODUIT
        // -----------------------------------------------------
        contentPanel.add(
                new ProposerPanel(),
                "proposer"
        );

        // -----------------------------------------------------
        // WRAPPER DU CONTENU
        // -----------------------------------------------------
        JPanel contentWrapper
                = new JPanel(
                        new BorderLayout()
                );

        contentWrapper.setBackground(
                WHITE
        );

        /*
         * Très important :
         *
         * On ne donne PAS de largeur fixe au contenu.
         * Il prendra automatiquement toute la place disponible.
         */
        contentWrapper.setBorder(
                new EmptyBorder(
                        0,
                        8,
                        8,
                        8
                )
        );

        contentWrapper.add(
                contentPanel,
                BorderLayout.CENTER
        );

        // -----------------------------------------------------
        // PARTIE DROITE
        // -----------------------------------------------------
        rightPanel.add(
                topBar,
                BorderLayout.NORTH
        );

        rightPanel.add(
                contentWrapper,
                BorderLayout.CENTER
        );

        // -----------------------------------------------------
        // ASSEMBLAGE
        // -----------------------------------------------------
        mainPanel.add(
                sidebar,
                BorderLayout.WEST
        );

        mainPanel.add(
                rightPanel,
                BorderLayout.CENTER
        );

        // -----------------------------------------------------
        // CONTENT PANE
        // -----------------------------------------------------
        setContentPane(
                mainPanel
        );

        // -----------------------------------------------------
        // FORME ARRONDIE DE LA FENÊTRE
        // -----------------------------------------------------
        updateWindowShape();

        addComponentListener(
                new java.awt.event.ComponentAdapter() {

            @Override
            public void componentResized(
                    java.awt.event.ComponentEvent e) {

                updateWindowShape();
            }
        }
        );

        // -----------------------------------------------------
        // DÉPLACEMENT DE LA FENÊTRE
        // -----------------------------------------------------
        enableWindowDrag(
                topBar
        );

        // -----------------------------------------------------
        // DASHBOARD ACTIF AU DÉMARRAGE
        // -----------------------------------------------------
        SwingUtilities.invokeLater(
                () -> {

                    JButton dashboardButton
                    = findMenuButton(
                            "Dashboard"
                    );

                    if (dashboardButton != null) {

                        setActiveButton(
                                dashboardButton
                        );
                    }
                }
        );
    }

    // =========================================================
    // FORME DE LA FENÊTRE
    // =========================================================
    private void updateWindowShape() {

        if (!GraphicsEnvironment
                .isHeadless()) {

            setShape(
                    new RoundRectangle2D.Double(
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            RADIUS,
                            RADIUS
                    )
            );
        }
    }

    // =========================================================
    // SIDEBAR
    // =========================================================
    private JPanel createSidebar() {

        JPanel sidebar
                = new JPanel(
                        new MigLayout(
                                "wrap, fillx",
                                "[grow,fill]",
                                "[]18[]10[]8[]8[]"
                        )
                ) {

            @Override
            protected void paintComponent(
                    Graphics g) {

                Graphics2D g2
                        = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(
                        BLACK
                );

                int w
                        = getWidth();

                int h
                        = getHeight();

                int r
                        = RADIUS;

                Path2D path
                        = new Path2D.Double();

                path.moveTo(
                        r,
                        0
                );

                path.lineTo(
                        w,
                        0
                );

                path.lineTo(
                        w,
                        h
                );

                path.lineTo(
                        r,
                        h
                );

                path.quadTo(
                        0,
                        h,
                        0,
                        h - r
                );

                path.lineTo(
                        0,
                        r
                );

                path.quadTo(
                        0,
                        0,
                        r,
                        0
                );

                path.closePath();

                g2.fill(
                        path
                );

                g2.dispose();
            }
        };

        sidebar.setOpaque(
                false
        );

        sidebar.setPreferredSize(
                new Dimension(
                        SIDEBAR_WIDTH,
                        0
                )
        );

        // =====================================================
        // LOGO
        // =====================================================
        JPanel logoPanel
                = new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        logoPanel.setOpaque(
                false
        );

        logoPanel.setBorder(
                new EmptyBorder(
                        24,
                        20,
                        18,
                        15
                )
        );

        JLabel cart
                = new JLabel(
                        new CartIcon(
                                WHITE,
                                25
                        )
                );

        cart.setBorder(
                new EmptyBorder(
                        0,
                        0,
                        0,
                        12
                )
        );

        JLabel logo
                = new JLabel(
                        "Achat"
                );

        logo.setForeground(
                WHITE
        );

        logo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        22
                )
        );

        logoPanel.add(
                cart
        );

        logoPanel.add(
                logo
        );

        sidebar.add(
                logoPanel,
                "growx"
        );

        // =====================================================
        // TITRE MENU
        // =====================================================
        JLabel menuLabel
                = new JLabel(
                        "MENU"
                );

        menuLabel.setForeground(
                DARK_GRAY
        );

        menuLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        10
                )
        );

        menuLabel.setBorder(
                new EmptyBorder(
                        0,
                        20,
                        5,
                        0
                )
        );

        sidebar.add(
                menuLabel,
                "growx"
        );

        // =====================================================
        // DASHBOARD
        // =====================================================
        sidebar.add(
                createMenuButton(
                        new HomeIcon(
                                GRAY,
                                21
                        ),
                        "Dashboard",
                        "dashboard"
                ),
                "growx"
        );

        // =====================================================
        // FOURNISSEURS
        // =====================================================
        sidebar.add(
                createMenuButton(
                        new UsersIcon(
                                GRAY,
                                21
                        ),
                        "Fournisseurs",
                        "fournisseur"
                ),
                "growx"
        );

        // =====================================================
        // PRODUITS
        // =====================================================
        sidebar.add(
                createMenuButton(
                        new BoxIcon(
                                GRAY,
                                21
                        ),
                        "Produits",
                        "produit"
                ),
                "growx"
        );

        // =====================================================
        // COMMANDES
        // =====================================================
        sidebar.add(
                createMenuButton(
                        new CommandIcon(
                                GRAY,
                                21
                        ),
                        "Commandes",
                        "commande"
                ),
                "growx"
        );

        sidebar.add(
                createMenuButton(
                        new ReceptionIcon(
                                GRAY,
                                21
                        ),
                        "Réceptions",
                        "reception"
                ),
                "growx"
        );

        // =====================================================
        // FACTURES
        // =====================================================
        sidebar.add(
                createMenuButton(
                        new FactureIcon(
                                GRAY,
                                21
                        ),
                        "Factures",
                        "facture"
                ),
                "growx"
        );

        // =====================================================
        // PROPOSER
        // =====================================================
        sidebar.add(
                createMenuButton(
                        new ProposerIcon(
                                GRAY,
                                21
                        ),
                        "Proposer",
                        "proposer"
                ),
                "growx"
        );

        return sidebar;
    }

    // =========================================================
    // BOUTON MENU
    // =========================================================
    private JButton createMenuButton(
            Icon icon,
            String text,
            String cardName) {

        JButton button
                = new JButton() {

            @Override
            protected void paintComponent(
                    Graphics g) {

                Graphics2D g2
                        = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                // -------------------------------------------------
                // ACTIF
                // -------------------------------------------------
                if (this == activeButton) {

                    g2.setColor(
                            WHITE
                    );

                    g2.fillRoundRect(
                            4,
                            0,
                            getWidth() - 8,
                            getHeight(),
                            12,
                            12
                    );
                } // -------------------------------------------------
                // HOVER
                // -------------------------------------------------
                else if (getModel().isRollover()) {

                    g2.setColor(
                            BLACK_HOVER
                    );

                    g2.fillRoundRect(
                            4,
                            0,
                            getWidth() - 8,
                            getHeight(),
                            12,
                            12
                    );
                }

                g2.dispose();

                super.paintComponent(
                        g
                );
            }
        };

        // =====================================================
        // ICÔNE
        // =====================================================
        JLabel iconLabel
                = new JLabel(
                        icon
                );

        iconLabel.setPreferredSize(
                new Dimension(
                        43,
                        43
                )
        );

        iconLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        // =====================================================
        // TEXTE
        // =====================================================
        JLabel textLabel
                = new JLabel(
                        text
                );

        textLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        textLabel.setForeground(
                GRAY
        );

        // =====================================================
        // LAYOUT
        // =====================================================
        button.setLayout(
                new BorderLayout()
        );

        button.add(
                iconLabel,
                BorderLayout.WEST
        );

        button.add(
                textLabel,
                BorderLayout.CENTER
        );

        // =====================================================
        // DIMENSIONS
        // =====================================================
        button.setPreferredSize(
                new Dimension(
                        215,
                        46
                )
        );

        button.setMinimumSize(
                new Dimension(
                        190,
                        46
                )
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        7,
                        0,
                        7
                )
        );

        button.setContentAreaFilled(
                false
        );

        button.setFocusPainted(
                false
        );

        button.setBorderPainted(
                false
        );

        button.setOpaque(
                false
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        // =====================================================
        // PROPRIÉTÉS
        // =====================================================
        button.putClientProperty(
                "menuText",
                text
        );

        button.putClientProperty(
                "cardName",
                cardName
        );

        button.putClientProperty(
                "menuIcon",
                icon
        );

        // =====================================================
        // ACTION
        // =====================================================
        button.addActionListener(
                e -> {

                    showPanel(
                            cardName
                    );

                    setActiveButton(
                            button
                    );
                }
        );

        // =====================================================
        // HOVER
        // =====================================================
        button.addMouseListener(
                new MouseAdapter() {

            @Override
            public void mouseEntered(
                    MouseEvent e) {

                if (button != activeButton) {

                    iconLabel.setIcon(
                            changeIconColor(
                                    icon,
                                    WHITE
                            )
                    );

                    textLabel.setForeground(
                            WHITE
                    );
                }

                button.repaint();
            }

            @Override
            public void mouseExited(
                    MouseEvent e) {

                if (button != activeButton) {

                    iconLabel.setIcon(
                            changeIconColor(
                                    icon,
                                    GRAY
                            )
                    );

                    textLabel.setForeground(
                            GRAY
                    );
                }

                button.repaint();
            }
        }
        );

        return button;
    }

    // =========================================================
    // CHANGER COULEUR ICÔNE
    // =========================================================
    private Icon changeIconColor(
            Icon icon,
            Color color) {

        if (icon instanceof ColorableIcon) {

            return ((ColorableIcon) icon)
                    .withColor(color);
        }

        return icon;
    }

    // =========================================================
    // BOUTON ACTIF
    // =========================================================
    private void setActiveButton(
            JButton button) {

        // -----------------------------------------------------
        // ANCIEN BOUTON
        // -----------------------------------------------------
        if (activeButton != null) {

            JLabel oldText
                    = getTextLabel(
                            activeButton
                    );

            if (oldText != null) {

                oldText.setForeground(
                        GRAY
                );

                oldText.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                13
                        )
                );
            }

            JLabel oldIcon
                    = getIconLabel(
                            activeButton
                    );

            if (oldIcon != null) {

                Icon old
                        = (Icon) activeButton
                                .getClientProperty(
                                        "menuIcon"
                                );

                oldIcon.setIcon(
                        changeIconColor(
                                old,
                                GRAY
                        )
                );
            }

            activeButton.repaint();
        }

        // -----------------------------------------------------
        // NOUVEAU BOUTON
        // -----------------------------------------------------
        activeButton
                = button;

        JLabel text
                = getTextLabel(
                        button
                );

        if (text != null) {

            text.setForeground(
                    BLACK
            );

            text.setFont(
                    new Font(
                            "SansSerif",
                            Font.BOLD,
                            13
                    )
            );
        }

        JLabel iconLabel
                = getIconLabel(
                        button
                );

        if (iconLabel != null) {

            Icon icon
                    = (Icon) button
                            .getClientProperty(
                                    "menuIcon"
                            );

            iconLabel.setIcon(
                    changeIconColor(
                            icon,
                            BLACK
                    )
            );
        }

        button.repaint();
    }

    // =========================================================
    // RÉCUPÉRER LABEL TEXTE
    // =========================================================
    private JLabel getTextLabel(
            JButton button) {

        JLabel iconLabel
                = getIconLabel(
                        button
                );

        for (Component component
                : button.getComponents()) {

            if (component instanceof JLabel
                    && component != iconLabel) {

                return (JLabel) component;
            }
        }

        return null;
    }

    // =========================================================
    // RÉCUPÉRER LABEL ICÔNE
    // =========================================================
    private JLabel getIconLabel(
            JButton button) {

        for (Component component
                : button.getComponents()) {

            if (component instanceof JLabel
                    && ((JLabel) component).getIcon() != null) {

                return (JLabel) component;
            }
        }

        return null;
    }

    // =========================================================
    // RECHERCHER BOUTON MENU
    // =========================================================
    private JButton findMenuButton(
            String text) {

        return findButtonRecursive(
                getContentPane(),
                text
        );
    }

    private JButton findButtonRecursive(
            Container container,
            String text) {

        for (Component component
                : container.getComponents()) {

            if (component instanceof JButton) {

                JButton button
                        = (JButton) component;

                Object menuText
                        = button.getClientProperty(
                                "menuText"
                        );

                if (text.equals(menuText)) {

                    return button;
                }
            }

            if (component instanceof Container) {

                JButton result
                        = findButtonRecursive(
                                (Container) component,
                                text
                        );

                if (result != null) {

                    return result;
                }
            }
        }

        return null;
    }

    // =========================================================
    // NAVIGATION
    // =========================================================
    private void showPanel(
            String panelName) {

        CardLayout layout
                = (CardLayout) contentPanel
                        .getLayout();

        layout.show(
                contentPanel,
                panelName
        );

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // =========================================================
    // BARRE SUPÉRIEURE
    // =========================================================
    private JPanel createTopBar() {

        JPanel topBar
                = new JPanel(
                        new BorderLayout()
                );

        topBar.setBackground(
                WHITE
        );

        topBar.setPreferredSize(
                new Dimension(
                        0,
                        58
                )
        );

        topBar.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        BORDER
                )
        );

        // =====================================================
        // TITRE
        // =====================================================
        JLabel title
                = new JLabel(
                        "Gestion des achats"
                );

        title.setForeground(
                TEXT
        );

        title.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        15
                )
        );

        title.setBorder(
                new EmptyBorder(
                        0,
                        20,
                        0,
                        0
                )
        );

        topBar.add(
                title,
                BorderLayout.WEST
        );

        // =====================================================
        // BOUTONS FENÊTRE
        // =====================================================
        JPanel windowButtons
                = new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                4,
                                10
                        )
                );

        windowButtons.setOpaque(
                false
        );

        windowButtons.setBorder(
                new EmptyBorder(
                        0,
                        0,
                        0,
                        10
                )
        );

        // -----------------------------------------------------
        // MINIMISER
        // -----------------------------------------------------
        JButton minimize
                = createWindowButton(
                        new MinimizeIcon(
                                BLACK,
                                15
                        ),
                        false
                );

        // -----------------------------------------------------
        // FERMER
        // -----------------------------------------------------
        JButton close
                = createWindowButton(
                        new CloseIcon(
                                BLACK,
                                15
                        ),
                        true
                );

        // -----------------------------------------------------
        // ACTION MINIMISER
        // -----------------------------------------------------
        minimize.addActionListener(
                e -> setState(
                        JFrame.ICONIFIED
                )
        );

        // -----------------------------------------------------
        // ACTION FERMER
        // -----------------------------------------------------
        close.addActionListener(
                e -> {

                    int result
                    = JOptionPane.showConfirmDialog(
                            this,
                            "Voulez-vous vraiment quitter l'application ?",
                            "Quitter",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (result
                    == JOptionPane.YES_OPTION) {

                        System.exit(0);
                    }
                }
        );

        windowButtons.add(
                minimize
        );

        windowButtons.add(
                close
        );

        topBar.add(
                windowButtons,
                BorderLayout.EAST
        );

        return topBar;
    }

    // =========================================================
    // BOUTON FENÊTRE
    // =========================================================
    private JButton createWindowButton(
            Icon icon,
            boolean closeButton) {

        JButton button
                = new JButton(
                        icon
                );

        button.setPreferredSize(
                new Dimension(
                        36,
                        36
                )
        );

        button.setFocusPainted(
                false
        );

        button.setBorderPainted(
                false
        );

        button.setContentAreaFilled(
                false
        );

        button.setOpaque(
                false
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.addMouseListener(
                new MouseAdapter() {

            @Override
            public void mouseEntered(
                    MouseEvent e) {

                button.setOpaque(
                        true
                );

                if (closeButton) {

                    button.setBackground(
                            RED_HOVER
                    );

                    button.setIcon(
                            new CloseIcon(
                                    WHITE,
                                    15
                            )
                    );

                } else {

                    button.setBackground(
                            LIGHT_GRAY
                    );

                    button.setIcon(
                            new MinimizeIcon(
                                    BLACK,
                                    15
                            )
                    );
                }

                button.repaint();
            }

            @Override
            public void mouseExited(
                    MouseEvent e) {

                button.setOpaque(
                        false
                );

                button.setBackground(
                        WHITE
                );

                if (closeButton) {

                    button.setIcon(
                            new CloseIcon(
                                    BLACK,
                                    15
                            )
                    );

                } else {

                    button.setIcon(
                            new MinimizeIcon(
                                    BLACK,
                                    15
                            )
                    );
                }

                button.repaint();
            }
        }
        );

        return button;
    }

    // =========================================================
    // DÉPLACER LA FENÊTRE
    // =========================================================
    private void enableWindowDrag(
            Component component) {

        component.addMouseListener(
                new MouseAdapter() {

            @Override
            public void mousePressed(
                    MouseEvent e) {

                mouseLocation
                        = e.getPoint();
            }
        }
        );

        component.addMouseMotionListener(
                new MouseAdapter() {

            @Override
            public void mouseDragged(
                    MouseEvent e) {

                if (mouseLocation == null) {

                    return;
                }

                Point location
                        = getLocation();

                int x
                        = location.x
                        + e.getX()
                        - mouseLocation.x;

                int y
                        = location.y
                        + e.getY()
                        - mouseLocation.y;

                setLocation(
                        x,
                        y
                );
            }
        }
        );
    }

    // =========================================================
    // INTERFACE ICÔNE COLORABLE
    // =========================================================
    private interface ColorableIcon
            extends Icon {

        Icon withColor(
                Color color
        );
    }

    // =========================================================
    // ICÔNE PANIER
    // =========================================================
    private static class CartIcon
            implements ColorableIcon {

        private final Color color;
        private final int size;

        public CartIcon(
                Color color,
                int size) {

            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2
                    = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    color
            );

            g2.setStroke(
                    new BasicStroke(
                            2.2f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            // poignée
            g2.drawLine(
                    x + 2,
                    y + 5,
                    x + 7,
                    y + 5
            );

            Path2D path
                    = new Path2D.Double();

            path.moveTo(
                    x + 7,
                    y + 6
            );

            path.lineTo(
                    x + size - 3,
                    y + 6
            );

            path.lineTo(
                    x + size - 6,
                    y + 16
            );

            path.lineTo(
                    x + 10,
                    y + 16
            );

            path.closePath();

            g2.draw(
                    path
            );

            // roues
            g2.fillOval(
                    x + 10,
                    y + 19,
                    4,
                    4
            );

            g2.fillOval(
                    x + size - 9,
                    y + 19,
                    4,
                    4
            );

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public Icon withColor(
                Color color) {

            return new CartIcon(
                    color,
                    size
            );
        }
    }

    // =========================================================
    // ICÔNE HOME
    // =========================================================
    private static class HomeIcon
            implements ColorableIcon {

        private final Color color;
        private final int size;

        public HomeIcon(
                Color color,
                int size) {

            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2
                    = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    color
            );

            g2.setStroke(
                    new BasicStroke(
                            2f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            Path2D roof
                    = new Path2D.Double();

            roof.moveTo(
                    x + 3,
                    y + 10
            );

            roof.lineTo(
                    x + size / 2,
                    y + 3
            );

            roof.lineTo(
                    x + size - 3,
                    y + 10
            );

            g2.draw(
                    roof
            );

            g2.drawRoundRect(
                    x + 6,
                    y + 9,
                    size - 12,
                    size - 8,
                    2,
                    2
            );

            g2.drawLine(
                    x + size / 2,
                    y + size - 7,
                    x + size / 2,
                    y + size - 1
            );

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public Icon withColor(
                Color color) {

            return new HomeIcon(
                    color,
                    size
            );
        }
    }

    // =========================================================
    // ICÔNE UTILISATEURS
    // =========================================================
    private static class UsersIcon
            implements ColorableIcon {

        private final Color color;
        private final int size;

        public UsersIcon(
                Color color,
                int size) {

            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2
                    = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    color
            );

            g2.setStroke(
                    new BasicStroke(
                            1.9f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            // personne 1
            g2.drawOval(
                    x + 6,
                    y + 3,
                    7,
                    7
            );

            g2.drawArc(
                    x + 2,
                    y + 11,
                    15,
                    10,
                    0,
                    180
            );

            // personne 2
            g2.drawOval(
                    x + 14,
                    y + 6,
                    6,
                    6
            );

            g2.drawArc(
                    x + 12,
                    y + 13,
                    12,
                    9,
                    0,
                    180
            );

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public Icon withColor(
                Color color) {

            return new UsersIcon(
                    color,
                    size
            );
        }
    }

    // =========================================================
    // ICÔNE PRODUIT
    // =========================================================
    private static class BoxIcon
            implements ColorableIcon {

        private final Color color;
        private final int size;

        public BoxIcon(
                Color color,
                int size) {

            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2
                    = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    color
            );

            g2.setStroke(
                    new BasicStroke(
                            1.9f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            Path2D box
                    = new Path2D.Double();

            box.moveTo(
                    x + 3,
                    y + 8
            );

            box.lineTo(
                    x + size / 2,
                    y + 3
            );

            box.lineTo(
                    x + size - 3,
                    y + 8
            );

            box.lineTo(
                    x + size - 3,
                    y + size - 4
            );

            box.lineTo(
                    x + size / 2,
                    y + size - 1
            );

            box.lineTo(
                    x + 3,
                    y + size - 4
            );

            box.closePath();

            g2.draw(
                    box
            );

            g2.drawLine(
                    x + size / 2,
                    y + 3,
                    x + size / 2,
                    y + size - 1
            );

            g2.drawLine(
                    x + 3,
                    y + 8,
                    x + size / 2,
                    y + 13
            );

            g2.drawLine(
                    x + size / 2,
                    y + 13,
                    x + size - 3,
                    y + 8
            );

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public Icon withColor(
                Color color) {

            return new BoxIcon(
                    color,
                    size
            );
        }
    }

    // =========================================================
    // ICÔNE COMMANDE
    // =========================================================
    private static class CommandIcon
            implements ColorableIcon {

        private final Color color;
        private final int size;

        public CommandIcon(
                Color color,
                int size) {

            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2
                    = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    color
            );

            g2.setStroke(
                    new BasicStroke(
                            1.9f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            // feuille
            g2.drawRoundRect(
                    x + 5,
                    y + 3,
                    size - 10,
                    size - 5,
                    3,
                    3
            );

            // lignes
            g2.drawLine(
                    x + 9,
                    y + 9,
                    x + size - 7,
                    y + 9
            );

            g2.drawLine(
                    x + 9,
                    y + 13,
                    x + size - 7,
                    y + 13
            );

            g2.drawLine(
                    x + 9,
                    y + 17,
                    x + size - 10,
                    y + 17
            );

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public Icon withColor(
                Color color) {

            return new CommandIcon(
                    color,
                    size
            );
        }
    }

    // =========================================================
// ICÔNE RÉCEPTION
// =========================================================
    private static class ReceptionIcon
            implements ColorableIcon {

        private final Color color;
        private final int size;

        public ReceptionIcon(
                Color color,
                int size) {

            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2
                    = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);

            g2.setStroke(
                    new BasicStroke(
                            1.9f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            // Boîte
            Path2D box
                    = new Path2D.Double();

            box.moveTo(
                    x + 3,
                    y + 8
            );

            box.lineTo(
                    x + size / 2,
                    y + 3
            );

            box.lineTo(
                    x + size - 3,
                    y + 8
            );

            box.lineTo(
                    x + size - 3,
                    y + size - 4
            );

            box.lineTo(
                    x + size / 2,
                    y + size - 1
            );

            box.lineTo(
                    x + 3,
                    y + size - 4
            );

            box.closePath();

            g2.draw(box);

            // Ligne centrale
            g2.drawLine(
                    x + size / 2,
                    y + 3,
                    x + size / 2,
                    y + size - 1
            );

            // Flèche vers le bas
            g2.drawLine(
                    x + size / 2,
                    y + 7,
                    x + size / 2,
                    y + 14
            );

            g2.drawLine(
                    x + size / 2,
                    y + 14,
                    x + size / 2 - 3,
                    y + 11
            );

            g2.drawLine(
                    x + size / 2,
                    y + 14,
                    x + size / 2 + 3,
                    y + 11
            );

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public Icon withColor(
                Color color) {

            return new ReceptionIcon(
                    color,
                    size
            );
        }
    }

    // =========================================================
    // ICÔNE FACTURE
    // =========================================================
    private static class FactureIcon implements ColorableIcon {

        private final Color color;
        private final int size;

        public FactureIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);
            g2.setStroke(new BasicStroke(
                    1.9f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            g2.drawRoundRect(
                    x + 5, y + 3,
                    size - 10, size - 5,
                    3, 3
            );

            g2.drawLine(x + 9, y + 8, x + size - 7, y + 8);
            g2.drawLine(x + 9, y + 12, x + size - 8, y + 12);
            g2.drawLine(x + 9, y + 16, x + size - 10, y + 16);

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public Icon withColor(Color color) {
            return new FactureIcon(color, size);
        }
    }

    // =========================================================
    // ICÔNE PROPOSER
    // =========================================================
    private static class ProposerIcon implements ColorableIcon {

        private final Color color;
        private final int size;

        public ProposerIcon(Color color, int size) {
            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(color);
            g2.setStroke(new BasicStroke(
                    1.9f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            // Étiquette / proposition de prix
            Path2D tag = new Path2D.Double();
            tag.moveTo(x + 3, y + 6);
            tag.lineTo(x + 9, y + 3);
            tag.lineTo(x + size - 3, y + 9);
            tag.lineTo(x + 10, y + size - 3);
            tag.closePath();
            g2.draw(tag);

            // Trou de l'étiquette
            g2.drawOval(x + 7, y + 6, 3, 3);

            // Signe € stylisé
            g2.drawLine(x + 13, y + 11, x + 18, y + 11);
            g2.drawLine(x + 13, y + 14, x + 18, y + 14);

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public Icon withColor(Color color) {
            return new ProposerIcon(color, size);
        }
    }

    // =========================================================
    // ICÔNE MINIMISER
    // =========================================================
    private static class MinimizeIcon
            implements Icon {

        private final Color color;
        private final int size;

        public MinimizeIcon(
                Color color,
                int size) {

            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2
                    = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    color
            );

            g2.setStroke(
                    new BasicStroke(
                            1.8f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            int centerY
                    = y + size / 2;

            g2.drawLine(
                    x + 2,
                    centerY,
                    x + size - 2,
                    centerY
            );

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    // =========================================================
    // ICÔNE FERMER
    // =========================================================
    private static class CloseIcon
            implements Icon {

        private final Color color;
        private final int size;

        public CloseIcon(
                Color color,
                int size) {

            this.color = color;
            this.size = size;
        }

        @Override
        public void paintIcon(
                Component c,
                Graphics g,
                int x,
                int y) {

            Graphics2D g2
                    = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    color
            );

            g2.setStroke(
                    new BasicStroke(
                            1.8f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    )
            );

            g2.drawLine(
                    x + 3,
                    y + 3,
                    x + size - 3,
                    y + size - 3
            );

            g2.drawLine(
                    x + size - 3,
                    y + 3,
                    x + 3,
                    y + size - 3
            );

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    // =========================================================
    // PANEL ARRONDI
    // =========================================================
    private static class RoundedPanel
            extends JPanel {

        private final int radius;

        public RoundedPanel(
                int radius) {

            this.radius
                    = radius;

            setOpaque(
                    false
            );
        }

        @Override
        protected void paintComponent(
                Graphics g) {

            Graphics2D g2
                    = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(
                    WHITE
            );

            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    radius,
                    radius
            );

            g2.dispose();
        }

        @Override
        protected void paintChildren(
                Graphics g) {

            Graphics2D g2
                    = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            Shape clip
                    = new RoundRectangle2D.Double(
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            radius,
                            radius
                    );

            g2.clip(
                    clip
            );

            super.paintChildren(
                    g2
            );

            g2.dispose();
        }
    }
}
