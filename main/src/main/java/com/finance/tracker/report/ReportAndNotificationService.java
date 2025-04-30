package com.finance.tracker.report;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 简化版报告和通知服务 - 不使用外部PDF和CSV库
 */
public class ReportAndNotificationService {

    // 定义常见的收入类别
    private static final Set<String> INCOME_CATEGORIES = new HashSet<>(Arrays.asList(
        "Salary", "工资", "薪资", "Income", "收入", "Bonus", "奖金", "Investment", "投资收益", 
        "Interest", "利息", "Dividend", "分红", "Gift", "礼金", "Refund", "退款"
    ));

    /**
     * 导出CSV报告 - 简化版直接使用FileWriter
     */
    public void exportCSVReport(List<Transaction> transactions, String filename) throws IOException {
        if (!filename.toLowerCase().endsWith(".csv")) {
            filename += ".csv";
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // 写入CSV头
            writer.println("日期,类别,金额,备注");
            
            // 写入交易记录
            for (Transaction transaction : transactions) {
                writer.println(
                    transaction.getDate() + "," +
                    transaction.getCategory() + "," +
                    transaction.getAmount() + "," +
                    escapeCSV(transaction.getNote())
                );
            }
        }
    }
    
    /**
     * 导出PDF报告 - 使用SimplePDFGenerator
     * @return 输出的文件名
     */
    public String exportPDFReport(List<Transaction> transactions, String filename) throws IOException {
        // 确保文件有.pdf扩展名
        if (!filename.toLowerCase().endsWith(".pdf")) {
            filename += ".pdf";
        }
        
        // 使用PDF生成器创建PDF文件
        SimplePDFGenerator.generatePDF(transactions, filename);
        
        // 返回文件名
        return filename;
    }
    
    /**
     * 生成预算提醒通知 - 只检查支出类别
     */
    public String generateBudgetAlertNotification(List<Transaction> transactions, double budgetLimit) {
        // 过滤出支出类别的交易（排除收入类别）
        List<Transaction> expenseTransactions = transactions.stream()
            .filter(t -> !isIncomeCategory(t.getCategory()))
            .collect(Collectors.toList());
            
        Map<String, Double> categorySpending = expenseTransactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCategory, 
                Collectors.summingDouble(Transaction::getAmount)
            ));

        StringBuilder alerts = new StringBuilder("Budget Alert:\n");
        boolean hasAlert = false;

        for (Map.Entry<String, Double> entry : categorySpending.entrySet()) {
            if (entry.getValue() > budgetLimit) {
                alerts.append(String.format("Warning: %s category spending %.2f exceeds budget limit %.2f\n", 
                    entry.getKey(), entry.getValue(), budgetLimit));
                hasAlert = true;
            }
        }

        return hasAlert ? alerts.toString() : "All expense categories are within budget limits.";
    }
    
    /**
     * 判断某个类别是否为收入类别
     */
    public static boolean isIncomeCategory(String category) {
        if (category == null) return false;
        
        // 检查类别名称是否包含在预定义的收入类别中
        for (String incomeCategory : INCOME_CATEGORIES) {
            if (category.toLowerCase().contains(incomeCategory.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 保存通知到文件
     */
    public void saveNotificationToFile(String notification, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(notification);
        }
    }
    
    /**
     * 转义CSV字段中的特殊字符
     */
    private String escapeCSV(String field) {
        if (field == null) {
            return "";
        }
        // 如果字段包含逗号、双引号或换行符，需要用双引号包围并将内部双引号替换为两个双引号
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
} 