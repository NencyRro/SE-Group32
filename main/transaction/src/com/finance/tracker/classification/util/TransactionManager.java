package com.finance.tracker.classification.util;

import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Transaction Manager - Manages access to transaction records
 */
public class TransactionManager {
    private List<Transaction> transactions = new ArrayList<>();
    private CategoryManager categoryManager;
    private String csvFilePath;
    
    // CSV file header row
    private static final String CSV_HEADER = "ID,DateTime,CategoryID,CategoryType,Amount,Description";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Creates a new transaction manager
     * 
     * @param categoryManager Category manager instance
     * @param csvFilePath CSV file path
     */
    public TransactionManager(CategoryManager categoryManager, String csvFilePath) {
        this.categoryManager = categoryManager;
        this.csvFilePath = csvFilePath;
        loadTransactions(); // Load existing transactions from CSV
    }
    
    /**
     * Add new transaction record
     * 
     * @param transaction Transaction record
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        saveTransactions(); // Save to CSV
    }
    
    /**
     * Get all transaction records
     * 
     * @return List of transaction records
     */
    public List<Transaction> getAllTransactions() {
        // Return a sorted copy, in descending order by time (newest first)
        List<Transaction> sortedList = new ArrayList<>(transactions);
        sortedList.sort(Comparator.comparing(Transaction::getDateTime).reversed());
        return Collections.unmodifiableList(sortedList);
    }
    
    /**
     * Get transaction records by category type
     * 
     * @param type Category type
     * @return Transaction records of the specified type
     */
    public List<Transaction> getTransactionsByType(CategoryType type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getCategory().getType() == type) {
                result.add(t);
            }
        }
        
        // Sort by time in descending order
        result.sort(Comparator.comparing(Transaction::getDateTime).reversed());
        return result;
    }
    
    /**
     * Get transaction records for a specific category
     * 
     * @param category Category
     * @return Transaction records for that category
     */
    public List<Transaction> getTransactionsByCategory(Category category) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getCategory().getId() == category.getId()) {
                result.add(t);
            }
        }
        
        // Sort by time in descending order
        result.sort(Comparator.comparing(Transaction::getDateTime).reversed());
        return result;
    }
    
    /**
     * Calculate total income
     * 
     * @return Total income amount
     */
    public BigDecimal getTotalIncome() {
        BigDecimal total = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if (t.getCategory().getType() == CategoryType.INCOME) {
                total = total.add(t.getAmount());
            }
        }
        return total;
    }
    
    /**
     * Calculate total expenses
     * 
     * @return Total expense amount
     */
    public BigDecimal getTotalExpense() {
        BigDecimal total = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if (t.getCategory().getType() == CategoryType.EXPENSE) {
                total = total.add(t.getAmount());
            }
        }
        return total;
    }
    
    /**
     * Calculate balance
     * 
     * @return Current balance
     */
    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }
    
    /**
     * Load transaction records from CSV file
     */
    private void loadTransactions() {
        transactions.clear();
        File file = new File(csvFilePath);
        
        // If file doesn't exist, create an empty file
        if (!file.exists()) {
            saveTransactions();
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Read header row
            
            // If file is empty or format is incorrect, recreate it
            if (line == null || !line.equals(CSV_HEADER)) {
                saveTransactions();
                return;
            }
            
            int lineCount = 0;
            // Read transaction records
            while ((line = reader.readLine()) != null) {
                lineCount++;
                Transaction transaction = parseCsvLine(line);
                if (transaction != null) {
                    transactions.add(transaction);
                } else {
                    System.err.println("Unable to parse CSV line " + lineCount + ": " + line);
                }
            }
            
            System.out.println("Loaded " + transactions.size() + " transaction records from CSV");
            
        } catch (IOException e) {
            System.err.println("Error loading transaction records: " + e.getMessage());
        }
    }
    
    /**
     * Parse CSV line into transaction record
     * 
     * @param line CSV line
     * @return Transaction record object, or null
     */
    private Transaction parseCsvLine(String line) {
        try {
            // Split CSV line, handling commas within quotes
            List<String> fields = new ArrayList<>();
            boolean inQuotes = false;
            StringBuilder field = new StringBuilder();
            
            for (char c : line.toCharArray()) {
                if (c == '\"') {
                    inQuotes = !inQuotes;
                } else if (c == ',' && !inQuotes) {
                    fields.add(field.toString());
                    field = new StringBuilder();
                } else {
                    field.append(c);
                }
            }
            fields.add(field.toString());
            
            // Check field count
            if (fields.size() < 6) {
                return null;
            }
            
            String id = fields.get(0);
            LocalDateTime dateTime = LocalDateTime.parse(fields.get(1), DATE_FORMATTER);
            int categoryId = Integer.parseInt(fields.get(2));
            CategoryType categoryType = CategoryType.valueOf(fields.get(3));
            BigDecimal amount = new BigDecimal(fields.get(4));
            String description = fields.get(5);
            
            // If description has quotes, remove them
            if (description.startsWith("\"") && description.endsWith("\"")) {
                description = description.substring(1, description.length() - 1);
                description = description.replace("\"\"", "\"");
            }
            
            // Get category
            Category category = categoryManager.getCategoryById(categoryId);
            if (category == null) {
                // If category doesn't exist, try to create a generic category
                // Note: Using the type recorded in the transaction
                category = new Category(categoryId, "Unknown Category", categoryType, null);
                categoryManager.addCategory(category);
            }
            
            return new Transaction(id, dateTime, category, amount, description);
            
        } catch (DateTimeParseException | NumberFormatException e) {
            System.err.println("Error parsing transaction: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Save transaction records to CSV file
     */
    public void saveTransactions() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath))) {
            // Write header row
            writer.println(CSV_HEADER);
            
            // Write transaction records
            for (Transaction transaction : transactions) {
                writer.println(transaction.toCsvLine());
            }
            
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }
    
    /**
     * Delete transaction record
     * 
     * @param transaction Transaction record to delete
     * @return Whether deletion was successful
     */
    public boolean deleteTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        
        // Use ID comparison to find the transaction to delete
        boolean removed = transactions.removeIf(t -> t.getId().equals(transaction.getId()));
        
        if (removed) {
            // If successfully deleted, update CSV file
            saveTransactions();
            System.out.println("Transaction record deleted: " + transaction.getId());
        } else {
            System.out.println("Transaction record not found for deletion: " + transaction.getId());
        }
        
        return removed;
    }
}