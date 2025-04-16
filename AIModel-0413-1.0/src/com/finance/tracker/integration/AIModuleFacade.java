/*
 * Package: com.finance.tracker.integration
 * Purpose: API to connect with main application
 */

package com.finance.tracker.integration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.finance.tracker.ai.BudgetOptimizer;
import com.finance.tracker.ai.Recommendation;
import com.finance.tracker.ai.RecommendationEngine;
import com.finance.tracker.calendar.ChineseHolidayCalendar;
import com.finance.tracker.calendar.HolidayEvent;
import com.finance.tracker.calendar.SeasonalityManager;
import com.finance.tracker.feedback.FeedbackManager;
import com.finance.tracker.localization.CurrencyManager;
import com.finance.tracker.localization.RegionalSettings;
import com.finance.tracker.profile.UserProfile;

/**
 * Main integration facade for the AI module
 */
public class AIModuleFacade {
    private UserProfile userProfile;
    private RecommendationEngine recommendationEngine;
    private CurrencyManager currencyManager;
    private RegionalSettings regionalSettings;
    private ChineseHolidayCalendar holidayCalendar;
    private SeasonalityManager seasonalityManager;
    private BudgetOptimizer budgetOptimizer;
    private FeedbackManager feedbackManager;
    
    // Singleton pattern
    private static AIModuleFacade instance;
    
    public static AIModuleFacade getInstance() {
        if (instance == null) {
            instance = new AIModuleFacade();
        }
        return instance;
    }
    
    private AIModuleFacade() {
        userProfile = UserProfile.getInstance();
        recommendationEngine = RecommendationEngine.getInstance();
        currencyManager = CurrencyManager.getInstance();
        regionalSettings = RegionalSettings.getInstance();
        holidayCalendar = ChineseHolidayCalendar.getInstance();
        seasonalityManager = SeasonalityManager.getInstance();
        budgetOptimizer = BudgetOptimizer.getInstance();
        feedbackManager = FeedbackManager.getInstance();
    }
    
    /**
     * Import transaction data from main application
     */
    public void importTransaction(String category, BigDecimal amount, LocalDate date) {
        userProfile.recordTransaction(category, amount, date);
        // Generate new recommendations when transactions change
        recommendationEngine.generateAllRecommendations();
    }
    
    /**
     * Import multiple transactions at once
     */
    public void importTransactions(List<TransactionDTO> transactions) {
        for (TransactionDTO transaction : transactions) {
            userProfile.recordTransaction(
                transaction.getCategory(),
                transaction.getAmount(),
                transaction.getDate()
            );
        }
        // Generate new recommendations after batch import
        recommendationEngine.generateAllRecommendations();
    }
    
    /**
     * Get active recommendations
     */
    public List<Recommendation> getActiveRecommendations() {
        return recommendationEngine.getActiveRecommendations();
    }
    
    /**
     * Dismiss a recommendation
     */
    public void dismissRecommendation(String id) {
        recommendationEngine.dismissRecommendation(id);
    }
    
    /**
     * Record feedback for a recommendation
     */
    public void recordRecommendationFeedback(String id, boolean helpful, String comment) {
        recommendationEngine.recordRecommendationFeedback(id, helpful, comment);
        feedbackManager.recordFeedback("RECOMMENDATION", id, helpful, comment);
    }
    
    /**
     * Get optimized budget suggestions
     */
    public Map<String, BigDecimal> getOptimizedBudget() {
        return budgetOptimizer.generateOptimizedBudget();
    }
    
    /**
     * Get spending predictions for next month
     */
    public Map<String, BigDecimal> getPredictedSpending() {
        return budgetOptimizer.predictNextMonthSpending();
    }
    
    /**
     * Get upcoming holidays
     */
    public List<HolidayEvent> getUpcomingHolidays(int days) {
        return holidayCalendar.getUpcomingHolidays(days);
    }
    
    /**
     * Get seasonal budget recommendation
     */
    public String getSeasonalBudgetRecommendation() {
        return seasonalityManager.getSeasonalBudgetRecommendation();
    }
    
    /**
     * Set default currency
     */
    public void setDefaultCurrency(String currencyCode) {
        currencyManager.setDefaultCurrency(currencyCode);
    }
    
    /**
     * Convert amount between currencies
     */
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        return currencyManager.convert(amount, fromCurrency, toCurrency);
    }
    
    /**
     * Format amount with currency symbol
     */
    public String formatCurrency(BigDecimal amount, String currencyCode) {
        return currencyManager.format(amount, currencyCode);
    }
    
    /**
     * Set region
     */
    public void setRegion(String region) {
        regionalSettings.setRegion(region);
    }
    
    /**
     * Generate all recommendations
     */
    public void generateRecommendations() {
        recommendationEngine.generateAllRecommendations();
    }
}
