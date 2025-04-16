/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finance.tracker.feedback;

import java.time.LocalDate;

/**
 * Represents a single feedback entry
 */
public class FeedbackEntry {
    private String id;
    private String type; // RECOMMENDATION, FEATURE, CLASSIFICATION
    private String targetId; // ID of the target entity
    private boolean helpful;
    private String comment;
    private LocalDate date;
    
    public FeedbackEntry(String id, String type, String targetId, boolean helpful, String comment, LocalDate date) {
        this.id = id;
        this.type = type;
        this.targetId = targetId;
        this.helpful = helpful;
        this.comment = comment;
        this.date = date;
    }
    
    // Getters
    public String getId() { return id; }
    public String getType() { return type; }
    public String getTargetId() { return targetId; }
    public boolean isHelpful() { return helpful; }
    public String getComment() { return comment; }
    public LocalDate getDate() { return date; }
}

