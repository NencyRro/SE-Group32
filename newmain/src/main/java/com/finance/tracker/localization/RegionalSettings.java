/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finance.tracker.localization;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages regional settings and preferences
 */
public class RegionalSettings {
    private static final String SETTINGS_FILE = "config/regional_settings.json";
    private String region;
    private String language;
    private String dateFormat;
    
    // Singleton pattern
    private static RegionalSettings instance;
    
    private Map<String, String> regionalSettings = new HashMap<>();
    
    public static RegionalSettings getInstance() {
        if (instance == null) {
            instance = new RegionalSettings();
        }
        return instance;
    }
    
    private RegionalSettings() {
        loadSettings();
    }
    
    /**
     * Loads regional settings from JSON file
     */
    private void loadSettings() {
        JSONParser parser = new JSONParser();
        
        try (FileReader reader = new FileReader(SETTINGS_FILE)) {
            JSONObject settings = (JSONObject) parser.parse(reader);
            
            region = (String) settings.get("region");
            language = (String) settings.get("language");
            dateFormat = (String) settings.get("dateFormat");
            
        } catch (IOException | ParseException e) {
            System.err.println("Error loading regional settings: " + e.getMessage());
            // Initialize with default values
            initializeDefaultSettings();
        }
    }
    
    /**
     * Initialize with default values if settings file is not found
     */
    private void initializeDefaultSettings() {
        region = "China";
        language = "zh_CN";
        dateFormat = "yyyy-MM-dd";
    }
    
    /**
     * Saves regional settings to JSON file
     */
    public void saveSettings() {
        JSONObject settings = new JSONObject();
        settings.put("region", region);
        settings.put("language", language);
        settings.put("dateFormat", dateFormat);
        
        try (FileWriter writer = new FileWriter(SETTINGS_FILE)) {
            writer.write(settings.toJSONString());
        } catch (IOException e) {
            System.err.println("Error saving regional settings: " + e.getMessage());
        }
    }
    
    // Getters and setters
    public String getRegion() { return region; }
    public void setRegion(String region) { 
        this.region = region;
        saveSettings();
    }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { 
        this.language = language;
        saveSettings();
    }
    
    public String getDateFormat() { return dateFormat; }
    public void setDateFormat(String dateFormat) { 
        this.dateFormat = dateFormat;
        saveSettings();
    }

    public void setSetting(String key, String value) {
        regionalSettings.put(key, value);
        saveSettings();
    }
}
