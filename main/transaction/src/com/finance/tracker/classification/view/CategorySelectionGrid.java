package com.finance.tracker.classification.view;

import com.finance.tracker.classification.controller.CategoryController;
import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Category Grid Layout - Displays a grid of category buttons
 */
public class CategorySelectionGrid extends JPanel {
    private CategoryController controller;
    private List<CategoryButton> categoryButtons = new ArrayList<>();
    private CategoryButton selectedButton = null;
    
    /**
     * Create a category selection grid
     * 
     * @param controller Category controller
     */
    public CategorySelectionGrid(CategoryController controller) {
        this.controller = controller;
        
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);
    }
    
    /**
     * Load and display categories of specified type
     * 
     * @param type Category type
     */
    public void loadCategories(CategoryType type) {
        // Clear existing buttons
        removeAll();
        categoryButtons.clear();
        
        // Get the list of categories of this type
        List<Category> categories = controller.getCategoriesByType(type);
        
        // Create buttons for each category
        for (Category category : categories) {
            CategoryButton button = new CategoryButton(category);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectButton(button);
                    controller.selectCategory(category);
                }
            });
            
            add(button);
            categoryButtons.add(button);
        }
        
        // Add "new" button
        JButton addButton = createAddButton(type);
        add(addButton);
        
        // Refresh UI
        revalidate();
        repaint();
    }
    
    /**
     * Create a button for adding new categories
     * 
     * @param type Category type
     * @return Add button
     */
    private JButton createAddButton(CategoryType type) {
        JButton addButton = new JButton("+");
        addButton.setPreferredSize(new Dimension(80, 80));
        addButton.setFont(new Font("Arial", Font.BOLD, 24));
        addButton.setToolTipText("Add new " + type.getDisplayName() + " category");
        
        addButton.addActionListener(e -> {
            controller.showAddCategoryDialog(type, this);
            loadCategories(type);  // Reload category list
        });
        
        return addButton;
    }
    
    /**
     * Select a button and deselect other buttons
     * 
     * @param button Button to select
     */
    private void selectButton(CategoryButton button) {
        // Deselect previously selected button
        if (selectedButton != null) {
            selectedButton.setSelected(false);
        }
        
        // Set new selection
        selectedButton = button;
        selectedButton.setSelected(true);
    }
    
    /**
     * Clear current selection
     */
    public void clearSelection() {
        if (selectedButton != null) {
            selectedButton.setSelected(false);
            selectedButton = null;
        }
    }
    
    /**
     * Select specified category
     * 
     * @param category Category to select
     */
    public void selectCategory(Category category) {
        if (category == null) {
            clearSelection();
            return;
        }
        
        for (CategoryButton button : categoryButtons) {
            if (button.getCategory().equals(category)) {
                selectButton(button);
                break;
            }
        }
    }
}