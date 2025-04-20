package com.financeapp;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.json.simple.parser.ParseException;

import com.financeapp.model.FinanceData;
import com.financeapp.model.FinanceData.MonthData;
import com.financeapp.util.ChartGenerator;

public class FinanceAppGUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    private FinanceData financeData;
    private JPanel contentPanel;
    private JPanel timeSelectionPanel;
    private JPanel dataDisplayPanel;
    private CardLayout cardLayout;
    
    private JComboBox<String> analysisTypeComboBox;
    private JComboBox<String> monthComboBox;
    private JButton viewButton;
    
    private static final String[] ANALYSIS_TYPES = {
        "单月分析", "三个月趋势", "季度分析"
    };
    
    private static final String[] AVAILABLE_MONTHS = {
        "10月", "11月", "12月"
    };
    
    public FinanceAppGUI() {
        setTitle("财务分析应用");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Initialize data model
        financeData = new FinanceData();
        try {
            financeData.loadData("data/data.json");
        } catch (IOException | ParseException e) {
            JOptionPane.showMessageDialog(this, 
                "无法加载数据文件: " + e.getMessage(),
                "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create time selection panel (top section)
        createTimeSelectionPanel();
        mainPanel.add(timeSelectionPanel, BorderLayout.NORTH);
        
        // Create content panel for data display (center section)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Create empty data display panel initially
        dataDisplayPanel = new JPanel();
        dataDisplayPanel.setLayout(new BoxLayout(dataDisplayPanel, BoxLayout.Y_AXIS));
        
        JScrollPane scrollPane = new JScrollPane(dataDisplayPanel);
        contentPanel.add(scrollPane, "DATA_DISPLAY");
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Set main panel as content pane
        setContentPane(mainPanel);
    }
    
    private void createTimeSelectionPanel() {
        timeSelectionPanel = new JPanel();
        timeSelectionPanel.setBorder(BorderFactory.createTitledBorder("时间选择"));
        timeSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Analysis type selection
        JLabel analysisTypeLabel = new JLabel("分析类型:");
        analysisTypeComboBox = new JComboBox<>(ANALYSIS_TYPES);
        analysisTypeComboBox.addActionListener(this);
        
        // Month selection
        JLabel monthLabel = new JLabel("月份:");
        monthComboBox = new JComboBox<>(AVAILABLE_MONTHS);
        
        // View button
        viewButton = new JButton("查看数据");
        viewButton.addActionListener(this);
        
        // Add components to panel
        timeSelectionPanel.add(analysisTypeLabel);
        timeSelectionPanel.add(analysisTypeComboBox);
        timeSelectionPanel.add(monthLabel);
        timeSelectionPanel.add(monthComboBox);
        timeSelectionPanel.add(viewButton);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewButton) {
            updateDataDisplay();
        }
    }
    
    private void updateDataDisplay() {
        // Clear previous data display
        dataDisplayPanel.removeAll();
        
        String analysisType = (String) analysisTypeComboBox.getSelectedItem();
        String selectedMonth = (String) monthComboBox.getSelectedItem();
        
        JPanel chartsPanel = new JPanel(new GridLayout(1, 1, 10, 10));
        chartsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        switch (analysisType) {
            case "单月分析":
                displaySingleMonthAnalysis(selectedMonth, chartsPanel);
                break;
            case "三个月趋势":
                displayThreeMonthTrend(chartsPanel);
                break;
            case "季度分析":
                displayQuarterlyAnalysis(chartsPanel);
                break;
        }
        
        dataDisplayPanel.add(chartsPanel);
        dataDisplayPanel.revalidate();
        dataDisplayPanel.repaint();
    }
    
    private void displaySingleMonthAnalysis(String month, JPanel chartsPanel) {
        MonthData monthData = financeData.getMonthData(month);
        if (monthData == null) {
            showErrorMessage("无法找到所选月份的数据");
            return;
        }
        
        JPanel pieChartPanel = ChartGenerator.createMonthlyExpensesPieChart(monthData);
        
        // Predict next month expenses
        Map<String, Double> prediction = financeData.predictNextMonthExpenses(month);
        JPanel predictionChartPanel = ChartGenerator.createExpensePredictionChart(monthData, prediction);
        
        // Add charts to the panel
        chartsPanel.setLayout(new GridLayout(2, 1, 10, 10));
        chartsPanel.add(pieChartPanel);
        chartsPanel.add(predictionChartPanel);
        
        // Add summary label
        JLabel summaryLabel = new JLabel(
            "<html><h3>" + month + " 总结</h3>" +
            "收入: " + monthData.getIncome() + "<br>" +
            "总支出: " + monthData.getTotalExpenses() + "<br>" +
            "结余: " + monthData.getBalance() + "</html>"
        );
        summaryLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        dataDisplayPanel.add(summaryLabel);
    }
    
    private void displayThreeMonthTrend(JPanel chartsPanel) {
        List<String> months = Arrays.asList(AVAILABLE_MONTHS);
        List<MonthData> monthsData = financeData.getMonthsData(months);
        
        if (monthsData.isEmpty()) {
            showErrorMessage("无法找到三个月的数据");
            return;
        }
        
        JPanel trendChartPanel = ChartGenerator.createExpenseTrendChart(monthsData);
        chartsPanel.add(trendChartPanel);
        
        // Add budget suggestions
        double avgExpense = 0;
        for (MonthData monthData : monthsData) {
            avgExpense += monthData.getTotalExpenses();
        }
        avgExpense /= monthsData.size();
        
        // Simple budget suggestion based on average expenses
        JLabel budgetLabel = new JLabel(
            "<html><h3>预算建议</h3>" +
            "三个月平均支出: " + String.format("%.2f", avgExpense) + "<br>" +
            "建议下月预算: " + String.format("%.2f", avgExpense * 0.95) + "<br>" +
            "可以考虑在上个季度支出较多的类别上减少开支</html>"
        );
        budgetLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        dataDisplayPanel.add(budgetLabel);
    }
    
    private void displayQuarterlyAnalysis(JPanel chartsPanel) {
        List<String> months = Arrays.asList(AVAILABLE_MONTHS);
        List<MonthData> monthsData = financeData.getMonthsData(months);
        
        if (monthsData.isEmpty()) {
            showErrorMessage("无法找到季度数据");
            return;
        }
        
        // Show quarterly distribution chart
        JPanel distributionChartPanel = ChartGenerator.createQuarterlyDistributionChart(monthsData);
        chartsPanel.add(distributionChartPanel);
        
        // Add savings completion analysis
        double totalIncome = 0;
        double totalExpense = 0;
        
        for (MonthData monthData : monthsData) {
            totalIncome += monthData.getIncome();
            totalExpense += monthData.getTotalExpenses();
        }
        
        double savingsTarget = totalIncome * 0.2; // Assuming 20% savings target
        double actualSavings = totalIncome - totalExpense;
        double savingsPercentage = (actualSavings / savingsTarget) * 100;
        
        JLabel savingsLabel = new JLabel(
            "<html><h3>储蓄完成情况</h3>" +
            "季度总收入: " + String.format("%.2f", totalIncome) + "<br>" +
            "季度总支出: " + String.format("%.2f", totalExpense) + "<br>" +
            "储蓄目标: " + String.format("%.2f", savingsTarget) + "<br>" +
            "实际储蓄: " + String.format("%.2f", actualSavings) + "<br>" +
            "目标完成率: " + String.format("%.2f", savingsPercentage) + "%</html>"
        );
        savingsLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        dataDisplayPanel.add(savingsLabel);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
} 