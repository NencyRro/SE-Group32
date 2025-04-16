package com.finance.tracker.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 货币设置面板
 */
public class CurrencyPanel extends JPanel {
    private MainModuleUI parentFrame;
    private JComboBox<String> currencyComboBox;
    private JLabel conversionRateLabel;
    private JLabel sampleAmountLabel;
    
    // 货币符号映射
    private final Map<String, String> currencySymbols = new HashMap<>();
    
    // 货币兑换率映射 (相对于人民币)
    private final Map<String, Double> exchangeRates = new HashMap<>();
    
    /**
     * 构造函数
     */
    public CurrencyPanel(MainModuleUI parent) {
        this.parentFrame = parent;
        
        // 初始化货币符号
        currencySymbols.put("人民币", "¥");
        currencySymbols.put("美元", "$");
        currencySymbols.put("欧元", "€");
        currencySymbols.put("英镑", "£");
        currencySymbols.put("日元", "¥");
        
        // 初始化兑换率 (模拟数据)
        exchangeRates.put("人民币", 1.0);
        exchangeRates.put("美元", 0.14);
        exchangeRates.put("欧元", 0.13);
        exchangeRates.put("英镑", 0.11);
        exchangeRates.put("日元", 20.5);
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("货币设置", JLabel.LEFT);
        titleLabel.setFont(new Font("黑体", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 中间内容区域
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        // 描述标签
        JLabel descLabel = new JLabel("<html><p>选择您希望在应用中使用的货币单位。所有金额将以选定的货币显示。</p></html>");
        descLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        contentPanel.add(descLabel);
        
        // 货币选择面板
        JPanel currencySelectionPanel = new JPanel();
        currencySelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        currencySelectionPanel.setBackground(Color.WHITE);
        currencySelectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        currencySelectionPanel.setMaximumSize(new Dimension(1000, 50));
        
        JLabel currencyLabel = new JLabel("选择货币: ");
        currencyLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        currencySelectionPanel.add(currencyLabel);
        
        // 货币下拉框
        String[] currencies = {"人民币", "美元", "欧元", "英镑", "日元"};
        currencyComboBox = new JComboBox<>(currencies);
        currencyComboBox.setFont(new Font("Dialog", Font.PLAIN, 14));
        currencyComboBox.setPreferredSize(new Dimension(150, 30));
        currencyComboBox.addActionListener(event -> updateCurrencyInfo());
        currencySelectionPanel.add(currencyComboBox);
        
        contentPanel.add(currencySelectionPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // 货币信息面板
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "货币信息"));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        conversionRateLabel = new JLabel("兑换率: 1 人民币 = ?");
        conversionRateLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        conversionRateLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        conversionRateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(conversionRateLabel);
        
        sampleAmountLabel = new JLabel("示例: 100 人民币 = ?");
        sampleAmountLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        sampleAmountLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        sampleAmountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(sampleAmountLabel);
        
        contentPanel.add(infoPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // 应用按钮
        JButton applyButton = new JButton("应用设置");
        applyButton.setFont(new Font("Dialog", Font.BOLD, 14));
        applyButton.setBackground(new Color(25, 118, 210));
        applyButton.setForeground(Color.BLACK);
        applyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        applyButton.setFocusPainted(false);
        applyButton.addActionListener(e -> applyCurrencySettings());
        
        contentPanel.add(applyButton);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224)));
        add(scrollPane, BorderLayout.CENTER);
        
        // 初始化显示
        updateCurrencyInfo();
    }
    
    /**
     * 更新货币信息显示
     */
    private void updateCurrencyInfo() {
        String selectedCurrency = (String) currencyComboBox.getSelectedItem();
        double rate = exchangeRates.get(selectedCurrency);
        
        if (selectedCurrency.equals("人民币")) {
            conversionRateLabel.setText("兑换率: 1 人民币 = 1 人民币");
            sampleAmountLabel.setText("示例: 100 人民币 = ¥ 100.00");
        } else {
            double convertedRate = rate;
            conversionRateLabel.setText(String.format("兑换率: 1 人民币 = %.2f %s", convertedRate, selectedCurrency));
            
            String symbol = currencySymbols.get(selectedCurrency);
            double sampleAmount = 100 * convertedRate;
            sampleAmountLabel.setText(String.format("示例: 100 人民币 = %s %.2f", symbol, sampleAmount));
        }
    }
    
    /**
     * 应用货币设置
     */
    private void applyCurrencySettings() {
        String selectedCurrency = (String) currencyComboBox.getSelectedItem();
        String symbol = currencySymbols.get(selectedCurrency);
        
        // 格式化货币显示名称
        String currencyDisplay = selectedCurrency + " (" + symbol + ")";
        
        // 更新主界面状态
        if (parentFrame != null) {
            parentFrame.setCurrency(currencyDisplay);
            
            // 显示成功消息
            JOptionPane.showMessageDialog(this,
                "货币单位已更改为: " + currencyDisplay + "\n所有金额将以此货币显示。",
                "设置成功",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 