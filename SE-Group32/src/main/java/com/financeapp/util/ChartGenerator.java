package com.financeapp.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

// JFreeChart imports
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.classification.util.TransactionDataCenter;
import com.financeapp.model.FinanceData;
import com.financeapp.model.FinanceData.MonthData;

public class ChartGenerator {
    // Store transaction data
    private static List<Transaction> transactionData = new ArrayList<>();
    
    // Cache month data
    private static Map<String, MonthData> monthDataCache = new HashMap<>();
    
    // Chart colors
    private static final Color[] CHART_COLORS = {
        new Color(255, 102, 102), // Red
        new Color(102, 178, 255), // Blue
        new Color(102, 255, 102), // Green
        new Color(255, 178, 102), // Orange
        new Color(178, 102, 255), // Purple
        new Color(255, 255, 102), // Yellow
        new Color(102, 255, 178), // Cyan
        new Color(255, 102, 178), // Pink
        new Color(192, 192, 192), // Silver
        new Color(255, 215, 0)    // Gold
    };
    
    /**
     * Unified data injection entry point - fully compliant with user requirements
     * Provides transaction data to the chart generator
     * 
     * @param transactions Transaction data list
     */
    public static void setTransactionData(List<Transaction> transactions) {
        transactionData = new ArrayList<>(transactions);
        processTransactionData();
    }
    
    /**
     * Get the latest data from TransactionDataCenter
     * This method should be called before generating charts to ensure they use the latest data
     */
    public static void refreshDataFromDataCenter() {
        TransactionDataCenter dataCenter = TransactionDataCenter.getInstance();
        if (dataCenter != null) {
            transactionData = new ArrayList<>(dataCenter.getAllTransactions());
            processTransactionData();
        }
    }
    
    /**
     * Old method - maintains backward compatibility with the new unified method name
     * @deprecated Please use the same-named method {@link #setTransactionData(List)}
     */
    @Deprecated
    public static void setData(List<Transaction> transactions) {
        setTransactionData(transactions);
    }
    
    /**
     * Process transaction data, converting Transaction objects to the format required by FinanceData
     */
    private static void processTransactionData() {
        monthDataCache.clear();
        
        if (!transactionData.isEmpty()) {
            monthDataCache = convertTransactionsToMonthData();
        }
    }
    
