package com.finance.tracker.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import com.finance.tracker.ai.Recommendation;
import com.finance.tracker.ai.RecommendationEngine;

/**
 * AI推荐面板 - 显示个性化财务建议
 */
public class RecommendationPanel extends JPanel {
    private MainModuleUI parentFrame;
    private JPanel recommendationsContainer;
    private JButton generateButton;
    private JLabel statusLabel;
    private JScrollPane scrollPane;
    private boolean generatingRecommendations = false;
    
    // 引入推荐引擎
    private RecommendationEngine recommendationEngine;
    
    /**
     * 构造函数
     */
    public RecommendationPanel(MainModuleUI parent) {
        this.parentFrame = parent;
        
        // 获取推荐引擎实例
        try {
            recommendationEngine = RecommendationEngine.getInstance();
        } catch (Exception e) {
            System.err.println("无法获取推荐引擎: " + e.getMessage());
            e.printStackTrace();
        }
        
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
        JLabel titleLabel = new JLabel("AI 财务建议", JLabel.LEFT);
        titleLabel.setFont(new Font("黑体", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        // 添加生成按钮
        generateButton = new JButton("生成新建议");
        generateButton.setFocusPainted(false);
        generateButton.addActionListener(e -> generateRecommendations());
        topPanel.add(generateButton, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 中间内容区域
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        // 创建一个用于显示建议的容器
        recommendationsContainer = new JPanel();
        recommendationsContainer.setLayout(new BoxLayout(recommendationsContainer, BoxLayout.Y_AXIS));
        recommendationsContainer.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(recommendationsContainer);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // 底部状态面板
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("准备就绪，点击\"生成新建议\"按钮获取个性化财务建议");
        statusLabel.setFont(new Font("Dialog", Font.ITALIC, 12));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        add(statusPanel, BorderLayout.SOUTH);
        
        // 设置默认内容
        setDefaultContent();
    }
    
    /**
     * 设置默认内容
     */
    private void setDefaultContent() {
        recommendationsContainer.removeAll();
        
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String welcomeText = "<html><div style='width: 400px; text-align: center;'>"
                + "<h2 style='color: #1976D2;'>欢迎使用 AI 财务助手</h2>"
                + "<p>AI财务助手可以根据您的消费习惯和预算情况提供个性化的财务建议。</p>"
                + "<p>点击\"生成新建议\"按钮开始获取您的个性化财务建议。</p>"
                + "<p style='color: #757575;'><i>提示：建议将展示在此面板中，您可以保存或复制需要的内容。</i></p>"
                + "</div></html>";
        
        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalGlue());
        
        recommendationsContainer.add(welcomePanel);
        recommendationsContainer.revalidate();
        recommendationsContainer.repaint();
    }
    
    /**
     * 生成推荐
     */
    private void generateRecommendations() {
        // 防止重复点击
        if (generatingRecommendations) {
            return;
        }
        
        // 显示生成状态
        statusLabel.setText("正在生成AI财务建议，请稍候...");
        generateButton.setEnabled(false);
        generatingRecommendations = true;
        
        // 清空现有内容
        recommendationsContainer.removeAll();
        JLabel loadingLabel = new JLabel("正在分析您的财务数据，请稍候...", JLabel.CENTER);
        loadingLabel.setFont(new Font("Dialog", Font.ITALIC, 14));
        recommendationsContainer.add(loadingLabel);
        recommendationsContainer.revalidate();
        recommendationsContainer.repaint();
        
        // 在后台线程生成建议
        new Thread(() -> {
            try {
                // 模拟API请求延迟
                Thread.sleep(1500);
                
                // 调用真实的推荐引擎API获取建议
                if (recommendationEngine != null) {
                    try {
                        // 生成新的AI推荐
                        recommendationEngine.generateAIPersonalizedRecommendations();
                        
                        // 获取生成的推荐
                        List<Recommendation> recommendations = recommendationEngine.getActiveRecommendations();
                        
                        // 转换为文本列表
                        List<String> recommendationMessages = new ArrayList<>();
                        for (Recommendation rec : recommendations) {
                            if (!rec.isDismissed()) {
                                recommendationMessages.add(rec.getMessage());
                            }
                        }
                        
                        // 在EDT线程中更新UI
                        SwingUtilities.invokeLater(() -> {
                            displayRecommendations(recommendationMessages);
                            generatingRecommendations = false;
                            generateButton.setEnabled(true);
                            statusLabel.setText("AI财务建议已生成，共 " + recommendationMessages.size() + " 条建议");
                            
                            // 通知主框架更新状态
                            if (parentFrame != null) {
                                parentFrame.setCurrency(parentFrame.getCurrentCurrency());
                            }
                        });
                    } catch (Exception e) {
                        System.err.println("调用推荐引擎时出错: " + e.getMessage());
                        e.printStackTrace();
                        
                        // 如果API调用失败，使用模拟数据
                        SwingUtilities.invokeLater(() -> {
                            displayRecommendations(getSimulatedRecommendations());
                            generatingRecommendations = false;
                            generateButton.setEnabled(true);
                            statusLabel.setText("无法连接到AI服务，已显示模拟建议");
                        });
                    }
                } else {
                    // 引擎为空时使用模拟数据
                    SwingUtilities.invokeLater(() -> {
                        displayRecommendations(getSimulatedRecommendations());
                        generatingRecommendations = false;
                        generateButton.setEnabled(true);
                        statusLabel.setText("无法获取推荐引擎，已显示模拟建议");
                    });
                }
            } catch (InterruptedException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("生成建议时出错: " + e.getMessage());
                    generatingRecommendations = false;
                    generateButton.setEnabled(true);
                });
            }
        }).start();
    }
    
