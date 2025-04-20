package com.finance.tracker.classification.view;

import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.classification.util.TransactionManager;
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
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JLabel balanceLabel;
    private JLabel incomeLabel;
    private JLabel expenseLabel;
    
    /**
     * Create transaction list
     * 
     * @param transactionManager Transaction record manager
     */
    public TransactionList(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        
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
        String[] columnNames = {"Time", "Type", "Category", "Amount", "Description"};
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
        deleteButton.addActionListener(e -> deleteSelectedTransaction());
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
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get all transaction records
        List<Transaction> transactions = transactionManager.getAllTransactions();
        
        // Add to the table
        for (Transaction t : transactions) {
            Object[] rowData = {
                t.getFormattedDateTime(),
                t.getCategory().getType() == CategoryType.INCOME ? "Income" : "Expense",
                t.getCategory().getName(),
                t.getAmount(),
                t.getDescription() != null ? t.getDescription() : ""
            };
            tableModel.addRow(rowData);
        }
        
        // Update summary information
        updateSummary();
    }

    /**
     * Load transaction records by type
     * 
     * @param type Category type
     */
    private void loadTransactionsByType(CategoryType type) {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get transaction records of specified type
        List<Transaction> transactions = transactionManager.getTransactionsByType(type);
        
        // Add to the table
        for (Transaction t : transactions) {
            Object[] rowData = {
                t.getFormattedDateTime(),
                t.getCategory().getType() == CategoryType.INCOME ? "Income" : "Expense",
                t.getCategory().getName(),
                t.getAmount(),
                t.getDescription() != null ? t.getDescription() : ""
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
        
        BigDecimal income = transactionManager.getTotalIncome();
        BigDecimal expense = transactionManager.getTotalExpense();
        BigDecimal balance = transactionManager.getBalance();
        
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
        loadTransactions();
    }
    
    /**
     * Delete selected transaction record
     */
    private void deleteSelectedTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a transaction record to delete",
                "No Selection",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        try {
            // Get the currently displayed transaction list
            List<Transaction> currentTransactions;
            
            // Determine which type of transaction records is currently displayed
            String typeText = (String) tableModel.getValueAt(selectedRow, 1);
            if (typeText.equals("Income")) {
                currentTransactions = transactionManager.getTransactionsByType(CategoryType.INCOME);
            } else if (typeText.equals("Expense")) {
                currentTransactions = transactionManager.getTransactionsByType(CategoryType.EXPENSE);
            } else {
                currentTransactions = transactionManager.getAllTransactions();
            }
            
            // Ensure we haven't gone out of bounds
            if (selectedRow >= currentTransactions.size()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Selected row is invalid, please refresh and try again",
                    "Delete Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            
            // Get the transaction record to delete
            Transaction transactionToDelete = currentTransactions.get(selectedRow);
            
            // Confirm deletion
            int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this transaction record?\n" + 
                "Type: " + typeText + "\n" +
                "Category: " + transactionToDelete.getCategory().getName() + "\n" +
                "Amount: " + transactionToDelete.getAmount() + "\n" +
                "Time: " + transactionToDelete.getFormattedDateTime() + "\n" +
                "Description: " + (transactionToDelete.getDescription() != null ? 
                        transactionToDelete.getDescription() : "None"),
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (option == JOptionPane.YES_OPTION) {
                // Execute deletion
                boolean success = transactionManager.deleteTransaction(transactionToDelete);
                
                if (success) {
                    // Remove from table
                    tableModel.removeRow(selectedRow);
                    // Update summary information
                    updateSummary();
                    JOptionPane.showMessageDialog(
                        this,
                        "Transaction record has been successfully deleted",
                        "Delete Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Failed to delete transaction record",
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
            
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
}