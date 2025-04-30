package com.finance.tracker.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.finance.tracker.localization.LanguageManager;

/**
 * Transaction Management Panel
 */
public class TransactionPanel extends JPanel {
    private MainModuleUI parentFrame;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JTextField amountField;
    private JComboBox<String> categoryComboBox;
    private JTextField descriptionField;
    private List<Transaction> transactions = new ArrayList<>();
    private JLabel titleLabel;
    private JLabel statsLabel;
    private JButton addButton;
    private JButton deleteButton;
    private JButton generateReportButton;
    
    // 货币换算率 (相对于人民币)
    private static final double USD_RATE = 0.14;
    private static final double EUR_RATE = 0.13;
    private static final double GBP_RATE = 0.11;
    private static final double JPY_RATE = 21.0;
    private static final double HKD_RATE = 1.1;
    
    // Language manager
    private LanguageManager languageManager;
    
    /**
     * Constructor
     */
    public TransactionPanel(MainModuleUI parent) {
        this.parentFrame = parent;
        this.languageManager = parent.getLanguageManager();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadDemoTransactions();
    }
    
    /**
     * Update UI with current language
     */
    public void updateLanguage() {
        // Update title
        titleLabel.setText(languageManager.getText(LanguageManager.ADD_TRANSACTION));
        
        // Update buttons
        addButton.setText("Add Transaction");
        deleteButton.setText("Delete Selected");
        generateReportButton.setText("Generate Report");
        
        // Refresh display to update currency symbols and format
        refreshTransactionDisplay();
    }
    
    /**
     * 获取当前货币符号
     */
    private String getCurrencySymbol() {
        String currency = parentFrame.getCurrentCurrency();
        if (currency == null) {
            return "¥"; // 默认为人民币符号
        }
        
        if (currency.contains("Yuan")) return "¥";
        if (currency.contains("Dollar") && currency.contains("US")) return "$";
        if (currency.contains("Euro")) return "€";
        if (currency.contains("Pound")) return "£";
        if (currency.contains("Yen")) return "¥";
        if (currency.contains("Hong Kong")) return "HK$";
        
        // 如果都不匹配，返回默认符号
        return "¥";
    }
    
    /**
     * 根据当前货币设置获取汇率
     */
    private double getCurrentExchangeRate() {
        String currency = parentFrame.getCurrentCurrency();
        if (currency == null) {
            return 1.0; // 默认为人民币汇率
        }
        
        if (currency.contains("Yuan")) return 1.0;
        if (currency.contains("Dollar") && currency.contains("US")) return USD_RATE;
        if (currency.contains("Euro")) return EUR_RATE;
        if (currency.contains("Pound")) return GBP_RATE;
        if (currency.contains("Yen")) return JPY_RATE;
        if (currency.contains("Hong Kong")) return HKD_RATE;
        
        // 如果都不匹配，返回默认汇率
        return 1.0;
    }
    
    /**
     * 将人民币金额转换为当前货币金额
     */
    private double convertAmount(double yuanAmount) {
        return yuanAmount * getCurrentExchangeRate();
    }
    
    /**
     * 刷新交易表格，更新货币符号和金额
     */
    public void refreshTransactionDisplay() {
        if (tableModel == null || transactions.isEmpty()) {
            return;
        }
        
        // 清空表格
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        
        // 使用当前货币符号和汇率重新添加行
        String currencySymbol = getCurrencySymbol();
        for (Transaction transaction : transactions) {
            // 获取原始人民币金额并转换为当前货币
            double originalAmount = transaction.getYuanAmount();
            double convertedAmount = convertAmount(originalAmount);
            
            Object[] row = {
                transaction.getDate(),
                transaction.getCategory(),
                String.format("%s %.2f", currencySymbol, convertedAmount),
                transaction.getDescription()
            };
            tableModel.addRow(row);
        }
        
        // 更新统计信息
        updateStats();
    }
    
    /**
     * Initialize components
     */
    private void initComponents() {
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        titleLabel = new JLabel(languageManager.getText(LanguageManager.ADD_TRANSACTION), JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Transaction table
        String[] columns = {"Date", "Category", "Amount", "Description"};
        tableModel = new DefaultTableModel(columns, 0);
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(240);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Right panel for adding transactions
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.Y_AXIS));
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Transaction"));
        addPanel.setPreferredSize(new Dimension(250, 300));
        
