package com.financeapp.util;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.financeapp.model.FinanceData.MonthData;

public class ChartGenerator {
    
    // Generate pie chart for single month expenses
    public static JPanel createMonthlyExpensesPieChart(MonthData monthData) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        for (Map.Entry<String, Double> entry : monthData.getExpensesByCategory().entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        JFreeChart chart = ChartFactory.createPieChart(
            monthData.getMonth() + " 支出分布",
            dataset,
            true,
            true,
            false
        );
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        
        return chartPanel;
    }
    
    // Generate bar chart for expense prediction
    public static JPanel createExpensePredictionChart(MonthData currentMonth, Map<String, Double> prediction) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        Map<String, Double> currentExpenses = currentMonth.getExpensesByCategory();
        
        for (Map.Entry<String, Double> entry : prediction.entrySet()) {
            String category = entry.getKey();
            double predictedAmount = entry.getValue();
            double currentAmount = currentExpenses.get(category);
            
            dataset.addValue(currentAmount, "当前月", category);
            dataset.addValue(predictedAmount, "预测下月", category);
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "本月与下月预测支出对比",
            "类别",
            "金额",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 350));
        
        return chartPanel;
    }
    
    // Generate line chart for expense trend over multiple months
    public static JPanel createExpenseTrendChart(List<MonthData> monthsData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (MonthData monthData : monthsData) {
            dataset.addValue(monthData.getTotalExpenses(), "总支出", monthData.getMonth());
            dataset.addValue(monthData.getIncome(), "收入", monthData.getMonth());
            dataset.addValue(monthData.getBalance(), "结余", monthData.getMonth());
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            "财务趋势分析",
            "月份",
            "金额",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 350));
        
        return chartPanel;
    }
    
    // Generate stacked bar chart for quarterly category distribution
    public static JPanel createQuarterlyDistributionChart(List<MonthData> monthsData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        for (MonthData monthData : monthsData) {
            for (Map.Entry<String, Double> entry : monthData.getExpensesByCategory().entrySet()) {
                dataset.addValue(entry.getValue(), entry.getKey(), monthData.getMonth());
            }
        }
        
        JFreeChart chart = ChartFactory.createStackedBarChart(
            "季度支出类别分布",
            "月份",
            "金额",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 350));
        
        return chartPanel;
    }
} 