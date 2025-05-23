package com.finance.tracker.report;

import java.awt.*;
import java.awt.print.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;

/**
 * A simple PDF generator that uses Java's printing API to create PDF files
 */
public class SimplePDFGenerator {
    
    private static final int MARGIN = 50;
    private static final Font TITLE_FONT = new Font("Serif", Font.BOLD, 18);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);
    private static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, 10);
    
    /**
     * Generate a PDF report from the transaction list
     * 
     * @param transactions the list of transactions
     * @param filename the output filename
     * @throws IOException if an error occurs
     */
    public static void generatePDF(List<Transaction> transactions, String filename) throws IOException {
        // Convert transactions to printable content
        List<String> lines = convertTransactionsToLines(transactions);
        
        // Create a printable object
        Printable printable = new Printable() {
            @Override
            public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                
                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pf.getImageableX(), pf.getImageableY());
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Print title
                g2d.setFont(TITLE_FONT);
                g2d.drawString("Financial Report", MARGIN, MARGIN);
                
                // Print content
                int y = MARGIN + 30;
                g2d.setFont(NORMAL_FONT);
                
                for (String line : lines) {
                    // Format headers with bold font
                    if (line.startsWith("===") || line.startsWith("---") || 
                        line.startsWith("Date") || line.startsWith("Total")) {
                        g2d.setFont(HEADER_FONT);
                    } else {
                        g2d.setFont(NORMAL_FONT);
                    }
                    
                    // Draw the text
                    g2d.drawString(line, MARGIN, y);
                    y += 15;
                }
                
                return PAGE_EXISTS;
            }
        };
        
        // Create a print job
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(printable);
        
        // Set the output stream to a file
        if (!filename.toLowerCase().endsWith(".pdf")) {
            filename += ".pdf";
        }
        
        // Since Java's PrinterJob doesn't directly support PDF output,
        // we create a message to guide the user on how to save to PDF 
        JOptionPane.showMessageDialog(null, 
            "Please select 'Save as PDF' or 'Microsoft Print to PDF' when the print dialog appears.\n" +
            "Save the file as: " + filename,
            "Save as PDF Instructions",
            JOptionPane.INFORMATION_MESSAGE);
        
        // Show print dialog and print
        try {
            if (job.printDialog()) {
                job.print();
            }
        } catch (PrinterException e) {
            throw new IOException("Error printing: " + e.getMessage(), e);
        }
    }
    
    /**
     * Convert transactions to a list of text lines for printing
     */
    private static List<String> convertTransactionsToLines(List<Transaction> transactions) {
        List<String> lines = new ArrayList<>();
        
        // Add header
        lines.add("=== Financial Report ===");
        lines.add("");
        lines.add("Date\tCategory\tAmount\tDescription");
        lines.add("----------------------------------------");
        
        // Calculate totals
        double totalIncome = 0.0;
        double totalExpense = 0.0;
        
        // Add transaction lines
        for (Transaction t : transactions) {
            String category = t.getCategory();
            double amount = t.getAmount();
            
            // Determine if income or expense (based on predefined income categories)
            boolean isIncome = ReportAndNotificationService.isIncomeCategory(category);
            if (isIncome) {
                totalIncome += amount;
            } else {
                totalExpense += amount;
            }
            
            lines.add(String.format("%s\t%s\t%.2f\t%s", 
                t.getDate(), category, amount, t.getNote()));
        }
        
        // Add summary
        lines.add("----------------------------------------");
        lines.add(String.format("Total Income: %.2f", totalIncome));
        lines.add(String.format("Total Expense: %.2f", totalExpense));
        lines.add(String.format("Balance: %.2f", totalIncome - totalExpense));
        lines.add("");
        lines.add("Report generated: " + java.time.LocalDateTime.now());
        
        return lines;
    }
} 