        // Amount input
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.add(new JLabel("Amount:"));
        amountField = new JTextField(15);
        amountPanel.add(amountField);
        addPanel.add(amountPanel);
        
        // Category selection
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.add(new JLabel("Category:"));
        String[] categories = {"Food", "Housing", "Transportation", "Entertainment", "Shopping", "Healthcare", "Education", "Other"};
        categoryComboBox = new JComboBox<>(categories);
        categoryPanel.add(categoryComboBox);
        addPanel.add(categoryPanel);
        
        // Description input
        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        descPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField(15);
        descPanel.add(descriptionField);
        addPanel.add(descPanel);
        
        // Add button
        addButton = new JButton("Add Transaction");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setBackground(new Color(25, 118, 210));
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addTransaction());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        addPanel.add(Box.createVerticalStrut(20));
        addPanel.add(buttonPanel);
        
        // Action buttons
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        deleteButton = new JButton("Delete Selected");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setMargin(new Insets(5, 10, 5, 10));
        deleteButton.addActionListener(e -> deleteSelectedTransaction());
        
        generateReportButton = new JButton("Generate Report");
        generateReportButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateReportButton.setMargin(new Insets(5, 10, 5, 10));
        generateReportButton.addActionListener(e -> generateReport());
        
        actionsPanel.add(Box.createVerticalStrut(10));
        actionsPanel.add(deleteButton);
        actionsPanel.add(Box.createVerticalStrut(10));
        actionsPanel.add(generateReportButton);
        actionsPanel.add(Box.createVerticalStrut(10));
        
        addPanel.add(Box.createVerticalStrut(20));
        addPanel.add(actionsPanel);
        
        mainPanel.add(addPanel, BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);
        
        // Bottom stats panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        statsLabel = new JLabel("Total: 0 transactions | Total expense: ¥ 0.00");
        statsLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        statsPanel.add(statsLabel);
        
        add(statsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Add transaction
     */
    private void addTransaction() {
        try {
            // Get input values
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountText);
            String category = (String) categoryComboBox.getSelectedItem();
            String description = descriptionField.getText().trim();
            
            if (description.isEmpty()) {
                description = "No description";
            }
            
            // 创建交易对象 - 保存用户输入的金额（按当前货币）
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = dateFormat.format(date);
            
            // 将输入金额转换回人民币存储（如果当前不是人民币）
            double yuanAmount = amount;
            String currency = parentFrame.getCurrentCurrency();
            if (!currency.contains("Yuan")) {
                // 逆向转换回人民币金额
                yuanAmount = amount / getCurrentExchangeRate();
            }
            
            Transaction transaction = new Transaction(dateStr, category, yuanAmount, description);
            transactions.add(transaction);
            
            // 显示当前货币的金额
            String currencySymbol = getCurrencySymbol();
            Object[] row = {dateStr, category, String.format("%s %.2f", currencySymbol, amount), description};
            tableModel.addRow(row);
            
            // 把交易记录保存到用户配置文件，确保AI能够获取到最新数据
            try {
                // 将日期转换为Java 8 LocalDate格式
                java.time.LocalDate localDate = java.time.LocalDate.parse(dateStr);
                
                // 获取UserProfile实例并记录交易
                com.finance.tracker.profile.UserProfile userProfile = com.finance.tracker.profile.UserProfile.getInstance();
                userProfile.recordTransaction(category, new java.math.BigDecimal(yuanAmount), localDate);
                
                // 强制保存配置文件以确保数据持久化
                userProfile.saveProfile();
                
                System.out.println("DEBUG: 交易已保存到用户配置文件: " + category + ", " + yuanAmount + ", " + localDate);
                
                // 如果RecommendationPanel可见，则刷新推荐
                if (parentFrame.getMainContentPanel().isVisible()) {
                    try {
                        // 尝试获取并刷新推荐引擎
                        com.finance.tracker.ai.RecommendationEngine recommendationEngine = 
                            com.finance.tracker.ai.RecommendationEngine.getInstance();
                        recommendationEngine.generateAllRecommendations();
                        System.out.println("DEBUG: 已通知推荐引擎更新数据");
                    } catch (Exception ex) {
                        System.err.println("DEBUG: 无法刷新推荐引擎: " + ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                System.err.println("DEBUG: 保存交易到用户配置失败: " + ex.getMessage());
                ex.printStackTrace();
            }
            
            // Clear input fields
            amountField.setText("");
            descriptionField.setText("");
            
            // Update statistics
            updateStats();
            
            // Show success message
            JOptionPane.showMessageDialog(this, 
                "Transaction added successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Invalid amount format. Please enter a valid number", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Delete selected transaction
     */
    private void deleteSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Get the transaction before removing it
            Transaction deletedTransaction = transactions.get(selectedRow);
            
            // Remove from list and table
            transactions.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            
            // 尝试更新用户配置文件 - 注意：UserProfile可能不支持删除单个交易，但我们可以强制保存
            try {
                // 获取UserProfile实例并强制保存配置
                com.finance.tracker.profile.UserProfile userProfile = com.finance.tracker.profile.UserProfile.getInstance();
                
                // 记录一个负值交易来抵消被删除的交易（一种变通方法）
                // 将日期转换为Java 8 LocalDate格式
                java.time.LocalDate localDate = java.time.LocalDate.parse(deletedTransaction.getDate());
                
                // 创建一个负值金额来抵消删除的交易
                java.math.BigDecimal negativeAmount = new java.math.BigDecimal(-deletedTransaction.getYuanAmount());
                
                // 记录一个负值交易
                userProfile.recordTransaction(
                    deletedTransaction.getCategory(), 
                    negativeAmount, 
                    localDate
                );
                
                // 强制保存配置文件以确保数据持久化
                userProfile.saveProfile();
                
                System.out.println("DEBUG: 已从用户配置文件中抵消删除的交易: " + deletedTransaction.getCategory() + 
                                 ", " + (-deletedTransaction.getYuanAmount()) + ", " + localDate);
                
                // 如果RecommendationPanel可见，则刷新推荐
                if (parentFrame.getMainContentPanel().isVisible()) {
                    try {
                        // 尝试获取并刷新推荐引擎
                        com.finance.tracker.ai.RecommendationEngine recommendationEngine = 
                            com.finance.tracker.ai.RecommendationEngine.getInstance();
                        recommendationEngine.generateAllRecommendations();
                        System.out.println("DEBUG: 已通知推荐引擎更新数据");
                    } catch (Exception ex) {
                        System.err.println("DEBUG: 无法刷新推荐引擎: " + ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                System.err.println("DEBUG: 从用户配置删除交易失败: " + ex.getMessage());
                ex.printStackTrace();
            }
            
            // Update statistics
            updateStats();
            
            JOptionPane.showMessageDialog(this, 
                "Transaction deleted", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a transaction to delete", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Generate report
     */
    private void generateReport() {
        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No transactions available to generate a report", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create report dialog
        JDialog reportDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Transaction Report", true);
        reportDialog.setLayout(new BorderLayout());
        reportDialog.setSize(600, 400);
        reportDialog.setLocationRelativeTo(this);
        
        JPanel reportPanel = new JPanel(new BorderLayout(10, 10));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Report title
        JLabel titleLabel = new JLabel("Transaction Report Analysis", JLabel.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        reportPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Report content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Calculate total expense in Yuan
        double totalYuanAmount = 0;
        for (Transaction t : transactions) {
            totalYuanAmount += t.getYuanAmount();
        }
        
        // 转换为当前货币
        double convertedTotal = convertAmount(totalYuanAmount);
        String currencySymbol = getCurrencySymbol();
        
        // 创建一些模拟的报表数据
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Dialog", Font.PLAIN, 14));
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        
        StringBuilder reportText = new StringBuilder();
        reportText.append("报表生成时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");
        reportText.append("交易总数: ").append(transactions.size()).append("\n");
        reportText.append("总支出: ").append(currencySymbol).append(" ")
                .append(String.format("%.2f", convertedTotal)).append("\n\n");
        
        reportText.append("支出类别分析:\n");
        reportText.append("--------------------------------\n");
        
        // 生成一些模拟的类别分析
        String[] categories = {"食品", "住房", "交通", "娱乐", "购物", "医疗", "教育", "其他"};
        Random random = new Random();
        
        double totalPercent = 0;
        for (int i = 0; i < categories.length - 1; i++) {
            double percent = random.nextInt(20) + 5;
            if (totalPercent + percent > 90) {
                percent = 90 - totalPercent;
            }
            totalPercent += percent;
            reportText.append(categories[i]).append(": ").append(String.format("%.1f%%", percent))
                     .append(" (").append(currencySymbol).append(" ")
                     .append(String.format("%.2f", convertedTotal * percent / 100)).append(")\n");
        }
        
        double remainingPercent = 100 - totalPercent;
        reportText.append(categories[categories.length - 1]).append(": ").append(String.format("%.1f%%", remainingPercent))
                 .append(" (").append(currencySymbol).append(" ")
                 .append(String.format("%.2f", convertedTotal * remainingPercent / 100)).append(")\n\n");
        
        reportText.append("支出建议:\n");
        reportText.append("--------------------------------\n");
        reportText.append("1. 食品支出占比较高，可以考虑减少外出就餐频率。\n");
        reportText.append("2. 交通支出可以通过使用公共交通工具来降低。\n");
        reportText.append("3. 建议将收入的20%用于储蓄和投资。\n");
        
        reportArea.setText(reportText.toString());
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        contentPanel.add(scrollPane);
        
        reportPanel.add(contentPanel, BorderLayout.CENTER);
        
        // 底部按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printButton = new JButton("打印报表");
        printButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(reportDialog, 
                "报表打印功能将在完整版中提供", 
                "提示", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> reportDialog.dispose());
        
        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);
        
        reportPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        reportDialog.add(reportPanel);
        reportDialog.setVisible(true);
        
        // 使用parentFrame字段
        if (parentFrame != null) {
            // 通知主界面更新状态
            parentFrame.setCurrency(parentFrame.getCurrentCurrency());
        }
    }
    
    /**
     * 更新统计信息
     */
    private void updateStats() {
        double totalInYuan = 0;
        for (Transaction t : transactions) {
            totalInYuan += t.getYuanAmount();
        }
        
        // 转换为当前货币
        double convertedTotal = convertAmount(totalInYuan);
        
        String currencySymbol = getCurrencySymbol();
        statsLabel.setText("Total: " + transactions.size() + " transactions | Total expense: " + 
                          currencySymbol + " " + String.format("%.2f", convertedTotal));
    }
    
    /**
     * 加载示例交易数据 (以人民币为单位)
     */
    private void loadDemoTransactions() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        
        addTransactionData(today, "Food", 68.5, "Lunch and dinner");
        addTransactionData(today, "Transportation", 15.0, "Bus fare");
        addTransactionData(today, "Shopping", 299.0, "New clothes");
        addTransactionData(today, "Entertainment", 120.0, "Movie tickets");
        addTransactionData(today, "Food", 35.8, "Breakfast");
    }
    
    /**
     * 添加交易数据 (金额以人民币为单位)
     */
    private void addTransactionData(String date, String category, double yuanAmount, String description) {
        Transaction transaction = new Transaction(date, category, yuanAmount, description);
        transactions.add(transaction);
        
        // 显示为当前选择的货币
        String currencySymbol = getCurrencySymbol();
        double convertedAmount = convertAmount(yuanAmount);
        Object[] row = {date, category, String.format("%s %.2f", currencySymbol, convertedAmount), description};
        tableModel.addRow(row);
        
        updateStats();
    }
    
    /**
     * 交易数据类
     */
    private class Transaction {
        private String date;
        private String category;
        private double yuanAmount; // 以人民币为单位的金额
        private String description;
        
        public Transaction(String date, String category, double yuanAmount, String description) {
            this.date = date;
            this.category = category;
            this.yuanAmount = yuanAmount;
            this.description = description;
        }
        
        public String getDate() {
            return date;
        }
        
        public String getCategory() {
            return category;
        }
        
        public double getYuanAmount() {
            return yuanAmount;
        }
        
        // 获取转换后的金额
        public double getAmount() {
            return convertAmount(yuanAmount);
        }
        
        public String getDescription() {
            return description;
        }
    }
} 