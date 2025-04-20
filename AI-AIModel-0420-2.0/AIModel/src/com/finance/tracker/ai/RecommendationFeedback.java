/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finance.tracker.ai;

import java.time.LocalDate;

/**
 * Represents feedback for a recommendation
 */
public class RecommendationFeedback {
    private boolean helpful;
    private String comment;
    private LocalDate dateProvided;
    
    public RecommendationFeedback(boolean helpful, String comment, LocalDate dateProvided) {
        this.helpful = helpful;
        this.comment = comment;
        this.dateProvided = dateProvided;
    }
    
    // Getters
    public boolean isHelpful() { return helpful; }
    public String getComment() { return comment; }
    public LocalDate getDateProvided() { return dateProvided; }
}
