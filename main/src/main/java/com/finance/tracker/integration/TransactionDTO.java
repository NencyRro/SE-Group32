/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finance.tracker.integration;

import java.math.BigDecimal;
import java.time.LocalDate;
/**
 * Data transfer object for transactions
 */
public class TransactionDTO {
    private String category;
    private BigDecimal amount;
    private LocalDate date;
    
    public TransactionDTO(String category, BigDecimal amount, LocalDate date) {
        this.category = category;
        this.amount = amount;
        this.date = date;
    }
    
    // Getters
    public String getCategory() { return category; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDate() { return date; }
}
