package com.finance.tracker.classification.util;

import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Category Data Manager - Responsible for storing and retrieving category data
 */
public class CategoryManager {
    private List<Category> categories = new ArrayList<>();
    
    // CSV file path for category data
    private static final String CATEGORIES_CSV_PATH = "categories.csv";
    
    // CSV file header
    private static final String CSV_HEADER = "ID,Name,Type,IconPath,ColorRGB";
    
    /**
     * Creates a new category manager
     */
    public CategoryManager() {
        // Load category data from CSV
        loadCategories();
        
        // If no categories were loaded, add default categories
        if (categories.isEmpty()) {
            initDefaultCategories();
        }
    }
    
    /**
     * Initialize default categories
     */
    private void initDefaultCategories() {
        // Expense categories
        addCategory(new Category(1, "Food", CategoryType.EXPENSE, null, new Color(255, 153, 153)));
        addCategory(new Category(2, "Shopping", CategoryType.EXPENSE, null, new Color(153, 204, 255)));
        addCategory(new Category(3, "Transport", CategoryType.EXPENSE, null, new Color(153, 255, 153)));
        addCategory(new Category(4, "Rent", CategoryType.EXPENSE, null, new Color(255, 204, 153)));
        addCategory(new Category(5, "Bills", CategoryType.EXPENSE, null, new Color(204, 153, 255)));
        addCategory(new Category(6, "Medical", CategoryType.EXPENSE, null, new Color(255, 255, 153)));
        addCategory(new Category(7, "Other", CategoryType.EXPENSE, null, new Color(192, 192, 192)));
        
        // Income categories
        addCategory(new Category(8, "Salary", CategoryType.INCOME, null, new Color(102, 255, 102)));
        addCategory(new Category(9, "Bonus", CategoryType.INCOME, null, new Color(102, 204, 255)));
        addCategory(new Category(10, "Gift", CategoryType.INCOME, null, new Color(255, 204, 153)));
        addCategory(new Category(11, "Investment", CategoryType.INCOME, null, new Color(153, 204, 204)));
        addCategory(new Category(12, "Other", CategoryType.INCOME, null, new Color(192, 192, 192)));
        
        // Save categories to CSV
        saveCategories();
    }
    
    /**
     * Get all categories
     * 
     * @return List of all categories
     */
    public List<Category> getAllCategories() {
        return Collections.unmodifiableList(new ArrayList<>(categories));
    }
    
    /**
     * Get categories by type
     * 
     * @param type Category type
     * @return All categories of the specified type
     */
    public List<Category> getCategoriesByType(CategoryType type) {
        return categories.stream()
                .filter(c -> c.getType() == type)
                .collect(Collectors.toList());
    }
    
    /**
     * Get category by ID
     * 
     * @param id Category ID
     * @return Found category, or null
     */
    public Category getCategoryById(int id) {
        return categories.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Add new category
     * 
     * @param category Category to add
     */
    public void addCategory(Category category) {
        if (getCategoryById(category.getId()) == null) {
            categories.add(category);
            saveCategories(); // Save to CSV
        }
    }
    
    /**
     * Update existing category
     * 
     * @param category Category to update
     */
    public void updateCategory(Category category) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == category.getId()) {
                categories.set(i, category);
                saveCategories(); // Save to CSV
                return;
            }
        }
        
        // If category doesn't exist, add it
        addCategory(category);
    }
    
    /**
     * Delete category
     * 
     * @param category Category to delete
     * @return Whether deletion was successful
     */
    public boolean deleteCategory(Category category) {
        boolean removed = categories.removeIf(c -> c.getId() == category.getId());
        if (removed) {
            saveCategories(); // Save to CSV
        }
        return removed;
    }
    
    /**
     * Get next available ID
     * 
     * @return Next available ID
     */
    public int getNextAvailableId() {
        return categories.stream()
                .mapToInt(Category::getId)
                .max()
                .orElse(0) + 1;
    }
    
    /**
     * Save categories to CSV file
     */
    private void saveCategories() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CATEGORIES_CSV_PATH))) {
            // Write header
            writer.println(CSV_HEADER);
            
            // Write each category
            for (Category category : categories) {
                StringBuilder sb = new StringBuilder();
                sb.append(category.getId()).append(",");
                
                // Handle commas in name
                if (category.getName().contains(",")) {
                    sb.append("\"").append(category.getName().replace("\"", "\"\"")).append("\"");
                } else {
                    sb.append(category.getName());
                }
                sb.append(",");
                
                sb.append(category.getType().name()).append(",");
                
                // Handle icon path, may be null
                if (category.getIconPath() != null) {
                    sb.append(category.getIconPath());
                }
                sb.append(",");
                
                // Save color RGB values
                Color color = category.getColor();
                sb.append(color.getRed()).append(":").append(color.getGreen()).append(":").append(color.getBlue());
                
                writer.println(sb.toString());
            }
            
            System.out.println("Category data saved to: " + new File(CATEGORIES_CSV_PATH).getAbsolutePath());
            
        } catch (IOException e) {
            System.err.println("Error saving category data: " + e.getMessage());
        }
    }
    
    /**
     * Load categories from CSV file
     */
    private void loadCategories() {
        categories.clear();
        File file = new File(CATEGORIES_CSV_PATH);
        
        if (!file.exists()) {
            return; // File doesn't exist, return empty list
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Read header
            
            if (line == null || !line.equals(CSV_HEADER)) {
                return; // File is empty or format is incorrect
            }
            
            // Read each line of category data
            while ((line = reader.readLine()) != null) {
                Category category = parseCsvLine(line);
                if (category != null) {
                    categories.add(category);
                }
            }
            
            System.out.println("Loaded " + categories.size() + " categories from file");
            
        } catch (IOException e) {
            System.err.println("Error loading category data: " + e.getMessage());
        }
    }
    
    /**
     * Parse CSV line to category object
     * 
     * @param line CSV line
     * @return Category object, or null
     */
    private Category parseCsvLine(String line) {
        try {
            // Split CSV line, handle commas within quotes
            List<String> fields = new ArrayList<>();
            boolean inQuotes = false;
            StringBuilder field = new StringBuilder();
            
            for (char c : line.toCharArray()) {
                if (c == '\"') {
                    inQuotes = !inQuotes;
                } else if (c == ',' && !inQuotes) {
                    fields.add(field.toString());
                    field = new StringBuilder();
                } else {
                    field.append(c);
                }
            }
            fields.add(field.toString());
            
            // Check field count
            if (fields.size() < 5) {
                return null;
            }
            
            int id = Integer.parseInt(fields.get(0));
            
            String name = fields.get(1);
            // If name is quoted, remove quotes
            if (name.startsWith("\"") && name.endsWith("\"")) {
                name = name.substring(1, name.length() - 1);
                name = name.replace("\"\"", "\"");
            }
            
            CategoryType type = CategoryType.valueOf(fields.get(2));
            
            String iconPath = fields.get(3);
            if (iconPath.trim().isEmpty()) {
                iconPath = null;
            }
            
            // Parse color
            String colorStr = fields.get(4);
            String[] rgb = colorStr.split(":");
            
            Color color;
            if (rgb.length >= 3) {
                int r = Integer.parseInt(rgb[0]);
                int g = Integer.parseInt(rgb[1]);
                int b = Integer.parseInt(rgb[2]);
                color = new Color(r, g, b);
            } else {
                color = new Color(200, 200, 200); // Default color
            }
            
            return new Category(id, name, type, iconPath, color);
            
        } catch (Exception e) {
            System.err.println("Error parsing category data: " + e.getMessage());
            return null;
        }
    }
}