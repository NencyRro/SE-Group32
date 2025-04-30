/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finance.tracker.ai;

import com.finance.tracker.calendar.SeasonalityManager;
import com.finance.tracker.profile.UserProfile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

/**
 * Budget optimizer using AI techniques
 */
public class BudgetOptimizer {
    private UserProfile userProfile;
    
    // Singleton pattern
    private static BudgetOptimizer instance;
    
    public static BudgetOptimizer getInstance() {
        if (instance == null) {
            instance = new BudgetOptimizer();
        }
        return instance;
    }
    
    private BudgetOptimizer() {
        userProfile = UserProfile.getInstance();
    }
    
    /**
     * Generate optimized budget suggestions
     */
    public Map<String, BigDecimal> generateOptimizedBudget() {
        Map<String, BigDecimal> optimizedBudget = new HashMap<>();
        YearMonth currentMonth = YearMonth.now();
        
        // Get historical spending by category
        Map<String, BigDecimal> currentSpending = userProfile.getSpendingHistory().getMonthlyCategorySpending(currentMonth);
        Map<String, BigDecimal> currentBudgets = userProfile.getCategoryBudgets();
        
        // Get seasonality factor
        double seasonalFactor = SeasonalityManager.getInstance().getCurrentSeasonalityFactor();
        
        // For each category
        for (Map.Entry<String, BigDecimal> entry : currentBudgets.entrySet()) {
            String category = entry.getKey();
            BigDecimal currentBudget = entry.getValue();
            BigDecimal actualSpending = currentSpending.getOrDefault(category, BigDecimal.ZERO);
            
            // Apply simple optimization algorithm
            BigDecimal newBudget;
            
            if (actualSpending.compareTo(currentBudget) > 0) {
                // If overspent, suggest slightly higher budget
                newBudget = actualSpending.multiply(BigDecimal.valueOf(1.05));
            } else if (actualSpending.multiply(BigDecimal.valueOf(1.2)).compareTo(currentBudget) < 0) {
                // If significantly underspent, suggest lower budget
                newBudget = actualSpending.multiply(BigDecimal.valueOf(1.15));
            } else {
                // Otherwise keep same budget
                newBudget = currentBudget;
            }
            
            // Apply seasonality factor
            newBudget = newBudget.multiply(BigDecimal.valueOf(seasonalFactor));
            
            // Round to nearest 10
            newBudget = newBudget.divide(BigDecimal.TEN, 0, RoundingMode.HALF_UP).multiply(BigDecimal.TEN);
            
            optimizedBudget.put(category, newBudget);
        }
        
        return optimizedBudget;
    }
    
    /**
     * Generate spending predictions for next month
     */
    public Map<String, BigDecimal> predictNextMonthSpending() {
        Map<String, BigDecimal> predictions = new HashMap<>();
        YearMonth currentMonth = YearMonth.now();
        
        // Get historical spending
        Map<YearMonth, BigDecimal> trend = userProfile.getSpendingHistory().getSpendingTrend(3);
        Map<String, BigDecimal> currentCategorySpending = userProfile.getSpendingHistory().getMonthlyCategorySpending(currentMonth);
        
        // Get seasonality factor for next month
        YearMonth nextMonth = currentMonth.plusMonths(1);
        double seasonalFactor = SeasonalityManager.getInstance().getCurrentSeasonalityFactor();
        
        // For each category
        for (Map.Entry<String, BigDecimal> entry : currentCategorySpending.entrySet()) {
            String category = entry.getKey();
            BigDecimal currentSpending = entry.getValue();
            
            // Apply simple prediction model (current spending * seasonal factor)
            BigDecimal prediction = currentSpending.multiply(BigDecimal.valueOf(seasonalFactor));
            
            // Round to nearest 10
            prediction = prediction.divide(BigDecimal.TEN, 0, RoundingMode.HALF_UP).multiply(BigDecimal.TEN);
            
            predictions.put(category, prediction);
        }
        
        return predictions;
    }
}
