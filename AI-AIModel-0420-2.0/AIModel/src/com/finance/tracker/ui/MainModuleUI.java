package com.finance.tracker.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 主界面UI类 - 基于现代化UI设计
 */
public class MainModuleUI extends JFrame {
    // 常量定义
    private static final String TITLE = "AI 财务助手";
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private static final Color SECONDARY_COLOR = new Color(66, 66, 66);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    
    // UI组件
    private JPanel contentPanel;
    private JPanel menuPanel;
    private JPanel mainContentPanel;
    private JPanel statusBar;
    private CardLayout cardLayout;
    
    // 功能面板
    private RecommendationPanel recommendationPanel;
    private HolidayPanel holidayPanel;
    private CurrencyPanel currencyPanel;
    private RegionPanel regionPanel;
    private TransactionPanel transactionPanel;
    
    // 状态信息
    private String currentRegion = "中国大陆";
    private String currentCurrency = "人民币 (¥)";
    private LocalDateTime lastRecommendationTime = null;
    
    // 面板名称常量
    private static final String RECOMMENDATION_PANEL = "recommendation";
    private static final String HOLIDAY_PANEL = "holiday";
    private static final String CURRENCY_PANEL = "currency";
    private static final String REGION_PANEL = "region";
    private static final String TRANSACTION_PANEL = "transaction";
    
