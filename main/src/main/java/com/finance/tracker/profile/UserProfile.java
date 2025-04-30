/*
 * Package: com.finance.tracker.profile
 * Purpose: User profiling and preference tracking
 */

package com.finance.tracker.profile;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Manages user profile and spending preferences
 */
public class UserProfile {
    private static final String PROFILE_FILE = "data/user_profile.json";
    
    private String userId;
    private String name;
    private Map<String, Double> categoryPreferences = new HashMap<>(); // Category -> Preference score (0.0-1.0)
    private Map<String, BigDecimal> categoryBudgets = new HashMap<>(); // Category -> Budget amount
    private SpendingHistory spendingHistory;
    
    // Singleton pattern
    private static UserProfile instance;
    
    public static UserProfile getInstance() {
        if (instance == null) {
            instance = new UserProfile();
        }
        return instance;
    }
    
    private UserProfile() {
        spendingHistory = new SpendingHistory();
        loadProfile();
    }
    
    /**
     * Loads user profile from JSON file
     */
    private void loadProfile() {
        JSONParser parser = new JSONParser();
        
        try (FileReader reader = new FileReader(PROFILE_FILE)) {
            JSONObject profile = (JSONObject) parser.parse(reader);
            
            userId = (String) profile.get("userId");
            name = (String) profile.get("name");
            
            // Load category preferences
            JSONObject prefsJson = (JSONObject) profile.get("categoryPreferences");
            for (Object key : prefsJson.keySet()) {
                String category = (String) key;
                double score = ((Number) prefsJson.get(category)).doubleValue();
                categoryPreferences.put(category, score);
            }
            
            // Load category budgets
            JSONObject budgetsJson = (JSONObject) profile.get("categoryBudgets");
            for (Object key : budgetsJson.keySet()) {
                String category = (String) key;
                double amount = ((Number) budgetsJson.get(category)).doubleValue();
                categoryBudgets.put(category, BigDecimal.valueOf(amount));
            }
            
            // Load spending history
            spendingHistory.loadFromJson((JSONObject) profile.get("spendingHistory"));
            
        } catch (IOException | ParseException e) {
            System.err.println("Error loading user profile: " + e.getMessage());
            // Initialize with default values
            initializeDefaultProfile();
        }
    }
    
    /**
     * Initialize with default values if profile file is not found
     */
    private void initializeDefaultProfile() {
        userId = "user1";
        name = "Default User";
        
        // Default category preferences
        categoryPreferences.put("food", 0.8);
        categoryPreferences.put("transportation", 0.6);
        categoryPreferences.put("entertainment", 0.7);
        categoryPreferences.put("shopping", 0.5);
        categoryPreferences.put("housing", 0.4);
        categoryPreferences.put("healthcare", 0.3);
        categoryPreferences.put("education", 0.6);
        
        // Default category budgets
        categoryBudgets.put("food", BigDecimal.valueOf(2000));
        categoryBudgets.put("transportation", BigDecimal.valueOf(500));
        categoryBudgets.put("entertainment", BigDecimal.valueOf(800));
        categoryBudgets.put("shopping", BigDecimal.valueOf(1000));
        categoryBudgets.put("housing", BigDecimal.valueOf(3000));
        categoryBudgets.put("healthcare", BigDecimal.valueOf(500));
        categoryBudgets.put("education", BigDecimal.valueOf(1000));
    }
    
    /**
     * Saves user profile to JSON file
     */
    public void saveProfile() {
        JSONObject profile = new JSONObject();
        profile.put("userId", userId);
        profile.put("name", name);
        
        // Save category preferences
        JSONObject prefsJson = new JSONObject();
        for (Map.Entry<String, Double> entry : categoryPreferences.entrySet()) {
            prefsJson.put(entry.getKey(), entry.getValue());
        }
        profile.put("categoryPreferences", prefsJson);
        
        // Save category budgets
        JSONObject budgetsJson = new JSONObject();
        for (Map.Entry<String, BigDecimal> entry : categoryBudgets.entrySet()) {
            budgetsJson.put(entry.getKey(), entry.getValue().doubleValue());
        }
        profile.put("categoryBudgets", budgetsJson);
        
        // Save spending history
        profile.put("spendingHistory", spendingHistory.toJson());
        
        try (FileWriter writer = new FileWriter(PROFILE_FILE)) {
            writer.write(profile.toJSONString());
        } catch (IOException e) {
            System.err.println("Error saving user profile: " + e.getMessage());
        }
    }
    
