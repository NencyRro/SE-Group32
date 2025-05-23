package com.finance.tracker.classification.util;

import com.finance.tracker.classification.model.Category;
import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;
import com.finance.tracker.integration.TransactionSyncFacade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 交易数据中心 - 统一数据访问点
 * 
 * 所有模块都应该通过此类进行交易数据的读取和修改
 * 遵循单例模式确保全局唯一
 */
public class TransactionDataCenter {
    // 单例实例
    private static TransactionDataCenter instance;
    
    // 内部数据存储
    private final List<Transaction> transactions = new ArrayList<>();
    
    // 数据源管理
    private TransactionManager transactionManager;
    
    // 观察者列表
    private final List<TransactionChangeListener> listeners = new CopyOnWriteArrayList<>();
    
    /**
     * 私有构造函数确保单例
     */
    private TransactionDataCenter() {
        // 私有构造方法，防止外部创建实例
    }
    
    /**
     * 初始化数据中心，设置底层数据源
     * 
     * @param transactionManager 底层事务管理器
     */
    public void initialize(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        refreshData();
    }
    
    /**
     * 获取单例实例
     * 
     * @return TransactionDataCenter单例
     */
    public static synchronized TransactionDataCenter getInstance() {
        if (instance == null) {
            instance = new TransactionDataCenter();
        }
        return instance;
    }
    
    /**
     * 从底层存储刷新数据
     */
    public void refreshData() {
        if (transactionManager != null) {
            synchronized (transactions) {
                transactions.clear();
                transactions.addAll(transactionManager.getAllTransactions());
            }
            notifyListeners(ChangeType.REFRESH, null);
        }
    }
    
    /**
     * 添加交易记录
     * 
     * @param transaction 交易记录
     */
    public void addTransaction(Transaction transaction) {
        if (transactionManager != null) {
            transactionManager.addTransaction(transaction);
            synchronized (transactions) {
                transactions.add(transaction);
            }
            notifyListeners(ChangeType.ADD, transaction);
        }
    }
    
    /**
     * 删除交易记录
     * 
     * @param transaction 要删除的交易记录
     * @return 是否成功删除
     */
    public boolean deleteTransaction(Transaction transaction) {
        if (transactionManager != null && transactionManager.deleteTransaction(transaction)) {
            synchronized (transactions) {
                transactions.remove(transaction);
            }
            notifyListeners(ChangeType.DELETE, transaction);
            return true;
        }
        return false;
    }
    
    /**
     * 获取所有交易记录
     * 
     * @return 交易记录列表（不可修改）
     */
    public List<Transaction> getAllTransactions() {
        synchronized (transactions) {
            List<Transaction> sortedList = new ArrayList<>(transactions);
            sortedList.sort(Comparator.comparing(Transaction::getDateTime).reversed());
            return Collections.unmodifiableList(sortedList);
        }
    }
    
    /**
     * 根据类型获取交易记录
     * 
     * @param type 类别类型
     * @return 交易记录列表
     */
    public List<Transaction> getTransactionsByType(CategoryType type) {
        List<Transaction> result = new ArrayList<>();
        synchronized (transactions) {
            for (Transaction t : transactions) {
                if (t.getCategory().getType() == type) {
                    result.add(t);
                }
            }
        }
        
        // 按时间排序（降序）
        result.sort(Comparator.comparing(Transaction::getDateTime).reversed());
        return result;
    }
    
    /**
     * 根据类别获取交易记录
     * 
     * @param category 类别
     * @return 交易记录列表
     */
    public List<Transaction> getTransactionsByCategory(Category category) {
        List<Transaction> result = new ArrayList<>();
        synchronized (transactions) {
            for (Transaction t : transactions) {
                if (t.getCategory().getId() == category.getId()) {
                    result.add(t);
                }
            }
        }
        
        // 按时间排序（降序）
        result.sort(Comparator.comparing(Transaction::getDateTime).reversed());
        return result;
    }
    
    /**
     * 计算总收入
     * 
     * @return 总收入金额
     */
    public BigDecimal getTotalIncome() {
        BigDecimal total = BigDecimal.ZERO;
        synchronized (transactions) {
            for (Transaction t : transactions) {
                if (t.getCategory().getType() == CategoryType.INCOME) {
                    total = total.add(t.getAmount());
                }
            }
        }
        return total;
    }
    
    /**
     * 计算总支出
     * 
     * @return 总支出金额
     */
    public BigDecimal getTotalExpense() {
        BigDecimal total = BigDecimal.ZERO;
        synchronized (transactions) {
            for (Transaction t : transactions) {
                if (t.getCategory().getType() == CategoryType.EXPENSE) {
                    total = total.add(t.getAmount());
                }
            }
        }
        return total;
    }
    
    /**
     * 计算余额
     * 
     * @return 当前余额
     */
    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpense());
    }
    
    /**
     * 添加交易数据变更监听器
     * 
     * @param listener 监听器
     */
    public void addTransactionChangeListener(TransactionChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * 移除交易数据变更监听器
     * 
     * @param listener 监听器
     */
    public void removeTransactionChangeListener(TransactionChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * 通知所有监听器数据变更
     * 
     * @param type 变更类型
     * @param transaction 相关交易对象
     */
    private void notifyListeners(ChangeType type, Transaction transaction) {
        for (TransactionChangeListener listener : listeners) {
            listener.onTransactionDataChanged(type, transaction);
        }
    }
    
    /**
     * 交易数据变更类型
     */
    public enum ChangeType {
        ADD, DELETE, UPDATE, REFRESH
    }
    
    /**
     * 交易数据变更监听器接口
     */
    public interface TransactionChangeListener {
        /**
         * 数据变更通知方法
         * 
         * @param type 变更类型
         * @param transaction 变更的交易对象（仅对ADD/DELETE/UPDATE有效，REFRESH为null）
         */
        void onTransactionDataChanged(ChangeType type, Transaction transaction);
    }
    
    /**
     * 获取底层管理器
     * 
     * @return 交易管理器
     */
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
    
    /**
     * 同步所有交易到AI模块
     */
    public void syncAllTransactionsToAIModel() {
        if (transactionManager != null) {
            transactionManager.syncAllTransactionsToAIModel();
        }
    }
    
    /**
     * 更新AI推荐
     */
    public void updateAIRecommendations() {
        if (transactionManager != null) {
            transactionManager.updateAIRecommendations();
        }
    }
    
    /**
     * 获取分类管理器
     * 
     * @return 分类管理器
     */
    public CategoryManager getCategoryManager() {
        if (transactionManager != null) {
            return transactionManager.getCategoryManager();
        }
        return null;
    }
} 