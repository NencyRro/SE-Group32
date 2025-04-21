package com.finance.tracker.classification.view;

import com.finance.tracker.classification.controller.CategoryController;
import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Category Management Dialog - Used to add, edit, and delete categories
 */
public class CategoryManagementDialog extends JDialog {
    private CategoryController controller;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private CategoryType currentType;
    
    /**
     * Create category management dialog
     * 
     * @param parent Parent component
     * @param controller Category controller
     * @param initialType Initial category type
     */
    public CategoryManagementDialog(JFrame parent, CategoryController controller, CategoryType initialType) {
        super(parent, "Manage Categories", true);
        this.controller = controller;
        this.currentType = initialType;
        
        initializeUI();
        loadCategories(currentType);
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }
    
    /**
     * Initialize user interface
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create top panel, including type toggle
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JToggleButton expenseButton = new JToggleButton("Expense");
        JToggleButton incomeButton = new JToggleButton("Income");
        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(expenseButton);
        typeGroup.add(incomeButton);
        
        // Set default selection
        if (currentType == CategoryType.EXPENSE) {
            expenseButton.setSelected(true);
        } else {
            incomeButton.setSelected(true);
        }
        
        // Add toggle listeners
        expenseButton.addActionListener(e -> {
            currentType = CategoryType.EXPENSE;
            loadCategories(currentType);
        });
        
        incomeButton.addActionListener(e -> {
            currentType = CategoryType.INCOME;
            loadCategories(currentType);
        });
        
        topPanel.add(expenseButton);
        topPanel.add(incomeButton);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Create table
        String[] columnNames = {"ID", "Name", "Color"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Only allow editing the name column
            }
        };
        
        categoryTable = new JTable(tableModel);
        categoryTable.getColumnModel().getColumn(0).setMaxWidth(50);
        
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton editColorButton = new JButton("Edit Color");
        JButton closeButton = new JButton("Close");
        
        addButton.addActionListener(e -> addCategory());
        deleteButton.addActionListener(e -> deleteCategory());
        editColorButton.addActionListener(e -> editCategoryColor());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editColorButton);
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Load categories of specified type into the table
     * 
     * @param type Category type
     */
    private void loadCategories(CategoryType type) {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get category list
        List<Category> categories = controller.getCategoriesByType(type);
        
        // Add to table
        for (Category category : categories) {
            Object[] rowData = {
                category.getId(),
                category.getName(),
                renderColorCell(category.getColor())
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Render color cell
     * 
     * @param color Color value
     * @return String representation of color
     */
    private String renderColorCell(Color color) {
        return String.format("RGB(%d,%d,%d)", color.getRed(), color.getGreen(), color.getBlue());
    }
    
    /**
     * Add new category
     */
    private void addCategory() {
        String name = JOptionPane.showInputDialog(this, "Enter category name:", "Add Category", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            Color color = JColorChooser.showDialog(this, "Choose Category Color", Color.LIGHT_GRAY);
            if (color != null) {
                int nextId = controller.getCategoriesByType(currentType).size() + 1;
                Category newCategory = new Category(nextId, name, currentType, null, color);
                controller.updateCategory(newCategory);
                loadCategories(currentType);
            }
        }
    }
    
    /**
     * Delete selected category
     */
    private void deleteCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            
            int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete category '" + name + "'?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
            );
            
            if (option == JOptionPane.YES_OPTION) {
                // Find corresponding category object
                List<Category> categories = controller.getCategoriesByType(currentType);
                for (Category category : categories) {
                    if (category.getId() == id) {
                        controller.deleteCategory(category);
                        break;
                    }
                }
                
                loadCategories(currentType);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a category to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Edit category color
     */
    private void editCategoryColor() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            
            // Find corresponding category object
            List<Category> categories = controller.getCategoriesByType(currentType);
            for (Category category : categories) {
                if (category.getId() == id) {
                    // Show color chooser
                    Color newColor = JColorChooser.showDialog(
                        this,
                        "Choose New Color for '" + category.getName() + "'",
                        category.getColor()
                    );
                    
                    if (newColor != null) {
                        category.setColor(newColor);
                        controller.updateCategory(category);
                        loadCategories(currentType);
                    }
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a category to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
}