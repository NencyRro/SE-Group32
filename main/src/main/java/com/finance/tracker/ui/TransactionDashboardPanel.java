package com.finance.tracker.ui;

import com.finance.tracker.classification.view.TransactionFormWindow;
import com.finance.tracker.classification.view.TransactionList;
import com.finance.tracker.classification.util.CSVImportManager;
import com.finance.tracker.classification.util.CategoryManager;
import com.finance.tracker.classification.util.TransactionManager;
import com.finance.tracker.classification.model.CategoryType;
import com.finance.tracker.feedback.FeedbackForm;
import com.finance.tracker.integration.AIModuleFacade;
import com.finance.tracker.report.ReportAndNotificationUI;
import com.finance.tracker.report.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 交易导览界面 - 提供交易管理相关功能的入口点
 */
public class TransactionDashboardPanel extends JPanel {
    
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JLabel statusLabel;
    private MainModuleUI parentFrame;
    private TransactionManager transactionManager;
    private CategoryManager categoryManager;
    private CSVImportManager csvImportManager;
    private LocalDateTime lastSyncTime;
    
    /**
     * 创建交易导览界面
     * 
     * @param parentFrame 父窗口
     */
    public TransactionDashboardPanel(MainModuleUI parentFrame) {
        this.parentFrame = parentFrame;
        
        // 初始化管理器类
        this.categoryManager = new CategoryManager();
        this.transactionManager = new TransactionManager(categoryManager, "data/transactions.csv");
        this.csvImportManager = new CSVImportManager(categoryManager, transactionManager);
        
        // 初始化界面
        initializeUI();
    }
    
    /**
     * 初始化界面
     */
    private void initializeUI() {
        // 设置主布局
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // 创建顶部标题区域
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // 创建中间按钮区域
        add(createButtonPanel(), BorderLayout.CENTER);
        
        // 创建底部状态栏
        add(createStatusPanel(), BorderLayout.SOUTH);
        
        // 创建内容面板（用于显示子面板）
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        
        // 更新状态栏
        updateStatusBar();
    }
    