    /**
     * Create a pie chart showing monthly expense distribution
     * 
     * @param monthData Monthly data
     * @return Pie chart panel
     */
    public static JPanel createMonthlyExpensesPieChart(MonthData monthData) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(500, 300));
        panel.setBorder(BorderFactory.createTitledBorder(monthData.getMonth() + " Expense Distribution"));
        
        // If there is no expense data, display text information
        if (monthData.getTotalExpenses() <= 0) {
            panel.add(new JLabel("No expense data available for this month."), BorderLayout.CENTER);
            return panel;
        }
        
        // Create pie chart dataset - remove generics to adapt to JFreeChart 1.0.19
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        // Add expense data for each category
        for (Map.Entry<String, Double> entry : monthData.getExpensesByCategory().entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        // Create pie chart
        JFreeChart chart = ChartFactory.createPieChart(
            "Monthly Expenses by Category",
            dataset,
            true,  // Show legend
            true,  // Show tooltips
            false  // Don't generate URLs
        );
        
        // Customize pie chart appearance - remove generics to adapt to JFreeChart 1.0.19
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("No data available");
        plot.setCircular(true);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
            "{0}: {1} ({2})", java.text.NumberFormat.getInstance(), 
            java.text.NumberFormat.getPercentInstance()));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        
        // Set pie slice colors
        int colorIndex = 0;
        for (Map.Entry<String, Double> entry : monthData.getExpensesByCategory().entrySet()) {
            plot.setSectionPaint(entry.getKey(), 
                CHART_COLORS[colorIndex % CHART_COLORS.length]);
            colorIndex++;
        }
        
        // Create chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(450, 250));
        
        // Add to main panel
        panel.add(chartPanel, BorderLayout.CENTER);
        
        // Add total expense label
        JLabel totalLabel = new JLabel("Total Expenses: " + String.format("%.2f", monthData.getTotalExpenses()));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(totalLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create a bar chart comparing monthly income and expenses
     * 
     * @param monthsData Monthly data list
     * @return Bar chart panel
     */
    public static JPanel createIncomeExpenseBarChart(List<MonthData> monthsData) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(600, 350));
        panel.setBorder(BorderFactory.createTitledBorder("Monthly Income & Expense Comparison"));
        
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Add income and expense data for each month
        for (MonthData monthData : monthsData) {
            dataset.addValue(monthData.getIncome(), "Income", monthData.getMonth());
            dataset.addValue(monthData.getTotalExpenses(), "Expenses", monthData.getMonth());
            dataset.addValue(monthData.getBalance(), "Balance", monthData.getMonth());
        }
        
        // Create bar chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Income & Expense Comparison",
            "Month",
            "Amount",
            dataset,
            PlotOrientation.VERTICAL,
            true,   // Show legend
            true,   // Show tooltips
            false   // Don't generate URLs
        );
        
        // Customize bar chart appearance
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setOutlinePaint(null);
        
        // Customize X-axis
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.3);
        domainAxis.setLowerMargin(0.05);
        domainAxis.setUpperMargin(0.05);
        
        // Customize Y-axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
        
        // Customize renderer
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(102, 178, 255));  // Income color
        renderer.setSeriesPaint(1, new Color(255, 102, 102));   // Expense color
        renderer.setSeriesPaint(2, new Color(102, 255, 102));  // Balance color
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
        
        // Create chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(550, 300));
        
        // Add to main panel
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create a line chart showing expense trends
     * 
     * @param monthsData Monthly data list
     * @return Line chart panel
     */
    public static JPanel createExpenseTrendLineChart(List<MonthData> monthsData) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(600, 350));
        panel.setBorder(BorderFactory.createTitledBorder("Expense Trend Analysis"));
        
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Collect all categories
        Set<String> allCategories = new HashSet<>();
        for (MonthData monthData : monthsData) {
            allCategories.addAll(monthData.getExpensesByCategory().keySet());
        }
        
        // Add expense data for each month and category
        for (String category : allCategories) {
            for (MonthData monthData : monthsData) {
                double amount = monthData.getExpensesByCategory().getOrDefault(category, 0.0);
                dataset.addValue(amount, category, monthData.getMonth());
            }
        }
        
        // Create line chart
        JFreeChart chart = ChartFactory.createLineChart(
            "Expense Trend by Category",
            "Month",
            "Amount",
            dataset,
            PlotOrientation.VERTICAL,
            true,   // Show legend
            true,   // Show tooltips
            false   // Don't generate URLs
        );
        
        // Customize line chart appearance
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setOutlinePaint(null);
        
        // Customize renderer - adapt to JFreeChart 1.0.19 API
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseShapesVisible(true);  // Replace setDefaultShapesVisible
        renderer.setSeriesStroke(0, new java.awt.BasicStroke(2.0f));  // Replace setDefaultStroke
        
        // Set different colors for each category
        int colorIndex = 0;
        for (String category : allCategories) {
            renderer.setSeriesPaint(colorIndex, 
                CHART_COLORS[colorIndex % CHART_COLORS.length]);
            colorIndex++;
        }
        
        // Create chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(550, 300));
        
        // Add to main panel
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create a stacked bar chart showing quarterly expense category distribution
     * 
     * @param monthsData Monthly data list
     * @return Stacked bar chart panel
     */
    public static JPanel createQuarterlyStackedBarChart(List<MonthData> monthsData) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(600, 350));
        panel.setBorder(BorderFactory.createTitledBorder("Quarterly Expense Distribution"));
        
        // Create dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // Collect all categories
        Set<String> allCategories = new HashSet<>();
        for (MonthData monthData : monthsData) {
            allCategories.addAll(monthData.getExpensesByCategory().keySet());
        }
        
        // Add expense data for each month and category
        for (MonthData monthData : monthsData) {
            for (String category : allCategories) {
                double amount = monthData.getExpensesByCategory().getOrDefault(category, 0.0);
                dataset.addValue(amount, category, monthData.getMonth());
            }
        }
        
        // Create stacked bar chart
        JFreeChart chart = ChartFactory.createStackedBarChart(
            "Expense Distribution by Category",
            "Month",
            "Amount",
            dataset,
            PlotOrientation.VERTICAL,
            true,   // Show legend
            true,   // Show tooltips
            false   // Don't generate URLs
        );
        
        // Customize stacked bar chart appearance
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setOutlinePaint(null);
        
        // Set different colors for each category
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        int colorIndex = 0;
        for (String category : allCategories) {
            renderer.setSeriesPaint(colorIndex, 
                CHART_COLORS[colorIndex % CHART_COLORS.length]);
            colorIndex++;
        }
        
        // Create chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(550, 300));
        
        // Add to main panel
        panel.add(chartPanel, BorderLayout.CENTER);
        
        // Add table panel
        panel.add(createQuarterlyDistributionTable(monthsData), BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Create quarterly expense distribution table
     * 
     * @param monthsData Monthly data list
     * @return Table panel
     */
    private static JPanel createQuarterlyDistributionTable(List<MonthData> monthsData) {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setPreferredSize(new Dimension(580, 150));
        
        // Create table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Category");
        
        // Add month columns
        for (MonthData monthData : monthsData) {
            model.addColumn(monthData.getMonth());
        }
        
        // Get all categories
        Set<String> allCategories = new HashSet<>();
        for (MonthData monthData : monthsData) {
            allCategories.addAll(monthData.getExpensesByCategory().keySet());
        }
        
        // Fill data
        for (String category : allCategories) {
            Object[] rowData = new Object[monthsData.size() + 1];
            rowData[0] = category;
            
            for (int i = 0; i < monthsData.size(); i++) {
                MonthData monthData = monthsData.get(i);
                Double amount = monthData.getExpensesByCategory().getOrDefault(category, 0.0);
                rowData[i + 1] = String.format("%.2f", amount);
            }
            
            model.addRow(rowData);
        }
        
        // Create table
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(580, 120));
        
        // Add to panel
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    /**
     * Create comparison table for expense prediction
     * 
     * @param currentMonth Current month data
     * @param prediction Predicted expenses for next month
     * @return Comparison panel
     */
    public static JPanel createExpensePredictionChart(MonthData currentMonth, Map<String, Double> prediction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(600, 350));
        panel.setBorder(BorderFactory.createTitledBorder("Compare Current Month and Predicted Expenses"));
        
        // Create bar chart dataset
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        
        Map<String, Double> currentExpenses = currentMonth.getExpensesByCategory();
        
        // Merge all categories
        Set<String> allCategories = new HashSet<>();
        allCategories.addAll(currentExpenses.keySet());
        allCategories.addAll(prediction.keySet());
        
        // Add current month and predicted month data
        for (String category : allCategories) {
            double currentAmount = currentExpenses.getOrDefault(category, 0.0);
            double predictedAmount = prediction.getOrDefault(category, 0.0);
            
            barDataset.addValue(currentAmount, "Current Month", category);
            barDataset.addValue(predictedAmount, "Next Month (Predicted)", category);
        }
        
        // Create bar chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Current vs Predicted Expenses",
            "Category",
            "Amount",
            barDataset,
            PlotOrientation.VERTICAL,
            true,   // Show legend
            true,   // Show tooltips
            false   // Don't generate URLs
        );
        
        // Customize bar chart appearance
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));
        plot.setOutlinePaint(null);
        
        // Customize renderer
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(102, 178, 255));  // Current month color
        renderer.setSeriesPaint(1, new Color(255, 102, 102));   // Predicted month color
        
        // Create chart panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(550, 250));
        
        // Create table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        
        // Create table model
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Category");
        model.addColumn("Current Month");
        model.addColumn("Next Month (Predicted)");
        model.addColumn("Difference");
        
        // Fill data
        for (String category : allCategories) {
            double currentAmount = currentExpenses.getOrDefault(category, 0.0);
            double predictedAmount = prediction.getOrDefault(category, 0.0);
            double difference = predictedAmount - currentAmount;
            
            model.addRow(new Object[]{
                category,
                String.format("%.2f", currentAmount),
                String.format("%.2f", predictedAmount),
                String.format("%.2f", difference)
            });
        }
        
        // Create table
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(580, 100));
        
        // Add to table panel
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add to main panel
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(tablePanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Convert transaction data to MonthData objects
     * 
     * @return Map of month string to MonthData object
     */
    private static Map<String, MonthData> convertTransactionsToMonthData() {
        Map<String, List<com.financeapp.model.FinanceData.Expense>> expensesByMonth = new HashMap<>();
        Map<String, Double> incomeByMonth = new HashMap<>();
        Map<String, Double> totalExpensesByMonth = new HashMap<>();
        
        for (Transaction transaction : transactionData) {
            // Get month
            String month = String.format("%02d", transaction.getDateTime().getMonthValue());
            
            if (transaction.getCategory().getType() == CategoryType.EXPENSE) {
                // Expense
                if (!expensesByMonth.containsKey(month)) {
                    expensesByMonth.put(month, new ArrayList<>());
                    totalExpensesByMonth.put(month, 0.0);
                }
                
                // Convert Transaction to Expense
                expensesByMonth.get(month).add(new com.financeapp.model.FinanceData.Expense(
                    transaction.getCategory().getName(),
                    transaction.getAmount().doubleValue()
                ));
                
                // Update total expenses
                totalExpensesByMonth.put(month, 
                    totalExpensesByMonth.get(month) + transaction.getAmount().doubleValue());
            } else {
                // Income
                if (!incomeByMonth.containsKey(month)) {
                    incomeByMonth.put(month, 0.0);
                }
                
                // Update total income
                incomeByMonth.put(month, 
                    incomeByMonth.getOrDefault(month, 0.0) + transaction.getAmount().doubleValue());
            }
        }
        
        // Create MonthData objects
        Map<String, MonthData> result = new HashMap<>();
        
        for (String month : expensesByMonth.keySet()) {
            double income = incomeByMonth.getOrDefault(month, 0.0);
            List<com.financeapp.model.FinanceData.Expense> expenses = expensesByMonth.get(month);
            double totalExpenses = totalExpensesByMonth.get(month);
            double balance = income - totalExpenses;
            
            result.put(month, new MonthData(month, income, expenses, totalExpenses, balance));
        }
        
        // Ensure each month with income has data
        for (String month : incomeByMonth.keySet()) {
            if (!result.containsKey(month)) {
                double income = incomeByMonth.get(month);
                result.put(month, new MonthData(month, income, new ArrayList<>(), 0.0, income));
            }
        }
        
        return result;
    }
    
    /**
     * Get MonthData for a specific month
     * 
     * @param month Month string (e.g., "01" for January)
     * @return MonthData for the specified month or null if no data available
     */
    public static MonthData getMonthData(String month) {
        if (monthDataCache.isEmpty()) {
            processTransactionData();
        }
        return monthDataCache.get(month);
    }
    
    /**
     * Get MonthData for multiple months
     * 
     * @param months List of month strings
     * @return List of MonthData objects
     */
    public static List<MonthData> getMonthsData(List<String> months) {
        if (monthDataCache.isEmpty()) {
            processTransactionData();
        }
        
        List<MonthData> result = new ArrayList<>();
        for (String month : months) {
            MonthData data = monthDataCache.get(month);
            if (data != null) {
                result.add(data);
            }
        }
        return result;
    }
    
    /**
     * Get all available months from transaction data
     * 
     * @return List of available month strings
     */
    public static List<String> getAvailableMonths() {
        if (monthDataCache.isEmpty()) {
            processTransactionData();
        }
        
        List<String> months = new ArrayList<>(monthDataCache.keySet());
        Collections.sort(months);
        return months;
    }
    
    /**
     * Create simple bar chart showing expense trends
     * Alternative implementation preserving original functionality
     * 
     * @param monthsData Monthly data list
     * @return Bar chart panel
     */
    public static JPanel createExpenseTrendChart(List<MonthData> monthsData) {
        return createExpenseTrendLineChart(monthsData);
    }
    
    /**
     * Create quarterly distribution chart
     * Alternative implementation preserving original functionality
     * 
     * @param monthsData Monthly data list
     * @return Quarterly distribution chart panel
     */
    public static JPanel createQuarterlyDistributionChart(List<MonthData> monthsData) {
        return createQuarterlyStackedBarChart(monthsData);
    }
    
    // Simple bar chart implementation, preserve original inner class
    private static class SimpleBarChart extends JPanel {
        private List<MonthData> data;
        private static final int PADDING = 30;
        private static final int BAR_WIDTH = 60;
        private static final int BAR_SPACING = 40;
        
        public SimpleBarChart(List<MonthData> data) {
            this.data = data;
            setPreferredSize(new Dimension(600, 350));
            setBorder(BorderFactory.createTitledBorder("Finance Tendency Analysis"));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Calculate max value for scaling
            double maxValue = 0;
            for (MonthData monthData : data) {
                maxValue = Math.max(maxValue, Math.max(monthData.getIncome(), monthData.getTotalExpenses()));
            }
            
            // Draw coordinate system
            g2d.setColor(Color.BLACK);
            g2d.drawLine(PADDING, height - PADDING, width - PADDING, height - PADDING); // X-axis
            g2d.drawLine(PADDING, PADDING, PADDING, height - PADDING); // Y-axis
            
            // Draw scale on Y-axis
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= 5; i++) {
                int y = height - PADDING - (i * (height - 2 * PADDING) / 5);
                g2d.drawLine(PADDING - 5, y, PADDING, y);
                g2d.drawString(String.format("%.0f", i * maxValue / 5), 5, y + 5);
            }
            
            // Draw bars
            int x = PADDING + BAR_SPACING;
            for (MonthData monthData : data) {
                // Draw month label
                g2d.drawString(monthData.getMonth(), x + BAR_WIDTH / 6, height - PADDING + 15);
                
                // Draw income bar
                double incomeHeight = (height - 2 * PADDING) * (monthData.getIncome() / maxValue);
                g2d.setColor(new Color(0, 150, 0)); // Green for income
                g2d.fillRect(x, height - PADDING - (int)incomeHeight, BAR_WIDTH / 2, (int)incomeHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, height - PADDING - (int)incomeHeight, BAR_WIDTH / 2, (int)incomeHeight);
                
                // Draw expense bar
                double expenseHeight = (height - 2 * PADDING) * (monthData.getTotalExpenses() / maxValue);
                g2d.setColor(new Color(200, 0, 0)); // Red for expense
                g2d.fillRect(x + BAR_WIDTH / 2, height - PADDING - (int)expenseHeight, BAR_WIDTH / 2, (int)expenseHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x + BAR_WIDTH / 2, height - PADDING - (int)expenseHeight, BAR_WIDTH / 2, (int)expenseHeight);
                
                x += BAR_WIDTH + BAR_SPACING;
            }
            
            // Draw legend
            g2d.setColor(new Color(0, 150, 0));
            g2d.fillRect(width - 100, PADDING, 15, 15);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(width - 100, PADDING, 15, 15);
            g2d.drawString("Income", width - 80, PADDING + 12);
            
            g2d.setColor(new Color(200, 0, 0));
            g2d.fillRect(width - 100, PADDING + 20, 15, 15);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(width - 100, PADDING + 20, 15, 15);
            g2d.drawString("Expense", width - 80, PADDING + 32);
        }
    }
    
    /**
     * Predict expenses for the next month based on the current month
     * 
     * @param currentMonth Current month
     * @return Predicted expense mapping
     */
    public static Map<String, Double> predictNextMonthExpenses(String currentMonth) {
        MonthData currentData = getMonthData(currentMonth);
        if (currentData == null) {
            return new HashMap<>();
        }
        
        Map<String, Double> prediction = new HashMap<>();
        for (Map.Entry<String, Double> entry : currentData.getExpensesByCategory().entrySet()) {
            // Simple prediction: use current month data plus 5% growth
            prediction.put(entry.getKey(), entry.getValue() * 1.05);
        }
        
        return prediction;
    }
} 