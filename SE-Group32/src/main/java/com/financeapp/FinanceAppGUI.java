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
import javax.swing.JTabbedPane;
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
        "Monthly Analysis", "Three-Month Trend", "Quarterly Analysis"
    };
    
    // Use dynamically retrieved month data
    private List<String> availableMonths;
    
    public FinanceAppGUI() {
        setTitle("Financial Analysis Application");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Initialize data - use existing data from ChartGenerator
        try {
            // Get available months list
            availableMonths = ChartGenerator.getAvailableMonths();
            
            // If no data, show prompt
            if (availableMonths.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No transaction data available, please add transactions first", 
                    "Notice", JOptionPane.INFORMATION_MESSAGE);
                
                // Add some default months
                availableMonths = Arrays.asList("01", "02", "03");
            }
            
            // Create main interface
            initializeUI();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to initialize interface: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            dispose();
        }
    }
    
    private void initializeUI() {
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create top control panel
        createControlPanel();
        mainPanel.add(timeSelectionPanel, BorderLayout.NORTH);
        
        // Create data display panel
        dataDisplayPanel = new JPanel();
        dataDisplayPanel.setLayout(new BoxLayout(dataDisplayPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(dataDisplayPanel);
        
        // Keep only financial analysis tab
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Set content panel
        setContentPane(mainPanel);
    }
    
    private void createControlPanel() {
        timeSelectionPanel = new JPanel();
        timeSelectionPanel.setBorder(BorderFactory.createTitledBorder("Analysis Options"));
        timeSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        // Analysis type selection
        JLabel analysisTypeLabel = new JLabel("Analysis Type:");
        analysisTypeComboBox = new JComboBox<>(ANALYSIS_TYPES);
        analysisTypeComboBox.addActionListener(this);
        
        // Month selection
        JLabel monthLabel = new JLabel("Month:");
        monthComboBox = new JComboBox<>(availableMonths.toArray(new String[0]));
        
        // View button
        viewButton = new JButton("View Analysis");
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
            case "Monthly Analysis":
                displaySingleMonthAnalysis(selectedMonth, chartsPanel);
                break;
            case "Three-Month Trend":
                displayThreeMonthTrend(chartsPanel);
                break;
            case "Quarterly Analysis":
                displayQuarterlyAnalysis(chartsPanel);
                break;
        }
        
        dataDisplayPanel.add(chartsPanel);
        dataDisplayPanel.revalidate();
        dataDisplayPanel.repaint();
    }
    
    private void displaySingleMonthAnalysis(String month, JPanel chartsPanel) {
        // Get month data from ChartGenerator
        MonthData monthData = ChartGenerator.getMonthData(month);
        if (monthData == null) {
            showErrorMessage("Could not find data for selected month");
            return;
        }
        
        JPanel pieChartPanel = ChartGenerator.createMonthlyExpensesPieChart(monthData);
        
        // Predict next month expenses
        Map<String, Double> prediction = ChartGenerator.predictNextMonthExpenses(month);
        JPanel predictionChartPanel = ChartGenerator.createExpensePredictionChart(monthData, prediction);
        
        // Add charts to panel
        chartsPanel.setLayout(new GridLayout(2, 1, 10, 10));
        chartsPanel.add(pieChartPanel);
        chartsPanel.add(predictionChartPanel);
        
        // Add summary label
        JLabel summaryLabel = new JLabel(
            "<html><h3>Month " + month + " Summary</h3>" +
            "Income: " + String.format("%.2f", monthData.getIncome()) + "<br>" +
            "Total Expenses: " + String.format("%.2f", monthData.getTotalExpenses()) + "<br>" +
            "Balance: " + String.format("%.2f", monthData.getBalance()) + "</html>"
        );
        summaryLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        dataDisplayPanel.add(summaryLabel);
    }
    
    private void displayThreeMonthTrend(JPanel chartsPanel) {
        // Use all available months, but take at most 3
        List<String> months = new ArrayList<>(availableMonths);
        if (months.size() > 3) {
            months = months.subList(0, 3);
        }
        
        List<MonthData> monthsData = ChartGenerator.getMonthsData(months);
        
        if (monthsData.isEmpty()) {
            showErrorMessage("Could not find enough monthly data");
            return;
        }
        
        JPanel trendChartPanel = ChartGenerator.createExpenseTrendChart(monthsData);
        chartsPanel.add(trendChartPanel);
        
        // Add budget suggestion
        double avgExpense = 0;
        for (MonthData monthData : monthsData) {
            avgExpense += monthData.getTotalExpenses();
        }
        avgExpense /= monthsData.size();
        
        // Simple budget suggestion
        JLabel budgetLabel = new JLabel(
            "<html><h3>Budget Recommendation</h3>" +
            "Average Monthly Expenses: " + String.format("%.2f", avgExpense) + "<br>" +
            "Suggested Next Month Budget: " + String.format("%.2f", avgExpense * 0.95) + "<br>" +
            "Consider reducing spending in major expense categories</html>"
        );
        budgetLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        dataDisplayPanel.add(budgetLabel);
    }
    
    private void displayQuarterlyAnalysis(JPanel chartsPanel) {
        // Get all available month data
        List<MonthData> monthsData = ChartGenerator.getMonthsData(availableMonths);
        
        if (monthsData.isEmpty()) {
            showErrorMessage("Could not find quarterly data");
            return;
        }
        
        // Display quarterly distribution chart
        JPanel distributionChartPanel = ChartGenerator.createQuarterlyStackedBarChart(monthsData);
        chartsPanel.add(distributionChartPanel);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
} 