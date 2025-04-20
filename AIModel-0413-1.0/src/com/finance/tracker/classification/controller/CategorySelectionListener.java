package com.finance.tracker.classification.controller;

import com.finance.tracker.classification.model.Category;

/**
 * Category Selection Listener Interface - Callback when user selects a category
 */
public interface CategorySelectionListener {
    /**
     * Called when a category is selected
     * 
     * @param category The selected category
     */
    void onCategorySelected(Category category);
}