package com.qmul.financetracker;

import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) {
        ReportAndNotificationService service = new ReportAndNotificationService();
        
        List<Transaction> transactions = Arrays.asList(
            new Transaction("2025-03-01", "餐饮", 500.0, "外卖"),
            new Transaction("2025-03-02", "交通", 200.0, "打车"),
            new Transaction("2025-03-03", "餐饮", 300.0, "聚餐")
        );

        // 导出CSV报告
        service.exportCSVReport(transactions, "monthly_report.csv");

        // 导出PDF报告 
        service.exportPDFReport(transactions, "monthly_report.pdf");

        // 生成预算通知
        String notification = service.generateBudgetAlertNotification(transactions, 300.0);
        service.saveNotificationToFile(notification, "budget_alert.txt");
    }
}