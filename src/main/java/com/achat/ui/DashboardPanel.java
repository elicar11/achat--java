package com.achat.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {

        setLayout(
                new MigLayout(
                        "fill, insets 30",
                        "[grow]",
                        "[][grow]"
                )
        );

        JLabel title =
                new JLabel(
                        "Tableau de bord"
                );

        title.setFont(
                title.getFont().deriveFont(
                        Font.BOLD,
                        28f
                )
        );

        add(
                title,
                "wrap"
        );

        JPanel cards =
                new JPanel(
                        new MigLayout(
                                "fill, wrap 4",
                                "[grow][grow][grow][grow]",
                                "[120]"
                        )
                );

        cards.setOpaque(false);

        cards.add(
                createCard(
                        "Produits",
                        "0"
                ),
                "grow"
        );

        cards.add(
                createCard(
                        "Fournisseurs",
                        "0"
                ),
                "grow"
        );

        cards.add(
                createCard(
                        "Commandes",
                        "0"
                ),
                "grow"
        );

        cards.add(
                createCard(
                        "Stock en alerte",
                        "0"
                ),
                "grow"
        );

        add(
                cards,
                "grow"
        );
    }

    private JPanel createCard(
            String title,
            String value) {

        JPanel card =
                new JPanel(
                        new MigLayout(
                                "wrap",
                                "[grow]",
                                "[]10[]"
                        )
                );

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIManager.getColor(
                                        "Component.borderColor"
                                ),
                                1
                        ),
                        BorderFactory.createEmptyBorder(
                                15,
                                20,
                                15,
                                20
                        )
                )
        );

        JLabel label =
                new JLabel(title);

        JLabel number =
                new JLabel(value);

        number.setFont(
                number.getFont().deriveFont(
                        Font.BOLD,
                        30f
                )
        );

        card.add(
                label,
                "growx"
        );

        card.add(
                number
        );

        return card;
    }
}