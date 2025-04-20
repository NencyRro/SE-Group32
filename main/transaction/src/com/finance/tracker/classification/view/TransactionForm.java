package com.finance.tracker.classification.view;

import com.finance.tracker.classification.controller.CategoryController;
import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.classification.util.TransactionManager;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

/**
 * Transaction Form - Used to add new transaction records
 */
public class TransactionForm extends JPanel {
    private CategoryPanel categoryPanel;
    private JTextField amountField;
    private JTextField descriptionField;
    private JButton saveButton;
    private JButton clearButton;
    private TransactionManager transactionManager;
    private CategoryController categoryController;
    
    /**
     * Create transaction form
     * 
     * @param categoryPanel Category selection panel
     * @param transactionManager Transaction record manager
     * @param categoryController Category controller
     */
    public TransactionForm(CategoryPanel categoryPanel, 
                          TransactionManager transactionManager,
                          CategoryController categoryController) {
        this.categoryPanel = categoryPanel;
        this.transactionManager = transactionManager;
        this.categoryController = categoryController;
        
        initializeUI();
    }
    
    /**
     * Initialize user interface
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Add New Transaction"));
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Amount label and input field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel amountLabel = new JLabel("Amount:");
        formPanel.add(amountLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        // Create improved amount input field
        amountField = createAmountField();
        amountField.setColumns(15);
        formPanel.add(amountField, gbc);
        
        // Description label and input field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel descLabel = new JLabel("Description:");
        formPanel.add(descLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        descriptionField = new JTextField(20);
        formPanel.add(descriptionField, gbc);
        
        // Add category tip
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel tipLabel = new JLabel("Please select a category from above");
        tipLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(tipLabel, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        saveButton = new JButton("Save");
        clearButton = new JButton("Clear");
        
        saveButton.addActionListener(e -> saveTransaction());
        clearButton.addActionListener(e -> clearForm());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Initially disable save button
        updateSaveButtonState();
    }
    
    /**
     * Create improved amount input field
     * 
     * @return Amount input field
     */
    private JTextField createAmountField() {
        JTextField field = new JTextField();
        
        // Add text document filter
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                // Only allow inserting digits and decimal point
                if (isValidAmountInput(fb.getDocument().getText(0, fb.getDocument().getLength()) + string)) {
                    super.insertString(fb, offset, string, attr);
                    updateSaveButtonState();
                }
            }
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                // Calculate complete text after replacement
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String resultText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                
                // Validate text after replacement
                if (isValidAmountInput(resultText)) {
                    super.replace(fb, offset, length, text, attrs);
                    updateSaveButtonState();
                }
            }
            
            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                super.remove(fb, offset, length);
                updateSaveButtonState();
            }
            
            /**
             * Validate amount input
             */
            private boolean isValidAmountInput(String text) {
                // Empty text is valid
                if (text == null || text.isEmpty()) {
                    return true;
                }
                
                // Check if valid number format
                String regex = "^\\d*\\.?\\d{0,2}$"; // Allow integers or up to two decimal places
                if (!text.matches(regex)) {
                    return false;
                }
                
                // Check that it doesn't start with multiple zeros
                if (text.length() > 1 && text.startsWith("0") && text.charAt(1) != '.') {
                    return false;
                }
                
                return true;
            }
        });
        
        // Add focus listener for highlighting
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                SwingUtilities.invokeLater(() -> {
                    field.selectAll();
                });
            }
        });
        
        // Add tooltip
        field.setToolTipText("Enter amount, supports up to two decimal places");
        
        return field;
    }
    
    /**
     * Save transaction record
     */
    private void saveTransaction() {
        // Get selected category
        Category selectedCategory = categoryPanel.getSelectedCategory();
        
        if (selectedCategory == null) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a category", 
                    "Category Required", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get amount
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Please enter an amount", 
                    "Amount Required", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Parse amount
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText);
            
            // Check if amount is greater than zero
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, 
                        "Please enter an amount greater than zero", 
                        "Invalid Amount", 
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Please enter a valid amount", 
                    "Invalid Amount", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get description
        String description = descriptionField.getText().trim();
        
        // Create and save transaction record
        Transaction transaction = new Transaction(
                selectedCategory, 
                amount,
                description.isEmpty() ? null : description
        );
        
        transactionManager.addTransaction(transaction);
        
        // Display success message and format amount
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String formattedAmount = df.format(amount);
        String typeStr = selectedCategory.getType() == CategoryType.INCOME ? "Income" : "Expense";
        
        JOptionPane.showMessageDialog(this, 
                "Added new " + typeStr + " record!\n" +
                "Category: " + selectedCategory.getName() + "\n" +
                "Amount: " + formattedAmount,
                "Save Successful", 
                JOptionPane.INFORMATION_MESSAGE);
        
        // Clear form
        clearForm();
    }
    
    /**
     * Clear form
     */
    private void clearForm() {
        amountField.setText("");
        descriptionField.setText("");
        categoryPanel.clearSelection();
        updateSaveButtonState();
    }
    
    /**
     * Update save button state
     */
    public void updateSaveButtonState() {
        boolean hasCategory = categoryPanel.getSelectedCategory() != null;
        boolean hasValidAmount = false;
        
        String amountText = amountField.getText().trim();
        if (!amountText.isEmpty()) {
            try {
                BigDecimal amount = new BigDecimal(amountText);
                hasValidAmount = amount.compareTo(BigDecimal.ZERO) > 0;
            } catch (NumberFormatException e) {
                // Parse error, amount invalid
                hasValidAmount = false;
            }
        }
        
        saveButton.setEnabled(hasCategory && hasValidAmount);
    }
}