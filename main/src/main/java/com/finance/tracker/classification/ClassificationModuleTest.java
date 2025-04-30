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
        JFrame frame = new JFrame("Personal Finance Tracking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // 初始化管理器
        CategoryManager categoryManager = new CategoryManager();
        TransactionManager transactionManager = new TransactionManager(categoryManager, CSV_PATH);
    
        // 提前创建 TransactionList
        TransactionList transactionList = new TransactionList(transactionManager);
    
        // ✅ 把 transactionList 传给 CategoryController
        CategoryController categoryController = new CategoryController(transactionManager, transactionList);
    
        // 创建视图组件
        CategoryPanel categoryPanel = new CategoryPanel(categoryController);
        TransactionForm transactionForm = new TransactionForm(categoryPanel, transactionManager, categoryController);
    
        // 设置类别选择监听器
        categoryPanel.setCategorySelectionListener(new CategorySelectionListener() {
            @Override
            public void onCategorySelected(Category category) {
                transactionForm.updateSaveButtonState();
            }
        });
    
        // 创建上下分屏 UI
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(categoryPanel, BorderLayout.CENTER);
        topPanel.add(transactionForm, BorderLayout.SOUTH);
    
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(transactionList, BorderLayout.CENTER);
    
        mainSplitPane.setTopComponent(topPanel);
        mainSplitPane.setBottomComponent(bottomPanel);
        mainSplitPane.setDividerLocation(350);
        mainSplitPane.setResizeWeight(0.5);
    
        // 添加到窗口
        frame.getContentPane().add(mainSplitPane);
        frame.setSize(800, 700);
        frame.setLocationRelativeTo(null);
    
        System.out.println("Transaction data save location: " + new File(CSV_PATH).getAbsolutePath());
    
        frame.setVisible(true);
    }
    
}