    /**
     * Updates user preference for a category based on spending
     */
    public void updateCategoryPreference(String category, BigDecimal amount) {
        double currentScore = categoryPreferences.getOrDefault(category, 0.5);
        
        // Simple adaptive algorithm: increase preference if spending in this category
        double newScore = currentScore + 0.01; // Small increment
        newScore = Math.min(1.0, newScore); // Cap at 1.0
        
        categoryPreferences.put(category, newScore);
    }
    
    /**
     * Set or update budget for a category
     */
    public void setBudget(String category, BigDecimal amount) {
        categoryBudgets.put(category, amount);
        saveProfile();
    }
    
    /**
     * Get budget for a category
     */
    public BigDecimal getBudget(String category) {
        return categoryBudgets.getOrDefault(category, BigDecimal.ZERO);
    }
    
    /**
     * Record a new transaction to update user profile
     */
    public void recordTransaction(String category, BigDecimal amount, LocalDate date) {
        // Update preference
        updateCategoryPreference(category, amount);
        
        // Record in spending history
        spendingHistory.addTransaction(category, amount, date);
        
        // Save changes
        saveProfile();
    }
    
    /**
     * 清空所有交易记录历史（重置状态）
     * 注意：这个方法将删除所有交易记录，但保留用户的偏好设置和预算
     * 
     * @return 是否成功清空
     */
    public boolean clearTransactionHistory() {
        try {
            // 创建一个新的空白交易历史
            this.spendingHistory = new SpendingHistory();
            
            // 保存更改
            saveProfile();
            
            System.out.println("已清空所有交易记录历史");
            return true;
        } catch (Exception e) {
            System.err.println("清空交易记录失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get user's top spending categories
     */
    public List<CategorySpending> getTopSpendingCategories(int limit) {
        YearMonth currentMonth = YearMonth.now();
        Map<String, BigDecimal> monthlySpending = spendingHistory.getMonthlyCategorySpending(currentMonth);
        
        List<CategorySpending> categories = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : monthlySpending.entrySet()) {
            categories.add(new CategorySpending(entry.getKey(), entry.getValue()));
        }
        
        // Sort by amount descending
        categories.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));
        
        // Return top N categories
        return categories.subList(0, Math.min(limit, categories.size()));
    }
    
    /**
     * Check if user has overspent in any category
     */
    public Map<String, BigDecimal> getOverspentCategories() {
        YearMonth currentMonth = YearMonth.now();
        Map<String, BigDecimal> monthlySpending = spendingHistory.getMonthlyCategorySpending(currentMonth);
        Map<String, BigDecimal> overSpent = new HashMap<>();
        
        for (Map.Entry<String, BigDecimal> entry : monthlySpending.entrySet()) {
            String category = entry.getKey();
            BigDecimal spent = entry.getValue();
            BigDecimal budget = categoryBudgets.getOrDefault(category, BigDecimal.valueOf(0));
            
            if (spent.compareTo(budget) > 0) {
                overSpent.put(category, spent.subtract(budget));
            }
        }
        
        return overSpent;
    }
    
    // Getters and setters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name; 
        saveProfile();
    }
    
    public Map<String, Double> getCategoryPreferences() { 
        return new HashMap<>(categoryPreferences); 
    }
    
    public Map<String, BigDecimal> getCategoryBudgets() { 
        return new HashMap<>(categoryBudgets); 
    }
    
    public SpendingHistory getSpendingHistory() { 
        return spendingHistory; 
    }
    
    /**
     * Get user's region
     */
    public String getRegion() {
        return "CN"; // Default to China
    }
    
    /**
     * Get user's monthly income
     */
    public BigDecimal getMonthlyIncome() {
        return BigDecimal.valueOf(10000); // Default value
    }
    
    /**
     * Get all budget categories
     */
    public Map<String, BigDecimal> getBudgets() {
        return new HashMap<>(categoryBudgets);
    }

    public void setPreference(String key, String value) {
        // Implementation needed
    }
}
