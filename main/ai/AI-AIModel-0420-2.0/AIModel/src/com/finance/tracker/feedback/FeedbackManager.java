/*
 * Package: com.finance.tracker.feedback
 * Purpose: User feedback collection system
 */

package com.finance.tracker.feedback;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Manages feedback for AI suggestions and system features
 */
public class FeedbackManager {
    private static final String FEEDBACK_FILE = "data/user_feedback.json";
    
    private List<FeedbackEntry> feedbackHistory;
    
    // Singleton pattern
    private static FeedbackManager instance;
    
    public static FeedbackManager getInstance() {
        if (instance == null) {
            instance = new FeedbackManager();
        }
        return instance;
    }
    
    private FeedbackManager() {
        feedbackHistory = new ArrayList<>();
        loadFeedback();
    }
    
    /**
     * Loads feedback history from JSON file
     */
    private void loadFeedback() {
        JSONParser parser = new JSONParser();
        
        try (FileReader reader = new FileReader(FEEDBACK_FILE)) {
            JSONArray feedbackArray = (JSONArray) parser.parse(reader);
            
            for (Object obj : feedbackArray) {
                JSONObject fbJson = (JSONObject) obj;
                
                String id = (String) fbJson.get("id");
                String type = (String) fbJson.get("type");
                String targetId = (String) fbJson.get("targetId");
                boolean helpful = (boolean) fbJson.get("helpful");
                String comment = (String) fbJson.get("comment");
                String dateStr = (String) fbJson.get("date");
                LocalDate date = LocalDate.parse(dateStr);
                
                FeedbackEntry entry = new FeedbackEntry(id, type, targetId, helpful, comment, date);
                feedbackHistory.add(entry);
            }
            
        } catch (IOException | ParseException e) {
            System.err.println("Error loading feedback: " + e.getMessage());
            // Continue with empty feedback list
        }
    }
    
    /**
     * Saves feedback history to JSON file
     */
    private void saveFeedback() {
        JSONArray feedbackArray = new JSONArray();
        
        for (FeedbackEntry entry : feedbackHistory) {
            JSONObject fbJson = new JSONObject();
            fbJson.put("id", entry.getId());
            fbJson.put("type", entry.getType());
            fbJson.put("targetId", entry.getTargetId());
            fbJson.put("helpful", entry.isHelpful());
            fbJson.put("comment", entry.getComment());
            fbJson.put("date", entry.getDate().toString());
            
            feedbackArray.add(fbJson);
        }
        
        try (FileWriter writer = new FileWriter(FEEDBACK_FILE)) {
            writer.write(feedbackArray.toJSONString());
        } catch (IOException e) {
            System.err.println("Error saving feedback: " + e.getMessage());
        }
    }
    
    /**
     * Records new feedback
     */
    public void recordFeedback(String type, String targetId, boolean helpful, String comment) {
        String id = "feedback_" + System.currentTimeMillis();
        FeedbackEntry entry = new FeedbackEntry(id, type, targetId, helpful, comment, LocalDate.now());
        feedbackHistory.add(entry);
        saveFeedback();
    }
    
    /**
     * Gets all feedback entries
     */
    public List<FeedbackEntry> getAllFeedback() {
        return new ArrayList<>(feedbackHistory);
    }
    
    /**
     * Gets feedback for a specific target
     */
    public List<FeedbackEntry> getFeedbackForTarget(String targetId) {
        List<FeedbackEntry> targetFeedback = new ArrayList<>();
        
        for (FeedbackEntry entry : feedbackHistory) {
            if (entry.getTargetId().equals(targetId)) {
                targetFeedback.add(entry);
            }
        }
        
        return targetFeedback;
    }
    
    /**
     * Gets feedback of a specific type
     */
    public List<FeedbackEntry> getFeedbackByType(String type) {
        List<FeedbackEntry> typeFeedback = new ArrayList<>();
        
        for (FeedbackEntry entry : feedbackHistory) {
            if (entry.getType().equals(type)) {
                typeFeedback.add(entry);
            }
        }
        
        return typeFeedback;
    }
}

