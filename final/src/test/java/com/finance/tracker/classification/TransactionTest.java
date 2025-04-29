package com.finance.tracker.classification;

import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.classification.util.CategoryManager;
import com.finance.tracker.classification.util.TransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    private CategoryManager categoryManager;
    private TransactionManager transactionManager;
    private Category incomeCategory;
    private Category expenseCategory;
    
    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        // 创建临时CSV文件
        File csvFile = new File(tempDir, "transactions.csv");
        
        // 初始化CategoryManager和TransactionManager
        categoryManager = new CategoryManager();
        transactionManager = new TransactionManager(categoryManager, csvFile.getAbsolutePath());
        
        // 创建测试用的分类
        incomeCategory = new Category(1, "Salary", CategoryType.INCOME, null);
        expenseCategory = new Category(2, "Food", CategoryType.EXPENSE, null);
        
        categoryManager.addCategory(incomeCategory);
        categoryManager.addCategory(expenseCategory);
    }

    @Test
    void testCreateTransaction() {
        // 创建收入交易
        Transaction incomeTransaction = new Transaction(
            incomeCategory,
            new BigDecimal("1000.00"),
            "Monthly salary"
        );
        
        // 验证交易属性
        assertEquals(incomeCategory, incomeTransaction.getCategory());
        assertEquals(new BigDecimal("1000.00"), incomeTransaction.getAmount());
        assertEquals("Monthly salary", incomeTransaction.getDescription());
        assertEquals(CategoryType.INCOME, incomeTransaction.getCategory().getType());
        
        // 创建支出交易
        Transaction expenseTransaction = new Transaction(
            expenseCategory,
            new BigDecimal("50.00"),
            "Lunch"
        );
        
        // 验证交易属性
        assertEquals(expenseCategory, expenseTransaction.getCategory());
        assertEquals(new BigDecimal("50.00"), expenseTransaction.getAmount());
        assertEquals("Lunch", expenseTransaction.getDescription());
        assertEquals(CategoryType.EXPENSE, expenseTransaction.getCategory().getType());
    }

    @Test
    void testAddAndGetTransaction() {
        // 创建并添加交易
        Transaction transaction = new Transaction(
            incomeCategory,
            new BigDecimal("1000.00"),
            "Monthly salary"
        );
        transactionManager.addTransaction(transaction);
        
        // 获取所有交易
        List<Transaction> transactions = transactionManager.getAllTransactions();
        
        // 验证
        assertEquals(1, transactions.size());
        assertEquals(transaction.getId(), transactions.get(0).getId());
        assertEquals(transaction.getAmount(), transactions.get(0).getAmount());
        assertEquals(transaction.getCategory(), transactions.get(0).getCategory());
    }

    @Test
    void testGetTransactionsByType() {
        // 添加收入和支出交易
        Transaction incomeTransaction = new Transaction(
            incomeCategory,
            new BigDecimal("1000.00"),
            "Monthly salary"
        );
        Transaction expenseTransaction = new Transaction(
            expenseCategory,
            new BigDecimal("50.00"),
            "Lunch"
        );
        
        transactionManager.addTransaction(incomeTransaction);
        transactionManager.addTransaction(expenseTransaction);
        
        // 获取收入交易
        List<Transaction> incomeTransactions = transactionManager.getTransactionsByType(CategoryType.INCOME);
        assertEquals(1, incomeTransactions.size());
        assertEquals(incomeTransaction.getId(), incomeTransactions.get(0).getId());
        
        // 获取支出交易
        List<Transaction> expenseTransactions = transactionManager.getTransactionsByType(CategoryType.EXPENSE);
        assertEquals(1, expenseTransactions.size());
        assertEquals(expenseTransaction.getId(), expenseTransactions.get(0).getId());
    }

    @Test
    void testGetTransactionsByCategory() {
        // 添加交易
        Transaction transaction = new Transaction(
            incomeCategory,
            new BigDecimal("1000.00"),
            "Monthly salary"
        );
        transactionManager.addTransaction(transaction);
        
        // 获取特定分类的交易
        List<Transaction> transactions = transactionManager.getTransactionsByCategory(incomeCategory);
        
        // 验证
        assertEquals(1, transactions.size());
        assertEquals(transaction.getId(), transactions.get(0).getId());
        assertEquals(incomeCategory.getId(), transactions.get(0).getCategory().getId());
    }

    @Test
    void testGetTotalIncomeAndExpense() {
        // 添加收入和支出交易
        Transaction incomeTransaction = new Transaction(
            incomeCategory,
            new BigDecimal("1000.00"),
            "Monthly salary"
        );
        Transaction expenseTransaction = new Transaction(
            expenseCategory,
            new BigDecimal("50.00"),
            "Lunch"
        );
        
        transactionManager.addTransaction(incomeTransaction);
        transactionManager.addTransaction(expenseTransaction);
        
        // 验证总收入
        assertEquals(new BigDecimal("1000.00"), transactionManager.getTotalIncome());
        
        // 验证总支出
        assertEquals(new BigDecimal("50.00"), transactionManager.getTotalExpense());
        
        // 验证余额
        assertEquals(new BigDecimal("950.00"), transactionManager.getBalance());
    }

    @Test
    void testDeleteTransaction() {
        // 添加交易
        Transaction transaction = new Transaction(
            incomeCategory,
            new BigDecimal("1000.00"),
            "Monthly salary"
        );
        transactionManager.addTransaction(transaction);
        
        // 验证交易已添加
        assertEquals(1, transactionManager.getAllTransactions().size());
        
        // 删除交易
        boolean deleted = transactionManager.deleteTransaction(transaction);
        
        // 验证删除结果
        assertTrue(deleted);
        assertEquals(0, transactionManager.getAllTransactions().size());
    }

    @Test
    void testTransactionSignedAmount() {
        // 创建收入交易
        Transaction incomeTransaction = new Transaction(
            incomeCategory,
            new BigDecimal("1000.00"),
            "Monthly salary"
        );
        
        // 创建支出交易
        Transaction expenseTransaction = new Transaction(
            expenseCategory,
            new BigDecimal("50.00"),
            "Lunch"
        );
        
        // 验证收入金额为正
        assertEquals(new BigDecimal("1000.00"), incomeTransaction.getSignedAmount());
        
        // 验证支出金额为负
        assertEquals(new BigDecimal("-50.00"), expenseTransaction.getSignedAmount());
    }
} 