    /**
     * 显示推荐
     */
    private void displayRecommendations(List<String> recommendations) {
        recommendationsContainer.removeAll();
        
        if (recommendations == null || recommendations.isEmpty()) {
            JLabel emptyLabel = new JLabel("未找到适合您的建议", JLabel.CENTER);
            emptyLabel.setFont(new Font("Dialog", Font.ITALIC, 14));
            recommendationsContainer.add(emptyLabel);
        } else {
            int index = 1;
            for (String recommendation : recommendations) {
                recommendationsContainer.add(createRecommendationCard(index++, recommendation));
                recommendationsContainer.add(Box.createVerticalStrut(15)); // 添加间隙
            }
        }
        
        recommendationsContainer.revalidate();
        recommendationsContainer.repaint();
        
        // 滚动到顶部
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }
    
    /**
     * 创建推荐卡片
     */
    private JPanel createRecommendationCard(int index, String content) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(getCardColor(index));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1, true),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // 标题
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("建议 " + index + " " + getEmojiForIndex(index), JLabel.LEFT);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        card.add(headerPanel, BorderLayout.NORTH);
        
        // 内容
        JLabel contentLabel = new JLabel("<html><p>" + content.replace("\n", "<br>") + "</p></html>");
        contentLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        card.add(contentLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * 根据索引获取卡片颜色
     */
    private Color getCardColor(int index) {
        switch (index % 4) {
            case 0: return new Color(232, 245, 233); // 淡绿色
            case 1: return new Color(227, 242, 253); // 淡蓝色
            case 2: return new Color(255, 243, 224); // 淡橙色
            case 3: return new Color(243, 229, 245); // 淡紫色
            default: return new Color(248, 248, 248); // 淡灰色
        }
    }
    
    /**
     * 根据索引获取表情符号
     */
    private String getEmojiForIndex(int index) {
        switch (index % 5) {
            case 0: return "💰";
            case 1: return "📊";
            case 2: return "💡";
            case 3: return "📈";
            case 4: return "🎯";
            default: return "✨";
        }
    }
    
    /**
     * 获取模拟数据（仅在API调用失败时使用）
     */
    private List<String> getSimulatedRecommendations() {
        List<String> recommendations = new ArrayList<>();
        
        recommendations.add("您在食品类别的支出占比较高 (35%)，建议控制在25%以内，可通过减少外卖次数节省开支。");
        recommendations.add("基于您的消费记录，建议增加储蓄比例至收入的20%，为未来大额支出做准备。");
        recommendations.add("即将到来的春节可能导致支出增加，建议提前规划预算，尤其关注礼品和交通支出类别。");
        recommendations.add("您的住房支出占收入的42%，超出合理范围 (30%)，建议寻找降低成本的方案。");
        recommendations.add("分析显示您对娱乐类别的消费呈上升趋势，建议设置每月限额以控制非必要支出。");
        
        return recommendations;
    }
} 