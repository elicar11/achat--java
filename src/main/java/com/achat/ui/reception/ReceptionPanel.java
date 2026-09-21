package com.achat.ui.reception;

import com.achat.model.Reception;
import com.achat.service.ReceptionService;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Interface moderne de gestion des réceptions.
 * Le style est aligné sur CommandePanel.
 */
public class ReceptionPanel extends JPanel {

    private static final Color BACKGROUND = new Color(246, 246, 246);
    private static final Color WHITE = Color.WHITE;
    private static final Color BLACK = new Color(18, 18, 18);
    private static final Color GRAY = new Color(120, 120, 120);
    private static final Color BORDER = new Color(225, 225, 225);
    private static final Color LIGHT_GRAY = new Color(238, 238, 238);

    private final ReceptionService receptionService;
    private JTextField txtRecherche;
    private JTable table;
    private DefaultTableModel tableModel;

    public ReceptionPanel() {
        receptionService = new ReceptionService();
        construireInterface();
        chargerReceptions();
    }

    private void construireInterface() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(28, 28, 28, 28));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titres = new JPanel(new MigLayout("insets 0, wrap", "[grow]"));
        titres.setOpaque(false);

        JLabel titre = new JLabel("Gestion des réceptions");
        titre.setFont(new Font("SansSerif", Font.BOLD, 30));
        titre.setForeground(BLACK);
        titres.add(titre);

        JLabel sousTitre = new JLabel("Suivez les marchandises reçues et les bons de livraison");
        sousTitre.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sousTitre.setForeground(GRAY);
        titres.add(sousTitre);

        header.add(titres, BorderLayout.WEST);

        JButton btnAjouter = createBlackButton("+ Ajouter réception");
        btnAjouter.addActionListener(e -> ouvrirFormulaire());
        header.add(btnAjouter, BorderLayout.EAST);

        mainPanel.add(header, BorderLayout.NORTH);

        // CARD PRINCIPALE
        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(WHITE);
        tableCard.setBorder(new EmptyBorder(22, 22, 22, 22));

        // RECHERCHE
        JPanel recherchePanel = new JPanel(new BorderLayout(8, 0));
        recherchePanel.setOpaque(false);

        JLabel icone = new JLabel("⌕");
        icone.setFont(new Font("SansSerif", Font.BOLD, 20));
        icone.setForeground(GRAY);
        recherchePanel.add(icone, BorderLayout.WEST);

        txtRecherche = new JTextField();
        txtRecherche.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtRecherche.setPreferredSize(new Dimension(0, 48));
        txtRecherche.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 10, 0, 10)
        ));
        txtRecherche.putClientProperty("JTextField.placeholderText",
                "Rechercher par bon de livraison ou commande...");
        recherchePanel.add(txtRecherche, BorderLayout.CENTER);

        JPanel boutonsRecherche = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        boutonsRecherche.setOpaque(false);

        JButton btnRechercher = createWhiteButton("Rechercher", 125);
        JButton btnActualiser = createWhiteButton("Actualiser", 115);

        btnRechercher.addActionListener(e -> rechercher());
        btnActualiser.addActionListener(e -> {
            txtRecherche.setText("");
            chargerReceptions();
        });
        txtRecherche.addActionListener(e -> rechercher());

        boutonsRecherche.add(btnRechercher);
        boutonsRecherche.add(Box.createHorizontalStrut(8));
        boutonsRecherche.add(btnActualiser);
        recherchePanel.add(boutonsRecherche, BorderLayout.EAST);

        tableCard.add(recherchePanel, BorderLayout.NORTH);

        // TABLEAU
        String[] colonnes = {"ID", "Date", "Bon de livraison", "Commande", "Actions"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(48);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(235, 235, 235));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(LIGHT_GRAY);
        table.setSelectionForeground(BLACK);
        table.setBackground(WHITE);
        table.setForeground(BLACK);

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setForeground(BLACK);
        table.getTableHeader().setBackground(new Color(248, 248, 248));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(260);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.setBackground(WHITE);
        scrollPane.getViewport().setBackground(WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tableCard, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    modifierSelection();
                }
            }
        });
    }

    private void chargerReceptions() {
        try {
            afficherReceptions(receptionService.findAll());
        } catch (Exception e) {
            afficherErreur("Impossible de charger les réceptions.", e);
        }
    }

    private void afficherReceptions(List<Reception> receptions) {
        tableModel.setRowCount(0);
        for (Reception reception : receptions) {
            tableModel.addRow(new Object[]{
                    reception.getIdReception(),
                    reception.getDateReception(),
                    reception.getNumBonLivraison(),
                    reception.getIdCommande(),
                    "Modifier"
            });
        }
    }

    private void rechercher() {
        String recherche = txtRecherche.getText().trim();
        if (recherche.isEmpty()) {
            chargerReceptions();
            return;
        }
        try {
            afficherReceptions(receptionService.rechercher(recherche));
        } catch (Exception e) {
            afficherErreur("Erreur lors de la recherche.", e);
        }
    }

    private void ouvrirFormulaire() {
        afficherModal("Ajouter une réception", new ReceptionForm());
    }

    private void modifierSelection() {
        int ligne = table.getSelectedRow();
        if (ligne < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner une réception.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(ligne);
        int id = Integer.parseInt(tableModel.getValueAt(modelRow, 0).toString());

        try {
            Reception reception = receptionService.findById(id);
            if (reception == null) {
                JOptionPane.showMessageDialog(this,
                        "Réception introuvable.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            afficherModal("Modifier la réception", new ReceptionForm(reception));
        } catch (Exception e) {
            afficherErreur("Erreur lors de la modification.", e);
        }
    }

    private void afficherModal(String titre, ReceptionForm form) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;

        if (parent instanceof Frame) {
            dialog = new JDialog((Frame) parent, titre, true);
        } else if (parent instanceof Dialog) {
            dialog = new JDialog((Dialog) parent, titre, true);
        } else {
            dialog = new JDialog((Frame) null, titre, true);
        }

        form.setDialog(dialog);
        dialog.setContentPane(form);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(820, 720);
        dialog.setMinimumSize(new Dimension(760, 650));
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
        chargerReceptions();
    }

    private JButton createBlackButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(BLACK);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(0, 20, 0, 20));
        button.setPreferredSize(new Dimension(185, 48));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createWhiteButton(String text, int width) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(BLACK);
        button.setBackground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 14, 0, 14)
        ));
        button.setPreferredSize(new Dimension(width, 42));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void afficherErreur(String message, Exception e) {
        JOptionPane.showMessageDialog(this,
                message + "\n\n" + e.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
