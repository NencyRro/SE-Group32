package com.qmul.financetracker;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;

public class ReportAndNotificationService {

    public void exportCSVReport(List<Transaction> transactions, String filename) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            writer.writeNext(new String[]{"日期", "类别", "金额", "备注"});
            
            for (Transaction transaction : transactions) {
                writer.writeNext(new String[]{
                    transaction.getDate().toString(),
                    transaction.getCategory(),
                    String.valueOf(transaction.getAmount()),
                    transaction.getNote()
                });
            }
            System.out.println("CSV报告已导出：" + filename);
        } catch (IOException e) {
            System.err.println("导出CSV报告时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void exportPDFReport(List<Transaction> transactions, String filename) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("财务报告", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            PdfPTable table = new PdfPTable(4);
            table.addCell("日期");
            table.addCell("类别");
            table.addCell("金额");
            table.addCell("备注");

            for (Transaction transaction : transactions) {
                table.addCell(transaction.getDate().toString());
                table.addCell(transaction.getCategory());
                table.addCell(String.valueOf(transaction.getAmount()));
                table.addCell(transaction.getNote());
            }
            
            document.add(table);
            System.out.println("PDF报告已导出：" + filename);
        } catch (Exception e) {
            System.err.println("导出PDF报告时发生错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    public String generateBudgetAlertNotification(List<Transaction> transactions, double budgetLimit) {
        Map<String, Double> categorySpending = transactions.stream()
            .collect(Collectors.groupingBy(
                Transaction::getCategory, 
                Collectors.summingDouble(Transaction::getAmount)
            ));

        StringBuilder alerts = new StringBuilder("预算提醒：\n");
        boolean hasAlert = false;

        for (Map.Entry<String, Double> entry : categorySpending.entrySet()) {
            if (entry.getValue() > budgetLimit) {
                alerts.append(String.format("警告：%s类别支出 %.2f 元，已超过预算 %.2f 元\n", 
                    entry.getKey(), entry.getValue(), budgetLimit));
                hasAlert = true;
            }
        }

        return hasAlert ? alerts.toString() : "目前所有类别支出正常，未超过预算。";
    }

    public void saveNotificationToFile(String notification, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(notification);
            System.out.println("通知已保存：" + filename);
        } catch (IOException e) {
            System.err.println("保存通知时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }
}