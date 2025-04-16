/*
 * Package: com.finance.tracker.ui
 * Purpose: Custom UI components for your module
 */
package com.finance.tracker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.finance.tracker.ai.Recommendation;
import com.finance.tracker.calendar.HolidayEvent;
import com.finance.tracker.integration.AIModuleFacade;
import com.finance.tracker.localization.Currency;
import com.finance.tracker.localization.CurrencyManager;

/**
 * Main panel for the AI module's UI components
 */
public class AIModulePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private AIModuleFacade aiFacade;
    private JPanel recommendationsPanel;
    private JPanel settingsPanel;
    private JPanel budgetOptimizationPanel;
    
    public AIModulePanel() {
        aiFacade = AIModuleFacade.getInstance();
        
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Create recommendations panel
        recommendationsPanel = createRecommendationsPanel();
        tabbedPane.addTab("智能建议", recommendationsPanel);
        
        // Create budget optimization panel
        budgetOptimizationPanel = createBudgetOptimizationPanel();
        tabbedPane.addTab("预算优化", budgetOptimizationPanel);
        
        // Create settings panel
        settingsPanel = createSettingsPanel();
        tabbedPane.addTab("本地化设置", settingsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add refresh button at the bottom
        JButton refreshButton = new JButton("刷新数据");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAllPanels();
            }
        });
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the recommendations panel
     */
    private JPanel createRecommendationsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("个性化财务建议", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Recommendations container
        JPanel recommendationsContainer = new JPanel();
        recommendationsContainer.setLayout(new BoxLayout(recommendationsContainer, BoxLayout.Y_AXIS));
        
        // Get recommendations
        List<Recommendation> recommendations = aiFacade.getActiveRecommendations();
        
        if (recommendations.isEmpty()) {
            JLabel emptyLabel = new JLabel("暂无建议", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Dialog", Font.ITALIC, 14));
            recommendationsContainer.add(emptyLabel);
        } else {
            // Add each recommendation as a card
            for (Recommendation rec : recommendations) {
                JPanel recPanel = createRecommendationPanel(rec);
                recommendationsContainer.add(recPanel);
                recommendationsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(recommendationsContainer);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Holiday reminders
        JPanel holidayPanel = createHolidayPanel();
        panel.add(holidayPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates a panel for a single recommendation
     */
    private JPanel createRecommendationPanel(final Recommendation rec) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getTypeColor(rec.getType()), 2),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Recommendation message
        JLabel messageLabel = new JLabel("<html><div style='width:300px;'>" + rec.getMessage() + "</div></html>");
        panel.add(messageLabel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Feedback buttons
        JButton helpfulButton = new JButton("👍 有用");
        helpfulButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String comment = JOptionPane.showInputDialog(panel, "请提供反馈（可选）：");
                aiFacade.recordRecommendationFeedback(rec.getId(), true, comment);
                refreshAllPanels();
            }
        });
        
        JButton notHelpfulButton = new JButton("👎 无用");
        notHelpfulButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String comment = JOptionPane.showInputDialog(panel, "请提供改进建议（可选）：");
                aiFacade.recordRecommendationFeedback(rec.getId(), false, comment);
                refreshAllPanels();
            }
        });
        
        // Dismiss button
        JButton dismissButton = new JButton("✕ 关闭");
        dismissButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aiFacade.dismissRecommendation(rec.getId());
                refreshAllPanels();
            }
        });
        
        buttonsPanel.add(helpfulButton);
        buttonsPanel.add(notHelpfulButton);
        buttonsPanel.add(dismissButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        // Date created
        JLabel dateLabel = new JLabel(rec.getDateCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(dateLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    /**
     * Get color based on recommendation type
     */
    private Color getTypeColor(String type) {
        switch (type) {
            case "BUDGET":
                return new Color(70, 130, 180); // Steel Blue
            case "SAVINGS":
                return new Color(60, 179, 113); // Medium Sea Green
            case "PATTERN":
                return new Color(255, 165, 0);  // Orange
            default:
                return new Color(100, 100, 100); // Gray
        }
    }
    
    /**
     * Creates the holiday reminder panel
     */
    private JPanel createHolidayPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "节假日提醒",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Dialog", Font.BOLD, 12)
        ));
        
        // Get upcoming holidays
        List<HolidayEvent> upcomingHolidays = aiFacade.getUpcomingHolidays(30);
        
        if (upcomingHolidays.isEmpty()) {
            JLabel emptyLabel = new JLabel("未来30天内无节假日", SwingConstants.CENTER);
            panel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            JPanel holidaysContainer = new JPanel();
            holidaysContainer.setLayout(new BoxLayout(holidaysContainer, BoxLayout.Y_AXIS));
            
            // Add each holiday
            for (int i = 0; i < Math.min(3, upcomingHolidays.size()); i++) {
                HolidayEvent event = upcomingHolidays.get(i);
                
                JLabel holidayLabel = new JLabel(
                    event.getHoliday().getName() + " - " + 
                    event.getDate().format(DateTimeFormatter.ofPattern("MM月dd日"))
                );
                
                if (event.getHoliday().getType().equals("major")) {
                    holidayLabel.setFont(new Font("Dialog", Font.BOLD, 12));
                    holidayLabel.setForeground(new Color(178, 34, 34)); // Firebrick
                } else {
                    holidayLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
                }
                
                holidaysContainer.add(holidayLabel);
                if (i < Math.min(3, upcomingHolidays.size()) - 1) {
                    holidaysContainer.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
            
            panel.add(holidaysContainer, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    /**
     * Creates the budget optimization panel
     */
    private JPanel createBudgetOptimizationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("AI 预算优化", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Get optimized budget and predicted spending
        Map<String, BigDecimal> optimizedBudget = aiFacade.getOptimizedBudget();
        Map<String, BigDecimal> predictedSpending = aiFacade.getPredictedSpending();
        
        // Create content panel with GridBagLayout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Headers
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 2.0;
        JLabel categoryHeader = new JLabel("支出类别", SwingConstants.LEFT);
        categoryHeader.setFont(new Font("Dialog", Font.BOLD, 14));
        contentPanel.add(categoryHeader, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel currentHeader = new JLabel("当前预算", SwingConstants.RIGHT);
        currentHeader.setFont(new Font("Dialog", Font.BOLD, 14));
        contentPanel.add(currentHeader, gbc);
        
        gbc.gridx = 2;
        JLabel recommendedHeader = new JLabel("建议预算", SwingConstants.RIGHT);
        recommendedHeader.setFont(new Font("Dialog", Font.BOLD, 14));
        contentPanel.add(recommendedHeader, gbc);
        
        gbc.gridx = 3;
        JLabel predictedHeader = new JLabel("预测支出", SwingConstants.RIGHT);
        predictedHeader.setFont(new Font("Dialog", Font.BOLD, 14));
        contentPanel.add(predictedHeader, gbc);
        
        // Add category rows
        int row = 1;
        for (Map.Entry<String, BigDecimal> entry : optimizedBudget.entrySet()) {
            String category = entry.getKey();
            BigDecimal optimized = entry.getValue();
            BigDecimal predicted = predictedSpending.getOrDefault(category, BigDecimal.ZERO);
            BigDecimal current = BigDecimal.ZERO; // In a real app, get this from user profile
            
            // Category name
            gbc.gridx = 0;
            gbc.gridy = row;
            JLabel categoryLabel = new JLabel(category);
            contentPanel.add(categoryLabel, gbc);
            
            // Current budget
            gbc.gridx = 1;
            JLabel currentLabel = new JLabel(formatAmount(current), SwingConstants.RIGHT);
            contentPanel.add(currentLabel, gbc);
            
            // Recommended budget
            gbc.gridx = 2;
            JLabel recommendedLabel = new JLabel(formatAmount(optimized), SwingConstants.RIGHT);
            recommendedLabel.setForeground(new Color(0, 128, 0)); // Dark Green
            contentPanel.add(recommendedLabel, gbc);
            
            // Predicted spending
            gbc.gridx = 3;
            JLabel predictedLabel = new JLabel(formatAmount(predicted), SwingConstants.RIGHT);
            predictedLabel.setForeground(new Color(139, 69, 19)); // Saddle Brown
            contentPanel.add(predictedLabel, gbc);
            
            row++;
        }
        
        // Add content panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Seasonal recommendation
        String seasonalRec = aiFacade.getSeasonalBudgetRecommendation();
        if (seasonalRec != null && !seasonalRec.isEmpty()) {
            JPanel seasonalPanel = new JPanel(new BorderLayout());
            seasonalPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "季节性预算建议",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Dialog", Font.BOLD, 12)
            ));
            
            JLabel seasonalLabel = new JLabel("<html><div style='width:400px;'>" + seasonalRec + "</div></html>");
            seasonalLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
            seasonalPanel.add(seasonalLabel, BorderLayout.CENTER);
            
            panel.add(seasonalPanel, BorderLayout.SOUTH);
        }
        
        // Apply button
        JButton applyButton = new JButton("应用AI推荐预算");
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(
                    panel,
                    "确认将AI推荐的预算应用于所有类别吗？",
                    "确认",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (result == JOptionPane.YES_OPTION) {
                    // Apply optimized budget (in a real app, save to user profile)
                    JOptionPane.showMessageDialog(panel, "已应用AI推荐预算！");
                    refreshAllPanels();
                }
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(applyButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Format amount with ¥ symbol
     */
    private String formatAmount(BigDecimal amount) {
        return "¥ " + amount.toString();
    }
    
    /**
     * Creates the settings panel
     */
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel("本地化设置", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create content panel with grid bag layout
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Currency settings
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JLabel currencyLabel = new JLabel("货币设置", SwingConstants.LEFT);
        currencyLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        contentPanel.add(currencyLabel, gbc);
        
        // Default currency
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel defaultCurrencyLabel = new JLabel("默认货币：", SwingConstants.RIGHT);
        contentPanel.add(defaultCurrencyLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        
        // Get supported currencies
        Map<String, Currency> currencies = CurrencyManager.getInstance().getSupportedCurrencies();
        String[] currencyCodes = currencies.keySet().toArray(new String[0]);
        
        final JComboBox<String> currencyComboBox = new JComboBox<>(new DefaultComboBoxModel<>(currencyCodes));
        currencyComboBox.setSelectedItem(CurrencyManager.getInstance().getDefaultCurrency().getCode());
        currencyComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCurrency = (String) currencyComboBox.getSelectedItem();
                aiFacade.setDefaultCurrency(selectedCurrency);
            }
        });
        contentPanel.add(currencyComboBox, gbc);
        
        // Region settings
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel regionLabel = new JLabel("地区设置", SwingConstants.LEFT);
        regionLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        contentPanel.add(regionLabel, gbc);
        
        // Region selector
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel selectRegionLabel = new JLabel("选择地区：", SwingConstants.RIGHT);
        contentPanel.add(selectRegionLabel, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] regions = { "中国大陆", "香港特区", "澳门特区", "台湾地区" };
        final JComboBox<String> regionComboBox = new JComboBox<>(new DefaultComboBoxModel<>(regions));
        regionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRegion = (String) regionComboBox.getSelectedItem();
                aiFacade.setRegion(selectedRegion);
            }
        });
        contentPanel.add(regionComboBox, gbc);
        
        // Holiday calendar
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel holidayLabel = new JLabel("节假日设置", SwingConstants.LEFT);
        holidayLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        contentPanel.add(holidayLabel, gbc);
        
        // Holiday info
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        JTextArea holidayInfo = new JTextArea(
            "系统会自动识别中国主要节假日，并在节日前提供个性化预算建议。" +
            "系统支持农历节日（如春节、中秋）和公历节日（如国庆、元旦）。"
        );
        holidayInfo.setLineWrap(true);
        holidayInfo.setWrapStyleWord(true);
        holidayInfo.setEditable(false);
        holidayInfo.setBackground(panel.getBackground());
        holidayInfo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        holidayInfo.setMargin(new Insets(10, 10, 10, 10));
        contentPanel.add(holidayInfo, gbc);
        
        // AI settings
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(20, 10, 10, 10);
        JLabel aiLabel = new JLabel("AI推荐设置", SwingConstants.LEFT);
        aiLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        contentPanel.add(aiLabel, gbc);
        
        // AI info
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        JTextArea aiInfo = new JTextArea(
            "AI系统会基于您的消费习惯、所在地区和节假日信息提供个性化财务建议。" +
            "您可以对建议提供反馈，系统会不断学习并优化推荐质量。"
        );
        aiInfo.setLineWrap(true);
        aiInfo.setWrapStyleWord(true);
        aiInfo.setEditable(false);
        aiInfo.setBackground(panel.getBackground());
        aiInfo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        aiInfo.setMargin(new Insets(10, 10, 10, 10));
        contentPanel.add(aiInfo, gbc);
        
        // Save button
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JButton saveButton = new JButton("保存设置");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(panel, "设置已保存！");
                refreshAllPanels();
            }
        });
        contentPanel.add(saveButton, gbc);
        
        // Add content panel to the main panel
        panel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Refresh all panels
     */
    private void refreshAllPanels() {
        // Remove all components
        this.removeAll();
        
        // Re-create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Re-create all panels
        recommendationsPanel = createRecommendationsPanel();
        tabbedPane.addTab("智能建议", recommendationsPanel);
        
        budgetOptimizationPanel = createBudgetOptimizationPanel();
        tabbedPane.addTab("预算优化", budgetOptimizationPanel);
        
        settingsPanel = createSettingsPanel();
        tabbedPane.addTab("本地化设置", settingsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add refresh button at the bottom
        JButton refreshButton = new JButton("刷新数据");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAllPanels();
            }
        });
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Refresh UI
        revalidate();
        repaint();
    }
}
