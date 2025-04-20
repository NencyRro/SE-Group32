/*
 * Package: com.finance.tracker.ai
 * Purpose: AI recommendation model
 */
package com.finance.tracker.ai;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a financial recommendation
 */
public class Recommendation {
    private String id;
    private String type; // BUDGET, SAVINGS, PATTERN
    private String message;
    private LocalDate dateCreated;
    private boolean dismissed;
    private Map<String, RecommendationFeedback> feedback;
    
    public Recommendation(String id, String type, String message, LocalDate dateCreated) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.dateCreated = dateCreated;
        this.dismissed = false;
        this.feedback = new HashMap<>();
    }
    
    /**
     * Dismiss this recommendation
     */
    public void dismiss() {
        this.dismissed = true;
    }
    
    /**
     * Add feedback for this recommendation
     */
    public void addFeedback(boolean helpful, String comment) {
        RecommendationFeedback fb = new RecommendationFeedback(helpful, comment, LocalDate.now());
        feedback.put(System.currentTimeMillis() + "", fb);
    }
    
    // Getters
    public String getId() { return id; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public LocalDate getDateCreated() { return dateCreated; }
    public boolean isDismissed() { return dismissed; }
    public Map<String, RecommendationFeedback> getFeedback() { 
        return new HashMap<>(feedback); 
    }
}