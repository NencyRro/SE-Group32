package com.finance.tracker.classification.view;

import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.classification.util.TransactionDataCenter;
import com.finance.tracker.classification.util.TransactionManager;
import com.finance.tracker.integration.AIModuleFacade;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Transaction List - Displays existing transaction records
 */
public class TransactionList extends JPanel {
    private TransactionManager transactionManager;
    private TransactionDataCenter dataCenter;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JLabel balanceLabel;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private List<Transaction> currentTransactions;
    private CategoryType currentFilterType = null;
    
    /**
     * Create transaction list
     * 
     * @param transactionManager Transaction record manager (将被保留用于兼容)
     */
    public TransactionList(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.dataCenter = TransactionDataCenter.getInstance();
        
        // 注册为数据变化监听器
        dataCenter.addTransactionChangeListener(new TransactionDataCenter.TransactionChangeListener() {
            @Override
            public void onTransactionDataChanged(TransactionDataCenter.ChangeType type, Transaction transaction) {
                // 当数据发生变化时刷新列表
                refresh();
            }
        });
        
        initializeUI();
        loadTransactions();
    }
    
    /**
     * Initialize user interface
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Transaction Records"));
        
        // Create summary panel to display total amount information
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.NORTH);
        
        // Create transaction record table
        String[] columnNames = {"Time", "Type", "Category", "Amount", "Description", "Import Time"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Do not allow direct editing of the table
            }
        };
        
        transactionTable = new JTable(tableModel);
        configureTransactionTable();
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Configure transaction table
     */
    private void configureTransactionTable() {
        // Set column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Time
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // Type
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Category
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Amount
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Description
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Import Time

        // Set row height
        transactionTable.setRowHeight(25);
        
        // Set renderer for amount column
        transactionTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            private final DecimalFormat df = new DecimalFormat("#,##0.00");
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // Get transaction type
                String type = (String) table.getValueAt(row, 1);
                
                // Set amount color: expenses in red, income in green
                if (type.equals("Expense")) {
                    setForeground(new Color(255, 50, 50));
                } else {
                    setForeground(new Color(0, 150, 0));
                }
                
                // Set text alignment
                setHorizontalAlignment(SwingConstants.RIGHT);
                
                // Format amount
                if (value instanceof BigDecimal) {
                    BigDecimal amount = (BigDecimal) value;
                    setText(df.format(amount));
                }
                
                return c;
            }
        });
    }
    
    /**
     * Create summary panel
     * 
     * @return Summary panel
     */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        balanceLabel = new JLabel("Balance: ¥0.00", JLabel.CENTER);
        balanceLabel.setFont(new Font(balanceLabel.getFont().getName(), Font.BOLD, 14));
        
        incomeLabel = new JLabel("Total Income: ¥0.00", JLabel.CENTER);
        incomeLabel.setForeground(new Color(0, 150, 0));
        
        expenseLabel = new JLabel("Total Expense: ¥0.00", JLabel.CENTER);
        expenseLabel.setForeground(new Color(255, 50, 50));
        
        panel.add(incomeLabel);
        panel.add(balanceLabel);
        panel.add(expenseLabel);
        
        return panel;
    }
    
    /**
     * Create button panel
     * 
     * @return Button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton viewAllButton = new JButton("Show All");
        JButton viewIncomeButton = new JButton("Income Only");
        JButton viewExpenseButton = new JButton("Expense Only");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");
        
        viewAllButton.addActionListener(e -> loadTransactions());
        viewIncomeButton.addActionListener(e -> loadTransactionsByType(CategoryType.INCOME));
        viewExpenseButton.addActionListener(e -> loadTransactionsByType(CategoryType.EXPENSE));
        deleteButton.addActionListener(e -> deleteSelectedTransactions());
        refreshButton.addActionListener(e -> refresh());
        
        panel.add(viewAllButton);
        panel.add(viewIncomeButton);
        panel.add(viewExpenseButton);
        panel.add(deleteButton);
        panel.add(refreshButton);
        
        return panel;
    }
    
    /**
     * Load all transaction records
     */
    public void loadTransactions() {
        loadTransactions(null);
    }

    /**
     * Load transaction records by type
     * 
     * @param type Category type
     */
    private void loadTransactionsByType(CategoryType type) {
        loadTransactions(type);
    }
    
    /**
     * Load transaction records
     * 
     * @param type Category type
     */
    private void loadTransactions(CategoryType type) {
        currentFilterType = type;
        
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get transaction records from the data center
        List<Transaction> transactions;
        if (type == null) {
            transactions = dataCenter.getAllTransactions();
        } else {
            transactions = dataCenter.getTransactionsByType(type);
        }
        
        // Store current transactions
        currentTransactions = transactions;
        
        // Add to the table
        for (Transaction t : transactions) {
            Object[] rowData = {
                t.getFormattedDateTime(),
                t.getCategory().getType() == CategoryType.INCOME ? "Income" : "Expense",
                t.getCategory().getName(),
                t.getAmount(),
                t.getDescription() != null ? t.getDescription() : "",
                t.getFormattedImportTimestamp()  
            };
            
            tableModel.addRow(rowData);
        }
        
        // Update summary information
        updateSummary();
    }
    
    /**
     * Update summary information
     */
    private void updateSummary() {
        DecimalFormat df = new DecimalFormat("¥#,##0.00");
        
        BigDecimal income = dataCenter.getTotalIncome();
        BigDecimal expense = dataCenter.getTotalExpense();
        BigDecimal balance = dataCenter.getBalance();
        
        incomeLabel.setText("Total Income: " + df.format(income));
        expenseLabel.setText("Total Expense: " + df.format(expense));
        
        balanceLabel.setText("Balance: " + df.format(balance));
        
        // Display negative balance in red
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            balanceLabel.setForeground(new Color(255, 50, 50));
        } else {
            balanceLabel.setForeground(new Color(0, 150, 0));
        }
    }
    
    /**
     * Refresh transaction records
     */
    public void refresh() {
        loadTransactions(currentFilterType);
    }
    
    /**
     * Delete selected transaction(s)
     */
    private void deleteSelectedTransactions() {
        int[] selectedRows = transactionTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(
                this,
                "Please select at least one transaction to delete",
                "No Selection",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        
        // 确认删除
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete " + selectedRows.length + " transaction(s)?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            // 从后往前删除，避免索引变化问题
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int modelRow = transactionTable.convertRowIndexToModel(selectedRows[i]);
                
                // 获取当前的交易记录
                if (modelRow >= 0 && modelRow < currentTransactions.size()) {
                    Transaction transaction = currentTransactions.get(modelRow);
                    
                    // 通过数据中心删除
                    dataCenter.deleteTransaction(transaction);
                    
                    // 从表格模型中删除
                    tableModel.removeRow(modelRow);
                }
            }
            
            // 显示成功消息
            JOptionPane.showMessageDialog(
                this,
                selectedRows.length + " transaction(s) deleted successfully",
                "Delete Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
            
            // 数据中心会自动处理同步和通知
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error occurred during deletion: " + e.getMessage(),
                "Delete Failed",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    /**
     * 将所有交易全量同步到AI模型
     */
    private void syncAllTransactionsToAI() {
        try {
            // 使用数据中心的方法
            dataCenter.syncAllTransactionsToAIModel();
            System.out.println("已全量同步交易到AI模型");
        } catch (Exception e) {
            System.err.println("全量同步到AI模型失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Update AI recommendations
     */
    private void updateAIRecommendations() {
        try {
            // 方法1：通过TransactionManager
            // transactionManager.updateAIRecommendations();
            
            // 方法2：直接调用AIModuleFacade
            AIModuleFacade.getInstance().generateRecommendations();
            
            System.out.println("已更新AI推荐");
        } catch (Exception e) {
            System.err.println("更新AI推荐失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}