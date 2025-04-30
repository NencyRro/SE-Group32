package com.finance.tracker.classification.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Transaction Entity Class - Represents an income or expense transaction
 */
public class Transaction {
    private String id;
    private LocalDateTime dateTime;
    private Category category;
    private BigDecimal amount;
    private String description;
    private LocalDateTime importTimestamp;  // 新增字段：导入时间
    
    // Date time formatter
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // Import date time formatter
    private static final DateTimeFormatter IMPORT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates a new transaction record
     * 
     * @param category Transaction category
     * @param amount Transaction amount
     * @param description Transaction description
     */
    public Transaction(Category category, BigDecimal amount, String description) {
        this.id = UUID.randomUUID().toString();
        this.dateTime = LocalDateTime.now();
        this.importTimestamp = LocalDateTime.now();
        this.category = category;
        this.amount = amount;
        this.description = description;
    }
    
    public Transaction(String id, LocalDateTime dateTime, Category category, BigDecimal amount, String description) {
        this.id = id;
        this.dateTime = dateTime;
        this.importTimestamp = LocalDateTime.now();  
        this.category = category;
        this.amount = amount;
        this.description = description;
    }
    
    /**
     * Creates a transaction record from saved data
     * 
     * @param id Transaction ID
     * @param dateTime Transaction time
     * @param category Transaction category
     * @param amount Transaction amount
     * @param description Transaction description
     */

    
    /**
     * Converts the transaction record to a CSV line
     * 
     * @return CSV formatted transaction record
     */
    public String toCsvLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append(",");
        sb.append(dateTime.format(DATE_FORMATTER)).append(",");
        sb.append(category.getId()).append(",");
        sb.append(category.getType().name()).append(",");
        sb.append(amount.toString()).append(",");
        
        // Handle commas in description
        String escapedDescription = description != null ? 
                                    "\"" + description.replace("\"", "\"\"") + "\"" : 
                                    "";
        sb.append(escapedDescription);
        
        return sb.toString();
    }
    
    /**
     * Gets the signed amount of the transaction (expenses are negative, income is positive)
     * 
     * @return Signed amount
     */
    public BigDecimal getSignedAmount() {
        if (category.getType() == CategoryType.EXPENSE) {
            return amount.negate();
        }
        return amount;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public String getFormattedDateTime() {
        return dateTime.format(DATE_FORMATTER);
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getImportTimestamp() {
        return importTimestamp;
    }
    
    public void setImportTimestamp(LocalDateTime importTimestamp) {
        this.importTimestamp = importTimestamp;
    }
    
    public String getFormattedImportTimestamp() {
        if (importTimestamp != null) {
            return importTimestamp.format(IMPORT_FORMATTER);
        } else {
            return "";
        }
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "[" + getFormattedDateTime() + "] " + 
               category.getType().getDisplayName() + ": " + 
               category.getName() + " - " + 
               amount + (description != null ? " (" + description + ")" : "");
    }
}