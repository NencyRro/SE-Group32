package com.finance.tracker.classification.model;

/**
 * Category Type Enumeration - Defines the types of transaction categories (income or expense)
 */
public enum CategoryType {
    INCOME("Income"), 
    EXPENSE("Expense");
    
    private final String displayName;
    
    CategoryType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}