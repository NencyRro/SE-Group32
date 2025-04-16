package com.finance.tracker.ui;

import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * Main panel for module testing
 */
public class MainModuleTestPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    public MainModuleTestPanel() {
        setLayout(new BorderLayout());
        
        // Add the AI module panel
        AIModulePanel aiPanel = new AIModulePanel();
        add(aiPanel, BorderLayout.CENTER);
    }
}