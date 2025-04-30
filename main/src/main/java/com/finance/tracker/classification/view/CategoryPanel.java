package com.finance.tracker.classification.view;

import com.finance.tracker.classification.controller.CategoryController;
import com.finance.tracker.classification.controller.CategorySelectionListener;
import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import java.awt.*;
import javax.swing.*;

/**
 * Category Selection Main Panel - Integrates type switching and category grid
 */
public class CategoryPanel extends JPanel {
    private CategoryController controller;
    private CategorySelectionGrid selectionGrid;
    private JToggleButton expenseButton;
    private JToggleButton incomeButton;
    private JButton manageButton;
    private CategoryType currentType = CategoryType.EXPENSE;
    
    /**
     * Create category selection panel
     * 
     * @param controller Category controller
     */
    public CategoryPanel(CategoryController controller) {
        this.controller = controller;
        
        initializeUI();
        loadCategories(CategoryType.EXPENSE); // Default to loading expense categories
    }
    
    /**
     * Initialize user interface
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Create top toggle panel
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Create category grid
        selectionGrid = new CategorySelectionGrid(controller);
        JScrollPane scrollPane = new JScrollPane(selectionGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Create top toggle panel
     * 
     * @return Top panel
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create type toggle button group
        JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        expenseButton = new JToggleButton("Expense");
        incomeButton = new JToggleButton("Income");
        
        ButtonGroup group = new ButtonGroup();
        group.add(expenseButton);
        group.add(incomeButton);
        
        // Default to expense
        expenseButton.setSelected(true);
        
        // Add toggle listeners
        expenseButton.addActionListener(e -> {
            currentType = CategoryType.EXPENSE;
            controller.setCurrentType(currentType);
            loadCategories(currentType);
        });
        
        incomeButton.addActionListener(e -> {
            currentType = CategoryType.INCOME;
            controller.setCurrentType(currentType);
            loadCategories(currentType);
        });
        
        togglePanel.add(expenseButton);
        togglePanel.add(incomeButton);
        
        panel.add(togglePanel, BorderLayout.CENTER);
        
        // Add manage button
        manageButton = new JButton("Manage");
        manageButton.addActionListener(e -> {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            CategoryManagementDialog dialog = new CategoryManagementDialog(parentFrame, controller, currentType);
            dialog.setVisible(true);
            // Refresh categories after dialog closes
            loadCategories(currentType);
        });
        
        JPanel managePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        managePanel.add(manageButton);
        
        panel.add(managePanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Load categories of specified type
     * 
     * @param type Category type
     */
    public void loadCategories(CategoryType type) {
        selectionGrid.loadCategories(type);
    }
    
    /**
     * Set category selection listener
     * 
     * @param listener Listener
     */
    public void setCategorySelectionListener(CategorySelectionListener listener) {
        controller.setSelectionListener(listener);
    }
    
    /**
     * Get the currently selected category type
     * 
     * @return Current category type
     */
    public CategoryType getCurrentType() {
        return currentType;
    }
    
    /**
     * Get the selected category
     * 
     * @return Selected category
     */
    public Category getSelectedCategory() {
        return controller.getSelectedCategory();
    }
    
    /**
     * Select a specified category
     * 
     * @param category Category to select
     */
    public void selectCategory(Category category) {
        if (category != null) {
            currentType = category.getType();
            if (currentType == CategoryType.EXPENSE) {
                expenseButton.setSelected(true);
            } else {
                incomeButton.setSelected(true);
            }
            loadCategories(currentType);
            selectionGrid.selectCategory(category);
        }
    }
    
    /**
     * Clear current selection
     */
    public void clearSelection() {
        selectionGrid.clearSelection();
    }
}