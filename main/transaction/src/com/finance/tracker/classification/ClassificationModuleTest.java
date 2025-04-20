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

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Classification Module Test Class - Used for independent testing of classification module functionality
 */
public class ClassificationModuleTest {
    
    // CSV file path
    private static final String CSV_PATH = "transactions.csv";
    
    public static void main(String[] args) {
        // Run Swing UI in the event dispatch thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Set local system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Create test window
            createAndShowTestWindow();
        });
    }
    
    /**
     * Create and display test window
     */
    private static void createAndShowTestWindow() {
        // Create main window
        JFrame frame = new JFrame("Personal Finance Tracking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Initialize managers
        CategoryManager categoryManager = new CategoryManager();
        CategoryController categoryController = new CategoryController();
        TransactionManager transactionManager = new TransactionManager(categoryManager, CSV_PATH);
        
        // Create category selection panel
        CategoryPanel categoryPanel = new CategoryPanel(categoryController);
        
        // Create transaction form - used to add new transaction records
        TransactionForm transactionForm = new TransactionForm(categoryPanel, transactionManager, categoryController);
        
        // Create transaction list - used to view existing transaction records
        TransactionList transactionList = new TransactionList(transactionManager);
        
        // Set transaction selection listener
        categoryPanel.setCategorySelectionListener(new CategorySelectionListener() {
            @Override
            public void onCategorySelected(Category category) {
                // When a category is selected, update transaction form state
                transactionForm.updateSaveButtonState();
            }
        });
        
        // Create split screen interface
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        // Top half: category selection and transaction form
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(categoryPanel, BorderLayout.CENTER);
        topPanel.add(transactionForm, BorderLayout.SOUTH);
        
        // Bottom half: transaction record list
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(transactionList, BorderLayout.CENTER);
        
        mainSplitPane.setTopComponent(topPanel);
        mainSplitPane.setBottomComponent(bottomPanel);
        mainSplitPane.setDividerLocation(350);
        mainSplitPane.setResizeWeight(0.5);
        
        // Add to window
        frame.getContentPane().add(mainSplitPane);
        
        // Set window size and position
        frame.setSize(800, 700);
        frame.setLocationRelativeTo(null);
        
        // Display file path information
        System.out.println("Transaction data save location: " + new File(CSV_PATH).getAbsolutePath());
        
        // Display window
        frame.setVisible(true);
    }
}