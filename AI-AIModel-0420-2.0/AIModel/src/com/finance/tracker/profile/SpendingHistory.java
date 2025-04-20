package com.finance.tracker.profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 * Tracks spending history
 */
public class SpendingHistory {
    private Map<String, List<Transaction>> transactions; // Year-Month -> List of transactions
    
    public SpendingHistory() {
        transactions = new TreeMap<>();
    }
    
    /**
     * Add a transaction to history
     */
    public void addTransaction(String category, BigDecimal amount, LocalDate date) {
        String yearMonth = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
        
        if (!transactions.containsKey(yearMonth)) {
            transactions.put(yearMonth, new ArrayList<>());
        }
        
        transactions.get(yearMonth).add(new Transaction(category, amount, date));
    }
    
    /**
     * Get transactions for a specific month
     */
    public List<Transaction> getMonthlyTransactions(YearMonth yearMonth) {
        String key = yearMonth.getYear() + "-" + String.format("%02d", yearMonth.getMonthValue());
        return transactions.getOrDefault(key, new ArrayList<>());
    }
    
    /**
     * Get total spending for a specific month
     */
    public BigDecimal getMonthlySpending(YearMonth yearMonth) {
        List<Transaction> monthTransactions = getMonthlyTransactions(yearMonth);
        BigDecimal total = BigDecimal.ZERO;
        
        for (Transaction transaction : monthTransactions) {
            total = total.add(transaction.getAmount());
        }
        
        return total;
    }
    
    /**
     * Get spending by category for a specific month
     */
    public Map<String, BigDecimal> getMonthlyCategorySpending(YearMonth yearMonth) {
        List<Transaction> monthTransactions = getMonthlyTransactions(yearMonth);
        Map<String, BigDecimal> categorySpending = new HashMap<>();
        
        for (Transaction transaction : monthTransactions) {
            String category = transaction.getCategory();
            BigDecimal currentAmount = categorySpending.getOrDefault(category, BigDecimal.ZERO);
            categorySpending.put(category, currentAmount.add(transaction.getAmount()));
        }
        
        return categorySpending;
    }
    
    /**
     * Get spending trend over last few months
     */
    public Map<YearMonth, BigDecimal> getSpendingTrend(int months) {
        Map<YearMonth, BigDecimal> trend = new TreeMap<>();
        YearMonth current = YearMonth.now();
        
        for (int i = 0; i < months; i++) {
            YearMonth month = current.minusMonths(i);
            trend.put(month, getMonthlySpending(month));
        }
        
        return trend;
    }
    
    /**
     * Loads spending history from JSON
     */
    public void loadFromJson(JSONObject historyJson) {
        if (historyJson == null) return;
        
        transactions.clear();
        
        for (Object keyObj : historyJson.keySet()) {
            String yearMonth = (String) keyObj;
            JSONArray transactionsArray = (JSONArray) historyJson.get(yearMonth);
            
            List<Transaction> monthTransactions = new ArrayList<>();
            for (Object transObj : transactionsArray) {
                JSONObject transJson = (JSONObject) transObj;
                
                String category = (String) transJson.get("category");
                double amount = ((Number) transJson.get("amount")).doubleValue();
                String dateStr = (String) transJson.get("date");
                LocalDate date = LocalDate.parse(dateStr);
                
                monthTransactions.add(new Transaction(category, BigDecimal.valueOf(amount), date));
            }
            
            transactions.put(yearMonth, monthTransactions);
        }
    }
    
    /**
     * Converts spending history to JSON
     */
    public JSONObject toJson() {
        JSONObject historyJson = new JSONObject();
        
        for (Map.Entry<String, List<Transaction>> entry : transactions.entrySet()) {
            String yearMonth = entry.getKey();
            List<Transaction> monthTransactions = entry.getValue();
            
            JSONArray transactionsArray = new JSONArray();
            for (Transaction transaction : monthTransactions) {
                JSONObject transJson = new JSONObject();
                transJson.put("category", transaction.getCategory());
                transJson.put("amount", transaction.getAmount().doubleValue());
                transJson.put("date", transaction.getDate().toString());
                
                transactionsArray.add(transJson);
            }
            
            historyJson.put(yearMonth, transactionsArray);
        }
        
        return historyJson;
    }
    
    /**
     * Returns a map of all spending history by year-month
     * @return Map with YearMonth as key and Map of category to amount as value
     */
    public Map<YearMonth, Map<String, BigDecimal>> getAllSpendingHistory() {
        Map<YearMonth, Map<String, BigDecimal>> allHistory = new TreeMap<>();
        
        for (String yearMonthStr : transactions.keySet()) {
            String[] parts = yearMonthStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            YearMonth yearMonth = YearMonth.of(year, month);
            
            Map<String, BigDecimal> categorySpending = getMonthlyCategorySpending(yearMonth);
            allHistory.put(yearMonth, categorySpending);
        }
        
        return allHistory;
    }
}