    /**
     * 构造函数
     */
    public MainModuleUI() {
        // 设置窗口基本属性
        setTitle(TITLE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // 设置现代化外观
        setupLookAndFeel();
        
        // 初始化界面组件
        initComponents();
        
        // 显示窗口
        setVisible(true);
    }
    
    /**
     * 设置现代化UI外观
     */
    private void setupLookAndFeel() {
        try {
            // 尝试设置FlatLaf现代主题
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            // 如果FlatLaf不可用，回退到系统外观
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // 自定义UI属性
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("Panel.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 8);
        UIManager.put("ScrollBar.width", 12);
    }
    
    /**
     * 初始化界面组件
     */
    private void initComponents() {
        // 设置内容面板
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        setContentPane(contentPanel);
        
        // 创建顶部标题栏
        JPanel titleBar = createTitleBar();
        contentPanel.add(titleBar, BorderLayout.NORTH);
        
        // 创建中间内容区域
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        // 创建左侧菜单
        menuPanel = createMenuPanel();
        centerPanel.add(menuPanel, BorderLayout.WEST);
        
        // 创建主内容区域（使用CardLayout实现切换）
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 初始化功能面板
        initFunctionalPanels();
        
        centerPanel.add(mainContentPanel, BorderLayout.CENTER);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        // 创建底部状态栏
        statusBar = createStatusBar();
        contentPanel.add(statusBar, BorderLayout.SOUTH);
    }
    
    /**
     * 创建顶部标题栏
     */
    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setPreferredSize(new Dimension(WIDTH, 60));
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel(TITLE);
        titleLabel.setFont(new Font("黑体", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        titleBar.add(titleLabel, BorderLayout.CENTER);
        
        return titleBar;
    }
    
    /**
     * 创建左侧菜单面板
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, HEIGHT));
        panel.setBackground(new Color(240, 240, 240));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR));
        
        // 添加标题
        JLabel menuTitle = new JLabel("功能导航", SwingConstants.CENTER);
        menuTitle.setFont(new Font("黑体", Font.BOLD, 16));
        menuTitle.setForeground(SECONDARY_COLOR);
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(menuTitle);
        
        // 创建菜单按钮映射
        Map<String, String> menuItems = new HashMap<>();
        menuItems.put("recommendation", "生成 AI 财务建议");
        menuItems.put("holiday", "查看近期节假日");
        menuItems.put("currency", "切换货币单位");
        menuItems.put("region", "设置地区");
        menuItems.put("transaction", "添加测试交易");
        
        // 添加菜单按钮
        for (Map.Entry<String, String> entry : menuItems.entrySet()) {
            JButton button = createMenuButton(entry.getKey(), entry.getValue());
            panel.add(button);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // 填充剩余空间
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    /**
     * 创建菜单按钮
     */
    private JButton createMenuButton(String key, String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setPreferredSize(new Dimension(180, 40));
        
        // 设置样式
        button.setFont(new Font("Dialog", Font.PLAIN, 14));
        button.setBackground(new Color(250, 250, 250));
        button.setForeground(SECONDARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        
        // 添加点击事件
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(mainContentPanel, key);
                
                // 模拟更新状态栏
                if (key.equals("recommendation")) {
                    lastRecommendationTime = LocalDateTime.now();
                    updateStatusBar();
                } else if (key.equals("currency") || key.equals("region")) {
                    updateStatusBar();
                }
            }
        });
        
        return button;
    }
    
    /**
     * 创建底部状态栏
     */
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        panel.setPreferredSize(new Dimension(WIDTH, 30));
        
        JLabel statusLabel = new JLabel("就绪 | 地区: " + currentRegion + " | 货币: " + currentCurrency);
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        panel.add(statusLabel, BorderLayout.WEST);
        
        JLabel timeLabel = new JLabel("上次生成建议: 暂无");
        timeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        panel.add(timeLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * 更新状态栏信息
     */
    private void updateStatusBar() {
        JLabel statusLabel = (JLabel) ((BorderLayout) statusBar.getLayout()).getLayoutComponent(BorderLayout.WEST);
        statusLabel.setText("就绪 | 地区: " + currentRegion + " | 货币: " + currentCurrency);
        
        JLabel timeLabel = (JLabel) ((BorderLayout) statusBar.getLayout()).getLayoutComponent(BorderLayout.EAST);
        if (lastRecommendationTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            timeLabel.setText("上次生成建议: " + lastRecommendationTime.format(formatter));
        } else {
            timeLabel.setText("上次生成建议: 暂无");
        }
    }
    
    /**
     * 初始化功能面板
     */
    private void initFunctionalPanels() {
        try {
            // 创建各个功能面板
            recommendationPanel = new RecommendationPanel(this);
            holidayPanel = new HolidayPanel(this);
            currencyPanel = new CurrencyPanel(this);
            regionPanel = new RegionPanel(this);
            transactionPanel = new TransactionPanel(this);
            
            // 将面板添加到卡片布局中
            mainContentPanel.add(recommendationPanel, RECOMMENDATION_PANEL);
            mainContentPanel.add(holidayPanel, HOLIDAY_PANEL);
            mainContentPanel.add(currencyPanel, CURRENCY_PANEL);
            mainContentPanel.add(regionPanel, REGION_PANEL);
            mainContentPanel.add(transactionPanel, TRANSACTION_PANEL);
            
            // 默认显示推荐面板
            cardLayout.show(mainContentPanel, RECOMMENDATION_PANEL);
            
            System.out.println("所有功能面板初始化完成");
        } catch (Exception e) {
            System.err.println("初始化功能面板时出错: " + e.getMessage());
            e.printStackTrace();
            
            // 显示错误消息对话框
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                    "初始化功能面板时出错: " + e.getMessage(),
                    "初始化错误",
                    JOptionPane.ERROR_MESSAGE);
            });
        }
    }
    
    /**
     * 更新货币设置
     */
    public void setCurrency(String currency) {
        this.currentCurrency = currency;
        updateStatusBar();
    }
    
    /**
     * 获取当前货币设置
     */
    public String getCurrentCurrency() {
        return this.currentCurrency;
    }
    
    /**
     * 更新地区设置
     */
    public void setRegion(String region) {
        this.currentRegion = region;
        updateStatusBar();
    }
    
    /**
     * 获取当前地区设置
     */
    public String getCurrentRegion() {
        return this.currentRegion;
    }
    
    /**
     * 主入口方法
     */
    public static void main(String[] args) {
        // 在EDT线程中启动应用
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainModuleUI();
            }
        });
    }
} 