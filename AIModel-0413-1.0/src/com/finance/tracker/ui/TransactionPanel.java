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

/**
 * 交易记录管理面板
 */
public class TransactionPanel extends JPanel {
    private MainModuleUI parentFrame;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JTextField amountField;
    private JComboBox<String> categoryComboBox;
    private JTextField descriptionField;
    private List<Transaction> transactions = new ArrayList<>();
    
    /**
     * 构造函数
     */
    public TransactionPanel(MainModuleUI parent) {
        this.parentFrame = parent;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadDemoTransactions();
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("交易记录管理", JLabel.LEFT);
        titleLabel.setFont(new Font("黑体", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 中间主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // 交易表格
        String[] columns = {"日期", "类别", "金额", "描述"};
        tableModel = new DefaultTableModel(columns, 0);
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setReorderingAllowed(false);
        
        // 设置表格列宽
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(240);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 右侧添加交易面板
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.Y_AXIS));
        addPanel.setBorder(BorderFactory.createTitledBorder("添加新交易"));
        addPanel.setPreferredSize(new Dimension(250, 300));
        
        // 金额输入
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        amountPanel.add(new JLabel("金额:"));
        amountField = new JTextField(15);
        amountPanel.add(amountField);
        addPanel.add(amountPanel);
        
        // 类别选择
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.add(new JLabel("类别:"));
        String[] categories = {"食品", "住房", "交通", "娱乐", "购物", "医疗", "教育", "其他"};
        categoryComboBox = new JComboBox<>(categories);
        categoryPanel.add(categoryComboBox);
        addPanel.add(categoryPanel);
        
        // 描述输入
        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        descPanel.add(new JLabel("描述:"));
        descriptionField = new JTextField(15);
        descPanel.add(descriptionField);
        addPanel.add(descPanel);
        
        // 添加按钮
        JButton addButton = new JButton("添加交易");
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.setBackground(new Color(25, 118, 210));
        addButton.setForeground(Color.BLACK);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> addTransaction());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        addPanel.add(Box.createVerticalStrut(20));
        addPanel.add(buttonPanel);
        
        // 操作按钮
        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("操作"));
        
        JButton deleteButton = new JButton("删除选中交易");
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        deleteButton.setMargin(new Insets(5, 10, 5, 10));
        deleteButton.addActionListener(e -> deleteSelectedTransaction());
        
        JButton generateReportButton = new JButton("生成报表");
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
        
        // 底部统计面板
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        JLabel statsLabel = new JLabel("共 0 条交易记录 | 总支出: ¥ 0.00");
        statsLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        statsPanel.add(statsLabel);
        
        add(statsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * 添加交易
     */
    private void addTransaction() {
        try {
            // 获取输入值
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入金额", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountText);
            String category = (String) categoryComboBox.getSelectedItem();
            String description = descriptionField.getText().trim();
            
            if (description.isEmpty()) {
                description = "未添加描述";
            }
            
            // 创建交易对象
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = dateFormat.format(date);
            
            Transaction transaction = new Transaction(dateStr, category, amount, description);
            transactions.add(transaction);
            
            // 更新表格
            Object[] row = {dateStr, category, String.format("¥ %.2f", amount), description};
            tableModel.addRow(row);
            
            // 清空输入框
            amountField.setText("");
            descriptionField.setText("");
            
            // 更新统计信息
            updateStats();
            
            // 显示成功消息
            JOptionPane.showMessageDialog(this, 
                "交易已添加成功！", 
                "添加成功", 
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "金额格式不正确，请输入有效的数字", 
                "输入错误", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 删除选中的交易
     */
    private void deleteSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow >= 0) {
            // 从列表和表格中移除
            transactions.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            
            // 更新统计信息
            updateStats();
            
            JOptionPane.showMessageDialog(this, 
                "交易已删除", 
                "删除成功", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "请先选择要删除的交易", 
                "提示", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * 生成报表
     */
    private void generateReport() {
        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "暂无交易记录，无法生成报表", 
                "提示", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // 创建报表对话框
        JDialog reportDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "交易报表", true);
        reportDialog.setLayout(new BorderLayout());
        reportDialog.setSize(600, 400);
        reportDialog.setLocationRelativeTo(this);
        
        JPanel reportPanel = new JPanel(new BorderLayout(10, 10));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 报表标题
        JLabel titleLabel = new JLabel("交易记录报表分析", JLabel.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        reportPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 报表内容
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // 计算总支出
        double totalAmount = 0;
        for (Transaction t : transactions) {
            totalAmount += t.getAmount();
        }
        
        // 创建一些模拟的报表数据
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Dialog", Font.PLAIN, 14));
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        
        StringBuilder reportText = new StringBuilder();
        reportText.append("报表生成时间: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");
        reportText.append("交易总数: ").append(transactions.size()).append("\n");
        reportText.append("总支出: ¥ ").append(String.format("%.2f", totalAmount)).append("\n\n");
        
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
                     .append(" (¥ ").append(String.format("%.2f", totalAmount * percent / 100)).append(")\n");
        }
        
        double remainingPercent = 100 - totalPercent;
        reportText.append(categories[categories.length - 1]).append(": ").append(String.format("%.1f%%", remainingPercent))
                 .append(" (¥ ").append(String.format("%.2f", totalAmount * remainingPercent / 100)).append(")\n\n");
        
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
        double total = 0;
        for (Transaction t : transactions) {
            total += t.getAmount();
        }
        
        JLabel statsLabel = (JLabel) ((JPanel) getComponent(2)).getComponent(0);
        statsLabel.setText("共 " + transactions.size() + " 条交易记录 | 总支出: ¥ " + String.format("%.2f", total));
    }
    
    /**
     * 加载示例交易数据
     */
    private void loadDemoTransactions() {
        // 添加一些示例交易数据
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        
        addTransactionData(today, "食品", 68.5, "午餐和晚餐");
        addTransactionData(today, "交通", 15.0, "公交车费");
        addTransactionData(today, "购物", 299.0, "新衣服");
        addTransactionData(today, "娱乐", 120.0, "电影票");
        addTransactionData(today, "食品", 35.8, "早餐");
    }
    
    /**
     * 添加交易数据
     */
    private void addTransactionData(String date, String category, double amount, String description) {
        Transaction transaction = new Transaction(date, category, amount, description);
        transactions.add(transaction);
        
        Object[] row = {date, category, String.format("¥ %.2f", amount), description};
        tableModel.addRow(row);
        
        updateStats();
    }
    
    /**
     * 交易数据类
     */
    private class Transaction {
        private String date;
        private String category;
        private double amount;
        private String description;
        
        public Transaction(String date, String category, double amount, String description) {
            this.date = date;
            this.category = category;
            this.amount = amount;
            this.description = description;
        }
        
        public String getDate() {
            return date;
        }
        
        public String getCategory() {
            return category;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public String getDescription() {
            return description;
        }
    }
} 