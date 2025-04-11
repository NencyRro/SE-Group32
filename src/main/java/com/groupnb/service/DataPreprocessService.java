package com.groupnb.service;

import com.groupnb.model.Transaction;
import com.groupnb.util.DateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for preprocessing transaction data before storing it.
 * Handles data cleaning, normalization, and deduplication.
 */
public class DataPreprocessService {
    
    /**
     * Cleans and normalizes a list of transactions.
     * 
     * @param transactions The list of transactions to clean
     * @return A new list of cleaned transactions
     */
    public List<Transaction> cleanTransactions(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Transaction> cleanedTransactions = new ArrayList<>();
        
        for (Transaction transaction : transactions) {
            // Skip invalid transactions
            if (transaction == null || transaction.getDescription() == null || 
                transaction.getAmount() == 0) {
                continue;
            }
            
            // Create a cleaned copy of the transaction
            Transaction cleanedTransaction = new Transaction(
                transaction.getDate(),
                transaction.getAmount(),
                normalizeCategory(transaction.getCategory()),
                normalizeDescription(transaction.getDescription()),
                transaction.getType()
            );
            
            cleanedTransactions.add(cleanedTransaction);
        }
        
        return cleanedTransactions;
    }
    
    /**
     * Deduplicates a list of transactions based on date, description, and amount.
     * 
     * @param transactions The list of transactions to deduplicate
     * @return A new list with duplicates removed
     */
    public List<Transaction> removeDuplicates(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Use a Map to identify unique transactions
        Map<String, Transaction> uniqueTransactions = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            // Create a unique key based on date, description and amount
            String key = createUniqueKey(transaction);
            
            // If we already have this transaction, only keep the one with more complete information
            if (uniqueTransactions.containsKey(key)) {
                Transaction existing = uniqueTransactions.get(key);
                
                // If the new transaction has more information, replace the existing one
                if (hasMoreInformation(transaction, existing)) {
                    uniqueTransactions.put(key, transaction);
                }
            } else {
                uniqueTransactions.put(key, transaction);
            }
        }
        
        // Return the values as a list
        return new ArrayList<>(uniqueTransactions.values());
    }
    
    /**
     * Creates a unique key for a transaction based on its date, description, and amount.
     * 
     * @param transaction The transaction
     * @return A unique key string
     */
    private String createUniqueKey(Transaction transaction) {
        String dateStr = transaction.getDate() != null ? 
                DateUtil.formatDate(transaction.getDate()) : "";
        
        String description = transaction.getDescription() != null ? 
                transaction.getDescription().trim().toLowerCase() : "";
        
        // Remove common variations like spaces, punctuation from description when forming the key
        description = description.replaceAll("[^a-zA-Z0-9]", "");
        
        return dateStr + "_" + description + "_" + transaction.getAmount();
    }
    
    /**
     * Determines if transaction A has more information than transaction B.
     * 
     * @param a First transaction
     * @param b Second transaction
     * @return true if A has more information, false otherwise
     */
    private boolean hasMoreInformation(Transaction a, Transaction b) {
        int scoreA = informationScore(a);
        int scoreB = informationScore(b);
        return scoreA > scoreB;
    }
    
    /**
     * Calculates an information score for a transaction based on filled fields.
     * 
     * @param transaction The transaction
     * @return A score representing how much information the transaction contains
     */
    private int informationScore(Transaction transaction) {
        int score = 0;
        
        if (transaction.getDate() != null) score++;
        if (transaction.getDescription() != null && !transaction.getDescription().trim().isEmpty()) score++;
        if (transaction.getCategory() != null && !transaction.getCategory().trim().isEmpty()) score++;
        if (transaction.getAmount() != 0) score++;
        if (transaction.getType() != null && !transaction.getType().trim().isEmpty()) score++;
        
        return score;
    }
    
    /**
     * Normalizes a category string by trimming, converting to title case, and ensuring consistency.
     * 
     * @param category The category string to normalize
     * @return The normalized category
     */
    public String normalizeCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return "Uncategorized";
        }
        
        // Trim whitespace and convert to title case
        String normalized = toTitleCase(category.trim());
        
        // Map common variations to standard categories
        Map<String, String> categoryMap = new HashMap<>();
        categoryMap.put("Food", "Food & Drink");
        categoryMap.put("Grocery", "Food & Drink");
        categoryMap.put("Groceries", "Food & Drink");
        categoryMap.put("Dining", "Food & Drink");
        categoryMap.put("Restaurant", "Food & Drink");
        categoryMap.put("Transport", "Transportation");
        categoryMap.put("Travel", "Transportation");
        categoryMap.put("Bills", "Utilities");
        categoryMap.put("Utility", "Utilities");
        categoryMap.put("Shopping", "Retail");
        categoryMap.put("Clothes", "Retail");
        categoryMap.put("Income", "Income");
        categoryMap.put("Salary", "Income");
        categoryMap.put("Wages", "Income");
        
        // Check if our category is in the map
        for (Map.Entry<String, String> entry : categoryMap.entrySet()) {
            if (normalized.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return normalized;
    }
    
    /**
     * Normalizes a description string by trimming and cleaning.
     * 
     * @param description The description to normalize
     * @return The normalized description
     */
    public String normalizeDescription(String description) {
        if (description == null) {
            return "";
        }
        
        // Trim whitespace and limit length
        String normalized = description.trim();
        if (normalized.length() > 100) {
            normalized = normalized.substring(0, 100);
        }
        
        return normalized;
    }
    
    /**
     * Converts a string to title case (first letter of each word capitalized).
     * 
     * @param input The input string
     * @return The title-cased string
     */
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;
        
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            } else {
                c = Character.toLowerCase(c);
            }
            titleCase.append(c);
        }
        
        return titleCase.toString();
    }
    
    /**
     * Categorizes a transaction based on its description using simple keyword matching.
     * This could be enhanced with more sophisticated AI techniques.
     * 
     * @param transaction The transaction to categorize
     * @return The categorized transaction
     */
    public Transaction categorizeTransaction(Transaction transaction) {
        if (transaction == null || transaction.getDescription() == null || 
                !transaction.getCategory().equals("Uncategorized")) {
            return transaction;
        }
        
        String description = transaction.getDescription().toLowerCase();
        String category = "Uncategorized";
        
        // Simple keyword matching
        if (description.contains("grocery") || description.contains("supermarket") || 
            description.contains("food") || description.contains("restaurant") ||
            description.contains("cafe") || description.contains("dining")) {
            category = "Food & Drink";
        } else if (description.contains("uber") || description.contains("lyft") || 
                  description.contains("taxi") || description.contains("train") ||
                  description.contains("bus") || description.contains("subway") ||
                  description.contains("metro") || description.contains("transport")) {
            category = "Transportation";
        } else if (description.contains("rent") || description.contains("mortgage")) {
            category = "Housing";
        } else if (description.contains("electric") || description.contains("water") || 
                  description.contains("gas") || description.contains("internet") ||
                  description.contains("phone") || description.contains("utility") ||
                  description.contains("bill")) {
            category = "Utilities";
        } else if (description.contains("movie") || description.contains("concert") || 
                  description.contains("theatre") || description.contains("theater") ||
                  description.contains("entertainment")) {
            category = "Entertainment";
        } else if (description.contains("salary") || description.contains("paycheck") || 
                  description.contains("deposit") || description.contains("income")) {
            category = "Income";
        }
        
        // Create a new transaction with the determined category
        return new Transaction(
            transaction.getDate(),
            transaction.getAmount(),
            category,
            transaction.getDescription(),
            transaction.getType()
        );
    }
}