/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.finance.tracker.calendar;

/**
 * Represents a holiday definition
 */
public class Holiday {
    private String name;
    private String description;
    private String type; // public, major, minor
    
    public Holiday(String name, String description, String type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    
    @Override
    public String toString() {
        return name;
    }
}

