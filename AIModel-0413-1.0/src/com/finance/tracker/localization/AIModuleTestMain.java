package com.finance.tracker.localization;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main test class that can be used to run the module standalone
 */
public class AIModuleTestMain {
    public static void main(String[] args) {
        // Set up look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and show main frame
        javax.swing.JFrame frame = new javax.swing.JFrame("AI财务助手 - 本地化模块测试");
        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        
        // 创建简单测试面板
        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("AI财务助手本地化测试", JLabel.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // 添加按钮控制面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        // 创建生成建议按钮
        JButton generateButton = new JButton("生成AI财务建议");
        generateButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateButton.setEnabled(false);
                generateButton.setText("正在生成建议...");
                
                // 模拟后台处理
                new Thread(() -> {
                    try {
                        // 模拟API请求延迟
                        Thread.sleep(1500);
                        
                        // 在EDT线程中更新UI
                        SwingUtilities.invokeLater(() -> {
                            generateButton.setEnabled(true);
                            generateButton.setText("生成AI财务建议");
                            
                            // 显示模拟的AI建议
                            JOptionPane.showMessageDialog(frame,
                                "您的个性化财务建议：\n\n" +
                                "- 您在食品类别的支出占比较高 (35%)，建议控制在25%以内，可通过减少外卖次数节省开支。\n\n" +
                                "- 基于您的消费记录，建议增加储蓄比例至收入的20%，为未来大额支出做准备。\n\n" +
                                "- 即将到来的春节可能导致支出增加，建议提前规划预算，尤其关注礼品和交通支出类别。\n\n" +
                                "- 您的住房支出占收入的42%，超出合理范围 (30%)，建议寻找降低成本的方案。\n\n" +
                                "- 分析显示您对娱乐类别的消费呈上升趋势，建议设置每月限额以控制非必要支出。",
                                "AI财务建议",
                                JOptionPane.INFORMATION_MESSAGE);
                        });
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> {
                            generateButton.setEnabled(true);
                            generateButton.setText("生成AI财务建议");
                            JOptionPane.showMessageDialog(frame,
                                "生成建议时出错: " + ex.getMessage(),
                                "错误",
                                JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }).start();
            }
        });
        buttonPanel.add(generateButton);
        
        // 添加显示节假日按钮
        JButton holidayButton = new JButton("查看近期节假日");
        holidayButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        holidayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, 
                    "近期节假日及财务建议：\n\n" +
                    "即将到来的节日：\n" +
                    "- 春节：2月10日至16日（15天后）\n" +
                    "- 清明节：4月4日至6日（68天后）\n" +
                    "- 劳动节：5月1日至5日（95天后）\n\n" +
                    "节日预算建议：\n" +
                    "即将迎来春节，这是全年消费最高的时期。建议：\n" +
                    "1. 提前预留约2.5倍于平时的预算\n" +
                    "2. 优先规划年货、礼品和红包支出\n" +
                    "3. 关注促销活动，避免节日期间高价购物\n",
                    "节假日预算建议", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(holidayButton);
        
        // 添加货币切换按钮
        JButton currencyButton = new JButton("切换货币单位");
        currencyButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        currencyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"人民币 (¥)", "美元 ($)", "欧元 (€)", "英镑 (£)"};
                int choice = JOptionPane.showOptionDialog(frame,
                    "请选择默认货币单位：",
                    "货币设置",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
                
                if (choice >= 0) {
                    JOptionPane.showMessageDialog(frame,
                        "货币单位已切换为：" + options[choice] + "\n" +
                        "所有金额将以此货币显示。",
                        "设置成功",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        buttonPanel.add(currencyButton);
        
        // 添加区域设置按钮
        JButton regionButton = new JButton("设置地区");
        regionButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        regionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"中国大陆", "香港", "台湾", "新加坡"};
                int choice = JOptionPane.showOptionDialog(frame,
                    "请选择您所在的地区：",
                    "区域设置",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
                
                if (choice >= 0) {
                    JOptionPane.showMessageDialog(frame,
                        "区域已设置为：" + options[choice] + "\n" +
                        "系统将根据您所在区域的节假日和消费习惯提供个性化建议。",
                        "设置成功",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        buttonPanel.add(regionButton);
        
        // 添加测试交易按钮
        JButton addTransactionButton = new JButton("添加测试交易");
        addTransactionButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        addTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] categories = {"食品", "住房", "交通", "娱乐", "购物", "医疗", "教育"};
                
                int choice = JOptionPane.showOptionDialog(frame,
                    "请选择交易类别：",
                    "添加测试交易",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    categories,
                    categories[0]);
                
                if (choice >= 0) {
                    String amountStr = JOptionPane.showInputDialog(frame,
                        "请输入交易金额（元）：",
                        "500");
                    
                    if (amountStr != null && !amountStr.trim().isEmpty()) {
                        try {
                            double amount = Double.parseDouble(amountStr);
                            JOptionPane.showMessageDialog(frame,
                                "已添加交易：\n类别：" + categories[choice] + "\n金额：" + amount + " 元\n日期：今天",
                                "添加成功",
                                JOptionPane.INFORMATION_MESSAGE);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(frame,
                                "无效的金额格式",
                                "错误",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        buttonPanel.add(addTransactionButton);
        
        // 创建选项卡面板，集成所有我们创建的面板
        JTabbedPane tabbedPane = new JTabbedPane();
        
        try {
            // 尝试创建并添加各个面板
            tabbedPane.addTab("AI财务建议", new JLabel("无法加载面板：RecommendationPanel未正确编译"));
            tabbedPane.addTab("节日支出计划", new JLabel("无法加载面板：HolidayPanel未正确编译"));
            tabbedPane.addTab("货币设置", new JLabel("无法加载面板：CurrencyPanel未正确编译"));
            tabbedPane.addTab("地区设置", new JLabel("无法加载面板：RegionPanel未正确编译"));
            tabbedPane.addTab("交易记录", new JLabel("无法加载面板：TransactionPanel未正确编译"));
            
            // 如果不确定有哪些面板已经编译成功，使用原始按钮面板作为备用
            tabbedPane.addTab("原始测试面板", buttonPanel);
        } catch (Exception e) {
            System.err.println("创建面板时出错: " + e.getMessage());
            tabbedPane.addTab("原始测试面板", buttonPanel);
        }
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        frame.getContentPane().add(mainPanel);
        
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}