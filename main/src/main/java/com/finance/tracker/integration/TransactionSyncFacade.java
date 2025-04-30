package com.finance.tracker.integration;

import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.profile.UserProfile;
import com.finance.tracker.ai.RecommendationEngine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 交易数据同步工具类 - 用于classification模块与AIModel数据模型之间的桥接
 */
public class TransactionSyncFacade {
    
    private static TransactionSyncFacade instance;
    private final UserProfile userProfile;
    private final RecommendationEngine recommendationEngine;
    
    /**
     * 获取单例实例
     */
    public static TransactionSyncFacade getInstance() {
        if (instance == null) {
            instance = new TransactionSyncFacade();
        }
        return instance;
    }
    
    /**
     * 私有构造函数
     */
    private TransactionSyncFacade() {
        userProfile = UserProfile.getInstance();
        recommendationEngine = RecommendationEngine.getInstance();
    }
    
    /**
     * 添加单个交易记录并同步到AIModel
     * 
     * @param transaction classification模块的交易记录
     */
    public void appendTransaction(Transaction transaction) {
        if (transaction == null) {
            return;
        }
        
        // 获取必要信息
        String category = transaction.getCategory().getName();
        BigDecimal amount = transaction.getAmount();
        String description = transaction.getDescription();
        boolean isIncome = transaction.getCategory().getType() == CategoryType.INCOME;
        LocalDate date = transaction.getDateTime().toLocalDate();
        
        // 记录到用户配置文件
        userProfile.recordTransaction(category, amount, date);
        
        // 打印调试信息
        System.out.println("已同步交易: " + (isIncome ? "收入" : "支出") + " - " + 
                          category + " - " + amount + " - " + date);
    }
    
    /**
     * 删除交易记录并同步到AIModel（注意：因为记录已存入，只能追加抵消记录）
     * 
     * @param transaction 要删除的交易记录
     * @return 是否成功添加抵消记录
     */
    public boolean deleteTransaction(Transaction transaction) {
        if (transaction == null) {
            return false;
        }
        
        try {
            // 获取交易信息
            String category = transaction.getCategory().getName();
            BigDecimal amount = transaction.getAmount().negate(); // 使用负值抵消原始交易
            LocalDate date = LocalDate.now(); // 使用当前日期
            
            // 添加抵消记录（因UserProfile不支持直接删除单个交易）
            userProfile.recordTransaction(category, amount, date);
            
            // 打印调试信息
            System.out.println("已添加抵消交易: " + category + " - " + amount + " - " + date);
            return true;
        } catch (Exception e) {
            System.err.println("同步删除交易失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 同步所有交易记录从classification模块到AIModel
     * 
     * @param transactions 所有交易记录列表
     */
    public void syncAllTransactionsFromClassificationModule(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return;
        }
        
        // 清空现有记录（使用UserProfile中新增的清空方法）
        System.out.println("正在清空现有交易记录历史...");
        boolean cleared = userProfile.clearTransactionHistory();
        if (!cleared) {
            System.err.println("警告：清空现有交易记录失败，将继续同步（可能导致重复记录）");
        }
        
        // 添加所有交易
        for (Transaction transaction : transactions) {
            appendTransaction(transaction);
        }
        
        // 重新计算推荐
        updateRecommendations();
        
        System.out.println("全量同步完成，共同步 " + transactions.size() + " 条记录");
    }
    
    /**
     * 更新AIModel推荐
     */
    public void updateRecommendations() {
        try {
            AIModuleFacade.getInstance().generateRecommendations();
            System.out.println("已刷新AI推荐");
        } catch (Exception e) {
            System.err.println("更新AI推荐失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 转换单个交易记录从classification模型到profile模型
     * 
     * @param transaction classification交易记录
     * @return profile交易记录
     */
    private com.finance.tracker.profile.Transaction convertToProfileTransaction(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        // 获取必要信息
        String category = transaction.getCategory().getName();
        BigDecimal amount = transaction.getAmount();
        LocalDate date = transaction.getDateTime().toLocalDate();
        
        // 创建并返回profile交易记录
        return new com.finance.tracker.profile.Transaction(category, amount, date);
    }
} 