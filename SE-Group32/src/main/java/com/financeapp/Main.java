package com.financeapp;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinanceAppGUI app = new FinanceAppGUI();
            app.setVisible(true);
        });
    }
} 