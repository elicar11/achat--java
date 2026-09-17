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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FournisseurPanel extends JPanel {

    private final FournisseurService fournisseurService;
    private final PersonneService personneService;
    private final SocieteService societeService;

    private JTextField txtRecherche;

    private JTable table;

    private DefaultTableModel tableModel;

    public FournisseurPanel() {

        fournisseurService =
                new FournisseurService();

        personneService =
                new PersonneService();

        societeService =
                new SocieteService();

        construireInterface();

        chargerFournisseurs();
    }

    /**
     * Construction de l'interface.
     */
    private void construireInterface() {

        setLayout(
                new MigLayout(
                        "fill, insets 25",
                        "[grow]",
                        "[][grow]"
                )
        );

        setBorder(
                new EmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        // ==========================================
        // HEADER
        // ==========================================

        JPanel header =
                new JPanel(
                        new MigLayout(
                                "insets 0",
                                "[grow][]"
                        )
                );

        header.setOpaque(false);

        JLabel titre =
                new JLabel("Gestion des fournisseurs");

        titre.setFont(
                titre.getFont().deriveFont(
                        Font.BOLD,
                        26f
                )
        );

        header.add(
                titre,
                "growx"
        );

        JButton btnAjouter =
                new JButton("+ Ajouter fournisseur");

        btnAjouter.addActionListener(
                e -> ouvrirAjout()
        );

        header.add(btnAjouter);

        add(
                header,
                "growx, wrap"
        );

        // ==========================================
        // BARRE DE RECHERCHE
        // ==========================================

        JPanel recherchePanel =
                new JPanel(
                        new MigLayout(
                                "insets 0",
                                "[][grow][]"
                        )
                );

        recherchePanel.setOpaque(false);

        recherchePanel.add(
                new JLabel("Rechercher :")
        );

        txtRecherche =
                new JTextField();

        recherchePanel.add(
                txtRecherche,
                "growx"
        );

        JButton btnRechercher =
                new JButton("Rechercher");

        recherchePanel.add(
                btnRechercher
        );

        JButton btnActualiser =
                new JButton("Actualiser");

        recherchePanel.add(
                btnActualiser
        );

        add(
                recherchePanel,
                "growx, wrap 15"
        );

        // ==========================================
        // TABLE
        // ==========================================

        String[] colonnes = {
                "ID",
                "Type",
                "Nom / Société",
                "Adresse",
                "Email",
                "Téléphone",
                "Actions"
        };

        tableModel =
                new DefaultTableModel(
                        colonnes,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        table =
                new JTable(tableModel);

        table.setRowHeight(35);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.getTableHeader()
                .setReorderingAllowed(false);

        JScrollPane scrollPane =
                new JScrollPane(table);

        add(
                scrollPane,
                "grow"
        );

        // ==========================================
        // EVENEMENTS
        // ==========================================

        btnRechercher.addActionListener(
                e -> rechercher()
        );

        btnActualiser.addActionListener(
                e -> {
                    txtRecherche.setText("");
                    chargerFournisseurs();
                }
        );

        txtRecherche.addActionListener(
                e -> rechercher()
        );

        // Double clic = modification
        table.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent e) {

                        if (e.getClickCount() == 2
                                && SwingUtilities
                                .isLeftMouseButton(e)) {

                            modifierSelection();
                        }
                    }
                }
        );
    }

    /**
     * Charger tous les fournisseurs.
     */
    private void chargerFournisseurs() {

        try {

            List<Fournisseur> fournisseurs =
                    fournisseurService.findAll();

            afficherFournisseurs(
                    fournisseurs
            );

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors du chargement des fournisseurs.",
                    e
            );
        }
    }

    /**
     * Afficher les fournisseurs dans la table.
     */
    private void afficherFournisseurs(
            List<Fournisseur> fournisseurs) {

        tableModel.setRowCount(0);

        for (Fournisseur fournisseur :
                fournisseurs) {

            String nomSociete =
                    obtenirNomOuSociete(
                            fournisseur
                    );

            tableModel.addRow(
                    new Object[]{
                            fournisseur
                                    .getIdFournisseur(),

                            fournisseur
                                    .getTypeFournisseur(),

                            nomSociete,

                            valeur(
                                    fournisseur.getAdresse()
                            ),

                            valeur(
                                    fournisseur.getEmail()
                            ),

                            valeur(
                                    fournisseur.getTelephone()
                            ),

                            "Modifier | Supprimer"
                    }
            );
        }
    }

    /**
     * Obtenir le nom d'une personne
     * ou la raison sociale d'une société.
     */
    private String obtenirNomOuSociete(
            Fournisseur fournisseur) {

        try {

            int id =
                    fournisseur.getIdFournisseur();

            if ("PERSONNE".equals(
                    fournisseur.getTypeFournisseur())) {

                Personne personne =
                        personneService
                                .findByFournisseur(id);

                if (personne != null) {

                    return valeur(
                            personne.getNom()
                    )
                            + " "
                            + valeur(
                            personne.getPrenom()
                    );
                }

            } else {

                Societe societe =
                        societeService
                                .findByFournisseur(id);

                if (societe != null) {

                    return valeur(
                            societe.getRaisonSociale()
                    );
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "Erreur récupération "
                            + "nom fournisseur : "
                            + e.getMessage()
            );
        }

        return "";
    }

    /**
     * Rechercher un fournisseur.
     */
    private void rechercher() {

        try {

            String motCle =
                    txtRecherche
                            .getText()
                            .trim();

            List<Fournisseur> fournisseurs;

            if (motCle.isEmpty()) {

                fournisseurs =
                        fournisseurService.findAll();

            } else {

                fournisseurs =
                        fournisseurService
                                .rechercher(motCle);
            }

            afficherFournisseurs(
                    fournisseurs
            );

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la recherche.",
                    e
            );
        }
    }

    /**
     * Ouvrir le formulaire d'ajout.
     */
    private void ouvrirAjout() {

        FournisseurForm form =
                new FournisseurForm();

        afficherModal(
                "Ajouter un fournisseur",
                form
        );
    }

    /**
     * Modifier le fournisseur sélectionné.
     */
    private void modifierSelection() {

        int ligne =
                table.getSelectedRow();

        if (ligne == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Veuillez sélectionner un fournisseur.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        try {

            int id =
                    Integer.parseInt(
                            tableModel
                                    .getValueAt(
                                            ligne,
                                            0
                                    )
                                    .toString()
                    );

            Fournisseur fournisseur =
                    fournisseurService
                            .findById(id);

            if (fournisseur == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Fournisseur introuvable.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            FournisseurForm form =
                    new FournisseurForm(
                            fournisseur
                    );

            afficherModal(
                    "Modifier le fournisseur",
                    form
            );

        } catch (Exception e) {

            afficherErreur(
                    "Erreur lors de la modification.",
                    e
            );
        }
    }

    /**
     * Afficher le formulaire dans un JDialog modal.
     */
    private void afficherModal(
            String titre,
            FournisseurForm form) {

        Window parent =
                SwingUtilities
                        .getWindowAncestor(this);

        JDialog dialog;

        if (parent instanceof Frame) {

            dialog =
                    new JDialog(
                            (Frame) parent,
                            titre,
                            true
                    );

        } else if (parent instanceof Dialog) {

            dialog =
                    new JDialog(
                            (Dialog) parent,
                            titre,
                            true
                    );

        } else {

            dialog =
                    new JDialog(
                            (Frame) null,
                            titre,
                            true
                    );
        }

        form.setDialog(dialog);

        dialog.setContentPane(form);

        dialog.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        dialog.setSize(
                550,
                600
        );

        dialog.setLocationRelativeTo(this);

        dialog.setResizable(false);

        dialog.setVisible(true);

        // Actualiser après fermeture
        chargerFournisseurs();
    }

    /**
     * Afficher une erreur.
     */
    private void afficherErreur(
            String message,
            Exception e) {

        JOptionPane.showMessageDialog(
                this,
                message
                        + "\n\n"
                        + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );

        e.printStackTrace();
    }

    /**
     * Eviter null.
     */
    private String valeur(String valeur) {

        return valeur == null
                ? ""
                : valeur;
    }
}