package com.finance.tracker.classification;

import com.finance.tracker.classification.controller.CategoryController;
import com.finance.tracker.classification.controller.CategorySelectionListener;
import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.classification.util.CategoryManager;
import com.finance.tracker.classification.util.TransactionManager;
import com.finance.tracker.classification.view.CategoryPanel;
import com.finance.tracker.classification.view.TransactionForm;
import com.finance.tracker.classification.view.TransactionList;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;

/**
 * Classification Module Entry Class - Interface called by the main program
 */
public class ClassificationModule {
    private CategoryController controller;
    private CategoryPanel panel;
    private TransactionManager transactionManager;
    private TransactionForm transactionForm;
    private TransactionList transactionList;
    private JPanel mainPanel;
    
    /**
     * Create a new instance of the classification module
     * 
     * @param csvFilePath CSV file path
     */
    public ClassificationModule(String csvFilePath) {
        // Initialize controller and manager
        controller = new CategoryController();
        CategoryManager categoryManager = new CategoryManager();
        transactionManager = new TransactionManager(categoryManager, csvFilePath);
        
        // Initialize views
        panel = new CategoryPanel(controller);
        transactionForm = new TransactionForm(panel, transactionManager, controller);
        transactionList = new TransactionList(transactionManager);
        
        // Set category selection listener
        panel.setCategorySelectionListener(category -> {
            transactionForm.updateSaveButtonState();
        });
        
        // Create main panel
        mainPanel = new JPanel(new BorderLayout());
        
        // Create split screen interface
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // Top half: category selection and transaction form
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(panel, BorderLayout.CENTER);
        topPanel.add(transactionForm, BorderLayout.SOUTH);
        
        // Bottom half: transaction record list
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(transactionList, BorderLayout.CENTER);
        
        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.5);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
    }
    
    /**
     * Get category selection panel
     * 
     * @return Category selection panel
     */
    public JPanel getPanel() {
        return mainPanel;
    }
    
    /**
     * Set category selection listener
     * 
     * @param listener Listener
     */
    public void setCategorySelectionListener(CategorySelectionListener listener) {
        panel.setCategorySelectionListener(listener);
    }
    
    /**
     * Get currently selected category
     * 
     * @return Selected category
     */
    public Category getSelectedCategory() {
        return controller.getSelectedCategory();
    }
    
    /**
     * Get current category type (income/expense)
     * 
     * @return Current category type
     */
    public CategoryType getCurrentType() {
        return panel.getCurrentType();
    }
    
    /**
     * Select specified category
     * 
     * @param category Category to select
     */
    public void selectCategory(Category category) {
        panel.selectCategory(category);
    }
    
    /**
     * Clear current selection
     */
    public void clearSelection() {
        panel.clearSelection();
    }
    
    /**
     * Switch to specified type
     * 
     * @param type Category type
     */
    public void switchToType(CategoryType type) {
        if (type == CategoryType.INCOME) {
            panel.loadCategories(CategoryType.INCOME);
        } else {
            panel.loadCategories(CategoryType.EXPENSE);
        }
    }
    
    /**
     * Get all transaction records
     * 
     * @return Transaction record list
     */
    public List<Transaction> getAllTransactions() {
        return transactionManager.getAllTransactions();
    }
    
    /**
     * Add new transaction record
     * 
     * @param category Category
     * @param amount Amount
     * @param description Description
     * @return Newly created transaction
     */
    public Transaction addTransaction(Category category, BigDecimal amount, String description) {
        Transaction transaction = new Transaction(category, amount, description);
        transactionManager.addTransaction(transaction);
        transactionList.refresh();
        return transaction;
    }
    
    /**
     * Get current balance
     * 
     * @return Balance
     */
    public BigDecimal getBalance() {
        return transactionManager.getBalance();
    }
    
    /**
     * Get total income
     * 
     * @return Total income
     */
    public BigDecimal getTotalIncome() {
        return transactionManager.getTotalIncome();
    }
    
    /**
     * Get total expense
     * 
     * @return Total expense
     */
    public BigDecimal getTotalExpense() {
        return transactionManager.getTotalExpense();
    }
    
    /**
     * Refresh transaction list
     */
    public void refreshTransactions() {
        transactionList.refresh();
    }
}