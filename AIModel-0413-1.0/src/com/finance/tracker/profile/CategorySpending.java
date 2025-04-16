/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finance.tracker.profile;

import java.math.BigDecimal;

/**
 * Represents spending in a category
 */
public class CategorySpending {
    private String category;
    private BigDecimal amount;
    
    public CategorySpending(String category, BigDecimal amount) {
        this.category = category;
        this.amount = amount;
    }
    
    // Getters
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    
    @Override
    public String toString() {
        return category + ": " + amount;
    }
}