package com.finance.tracker.classification.model;

import java.awt.Color;
import java.util.Objects;

/**
 * Category Entity Class - Represents a transaction category
 */
public class Category {
    private int id;
    private String name;
    private CategoryType type;
    private String iconPath;
    private Color color;
    
    /**
     * Constructs a new category
     * 
     * @param id Category ID
     * @param name Category name
     * @param type Category type (income/expense)
     * @param iconPath Icon path
     * @param color Display color
     */
    public Category(int id, String name, CategoryType type, String iconPath, Color color) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.iconPath = iconPath;
        this.color = color != null ? color : new Color(200, 200, 200);
    }
    
    /**
     * Simplified constructor, uses default color
     */
    public Category(int id, String name, CategoryType type, String iconPath) {
        this(id, name, type, iconPath, new Color(200, 200, 200));
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public CategoryType getType() {
        return type;
    }
    
    public void setType(CategoryType type) {
        this.type = type;
    }
    
    public String getIconPath() {
        return iconPath;
    }
    
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return name;
    }
}