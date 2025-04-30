package com.finance.tracker.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import com.finance.tracker.localization.LanguageManager;
import com.finance.tracker.classification.view.TransactionFormWindow;
import com.finance.module.ai.FinanceAnalyzer;

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
            // 更新主框架的货币设置
            parentFrame.setCurrency(selectedCurrency);
            
            // 获取并刷新所有交易面板显示
            try {
                // 1. 刷新主界面中的交易面板
                refreshMainFramePanels();
                
                // 2. 刷新所有已打开的独立窗口
                refreshAllOpenWindows();
                
                // 3. 显示成功信息
                JOptionPane.showMessageDialog(this,
                    languageManager.getText(LanguageManager.CURRENCY_UPDATED) + ": " + selectedCurrency,
                    languageManager.getText(LanguageManager.SETTINGS_UPDATED),
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                System.err.println("Error refreshing currency displays: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 刷新主框架中的所有交易面板
     */
    private void refreshMainFramePanels() {
        try {
            Component[] components = parentFrame.getMainContentPanel().getComponents();
            for (Component comp : components) {
                if (comp instanceof TransactionPanel) {
                    // 刷新交易面板
                    TransactionPanel transactionPanel = (TransactionPanel) comp;
                    transactionPanel.refreshTransactionDisplay();
                    System.out.println("Refreshed TransactionPanel");
                } else if (comp instanceof TransactionDashboardPanel) {
                    // 刷新交易仪表盘面板
                    ((TransactionDashboardPanel) comp).refreshCurrencySettings();
                    System.out.println("Refreshed TransactionDashboardPanel");
                }
            }
        } catch (Exception e) {
            System.err.println("Error refreshing main frame panels: " + e.getMessage());
        }
    }
    
    /**
     * 刷新所有已打开的独立窗口
     */
    private void refreshAllOpenWindows() {
        try {
            // 遍历所有已打开的窗口
            Frame[] frames = Frame.getFrames();
            for (Frame frame : frames) {
                if (frame instanceof TransactionFormWindow && frame.isVisible()) {
                    // 刷新交易表单窗口
                    ((TransactionFormWindow) frame).refreshCurrencySettings();
                    System.out.println("Refreshed TransactionFormWindow");
                } else if (frame instanceof FinanceAnalyzer && frame.isVisible()) {
                    // 刷新财务分析器窗口
                    ((FinanceAnalyzer) frame).refreshCurrencyDisplay();
                    System.out.println("Refreshed FinanceAnalyzer");
                } else if (frame instanceof JFrame && frame.isVisible() && 
                           !(frame instanceof MainModuleUI)) {
                    // 尝试刷新其他类型的窗口（如果它们支持货币更新）
                    refreshFrameComponents(frame);
                }
            }
        } catch (Exception e) {
            System.err.println("Error refreshing open windows: " + e.getMessage());
        }
    }
    
    /**
     * 递归刷新框架中的所有组件
     */
    private void refreshFrameComponents(Container container) {
        try {
            Component[] components = container.getComponents();
            for (Component comp : components) {
                // 处理可能具有刷新货币功能的组件
                if (comp instanceof JTable) {
                    ((JTable) comp).repaint(); // 刷新表格显示
                } else if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    // 如果标签可能包含货币信息，刷新它
                    if (label.getText() != null && 
                        (label.getText().contains("$") || 
                         label.getText().contains("¥") || 
                         label.getText().contains("€") ||
                         label.getText().contains("£") ||
                         label.getText().contains("HK$"))) {
                        label.repaint();
                    }
                } else if (comp instanceof Container) {
                    // 递归处理子容器
                    refreshFrameComponents((Container) comp);
                }
            }
        } catch (Exception e) {
            // 忽略刷新组件时的异常
        }
    }
} 