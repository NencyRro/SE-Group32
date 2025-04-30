package com.finance.module.ai;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.classification.util.TransactionDataCenter;
import com.finance.tracker.localization.CurrencyManager;

public class FinanceAnalyzer extends JFrame {
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JTextArea resultArea;
    private JDialog expenseDetailsDialog;  // 保存对话框引用以便后续刷新

    public FinanceAnalyzer() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Financial Analyzer");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // Details menu
        JMenu detailMenu = new JMenu("Details");
        JMenuItem viewDetailsItem = new JMenuItem("View Details");
        viewDetailsItem.addActionListener(this::showDetails);
        detailMenu.add(viewDetailsItem);

        menuBar.add(detailMenu);
        setJMenuBar(menuBar);

        // Main UI layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        mainPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        add(mainPanel);
    }

    private void showDetails(ActionEvent e) {
        expenseDetailsDialog = new JDialog(this, "Expense Details", true);
        expenseDetailsDialog.setSize(600, 400);

        JPanel panel = new JPanel(new BorderLayout());

        // Table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Description");
        tableModel.addColumn("Amount");
        tableModel.addColumn("Date");

        dataTable = new JTable(tableModel);

        // Load data
        loadTransactionData();

        JButton analyzeBtn = new JButton("Analyze");
        analyzeBtn.addActionListener(this::performAnalysis);

        panel.add(new JScrollPane(dataTable), BorderLayout.CENTER);
        panel.add(analyzeBtn, BorderLayout.SOUTH);

        expenseDetailsDialog.add(panel);
        expenseDetailsDialog.setVisible(true);
    }

    private void loadTransactionData() {
        try {
            // Get transaction data from TransactionDataCenter
            List<Transaction> transactions = TransactionDataCenter.getInstance().getAllTransactions();
            
            // Get current currency information
            CurrencyManager currencyManager = CurrencyManager.getInstance();
            String currentCurrency = currencyManager.getDefaultCurrency().getCode();
            
            // Add transaction data to table
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            for (Transaction transaction : transactions) {
                // Only process expense type transactions
                if (transaction.getCategory().getType() == CategoryType.EXPENSE) {
                    String description = transaction.getDescription();
                    if (description == null || description.trim().isEmpty()) {
                        description = transaction.getCategory().getName();
                    }
                    
                    String formattedDate = transaction.getDateTime().format(formatter);
                    
                    // Convert transaction amount to current currency
                    BigDecimal amount = transaction.getAmount();
                    // Assuming transaction currency is stored or default is CNY
                    String transactionCurrency = "CNY"; // Default base currency
                    
                    // Convert amount to current display currency if different
                    if (!transactionCurrency.equals(currentCurrency)) {
                        amount = currencyManager.convert(amount, transactionCurrency, currentCurrency);
                    }
                    
                    // Format the amount with currency symbol
                    String formattedAmount = currencyManager.format(amount, currentCurrency);
                    
                    tableModel.addRow(new Object[]{
                        description, 
                        formattedAmount, 
                        formattedDate
                    });
                }
            }
            
            // If no expense transactions, add sample data
            if (tableModel.getRowCount() == 0) {
                // Format sample data with current currency
                BigDecimal sample1 = new BigDecimal("100.00");
                BigDecimal sample2 = new BigDecimal("150.00");
                BigDecimal sample3 = new BigDecimal("200.00");
                
                tableModel.addRow(new Object[]{"Sample Data", currencyManager.format(sample1, currentCurrency), "2025-04-01"});
                tableModel.addRow(new Object[]{"Sample Data", currencyManager.format(sample2, currentCurrency), "2025-04-05"});
                tableModel.addRow(new Object[]{"Sample Data", currencyManager.format(sample3, currentCurrency), "2025-04-10"});
            }
        } catch (Exception e) {
            String errorMsg = "Failed to read transaction data: " + e.getMessage() + "\n";
            JOptionPane.showMessageDialog(this, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void performAnalysis(ActionEvent e) {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data available for analysis", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground()  {
                try {
                    // Build expense data in natural language format
                    StringBuilder spendingData = new StringBuilder();
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        spendingData.append(String.format(
                                "[%s] spent %s on %s ",
                                tableModel.getValueAt(i, 0),
                                tableModel.getValueAt(i, 1).toString(),
                                tableModel.getValueAt(i, 2)
                        ));
                    }

                    // Build the complete request content in English
                    String userQuestion = "Based on the following expense records, please provide: " +
                            "1. Recommended monthly budget " +
                            "2. Reasonable savings target " +
                            "3. Potential cost reduction areas " +
                            "Expense records: " + spendingData;

                    // Construct JSON for DeepSeek API
                    String jsonRequest = String.format(
                            "{" +
                                    "\"model\": \"deepseek-chat\"," +
                                    "\"messages\": [{" +
                                    "   \"role\": \"user\"," +
                                    "   \"content\": \"%s\"" +
                                    "}]" +
                                    "}",
                            userQuestion.replace("\"", "\\\"")  // Escape double quotes
                    );

                    // API configuration
                    String apiUrl = "https://api.deepseek.com/v1/chat/completions";
                    String apiKey = "sk-9fc3f1a52c3143989fa3457ccf857b10";

                    URL url = new URL(apiUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Authorization", "Bearer " + apiKey);
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(150000);
                    conn.setReadTimeout(100000);

                    // Send request body
                    try (DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                        os.write(jsonRequest.getBytes(StandardCharsets.UTF_8));
                    }

                    // Handle response
                    int responseCode = conn.getResponseCode();
                    System.out.println("Response Code: " + responseCode);

                    BufferedReader br;
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    } else {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                    }

                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    // Parse the JSON response
                    String analysisResult = parseApiResponse(response.toString());
                    System.out.println(analysisResult);
                    publish("Analysis Result:\n" + analysisResult);

                } catch (Exception e) {
                    publish("API call failed: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }

            // Parse API response
            private String parseApiResponse(String jsonResponse) {
                try {
                    JSONObject responseJson = new JSONObject(jsonResponse);
                    JSONArray choices = responseJson.getJSONArray("choices");
                    if (choices.length() > 0) {
                        return choices.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                    }
                    return "No valid response received";
                } catch (Exception e) {
                    return "Failed to parse response: " + e.getMessage();
                }
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    resultArea.append(chunk + "\n");
                }
            }

            @Override
            protected void done() {
                resultArea.append("\n\nAnalysis completed");
            }
        }.execute();
    }

    /**
     * 刷新界面显示的货币
     */
    public void refreshCurrencyDisplay() {
        if (tableModel != null) {
            // 清除并重新加载交易数据，应用当前的货币设置
            tableModel.setRowCount(0);
            loadTransactionData();
            
            // 刷新表格UI
            if (dataTable != null) {
                dataTable.repaint();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            FinanceAnalyzer app = new FinanceAnalyzer();
            app.setLocationRelativeTo(null); // Center window
            app.setVisible(true);
        });
    }
} 