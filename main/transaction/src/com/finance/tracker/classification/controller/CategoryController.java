package com.finance.tracker.classification.controller;

import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.util.CategoryManager;
import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * Category Controller - Handles business logic related to categories
 */
public class CategoryController {
    private CategoryManager categoryManager;
    private Category selectedCategory;
    private CategorySelectionListener selectionListener;
    private CategoryType currentType = CategoryType.EXPENSE;
    
    /**
     * Creates a new instance of the category controller
     */
    public CategoryController() {
        categoryManager = new CategoryManager();
        // No longer need initDefaultCategories, as CategoryManager already handles initialization of default categories
    }
    
    /**
     * Gets the list of categories by type
     * 
     * @param type Category type
     * @return All categories of this type
     */
    public List<Category> getCategoriesByType(CategoryType type) {
        return categoryManager.getCategoriesByType(type);
    }
    
    /**
     * Selects a category and notifies listeners
     * 
     * @param category The selected category
     */
    public void selectCategory(Category category) {
        this.selectedCategory = category;
        if (selectionListener != null) {
            selectionListener.onCategorySelected(category);
        }
    }
    
    /**
     * Gets the currently selected category
     * 
     * @return The currently selected category
     */
    public Category getSelectedCategory() {
        return selectedCategory;
    }
    
    /**
     * Displays the add category dialog
     * 
     * @param type Type of the new category
     * @param parent Parent component
     */
    public void showAddCategoryDialog(CategoryType type, Component parent) {
        String name = JOptionPane.showInputDialog(parent, "Enter category name:", "Add " + type.getDisplayName() + " Category", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            Color color = JColorChooser.showDialog(parent, "Choose Category Color", Color.LIGHT_GRAY);
            if (color != null) {
                int nextId = categoryManager.getNextAvailableId();
                Category newCategory = new Category(nextId, name, type, null, color);
                categoryManager.addCategory(newCategory);
                // addCategory method now automatically saves changes to CSV
            }
        }
    }
    
    /**
     * Deletes a category
     * 
     * @param category The category to delete
     */
    public void deleteCategory(Category category) {
        categoryManager.deleteCategory(category);
        if (selectedCategory != null && selectedCategory.equals(category)) {
            selectedCategory = null;
        }
        // deleteCategory method now automatically saves changes to CSV
    }
    
    /**
     * Updates a category
     * 
     * @param category The category to update
     */
    public void updateCategory(Category category) {
        categoryManager.updateCategory(category);
        // updateCategory method now automatically saves changes to CSV
    }
    
    /**
     * Sets the category selection listener
     * 
     * @param listener Listener instance
     */
    public void setSelectionListener(CategorySelectionListener listener) {
        this.selectionListener = listener;
    }
    
    /**
     * Sets the current category type for operations
     * 
     * @param type Category type
     */
    public void setCurrentType(CategoryType type) {
        this.currentType = type;
    }
    
    /**
     * Gets the current category type for operations
     * 
     * @return The current category type
     */
    public CategoryType getCurrentType() {
        return currentType;
    }
}