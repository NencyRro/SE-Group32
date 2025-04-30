package com.finance.tracker.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import com.finance.tracker.localization.LanguageManager;

/**
 * Currency Settings Panel
 */
public class CurrencyPanel extends JPanel {
    private MainModuleUI parentFrame;
    private JComboBox<String> currencyComboBox;
    private JTextArea currencyInfoArea;
    private JLabel titleLabel;
    private JLabel instructionLabel;
    private JLabel currencyLabel;
    private JButton applyButton;
    private JPanel infoPanelBorder;
    
    // Currency information mapping
    private Map<String, String[]> currencyInfo = new HashMap<>();
    
    // Language manager
    private LanguageManager languageManager;
    
    /**
     * Constructor
     */
    public CurrencyPanel(MainModuleUI parent) {
        this.parentFrame = parent;
        this.languageManager = parent.getLanguageManager();
        
        // Initialize currency information
        initCurrencyInfo();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }
    
    /**
     * Initialize currency information
     */
    private void initCurrencyInfo() {
        // Format: [Exchange Rate Text, Example Text]
        currencyInfo.put("Yuan (¥)", new String[]{"Exchange Rate: 1 Yuan = 1 Yuan", "Example: 100 Yuan = ¥ 100.00"});
        currencyInfo.put("US Dollar ($)", new String[]{"Exchange Rate: 1 Yuan ≈ 0.14 USD", "Example: 100 Yuan ≈ $ 14.00"});
        currencyInfo.put("Euro (€)", new String[]{"Exchange Rate: 1 Yuan ≈ 0.13 EUR", "Example: 100 Yuan ≈ € 13.00"});
        currencyInfo.put("British Pound (£)", new String[]{"Exchange Rate: 1 Yuan ≈ 0.11 GBP", "Example: 100 Yuan ≈ £ 11.00"});
        currencyInfo.put("Japanese Yen (¥)", new String[]{"Exchange Rate: 1 Yuan ≈ 21 JPY", "Example: 100 Yuan ≈ ¥ 2,100.00"});
        currencyInfo.put("Hong Kong Dollar (HK$)", new String[]{"Exchange Rate: 1 Yuan ≈ 1.1 HKD", "Example: 100 Yuan ≈ HK$ 110.00"});
    }
    
    /**
     * Initialize components
     */
    private void initComponents() {
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel(languageManager.getText(LanguageManager.CURRENCY_SETTINGS), JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // Instruction label
        instructionLabel = new JLabel(
                "<html>" + languageManager.getText(LanguageManager.CURRENCY_INSTRUCTION) + "</html>");
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(instructionLabel);
        
        // Currency selection panel
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        currencyLabel = new JLabel(languageManager.getText(LanguageManager.SELECT_CURRENCY) + ":");
        selectionPanel.add(currencyLabel);
        
        currencyComboBox = new JComboBox<>(new String[]{
            "Yuan (¥)", 
            "US Dollar ($)", 
            "Euro (€)", 
            "British Pound (£)", 
            "Japanese Yen (¥)", 
            "Hong Kong Dollar (HK$)"
        });
        
        // Set initial selection based on parent frame's current currency
        String currentCurrency = parentFrame.getCurrentCurrency();
        for (int i = 0; i < currencyComboBox.getItemCount(); i++) {
            if (currencyComboBox.getItemAt(i).equals(currentCurrency)) {
                currencyComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        currencyComboBox.addActionListener(e -> updateCurrencyInfo());
        selectionPanel.add(currencyComboBox);
        mainPanel.add(selectionPanel);
        
        // Currency information panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanelBorder = infoPanel;
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            languageManager.getText(LanguageManager.CURRENCY_INFO)));
        
        currencyInfoArea = new JTextArea(3, 40);
        currencyInfoArea.setEditable(false);
        currencyInfoArea.setFont(new Font("Dialog", Font.PLAIN, 14));
        currencyInfoArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        updateCurrencyInfo(); // Initialize with selected currency info
        
        infoPanel.add(currencyInfoArea, BorderLayout.CENTER);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(infoPanel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        applyButton = new JButton(languageManager.getText(LanguageManager.APPLY_SETTINGS));
        applyButton.addActionListener(e -> applyCurrencySettings());
        bottomPanel.add(applyButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Update UI with current language
     */
    public void updateLanguage() {
        titleLabel.setText(languageManager.getText(LanguageManager.CURRENCY_SETTINGS));
        instructionLabel.setText("<html>" + languageManager.getText(LanguageManager.CURRENCY_INSTRUCTION) + "</html>");
        currencyLabel.setText(languageManager.getText(LanguageManager.SELECT_CURRENCY) + ":");
        applyButton.setText(languageManager.getText(LanguageManager.APPLY_SETTINGS));
        
        // Update border title
        if (infoPanelBorder instanceof JPanel) {
            TitledBorder titledBorder = (TitledBorder) infoPanelBorder.getBorder();
            if (titledBorder != null) {
                titledBorder.setTitle(languageManager.getText(LanguageManager.CURRENCY_INFO));
                infoPanelBorder.repaint();
            }
        }
        
        // Refresh currency info
        updateCurrencyInfo();
    }
    
    /**
     * Update currency information display
     */
    private void updateCurrencyInfo() {
        String selectedCurrency = (String) currencyComboBox.getSelectedItem();
        if (selectedCurrency != null && currencyInfo.containsKey(selectedCurrency)) {
            String[] info = currencyInfo.get(selectedCurrency);
            currencyInfoArea.setText(info[0] + "\n" + info[1]);
        } else {
            currencyInfoArea.setText(languageManager.getText(LanguageManager.NO_CURRENCY_INFO));
        }
    }
    
    /**
     * Apply currency settings
     */
    private void applyCurrencySettings() {
        String selectedCurrency = (String) currencyComboBox.getSelectedItem();
        if (selectedCurrency != null) {
            parentFrame.setCurrency(selectedCurrency);
            
            // 获取并刷新交易面板显示
            try {
                // 尝试获取 TransactionPanel 实例并刷新显示
                Component[] components = parentFrame.getMainContentPanel().getComponents();
                for (Component comp : components) {
                    if (comp instanceof TransactionPanel) {
                        TransactionPanel transactionPanel = (TransactionPanel) comp;
                        transactionPanel.refreshTransactionDisplay();
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error refreshing transaction panel: " + e.getMessage());
            }
            
            JOptionPane.showMessageDialog(this,
                languageManager.getText(LanguageManager.CURRENCY_UPDATED) + ": " + selectedCurrency,
                languageManager.getText(LanguageManager.SETTINGS_UPDATED),
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 