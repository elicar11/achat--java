package com.achat.ui.reception;

import com.achat.model.Reception;
import com.achat.service.ReceptionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Interface principale de gestion des réceptions.
 * Version en Swing classique (Look &amp; Feel par défaut,
 * sans styles personnalisés).
 */
public class ReceptionPanel extends JPanel {

    private final ReceptionService receptionService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtRecherche;

    private JButton btnActualiser;
    private JButton btnNouvelle;
    private JButton btnRechercher;

    public ReceptionPanel() {

        receptionService = new ReceptionService();

        construireInterface();
        chargerReceptions();
    }

    /**
     * Construction de l'interface.
     */
    private void construireInterface() {

        setLayout(new BorderLayout(10, 10));
        setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        // =========================================================
        // EN-TÊTE (titre + bouton nouvelle réception)
        // =========================================================

        JPanel header = new JPanel(new BorderLayout());

        JLabel lblTitre = new JLabel("Réceptions");
        lblTitre.setFont(
                lblTitre.getFont().deriveFont(Font.BOLD, 20f)
        );

        header.add(lblTitre, BorderLayout.WEST);

        btnNouvelle = new JButton("Nouvelle réception");
        btnNouvelle.addActionListener(e -> ouvrirFormulaire());

        header.add(btnNouvelle, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // =========================================================
        // CENTRE (recherche + tableau)
        // =========================================================

        JPanel centre = new JPanel(new BorderLayout(10, 10));

        // ---------------------------------------------------------
        // BARRE DE RECHERCHE
        // ---------------------------------------------------------

        JPanel recherchePanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 5, 5)
        );

        recherchePanel.add(new JLabel("Recherche :"));

        txtRecherche = new JTextField(25);
        recherchePanel.add(txtRecherche);

        btnRechercher = new JButton("Rechercher");
        recherchePanel.add(btnRechercher);

        btnActualiser = new JButton("Actualiser");
        recherchePanel.add(btnActualiser);

        btnRechercher.addActionListener(e -> rechercher());
        btnActualiser.addActionListener(e -> {
            txtRecherche.setText("");
            chargerReceptions();
        });

        txtRecherche.addActionListener(e -> rechercher());

        centre.add(recherchePanel, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // TABLEAU
        // ---------------------------------------------------------

        String[] colonnes = {
            "ID",
            "Date réception",
            "Bon de livraison",
            "Commande",
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
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        table.setAutoResizeMode(
                JTable.AUTO_RESIZE_ALL_COLUMNS
        );

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(table);

        centre.add(scrollPane, BorderLayout.CENTER);

        add(centre, BorderLayout.CENTER);

        // Double clic pour modifier
        table.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (e.getClickCount() == 2
                                && SwingUtilities.isLeftMouseButton(e)) {

                            modifierSelection();
                        }
                    }
                }
        );
    }

    /**
     * Charge toutes les réceptions.
     */
    private void chargerReceptions() {

        try {

            tableModel.setRowCount(0);

            List<Reception> receptions =
                    receptionService.findAll();

            for (Reception reception : receptions) {

                tableModel.addRow(
                        new Object[]{
                            reception.getIdReception(),
                            reception.getDateReception(),
                            reception.getNumBonLivraison(),
                            reception.getIdCommande(),
                            "Double-cliquer pour modifier"
                        }
                );
            }

        } catch (Exception e) {

            afficherErreur(
                    "Impossible de charger les réceptions.",
                    e
            );
        }
    }

    /**
     * Recherche.
     */
    private void rechercher() {

        String recherche =
                txtRecherche.getText().trim();

        if (recherche.isEmpty()) {

            chargerReceptions();
            return;
        }

        try {

            tableModel.setRowCount(0);

            List<Reception> receptions =
                    receptionService.rechercher(recherche);

            for (Reception reception : receptions) {

                tableModel.addRow(
                        new Object[]{
                            reception.getIdReception(),
                            reception.getDateReception(),
                            reception.getNumBonLivraison(),
                            reception.getIdCommande(),
                            "Double-cliquer pour modifier"
                        }
                );
            }

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la recherche.",
                    e
            );
        }
    }

    /**
     * Ouvre le formulaire d'ajout.
     */
    private void ouvrirFormulaire() {

        afficherModal(
                new ReceptionForm(),
                "Nouvelle réception"
        );
    }

    /**
     * Modification de la réception sélectionnée.
     */
    private void modifierSelection() {

        int ligne =
                table.getSelectedRow();

        if (ligne < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez sélectionner une réception.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int id =
                Integer.parseInt(
                        tableModel
                                .getValueAt(ligne, 0)
                                .toString()
                );

        try {

            Reception reception =
                    receptionService.findById(id);

            if (reception == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Réception introuvable.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            afficherModal(
                    new ReceptionForm(reception),
                    "Modifier la réception"
            );

        } catch (Exception e) {

            afficherErreur(
                    "Impossible de charger la réception.",
                    e
            );
        }
    }

    /**
     * Affiche le formulaire dans une fenêtre modale.
     */
    private void afficherModal(
            ReceptionForm form,
            String titre
    ) {

        Window parent =
                SwingUtilities.getWindowAncestor(this);

        JDialog dialog =
                new JDialog(
                        parent,
                        titre,
                        Dialog.ModalityType.APPLICATION_MODAL
                );

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        dialog.setContentPane(form);

        form.setDialog(dialog);

        dialog.setSize(700, 720);

        dialog.setMinimumSize(
                new Dimension(650, 650)
        );

        dialog.setLocationRelativeTo(this);

        dialog.setResizable(false);

        dialog.setVisible(true);

        // Recharge après fermeture
        chargerReceptions();
    }

    private void afficherErreur(
            String message,
            Exception e
    ) {

        JOptionPane.showMessageDialog(
                this,
                message + "\n\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }
}