package com.finance.tracker.classification.view;

import com.finance.tracker.classification.controller.CategoryController;
import com.finance.tracker.classification.util.CategoryManager;
import com.finance.tracker.classification.util.TransactionManager;
import com.finance.tracker.localization.CurrencyManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Transaction Form Window - Standalone window for transaction management
 */
public class TransactionFormWindow extends JFrame {
    
    private CurrencyManager currencyManager;
    
    /**
     * Creates a standalone transaction form window
     */
    public TransactionFormWindow() {
        // 设置窗口标题和关闭操作
        setTitle("Transaction Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // 初始化货币管理器
        currencyManager = CurrencyManager.getInstance();
        
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
     * 刷新货币设置，用于当全局货币被更改时调用
     */
    public void refreshCurrencySettings() {
        System.out.println("TransactionFormWindow: refreshing currency settings");
        
        try {
            // 获取所有子组件并通知它们刷新货币设置
            Component[] components = getContentPane().getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    notifyAllComponentsOfCurrencyChange((JPanel)comp);
                }
            }
            
            // 刷新窗口标题，添加当前货币信息
            String currencyCode = currencyManager.getDefaultCurrency().getCode();
            String currencySymbol = currencyManager.getDefaultCurrency().getSymbol();
            setTitle("Transaction Management - Currency: " + currencyCode + " " + currencySymbol);
            
            // 重绘整个窗口
            revalidate();
            repaint();
        } catch (Exception e) {
            System.err.println("Error refreshing currency settings in TransactionFormWindow: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * A more robust implementation for notifying currency changes
     */
    private void notifyAllComponentsOfCurrencyChange(Container container) {
        try {
            // 遍历所有子组件
            Component[] components = container.getComponents();
            for (Component comp : components) {
                // 针对不同类型的组件调用相应的刷新方法
                if (comp instanceof TransactionList) {
                    System.out.println("Refreshing TransactionList");
                    ((TransactionList) comp).refresh();
                } else if (comp instanceof TransactionForm) {
                    System.out.println("Refreshing TransactionForm");
                    ((TransactionForm) comp).refreshCurrencyDisplay();
                } else if (comp instanceof JTable) {
                    // 表格需要重绘以更新货币显示
                    ((JTable) comp).repaint();
                } else if (comp instanceof JLabel) {
                    // 检查标签是否包含货币信息
                    JLabel label = (JLabel) comp;
                    if (label.getText() != null && 
                        (label.getText().contains("$") || 
                         label.getText().contains("¥") || 
                         label.getText().contains("€") || 
                         label.getText().contains("£") || 
                         label.getText().contains("HK$"))) {
                        label.repaint();
                    }
                } else if (comp instanceof Container) {
                    // 递归处理所有子容器
                    notifyAllComponentsOfCurrencyChange((Container) comp);
                }
            }
        } catch (Exception e) {
            System.err.println("Error notifying components: " + e.getMessage());
        }
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