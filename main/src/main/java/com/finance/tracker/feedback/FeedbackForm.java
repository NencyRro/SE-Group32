package com.finance.tracker.feedback;

import com.finance.tracker.feedback.FeedbackManager;
import com.finance.tracker.ui.MainModuleUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * 反馈表单 - 用于收集用户反馈和报告问题
 */
public class FeedbackForm extends JFrame {
    
    private JTextArea feedbackTextArea;
    private JComboBox<String> categoryComboBox;
    private JTextField emailField;
    private JCheckBox followupCheckBox;
    private JButton submitButton;
    private JButton cancelButton;
    private MainModuleUI parentFrame;
    private FeedbackManager feedbackManager;
    
    /**
     * 创建反馈表单窗口
     * 
     * @param parentFrame 父窗口
     */
    public FeedbackForm(MainModuleUI parentFrame) {
        this.parentFrame = parentFrame;
        this.feedbackManager = FeedbackManager.getInstance();
        
        initializeUI();
    }
    
    /**
     * 初始化界面
     */
    private void initializeUI() {
        // 设置窗口属性
        setTitle("Submit Feedback");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // 创建表单面板
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // 创建按钮面板
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 设置内容面板
        setContentPane(mainPanel);
    }
    
    /**
     * 创建表单面板
     */
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // 添加标题标签
        JLabel titleLabel = new JLabel("We Value Your Feedback");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // 添加描述标签
        JLabel descLabel = new JLabel("Please share your thoughts, suggestions, or report any issues:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(descLabel, gbc);
        
        // 添加类别标签
        JLabel categoryLabel = new JLabel("Category:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(categoryLabel, gbc);
        
        // 添加类别下拉框
        String[] categories = {"General Feedback", "Bug Report", "Feature Request", "UI/UX Feedback", "Performance Issue", "Other"};
        categoryComboBox = new JComboBox<>(categories);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(categoryComboBox, gbc);
        
        // 添加反馈文本区
        feedbackTextArea = new JTextArea(10, 30);
        feedbackTextArea.setLineWrap(true);
        feedbackTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);
        
        // 添加联系信息面板
        JPanel contactPanel = new JPanel(new BorderLayout(5, 5));
        contactPanel.setBorder(BorderFactory.createTitledBorder("Contact Information (Optional)"));
        
        JPanel contactFieldsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JPanel emailPanel = new JPanel(new BorderLayout(5, 0));
        emailPanel.add(new JLabel("Email:"), BorderLayout.WEST);
        emailField = new JTextField();
        emailPanel.add(emailField, BorderLayout.CENTER);
        
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        followupCheckBox = new JCheckBox("I would like to receive a follow-up response");
        checkboxPanel.add(followupCheckBox);
        
        contactFieldsPanel.add(emailPanel);
        contactFieldsPanel.add(checkboxPanel);
        
        contactPanel.add(contactFieldsPanel, BorderLayout.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(contactPanel, gbc);
        
        return panel;
    }
    
    /**
     * 创建按钮面板
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        cancelButton = new JButton("Cancel");
        submitButton = new JButton("Submit Feedback");
        
        // 添加取消按钮监听器
        cancelButton.addActionListener(e -> dispose());
        
        // 添加提交按钮监听器
        submitButton.addActionListener(e -> {
            if (validateAndSubmit()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Thank you for your feedback! We appreciate your input.",
                    "Feedback Submitted",
                    JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            }
        });
        
        panel.add(cancelButton);
        panel.add(submitButton);
        
        return panel;
    }
    
    /**
     * 验证并提交反馈
     */
    private boolean validateAndSubmit() {
        // 获取表单数据
        String feedbackText = feedbackTextArea.getText().trim();
        String category = (String) categoryComboBox.getSelectedItem();
        String email = emailField.getText().trim();
        boolean wantsFollowup = followupCheckBox.isSelected();
        
        // 验证反馈内容
        if (feedbackText.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter your feedback before submitting.",
                "Empty Feedback",
                JOptionPane.WARNING_MESSAGE
            );
            feedbackTextArea.requestFocus();
            return false;
        }
        
        // 验证电子邮件格式（如果提供）
        if (!email.isEmpty() && !isValidEmail(email)) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid email address.",
                "Invalid Email",
                JOptionPane.WARNING_MESSAGE
            );
            emailField.requestFocus();
            return false;
        }
        
        // 如果要求回复但没有提供电子邮件
        if (wantsFollowup && email.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please provide an email address if you want a follow-up response.",
                "Email Required",
                JOptionPane.WARNING_MESSAGE
            );
            emailField.requestFocus();
            return false;
        }
        
        try {
            // 使用反馈管理器记录反馈
            feedbackManager.recordFeedback(
                category,              // 类型
                feedbackText,          // 目标ID（使用反馈文本作为目标ID）
                true,                  // 是否有帮助
                wantsFollowup ? email : "" // 评论（使用电子邮件地址作为评论）
            );
            
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Error submitting feedback: " + ex.getMessage(),
                "Submission Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }
    
    /**
     * 验证电子邮件格式
     */
    private boolean isValidEmail(String email) {
        // 简单的电子邮件验证
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
} 