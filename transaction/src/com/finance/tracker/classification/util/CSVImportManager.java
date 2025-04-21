package com.finance.tracker.classification.util;

import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CSVImportManager {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private CategoryManager categoryManager;
    private TransactionManager transactionManager;

    // 构造器中引入两个管理类实例
    public CSVImportManager(CategoryManager categoryManager, TransactionManager transactionManager) {
        this.categoryManager = categoryManager;
        this.transactionManager = transactionManager;
    }

    // 导入CSV并保存交易记录
    public List<Transaction> importAndSaveTransactions(File csvFile) throws IOException {
        List<Transaction> importedTransactions = importTransactions(csvFile);
        List<Transaction> existingTransactions = transactionManager.getAllTransactions();
        
        List<Transaction> processedTransactions = preprocessTransactions(importedTransactions, existingTransactions);
        
        for (Transaction transaction : processedTransactions) {
            transactionManager.addTransaction(transaction);
        }
        
        return processedTransactions;
    }

    // 从CSV导入交易记录
    public List<Transaction> importTransactions(File csvFile) throws IOException {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            br.readLine(); // 跳过CSV标题行
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                Transaction transaction = parseTransaction(values);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }

    // 解析每一条CSV交易记录
    private Transaction parseTransaction(String[] values) {
        try {
            if (values.length < 6) {
                System.err.println("CSV数据不完整：" + Arrays.toString(values));
                return null;
            }

            String id = values[0];
            LocalDateTime dateTime = LocalDateTime.parse(values[1], DATE_FORMATTER);
            int categoryId = Integer.parseInt(values[2]);
            CategoryType categoryType = CategoryType.valueOf(values[3]);
            BigDecimal amount = new BigDecimal(values[4]);
            String description = values[5].replaceAll("^\"|\"$", "").replace("\"\"", "\"");

            Category category = categoryManager.getCategoryById(categoryId);
            if (category == null) {
                category = new Category(categoryId, "未知类别", categoryType, null);
                categoryManager.addCategory(category);
            }

            return new Transaction(id, dateTime, category, amount, description);
        } catch (Exception e) {
            System.err.println("解析交易数据错误：" + e.getMessage());
            return null;
        }
    }

    // 数据预处理：去重和格式化
    public List<Transaction> preprocessTransactions(List<Transaction> imported, List<Transaction> existing) {
        Set<String> existingIds = new HashSet<>();
        for (Transaction t : existing) {
            existingIds.add(t.getId());
        }

        List<Transaction> processed = new ArrayList<>();
        for (Transaction t : imported) {
            if (!existingIds.contains(t.getId())) {
                formatTransaction(t);
                processed.add(t);
            }
        }
        return processed;
    }

    // 数据格式化
    private void formatTransaction(Transaction transaction) {
        BigDecimal amount = transaction.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
        transaction.setAmount(amount);

        String description = transaction.getDescription();
        if (description != null) {
            transaction.setDescription(description.trim().replaceAll("[\\r\\n]", " "));
        }
    }
}
