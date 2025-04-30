package com.finance.tracker.report;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class ReportAndNotificationUI extends JFrame {
    private final ReportAndNotificationService service;
    private final List<Transaction> transactions;

    public ReportAndNotificationUI(List<Transaction> transactions) {
        this.transactions = transactions;
        this.service = new ReportAndNotificationService();

        setTitle("Report and Notification Center");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Report export panel
        JPanel reportPanel = createReportPanel();
        
        // Budget alert panel
        JPanel budgetAlertPanel = createBudgetAlertPanel();
        
        // Notification settings panel
        JPanel notificationSettingPanel = createNotificationSettingPanel();

        mainPanel.add(reportPanel);
        mainPanel.add(budgetAlertPanel);
        mainPanel.add(notificationSettingPanel);

        add(mainPanel);
    }

    // Report export panel
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Report Export"));

        JButton csvExportBtn = new JButton("Export CSV Report");
        csvExportBtn.addActionListener(actionEvent -> exportCSVReport());

        JButton txtExportBtn = new JButton("Export PDF Report");
        txtExportBtn.addActionListener(actionEvent -> exportPDFReport());

        panel.add(csvExportBtn);
        panel.add(txtExportBtn);

        return panel;
    }

    // Budget alert panel
    private JPanel createBudgetAlertPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Budget Alert"));

        JTextField budgetLimitField = new JTextField("300.0");
        JButton checkBudgetBtn = new JButton("Check Budget");
        
        checkBudgetBtn.addActionListener(actionEvent -> {
            try {
                double budgetLimit = Double.parseDouble(budgetLimitField.getText());
                
                // 从数据中心获取最新交易数据
                List<Transaction> latestTransactions = new ArrayList<>();
                List<com.finance.tracker.classification.model.Transaction> sourceTransactions = 
                    com.finance.tracker.classification.util.TransactionDataCenter.getInstance().getAllTransactions();
                
                // 转换交易数据格式
                for (com.finance.tracker.classification.model.Transaction t : sourceTransactions) {
                    // 日期格式转换
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    String dateStr = t.getDateTime().toLocalDate().format(formatter);
                    
                    // 创建报告模块需要的Transaction对象
                    latestTransactions.add(new Transaction(
                        dateStr,
                        t.getCategory().getName(),
                        t.getAmount().doubleValue(),
                        t.getDescription() != null ? t.getDescription() : ""
                    ));
                }
                
                // 使用最新数据检查预算
                String notification = service.generateBudgetAlertNotification(latestTransactions, budgetLimit);
                JOptionPane.showMessageDialog(this, notification, "Budget Alert", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid budget amount", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error checking budget: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Budget Limit:"));
        inputPanel.add(budgetLimitField);
        inputPanel.add(checkBudgetBtn);

        panel.add(inputPanel, BorderLayout.CENTER);

        return panel;
    }

    // Notification settings panel
    private JPanel createNotificationSettingPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Notification Settings"));

        JCheckBox emailNotificationCheck = new JCheckBox("Enable Email Notifications");
        JCheckBox smsNotificationCheck = new JCheckBox("Enable SMS Notifications");
        
        JButton saveSettingsBtn = new JButton("Save Notification Settings");
        saveSettingsBtn.addActionListener(actionEvent -> {
            boolean emailEnabled = emailNotificationCheck.isSelected();
            boolean smsEnabled = smsNotificationCheck.isSelected();
            saveNotificationSettings(emailEnabled, smsEnabled);
        });

        panel.add(emailNotificationCheck);
        panel.add(smsNotificationCheck);
        panel.add(saveSettingsBtn);

        return panel;
    }

    // Export CSV report
    private void exportCSVReport() {
        try {
            String filename = "monthly_report_" + System.currentTimeMillis() + ".csv";
            service.exportCSVReport(transactions, filename);
            JOptionPane.showMessageDialog(this, "CSV Report Exported: " + filename, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error exporting CSV report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Export PDF report
    private void exportPDFReport() {
        try {
            String filename = "monthly_report_" + System.currentTimeMillis() + ".pdf";
            String actualFilename = service.exportPDFReport(transactions, filename);
            JOptionPane.showMessageDialog(
                this, 
                "Report successfully generated as: " + actualFilename,
                "Export Successful", 
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting PDF report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Save notification settings
    private void saveNotificationSettings(boolean emailEnabled, boolean smsEnabled) {
        try {
            // Implement actual notification settings saving logic
            String settings = "Email Notifications: " + (emailEnabled ? "Enabled" : "Disabled") + 
                              "\nSMS Notifications: " + (smsEnabled ? "Enabled" : "Disabled");
            service.saveNotificationToFile(settings, "notification_settings.txt");
            JOptionPane.showMessageDialog(this, "Notification Settings Saved", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving notification settings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        List<Transaction> testTransactions = Arrays.asList(
            new Transaction("2025-03-01", "Food", 500.0, "Takeout"),
            new Transaction("2025-03-02", "Transportation", 200.0, "Taxi"),
            new Transaction("2025-03-03", "Food", 300.0, "Dining out")
        );

        SwingUtilities.invokeLater(() -> {
            new ReportAndNotificationUI(testTransactions).setVisible(true);
        });
    }
} 