    /**
     * 创建顶部标题区域
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // 创建大标题
        JLabel titleLabel = new JLabel("Add & Manage Transactions", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * 创建中间按钮区域
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // 创建按钮：添加新交易
        JButton addTransactionBtn = createFeatureButton(
            "Add New Transaction", 
            "",
            "Add new income or expense transaction"
        );
        addTransactionBtn.addActionListener(e -> openTransactionForm());
        
        // 创建按钮：查看统计图表
        JButton chartBtn = createFeatureButton(
            "Statistics Chart", 
            "",
            "View spending and income statistics"
        );
        chartBtn.addActionListener(e -> openChartPanel());
        
        // 创建按钮：导出报告（替换原来的导入CSV按钮）
        JButton exportReportBtn = createFeatureButton(
            "Export Report", 
            "",
            "Export and view transaction reports"
        );
        exportReportBtn.addActionListener(e -> openReportUI());
        
        // 创建按钮：查看交易记录详情
        JButton viewTransactionsBtn = createFeatureButton(
            "Transaction Records", 
            "",
            "View and manage all transactions"
        );
        viewTransactionsBtn.addActionListener(e -> openTransactionList());
        
        // 创建按钮：AI推荐分析
        JButton aiRecommendBtn = createFeatureButton(
            "AI Recommendations", 
            "",
            "View AI-powered recommendations"
        );
        aiRecommendBtn.addActionListener(e -> openRecommendationPanel());
        
        // 创建按钮：反馈与问题提交
        JButton feedbackBtn = createFeatureButton(
            "Submit Feedback", 
            "",
            "Submit feedback or report issues"
        );
        feedbackBtn.addActionListener(e -> openFeedbackForm());
        
        // 添加按钮到面板
        buttonPanel.add(addTransactionBtn);
        buttonPanel.add(chartBtn);
        buttonPanel.add(exportReportBtn);
        buttonPanel.add(viewTransactionsBtn);
        buttonPanel.add(aiRecommendBtn);
        buttonPanel.add(feedbackBtn);
        
        return buttonPanel;
    }
    
    /**
     * 创建底部状态栏
     */
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            new EmptyBorder(10, 5, 5, 5)
        ));
        
        statusLabel = new JLabel("Ready", JLabel.LEFT);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(120, 120, 120));
        
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        return statusPanel;
    }
    
    /**
     * 创建特性按钮
     */
    private JButton createFeatureButton(String text, String emoji, String tooltip) {
        JButton button = new JButton(text);
        
        // 设置统一按钮样式 - 浅蓝背景、深灰文字
        Color buttonBgColor = new Color(240, 244, 250); // 浅蓝色背景 #f0f4fa
        Color buttonTextColor = new Color(26, 26, 26);  // 深灰文字 #1a1a1a
        Color buttonBorderColor = new Color(200, 210, 230); // 边框颜色稍深
        
        button.setBackground(buttonBgColor);
        button.setForeground(buttonTextColor);
        
        // 使用Segoe UI或系统默认sans-serif字体
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // 设置emoji表情与文字居中
        if (emoji != null && !emoji.isEmpty()) {
            String buttonText = emoji + "  " + text;
            button.setText(buttonText);
            button.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        // 设置按钮样式
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true);
        
        // 设置圆角边框 (10px)
        int radius = 10;
        button.setBorder(BorderFactory.createCompoundBorder(
            new javax.swing.border.LineBorder(buttonBorderColor, 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(180, 80));
        
        // 添加悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(214, 228, 240)); // 悬浮时背景色 #d6e4f0
                button.setBorder(BorderFactory.createCompoundBorder(
                    new javax.swing.border.LineBorder(new Color(180, 190, 210), 2, true),
                    BorderFactory.createEmptyBorder(9, 14, 9, 14)
                ));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(buttonBgColor); // 恢复原来的浅蓝色
                button.setBorder(BorderFactory.createCompoundBorder(
                    new javax.swing.border.LineBorder(buttonBorderColor, 1, true),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        
        return button;
    }
    
    /**
     * 打开交易表单窗口
     */
    private void openTransactionForm() {
        // 创建并显示一个独立的交易表单窗口
        TransactionFormWindow window = new TransactionFormWindow();
        window.setVisible(true);
        
        // 更新状态栏
        updateStatusBar("Transaction form opened");
    }
    
    /**
     * 打开图表面板
     */
    private void openChartPanel() {
        // TODO: 实现图表面板
        JOptionPane.showMessageDialog(
            this,
            "Chart panel will be implemented in future versions",
            "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE
        );
        
        // 更新状态栏
        updateStatusBar("Chart panel feature coming soon");
    }
    
    /**
     * 打开报告UI界面
     */
    private void openReportUI() {
        try {
            // 创建交易列表以将分类模块的交易转换为报告UI需要的格式
            List<com.finance.tracker.report.Transaction> reportTransactions = new ArrayList<>();
            
            // 获取收入交易
            List<com.finance.tracker.classification.model.Transaction> incomeTransactions = 
                transactionManager.getTransactionsByType(CategoryType.INCOME);
            
            // 获取支出交易
            List<com.finance.tracker.classification.model.Transaction> expenseTransactions = 
                transactionManager.getTransactionsByType(CategoryType.EXPENSE);
            
            // 合并所有交易
            List<com.finance.tracker.classification.model.Transaction> allTransactions = new ArrayList<>();
            allTransactions.addAll(incomeTransactions);
            allTransactions.addAll(expenseTransactions);
            
            // 转换交易格式
            for (com.finance.tracker.classification.model.Transaction t : allTransactions) {
                // 日期格式转换：将LocalDateTime转为yyyy-MM-dd字符串
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String dateStr = t.getDateTime().toLocalDate().format(formatter);
                
                // 创建报告UI需要的Transaction对象
                reportTransactions.add(new com.finance.tracker.report.Transaction(
                    dateStr,
                    t.getCategory().getName(),
                    t.getAmount().doubleValue(),  // BigDecimal转为double
                    t.getDescription() != null ? t.getDescription() : ""
                ));
            }
            
            // 创建并显示报告窗口
            com.finance.tracker.report.ReportAndNotificationUI reportUI = 
                new com.finance.tracker.report.ReportAndNotificationUI(reportTransactions);
            reportUI.setVisible(true);
            
            // 更新状态栏
            updateStatusBar("Report UI opened");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error opening report UI: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }
    
    /**
     * 打开交易记录列表
     */
    private void openTransactionList() {
        // 创建交易列表面板
        TransactionList transactionList = new TransactionList(transactionManager);
        
        // 显示交易列表窗口
        JFrame frame = new JFrame("Transaction Records");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(this);
        frame.getContentPane().add(transactionList);
        frame.setVisible(true);
        
        // 更新状态栏
        updateStatusBar("Transaction records opened");
    }
    
    /**
     * 打开推荐面板
     */
    private void openRecommendationPanel() {
        // 切换到主界面的推荐页面
        if (parentFrame != null) {
            parentFrame.showPanel("recommendation");
            updateStatusBar("Recommendation panel opened");
        } else {
            // 如果未找到主界面，显示消息
            JOptionPane.showMessageDialog(
                this,
                "Could not access recommendation panel",
                "Navigation Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * 打开反馈表单
     */
    private void openFeedbackForm() {
        // 创建并显示反馈表单窗口
        FeedbackForm feedbackForm = new FeedbackForm(parentFrame);
        feedbackForm.setVisible(true);
        
        // 更新状态栏
        updateStatusBar("Feedback form opened");
    }
    
    /**
     * 更新状态栏
     */
    private void updateStatusBar() {
        updateStatusBar(null);
    }
    
    /**
     * 更新状态栏文本
     * 
     * @param message 状态消息，如果为null则只更新同步时间
     */
    private void updateStatusBar(String message) {
        // 格式化最后同步时间
        String syncTimeStr = "Never";
        if (lastSyncTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            syncTimeStr = lastSyncTime.format(formatter);
        }
        
        // 创建状态文本
        StringBuilder status = new StringBuilder();
        status.append("Last Sync: ").append(syncTimeStr);
        
        // 添加用户消息（如果有）
        if (message != null && !message.isEmpty()) {
            status.append(" | ").append(message);
        }
        
        // 检查推荐状态
        try {
            int recCount = AIModuleFacade.getInstance().getActiveRecommendations().size();
            status.append(" | Active Recommendations: ").append(recCount);
        } catch (Exception e) {
            status.append(" | Recommendation status: unavailable");
        }
        
        // 更新标签
        statusLabel.setText(status.toString());
    }

    /**
     * 更新界面语言
     */
    public void updateLanguage() {
        // 目前暂不实现多语言支持，未来可添加
        // 如果需要实现，请参考其他面板的实现方式
        
        // 示例：更新标题
        // titleLabel.setText(parentFrame.getLanguageManager().getText("key_dashboard_title"));
    }
} 