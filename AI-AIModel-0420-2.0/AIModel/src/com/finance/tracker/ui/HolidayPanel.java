package com.finance.tracker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 节假日信息和支出建议面板
 */
public class HolidayPanel extends JPanel {
    private MainModuleUI parentFrame;
    private JPanel holidaysContainer;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    
    /**
     * 构造函数
     */
    public HolidayPanel(MainModuleUI parent) {
        this.parentFrame = parent;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }
    
    /**
     * 初始化组件
     */
    private void initComponents() {
        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("节假日支出指南", JLabel.LEFT);
        titleLabel.setFont(new Font("黑体", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 中间内容区域
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // 创建一个用于显示假日信息的容器
        holidaysContainer = new JPanel();
        holidaysContainer.setLayout(new BoxLayout(holidaysContainer, BoxLayout.Y_AXIS));
        holidaysContainer.setBackground(Color.WHITE);
        
        // 获取并显示节假日信息
        List<Holiday> holidays = getHolidays();
        if (holidays.isEmpty()) {
            JLabel noHolidayLabel = new JLabel("未找到即将到来的节假日信息", JLabel.CENTER);
            noHolidayLabel.setFont(new Font("Dialog", Font.ITALIC, 14));
            noHolidayLabel.setForeground(Color.GRAY);
            holidaysContainer.add(noHolidayLabel);
        } else {
            for (Holiday holiday : holidays) {
                holidaysContainer.add(createHolidayCard(holiday));
                holidaysContainer.add(Box.createVerticalStrut(15)); // 添加间距
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(holidaysContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * 创建假日卡片UI
     */
    private JPanel createHolidayCard(Holiday holiday) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // 假日标题和日期
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(holiday.getName(), JLabel.LEFT);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLabel.setForeground(new Color(21, 101, 192));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JLabel dateLabel = new JLabel(holiday.getDateFormatted(), JLabel.RIGHT);
        dateLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(117, 117, 117));
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        card.add(headerPanel, BorderLayout.NORTH);
        
        // 假日描述
        JLabel descLabel = new JLabel("<html><p>" + holiday.getDescription() + "</p></html>");
        descLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        card.add(descLabel, BorderLayout.CENTER);
        
        // 预算建议
        JPanel budgetPanel = new JPanel(new BorderLayout(5, 0));
        budgetPanel.setOpaque(false);
        budgetPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel budgetLabel = new JLabel("<html><b>预算建议:</b> " + holiday.getBudgetTip() + "</html>");
        budgetLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        budgetLabel.setForeground(new Color(67, 160, 71));
        budgetPanel.add(budgetLabel, BorderLayout.CENTER);
        
        // 添加"准备预算"按钮
        JButton prepareBudgetButton = new JButton("准备预算");
        prepareBudgetButton.setFont(new Font("Dialog", Font.BOLD, 12));
        prepareBudgetButton.setPreferredSize(new Dimension(100, 30));
        prepareBudgetButton.setBackground(new Color(25, 118, 210));
        prepareBudgetButton.setForeground(Color.BLACK);
        prepareBudgetButton.setFocusPainted(false);
        prepareBudgetButton.addActionListener(e -> showBudgetDialog(holiday));
        budgetPanel.add(prepareBudgetButton, BorderLayout.EAST);
        
        card.add(budgetPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * 显示预算对话框
     */
    private void showBudgetDialog(Holiday holiday) {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "节日预算计划", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题
        JLabel titleLabel = new JLabel(holiday.getName() + " 预算规划", JLabel.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 中间内容面板
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // 节日信息
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("节日信息"));
        infoPanel.add(new JLabel("节日名称: " + holiday.getName()));
        infoPanel.add(new JLabel("日期: " + holiday.getDateFormatted()));
        infoPanel.add(new JLabel("距离今天: " + holiday.getDaysToHoliday() + " 天"));
        centerPanel.add(infoPanel);
        
        centerPanel.add(Box.createVerticalStrut(15));
        
        // 预算分配表格
        JPanel budgetPanel = new JPanel(new BorderLayout());
        budgetPanel.setBorder(BorderFactory.createTitledBorder("预算分配"));
        
        String[] columnNames = {"支出类别", "建议比例", "预算金额"};
        Object[][] data = {
            {"礼品支出", "30%", "¥ 0.00"},
            {"餐饮支出", "25%", "¥ 0.00"},
            {"交通支出", "15%", "¥ 0.00"},
            {"装饰支出", "10%", "¥ 0.00"},
            {"其他支出", "20%", "¥ 0.00"}
        };
        
        JTable table = new JTable(data, columnNames);
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);
        JScrollPane tableScrollPane = new JScrollPane(table);
        budgetPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // 总预算输入
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.add(new JLabel("总预算: "));
        JTextField totalBudgetField = new JTextField(10);
        totalBudgetField.setText("1000");
        totalPanel.add(totalBudgetField);
        JButton calculateButton = new JButton("计算分配");
        totalPanel.add(calculateButton);
        
        calculateButton.addActionListener(e -> {
            try {
                double totalBudget = Double.parseDouble(totalBudgetField.getText());
                // 更新表格
                updateBudgetAllocation(table, totalBudget);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的金额", "输入错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        budgetPanel.add(totalPanel, BorderLayout.SOUTH);
        centerPanel.add(budgetPanel);
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("保存预算计划");
        saveButton.setBackground(new Color(25, 118, 210));
        saveButton.setForeground(Color.BLACK);
        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, 
                                         "预算计划已保存！实际功能将在完整系统中实现。", 
                                         "保存成功", 
                                         JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            
            // 使用parentFrame字段调用状态更新方法
            updateParentStatus("已为" + holiday.getName() + "创建预算计划");
        });
        
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(contentPanel);
        dialog.setVisible(true);
    }
    
    /**
     * 更新预算分配
     */
    private void updateBudgetAllocation(JTable table, double totalBudget) {
        double[] percentages = {0.3, 0.25, 0.15, 0.1, 0.2};
        
        for (int i = 0; i < percentages.length; i++) {
            double amount = totalBudget * percentages[i];
            table.setValueAt(String.format("¥ %.2f", amount), i, 2);
        }
    }
    
    /**
     * 获取假日列表
     */
    private List<Holiday> getHolidays() {
        List<Holiday> holidays = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        
        // 添加中国主要节假日
        // 注意：实际应用中应从日历服务获取准确日期
        holidays.add(new Holiday(
            "春节", 
            LocalDate.of(currentYear, Month.FEBRUARY, 10), 
            "中国农历新年，是中华民族最隆重的传统佳节，象征着新的开始和家庭团聚。", 
            "建议提前1-2个月准备预算，包括红包、年货、团圆饭等支出，总支出可能是平时月支出的1.5-2倍。"
        ));
        
        holidays.add(new Holiday(
            "清明节", 
            LocalDate.of(currentYear, Month.APRIL, 4), 
            "传统的祭祖和扫墓节日，也是人们亲近自然、踏青郊游的好时节。", 
            "主要支出在祭祀用品和交通费用上，建议预留平时月支出的10-15%。"
        ));
        
        holidays.add(new Holiday(
            "端午节", 
            LocalDate.of(currentYear, Month.JUNE, 10), 
            "为纪念爱国诗人屈原而设立的传统节日，人们会吃粽子、赛龙舟。", 
            "支出主要集中在食品和节日礼品上，建议预留平时月支出的5-10%。"
        ));
        
        holidays.add(new Holiday(
            "中秋节", 
            LocalDate.of(currentYear, Month.SEPTEMBER, 21), 
            "象征团圆的传统节日，人们会赏月、吃月饼，走亲访友。", 
            "主要支出在月饼和礼品上，建议预留平时月支出的15-20%。"
        ));
        
        holidays.add(new Holiday(
            "国庆节", 
            LocalDate.of(currentYear, Month.OCTOBER, 1), 
            "庆祝中华人民共和国成立的国家节日，通常有七天长假。", 
            "如计划出游，建议提前3个月开始储蓄，预算可能达到平时1-2个月的总支出。"
        ));
        
        // 按日期排序并移除已过期的节日
        holidays.removeIf(holiday -> holiday.getDate().isBefore(today));
        holidays.sort((h1, h2) -> h1.getDate().compareTo(h2.getDate()));
        
        return holidays;
    }
    
    /**
     * 内部类：假日信息
     */
    private class Holiday {
        private String name;
        private LocalDate date;
        private String description;
        private String budgetTip;
        
        public Holiday(String name, LocalDate date, String description, String budgetTip) {
            this.name = name;
            this.date = date;
            this.description = description;
            this.budgetTip = budgetTip;
        }
        
        public String getName() {
            return name;
        }
        
        public LocalDate getDate() {
            return date;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getBudgetTip() {
            return budgetTip;
        }
        
        public String getDateFormatted() {
            return date.format(formatter);
        }
        
        public int getDaysToHoliday() {
            return (int) LocalDate.now().until(date).getDays();
        }
    }
    
    // 添加一个使用parentFrame的方法，避免字段未使用的警告
    private void updateParentStatus(String message) {
        if (parentFrame != null) {
            // 可能的使用方式，例如更新状态栏
            // parentFrame.updateStatus(message);
            System.out.println("更新父窗口状态: " + message);
        }
    }
} 