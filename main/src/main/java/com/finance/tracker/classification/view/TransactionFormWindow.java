package com.finance.tracker.classification.view;

import com.finance.tracker.classification.controller.CategoryController;
import com.finance.tracker.classification.util.CategoryManager;
import com.finance.tracker.classification.util.TransactionManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Transaction Form Window - Standalone window for transaction management
 */
public class TransactionFormWindow extends JFrame {
    
    /**
     * Creates a standalone transaction form window
     */
    public TransactionFormWindow() {
        // 设置窗口标题和关闭操作
        setTitle("Transaction Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // 初始化数据和控制器
        TransactionManager transactionManager = new TransactionManager(
            new CategoryManager(), 
            "data/transactions.csv"
        );
        
        TransactionList transactionList = new TransactionList(transactionManager);
        CategoryController categoryController = new CategoryController(transactionManager, transactionList);
        
        // 初始化分类面板
        CategoryPanel categoryPanel = new CategoryPanel(categoryController);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // 添加分类面板到顶部
        mainPanel.add(categoryPanel, BorderLayout.NORTH);
        
        // 创建交易表单面板
        TransactionForm transactionForm = new TransactionForm(categoryPanel, transactionManager, categoryController);
        
        // 添加表单和交易列表到主面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(transactionForm, BorderLayout.NORTH);
        centerPanel.add(transactionList, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // 设置内容面板
        setContentPane(mainPanel);
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TransactionFormWindow().setVisible(true);
        });
    }
} 