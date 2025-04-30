package com.finance.tracker.statistics;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.classification.model.Transaction;

/**
 * 统计模块启动器 - 负责启动统计功能GUI
 */
public class StatisticsLauncher extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private List<Transaction> transactions;
    private JPanel chartPanel;
    
    /**
     * 创建统计模块启动器
     * 
     * @param transactions 交易数据列表
     */
    public StatisticsLauncher(List<Transaction> transactions) {
        this.transactions = transactions;
        setLayout(new BorderLayout());
        initializeChartPanel();
    }
    
    /**
     * 初始化图表面板
     */
    private void initializeChartPanel() {
        try {
            // 创建临时JSON数据文件
            File tempDir = new File("temp");
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }
            
            File jsonFile = new File("temp/finance_data.json");
            generateJSONFile(jsonFile);
            
            // 创建内部图表面板
            chartPanel = createChartPanel(jsonFile.getAbsolutePath());
            add(chartPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error initializing chart panel: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * 生成JSON数据文件
     * 
     * @param file 输出文件
     * @throws Exception 如果写入失败
     */
    private void generateJSONFile(File file) throws Exception {
        // 创建JSON对象结构
        JSONObject rootObj = new JSONObject();
        rootObj.put("year", LocalDateTime.now().getYear());
        
        // 处理交易数据，按月分组
        Map<String, JSONObject> monthData = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            LocalDateTime dateTime = transaction.getDateTime();
            String month = dateTime.format(DateTimeFormatter.ofPattern("MM"));
            
            // 如果这个月的数据不存在，创建它
            if (!monthData.containsKey(month)) {
                JSONObject monthObj = new JSONObject();
                monthObj.put("month", month);
                monthObj.put("income", 0.0);
                monthObj.put("total_expenses", 0.0);
                monthObj.put("balance", 0.0);
                monthObj.put("expenses", new JSONArray());
                
                monthData.put(month, monthObj);
            }
            
            JSONObject monthObj = monthData.get(month);
            
            // 处理收入或支出
            BigDecimal amount = transaction.getAmount();
            CategoryType type = transaction.getCategory().getType();
            
            if (type == CategoryType.INCOME) {
                // 收入：增加月收入
                double currentIncome = (double) monthObj.get("income");
                monthObj.put("income", currentIncome + amount.doubleValue());
            } else {
                // 支出：添加到支出列表
                JSONArray expenses = (JSONArray) monthObj.get("expenses");
                
                JSONObject expenseObj = new JSONObject();
                expenseObj.put("category", transaction.getCategory().getName());
                expenseObj.put("amount", amount.doubleValue());
                
                expenses.add(expenseObj);
                
                // 更新总支出
                double currentExpenses = (double) monthObj.get("total_expenses");
                monthObj.put("total_expenses", currentExpenses + amount.doubleValue());
            }
            
            // 更新余额
            double income = (double) monthObj.get("income");
            double expenses = (double) monthObj.get("total_expenses");
            monthObj.put("balance", income - expenses);
        }
        
        // 创建bills数组
        JSONArray billsArray = new JSONArray();
        for (JSONObject monthObj : monthData.values()) {
            billsArray.add(monthObj);
        }
        
        rootObj.put("bills", billsArray);
        
        // 写入JSON文件
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(rootObj.toJSONString());
        }
    }
    
    /**
     * 创建图表面板
     * 
     * @param jsonPath JSON数据文件路径
     * @return 图表面板
     * @throws Exception 如果创建失败
     */
    private JPanel createChartPanel(String jsonPath) throws Exception {
        // 使用反射加载和初始化FinanceAppGUI
        // 通过修改FinanceData加载路径来使用我们生成的JSON
        
        try {
            // 加载FinanceAppGUI类
            Class<?> appClass = Class.forName("com.financeapp.FinanceAppGUI");
            
            // 创建一个JPanel容器
            JPanel containerPanel = new JPanel(new BorderLayout());
            
            // 创建FinanceAppGUI实例
            Object appInstance = appClass.getDeclaredConstructor().newInstance();
            
            // 获取其内容面板
            if (appInstance instanceof JFrame) {
                JFrame frame = (JFrame) appInstance;
                frame.setVisible(false); // 不显示JFrame
                
                // 将内容面板添加到我们的容器中
                containerPanel.add(frame.getContentPane(), BorderLayout.CENTER);
                
                // 获取setJsonPath方法并调用它
                try {
                    // 先尝试找到设置路径的方法
                    java.lang.reflect.Method setPathMethod = appClass.getMethod("setJsonPath", String.class);
                    setPathMethod.invoke(appInstance, jsonPath);
                } catch (NoSuchMethodException e) {
                    // 如果没有这样的方法，可能需要直接设置字段或使用其他方式
                    System.err.println("Warning: Could not set JSON path: " + e.getMessage());
                }
                
                return containerPanel;
            } else {
                throw new ClassCastException("FinanceAppGUI is not a JFrame");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Failed to initialize chart panel: " + e.getMessage());
        }
    }
    
    /**
     * 启动统计图表模块
     * 
     * @param transactions 交易数据
     */
    public static void launchStatisticsModule(List<Transaction> transactions) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Finance Statistics Dashboard");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            
            StatisticsLauncher launcher = new StatisticsLauncher(transactions);
            frame.getContentPane().add(launcher);
            
            frame.setVisible(true);
        });
    }
} 