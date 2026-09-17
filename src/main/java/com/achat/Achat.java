package com.achat;

import com.achat.database.DatabaseInitializer;
import com.achat.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import java.awt.Font;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Achat {

    public static void main(String[] args) {

        /*
         * =========================================
         * 1. Initialiser la base de données
         * =========================================
         */
        DatabaseInitializer.initialize();


        /*
         * =========================================
         * 2. Installer la police Roboto
         * =========================================
         */
        FlatRobotoFont.install();


        /*
         * =========================================
         * 3. Définir la police par défaut
         * =========================================
         */
        Font regularFont = new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 14);

        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));


        /*
         * =========================================
         * 4. Installer FlatLaf
         * =========================================
         */
        FlatLightLaf.setup();


        /*
         * =========================================
         * 5. Lancer l'application Swing
         * =========================================
         */
        SwingUtilities.invokeLater(() -> {

            MainFrame frame = new MainFrame();

            frame.setVisible(true);
        });
    }
}
