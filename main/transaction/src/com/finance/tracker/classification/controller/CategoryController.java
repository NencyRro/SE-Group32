package com.finance.tracker.classification.controller;

import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.classification.util.CategoryManager;
import com.finance.tracker.classification.util.TransactionManager;
import com.finance.tracker.classification.view.TransactionList;
import com.finance.tracker.classification.util.CSVImportManager;
import com.finance.tracker.classification.util.*;
import com.finance.tracker.classification.view.TransactionList;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

/**
 * Category Controller - Handles business logic related to categories
 */
public class CategoryController {

    private Category selectedCategory;
    private CategorySelectionListener selectionListener;
    private CategoryType currentType = CategoryType.EXPENSE;
    
    private final TransactionManager transactionManager;
    private final TransactionList transactionList;
    private final CategoryManager categoryManager;
    private final CSVImportManager csvImportManager;
    /**
     * Creates a new instance of the category controller
     */
    public CategoryController(TransactionManager transactionManager, TransactionList transactionList) {
        this.transactionManager = transactionManager;
        this.transactionList = transactionList;
        this.categoryManager = transactionManager.getCategoryManager();
        this.csvImportManager = new CSVImportManager(categoryManager, transactionManager);
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
    
    // 导入CSV，并调用现有的refresh()方法更新界面
    public void importCSV(File file, Component parent) {
        try {
            // 读取 CSV 数据
            List<Transaction> imported = csvImportManager.importTransactions(file);
            System.out.println("CSV读取行数：" + imported.size());
    
            // 获取已有记录并去重
            List<Transaction> existing = transactionManager.getAllTransactions();
            List<Transaction> clean = csvImportManager.preprocessTransactions(imported, existing);
            System.out.println("去重后新增行数：" + clean.size());
    
            // 写入 TransactionManager
            for (Transaction t : clean) {
                transactionManager.addTransaction(t);
            }
    
            // 刷新界面
            transactionList.refresh();
    
            // 提示用户
            JOptionPane.showMessageDialog(parent, "成功导入 " + clean.size() + " 条交易记录！",
                    "导入成功", JOptionPane.INFORMATION_MESSAGE);
    
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "导入失败：" + e.getMessage(),
                    "导入失败", JOptionPane.ERROR_MESSAGE);
        }
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