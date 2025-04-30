package com.finance.tracker.classification.controller;

import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.classification.util.CategoryManager;
import com.finance.tracker.classification.util.CSVImportManager;
import com.finance.tracker.classification.util.TransactionManager;
import com.finance.tracker.classification.view.TransactionForm;
import com.finance.tracker.classification.view.TransactionList;
import com.finance.tracker.integration.AIModuleFacade;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;
import javax.swing.*;

/**
 * Category Controller - Controls category selection and management
 */
public class CategoryController {
    private Category selectedCategory;
    private final TransactionManager transactionManager;
    private final CategoryManager categoryManager;
    private final CSVImportManager csvImportManager;
    private final TransactionList transactionList;
    private final java.util.List<CategorySelectionListener> listeners = new ArrayList<>();
    private CategoryType currentType = CategoryType.EXPENSE; // 当前分类类型
    
    /**
     * Constructor
     * 
     * @param transactionManager Transaction manager
     * @param transactionList Transaction list UI component (can be null)
     */
    public CategoryController(TransactionManager transactionManager, TransactionList transactionList) {
        this.transactionManager = transactionManager;
        this.transactionList = transactionList;
        this.categoryManager = transactionManager.getCategoryManager();
        this.csvImportManager = new CSVImportManager(categoryManager, transactionManager);
    }
    
    /**
     * 设置当前分类类型
     * 
     * @param type 分类类型
     */
    public void setCurrentType(CategoryType type) {
        this.currentType = type;
    }
    
    /**
     * 获取当前分类类型
     * 
     * @return 当前分类类型
     */
    public CategoryType getCurrentType() {
        return this.currentType;
    }
    
    /**
     * 设置分类选择监听器
     * 
     * @param listener 监听器
     */
    public void setSelectionListener(CategorySelectionListener listener) {
        this.listeners.clear();
        this.addCategorySelectionListener(listener);
    }
    
    /**
     * 从CSV文件导入交易记录
     * 
     * @param file CSV文件
     * @param form 交易表单
     * @return 导入的记录数量
     */
    public int importCSV(File file, TransactionForm form) {
        if (file == null) {
            return 0;
        }
        
        try {
            // 调用已有的CSV导入方法
            return importTransactionsFromCSV(file.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(form, 
                    "导入失败: " + e.getMessage(), 
                    "导入错误", 
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Handle category selection
     * 
     * @param category Selected category
     */
    public void selectCategory(Category category) {
        this.selectedCategory = category;
        
        // Notify listeners
        for (CategorySelectionListener listener : this.listeners) {
            listener.onCategorySelected(category);
        }
    }
    
    /**
     * Get currently selected category
     * 
     * @return Selected category
     */
    public Category getSelectedCategory() {
        return selectedCategory;
    }
    
    /**
     * Add category selection listener
     * 
     * @param listener Listener to add
     */
    public void addCategorySelectionListener(CategorySelectionListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    
    /**
     * Remove category selection listener
     * 
     * @param listener Listener to remove
     */
    public void removeCategorySelectionListener(CategorySelectionListener listener) {
        this.listeners.remove(listener);
    }
    
    /**
     * Get all categories of specified type
     * 
     * @param type Category type
     * @return List of categories
     */
    public java.util.List<Category> getCategoriesByType(CategoryType type) {
        return categoryManager.getCategoriesByType(type);
    }
    
    /**
     * Get a specific category by ID
     * 
     * @param id Category ID
     * @return Category or null if not found
     */
    public Category getCategoryById(int id) {
        return categoryManager.getCategoryById(id);
    }
    
    /**
     * Create a new transaction with the currently selected category
     * 
     * @param amount Transaction amount
     * @param description Transaction description
     * @return Created transaction
     */
    public Transaction createTransaction(BigDecimal amount, String description) {
        if (selectedCategory == null) {
            throw new IllegalStateException("No category selected");
        }
        
        Transaction t = new Transaction(selectedCategory, amount, description);
        
        // Add to transaction manager
        transactionManager.addTransaction(t);
        
        // Update transaction list if available
        if (transactionList != null) {
            transactionList.refresh();
        }
        
        // 同步到AI模型并更新推荐（通过TransactionManager自动完成，这里不需要额外操作）
        
        return t;
    }
    
    /**
     * Batch import transactions from CSV
     * 
     * @param csvFile CSV file path
     * @return Number of imported transactions
     * @throws IOException if there is an error reading the CSV file
     */
    public int importTransactionsFromCSV(String csvFile) throws IOException {
        java.util.List<Transaction> importedTransactions = csvImportManager.importTransactions(new File(csvFile));
        int count = importedTransactions.size();
        
        // Update transaction list if available
        if (transactionList != null) {
            transactionList.refresh();
        }
        
        // 显式触发AI推荐更新
        if (count > 0) {
            try {
                // 检查现有交易数据
                java.util.List<Transaction> existing = transactionManager.getAllTransactions();
                
                // 触发AI模块更新推荐
                AIModuleFacade.getInstance().syncAllTransactions(existing);
                
                System.out.println("已同步 " + count + " 条交易记录到AI模型并更新推荐");
            } catch (Exception e) {
                System.err.println("触发AI推荐更新失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return count;
    }
    
    /**
     * 批量创建交易记录（主要用于测试）
     * 
     * @param categoryId 分类ID
     * @param number 创建数量
     */
    public void createRandomTransactions(int categoryId, int number) {
        Category category = categoryManager.getCategoryById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Invalid category ID: " + categoryId);
        }
        
        Random random = new Random();
        
        for (int i = 0; i < number; i++) {
            BigDecimal amount = BigDecimal.valueOf(10 + random.nextInt(990));
            
            // 创建交易记录
            Transaction t = new Transaction(
                category,
                amount,
                "Test transaction #" + (i+1)
            );
            
            // 写入 TransactionManager
            transactionManager.addTransaction(t);
        }
        
        // 更新列表
        if (transactionList != null) {
            transactionList.refresh();
        }
        
        // 手动触发一次全量同步到AI模型
        try {
            java.util.List<Transaction> allTransactions = transactionManager.getAllTransactions();
            AIModuleFacade.getInstance().syncAllTransactions(allTransactions);
        } catch (Exception e) {
            System.err.println("同步到AI模型失败: " + e.getMessage());
            e.printStackTrace();
        }
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
}