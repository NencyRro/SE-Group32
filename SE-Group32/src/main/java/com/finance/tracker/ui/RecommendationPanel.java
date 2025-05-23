package com.finance.tracker.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import com.finance.tracker.ai.Recommendation;
import com.finance.tracker.ai.RecommendationEngine;
import com.finance.tracker.localization.LanguageManager;

/**
 * AI Recommendation Panel - Displays personalized financial advice
 */
public class RecommendationPanel extends JPanel {
    private MainModuleUI parentFrame;
    private JPanel recommendationsContainer;
    private JButton generateButton;
    private JLabel statusLabel;
    private JScrollPane scrollPane;
    private boolean generatingRecommendations = false;
    private JLabel titleLabel;
    
    // Recommendation engine
    private RecommendationEngine recommendationEngine;
    private LanguageManager languageManager;
    
    // Modern UI colors
    private final Color HEADER_COLOR = new Color(41, 128, 185);
    private final Color BUTTON_COLOR = new Color(46, 204, 113);
    private final Color BUTTON_HOVER_COLOR = new Color(39, 174, 96);
    
    /**
     * Constructor
     */
    public RecommendationPanel(MainModuleUI parent) {
        this.parentFrame = parent;
        this.languageManager = parent.getLanguageManager();
        
        // Initialize recommendation engine
        try {
            recommendationEngine = RecommendationEngine.getInstance();
        } catch (Exception e) {
            System.err.println("Error initializing recommendation engine: " + e.getMessage());
            // Continue without engine - we'll use simulated data
        }
        
        setLayout(new BorderLayout(10, 10));
        initComponents();
    }
    
    /**
     * Initialize components
     */
    private void initComponents() {
        // Top panel with title and button
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Title label
        titleLabel = new JLabel(languageManager.getText(LanguageManager.AI_RECOMMENDATIONS));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(HEADER_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        // Generate button with hover effect
        generateButton = new RoundedButton(languageManager.getText(LanguageManager.GENERATE_AI));
        generateButton.setFont(new Font("Dialog", Font.BOLD, 14));
        generateButton.setForeground(Color.WHITE);
        generateButton.setFocusPainted(false);
        generateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        generateButton.addActionListener(e -> generateRecommendations());
        
        // Add hover effect
        generateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                generateButton.setBackground(BUTTON_HOVER_COLOR);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                generateButton.setBackground(BUTTON_COLOR);
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(generateButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Create scrollable container for recommendations
        recommendationsContainer = new JPanel();
        recommendationsContainer.setLayout(new BoxLayout(recommendationsContainer, BoxLayout.Y_AXIS));
        recommendationsContainer.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(recommendationsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Add custom shadow border to scrollpane
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new ShadowBorder());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Status label
        statusLabel = new JLabel(languageManager.getText(LanguageManager.READY) + ". " + 
                               languageManager.getText(LanguageManager.WELCOME_CLICK));
        statusLabel.setFont(new Font("Dialog", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        add(statusLabel, BorderLayout.SOUTH);
        
        // Set default welcome content
        setDefaultContent();
    }
    
    /**
     * Update UI with current language
     */
    public void updateLanguage() {
        // Update title
        titleLabel.setText(languageManager.getText(LanguageManager.AI_RECOMMENDATIONS));
        
        // Update generate button
        generateButton.setText(languageManager.getText(LanguageManager.GENERATE_AI));
        
        // Update status label
        if (generatingRecommendations) {
            statusLabel.setText(languageManager.getText(LanguageManager.GENERATING));
        } else {
            statusLabel.setText(languageManager.getText(LanguageManager.READY) + ". " + 
                              languageManager.getText(LanguageManager.WELCOME_CLICK));
        }
        
        // Update welcome panel
        setDefaultContent();
    }
    
    /**
     * Set default content
     */
    private void setDefaultContent() {
        recommendationsContainer.removeAll();
        
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Create welcome icon
        JLabel iconLabel = new JLabel(createWelcomeIcon());
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create animated welcome text
        String welcomeText = "<html><div style='width: 450px; text-align: center;'>"
                + "<h1 style='color: #2980b9; margin-bottom: 15px;'>" + languageManager.getText(LanguageManager.WELCOME_TITLE) + "</h1>"
                + "<p style='font-size: 14px; line-height: 1.5; color: #34495e;'>" + languageManager.getText(LanguageManager.WELCOME_DESC) + "</p>"
                + "<p style='font-size: 14px; margin-top: 20px; color: #34495e;'>" + languageManager.getText(LanguageManager.WELCOME_CLICK) + "</p>"
                + "<p style='font-size: 13px; margin-top: 20px; color: #7f8c8d; font-style: italic;'>" 
                + "<span style='color: #e67e22;'>★</span> " + languageManager.getText(LanguageManager.WELCOME_TIP) + "</p>"
                + "</div></html>";
        
        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        welcomePanel.add(Box.createVerticalGlue());
        welcomePanel.add(iconLabel);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalGlue());
        
        recommendationsContainer.add(welcomePanel);
        recommendationsContainer.revalidate();
        recommendationsContainer.repaint();
        
        // Add a subtle welcome animation
        animateWelcomePanel(welcomePanel);
    }
    
    /**
     * Create a welcome icon
     */
    private Icon createWelcomeIcon() {
        // Create a simple financial chart icon
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circle background
                g2d.setColor(new Color(52, 152, 219, 30));
                g2d.fillOval(x, y, 80, 80);
                
                // Draw dollar sign
                g2d.setColor(new Color(41, 128, 185));
                g2d.setStroke(new BasicStroke(3f));
                
                // Draw chart bars
                g2d.fillRect(x + 20, y + 50, 8, 20);
                g2d.fillRect(x + 36, y + 40, 8, 30);
                g2d.fillRect(x + 52, y + 30, 8, 40);
                
                // Draw trend line
                g2d.setColor(new Color(46, 204, 113));
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawLine(x + 15, y + 45, x + 30, y + 35);
                g2d.drawLine(x + 30, y + 35, x + 45, y + 40);
                g2d.drawLine(x + 45, y + 40, x + 65, y + 25);
                
                // Draw circle at end of line
                g2d.fillOval(x + 62, y + 22, 6, 6);
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return 80;
            }
            
            @Override
            public int getIconHeight() {
                return 80;
            }
        };
    }
    
    /**
     * Animate the welcome panel appearance
     */
    private void animateWelcomePanel(JPanel welcomePanel) {
        welcomePanel.setVisible(false);
        
        Timer fadeInTimer = new Timer(20, null);
        final float[] alpha = {0.0f};
        
        fadeInTimer.addActionListener(e -> {
            alpha[0] += 0.05f;
            if (alpha[0] >= 1.0f) {
                alpha[0] = 1.0f;
                fadeInTimer.stop();
                welcomePanel.setVisible(true);
            }
            
            welcomePanel.setVisible(true);
            welcomePanel.setOpaque(false);
            welcomePanel.setBackground(new Color(1f, 1f, 1f, alpha[0]));
            
            welcomePanel.repaint();
        });
        
        SwingUtilities.invokeLater(() -> {
            welcomePanel.setVisible(true);
            fadeInTimer.start();
        });
    }
    
    /**
     * Generate recommendations
     */
    private void generateRecommendations() {
        // Prevent multiple clicks
        if (generatingRecommendations) {
            return;
        }
        
        // Show generation status
        statusLabel.setText(languageManager.getText(LanguageManager.GENERATING));
        generateButton.setEnabled(false);
        generatingRecommendations = true;
        
        // Clear existing content
        recommendationsContainer.removeAll();
        
        // Create loading animation
        JPanel loadingPanel = new JPanel(new BorderLayout());
        loadingPanel.setBackground(Color.WHITE);
        loadingPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        spinnerPanel.setOpaque(false);
        LoadingSpinner spinner = new LoadingSpinner();
        spinnerPanel.add(spinner);
        
        JLabel loadingLabel = new JLabel(languageManager.getText(LanguageManager.ANALYZING), JLabel.CENTER);
        loadingLabel.setFont(new Font("Dialog", Font.ITALIC, 16));
        loadingLabel.setForeground(new Color(80, 80, 80));
        
        loadingPanel.add(spinnerPanel, BorderLayout.CENTER);
        loadingPanel.add(loadingLabel, BorderLayout.SOUTH);
        
        recommendationsContainer.add(loadingPanel);
        recommendationsContainer.revalidate();
        recommendationsContainer.repaint();
        
        // Start spinner animation
        spinner.startAnimation();
        
        // Generate recommendations in background thread
        new Thread(() -> {
            try {
                // Simulate API request delay
                Thread.sleep(1500);
                
                // Call the real recommendation engine API for recommendations
                if (recommendationEngine != null) {
                    try {
                        // Generate new AI recommendations
                        recommendationEngine.generateAIPersonalizedRecommendations();
                        
                        // Get generated recommendations
                        List<Recommendation> recommendations = recommendationEngine.getActiveRecommendations();
                        
                        // Convert to text list
                        List<String> recommendationMessages = new ArrayList<>();
                        for (Recommendation rec : recommendations) {
                            if (!rec.isDismissed()) {
                                recommendationMessages.add(rec.getMessage());
                            }
                        }
                        
                        // Update UI in EDT thread
                        SwingUtilities.invokeLater(() -> {
                            displayRecommendations(recommendationMessages);
                            generatingRecommendations = false;
                            generateButton.setEnabled(true);
                            statusLabel.setText("AI financial recommendations generated, " + recommendationMessages.size() + " recommendations total");
                            
                            // Notify main frame to update status
                            if (parentFrame != null) {
                                parentFrame.setCurrency(parentFrame.getCurrentCurrency());
                            }
                        });
                    } catch (Exception e) {
                        System.err.println("Error calling recommendation engine: " + e.getMessage());
                        e.printStackTrace();
                        
                        // If API call fails, use simulated data
                        SwingUtilities.invokeLater(() -> {
                            displayRecommendations(getSimulatedRecommendations());
                            generatingRecommendations = false;
                            generateButton.setEnabled(true);
                            statusLabel.setText(languageManager.getText(LanguageManager.UNABLE_CONNECT));
                        });
                    }
                } else {
                    // Use simulated data when engine is null
                    SwingUtilities.invokeLater(() -> {
                        displayRecommendations(getSimulatedRecommendations());
                        generatingRecommendations = false;
                        generateButton.setEnabled(true);
                        statusLabel.setText(languageManager.getText(LanguageManager.UNABLE_CONNECT));
                    });
                }
            } catch (InterruptedException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Error generating recommendations: " + e.getMessage());
                    generatingRecommendations = false;
                    generateButton.setEnabled(true);
                });
            }
        }).start();
    }
    
    /**
     * Display recommendations
     */
    private void displayRecommendations(List<String> recommendations) {
        recommendationsContainer.removeAll();
        
        if (recommendations == null || recommendations.isEmpty()) {
            JLabel emptyLabel = new JLabel(languageManager.getText(LanguageManager.NO_RECOMMENDATIONS), JLabel.CENTER);
            emptyLabel.setFont(new Font("Dialog", Font.ITALIC, 16));
            emptyLabel.setForeground(new Color(120, 120, 120));
            
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            
            recommendationsContainer.add(emptyPanel);
        } else {
            // Add padding at the top
            recommendationsContainer.add(Box.createVerticalStrut(10));
            
            int index = 1;
            for (String recommendation : recommendations) {
                // Add card with animation delay
                final int currentIndex = index;
                Timer timer = new Timer(150 * index, e -> {
                    recommendationsContainer.add(createRecommendationCard(currentIndex, recommendation));
                    recommendationsContainer.add(Box.createVerticalStrut(15));
                    recommendationsContainer.revalidate();
                    recommendationsContainer.repaint();
                });
                timer.setRepeats(false);
                timer.start();
                index++;
            }
            
            // Add padding at the bottom
            recommendationsContainer.add(Box.createVerticalStrut(10));
        }
        
        recommendationsContainer.revalidate();
        recommendationsContainer.repaint();
        
        // Scroll to top
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }
    
    /**
     * Create recommendation card
     */
    private JPanel createRecommendationCard(int index, String content) {
        // Create panel with animation
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint rounded rectangle background
                g2d.setColor(getCardColor(index));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
            }
        };
        
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(800, 500));
        
        // Add initial scale animation
        card.setVisible(false);
        animateCardAppearance(card);
        
        // Title
        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setOpaque(false);
        
        // Add number badge
        JLabel numberLabel = new JLabel(String.valueOf(index));
        numberLabel.setFont(new Font("Arial", Font.BOLD, 14));
        numberLabel.setForeground(Color.WHITE);
        numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        numberLabel.setPreferredSize(new Dimension(28, 28));
        numberLabel.setBackground(new Color(52, 152, 219));
        numberLabel.setOpaque(true);
        
        // Make the number badge a circle
        numberLabel.setBorder(new EmptyBorder(0, 0, 0, 0) {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(52, 152, 219));
                g2.fillOval(x, y, width, height);
                g2.dispose();
            }
        });
        
        JPanel badgePanel = new JPanel(new BorderLayout());
        badgePanel.setPreferredSize(new Dimension(35, 35));
        badgePanel.setOpaque(false);
        badgePanel.add(numberLabel, BorderLayout.CENTER);
        
        JLabel titleLabel = new JLabel(languageManager.getText(LanguageManager.RECOMMENDATION) + " " + getEmojiForIndex(index), JLabel.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        headerPanel.add(badgePanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        card.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JLabel contentLabel = new JLabel("<html><div style='width: 500px;'><p style='font-size: 14px; line-height: 1.5;'>" 
                                      + content.replace("\n", "<br>") + "</p></div></html>");
        contentLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        contentLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        
        card.add(contentLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Animate card appearance with scale effect
     */
    private void animateCardAppearance(JPanel card) {
        card.setVisible(true);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        Timer scaleTimer = new Timer(15, null);
        final int[] scale = {90};
        
        scaleTimer.addActionListener(e -> {
            scale[0] += 2;
            
            if (scale[0] >= 100) {
                scale[0] = 100;
                scaleTimer.stop();
            }
            
            float scaleF = scale[0] / 100.0f;
            card.setSize(new Dimension((int)(card.getPreferredSize().width * scaleF), 
                                     (int)(card.getPreferredSize().height * scaleF)));
            card.revalidate();
            card.repaint();
        });
        
        SwingUtilities.invokeLater(() -> {
            card.setVisible(true);
            scaleTimer.start();
        });
    }
    
    /**
     * Get card color based on index
     */
    private Color getCardColor(int index) {
        switch (index % 5) {
            case 0: return new Color(232, 245, 233); // Light green
            case 1: return new Color(227, 242, 253); // Light blue
            case 2: return new Color(255, 243, 224); // Light orange
            case 3: return new Color(243, 229, 245); // Light purple
            case 4: return new Color(251, 233, 231); // Light red
            default: return new Color(248, 248, 248); // Light gray
        }
    }
    
    /**
     * Get emoji based on index
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
     * Get simulated data (only used when API call fails)
     */
    private List<String> getSimulatedRecommendations() {
        List<String> recommendations = new ArrayList<>();
        
        recommendations.add("Based on your spending patterns, you could save up to $1500 annually by reducing discretionary expenses in entertainment and dining.");
        recommendations.add("Consider allocating 15% of your monthly income to your retirement fund. Increasing your contribution by 5% now could result in an additional $100,000 by retirement age.");
        recommendations.add("You've been spending consistently on subscriptions totaling $180 monthly. Review your subscription services to identify any that you're not fully utilizing.");
        recommendations.add("Creating an emergency fund covering 3-6 months of expenses should be prioritized. Based on your current spending, aim for $9,000-$18,000.");
        recommendations.add("Your spending in the 'Dining Out' category has increased by 25% compared to last month. Consider setting a budget limit of $400 for this category.");
        
        return recommendations;
    }
    
    /**
     * Custom rounded button with gradient
     */
    private class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBackground(BUTTON_COLOR);
            setBorderPainted(false);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2d.setColor(BUTTON_COLOR.darker());
            } else if (getModel().isRollover()) {
                g2d.setColor(BUTTON_HOVER_COLOR);
            } else {
                g2d.setColor(getBackground());
            }
            
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            
            super.paintComponent(g);
            g2d.dispose();
        }
    }
    
    /**
     * Custom Loading Spinner
     */
    private class LoadingSpinner extends JPanel {
        private Timer timer;
        private int angle = 0;
        
        public LoadingSpinner() {
            setPreferredSize(new Dimension(60, 60));
            setOpaque(false);
            
            timer = new Timer(50, e -> {
                angle = (angle + 10) % 360;
                repaint();
            });
        }
        
        public void startAnimation() {
            timer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int diameter = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;
            
            g2d.setStroke(new BasicStroke(4f));
            
            // Draw background circle
            g2d.setColor(new Color(230, 230, 230));
            g2d.drawArc(x, y, diameter, diameter, 0, 360);
            
            // Draw colored arc
            g2d.setColor(new Color(41, 128, 185));
            g2d.drawArc(x, y, diameter, diameter, angle, 120);
            
            g2d.dispose();
        }
        
        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible);
            if (!visible) {
                timer.stop();
            }
        }
    }
    
    /**
     * Shadow Border
     */
    private class ShadowBorder extends AbstractBorder {
        private int shadowSize = 5;
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create drop shadow effect
            for (int i = 0; i < shadowSize; i++) {
                int alpha = 50 - (i * 10);
                if (alpha < 0) alpha = 0;
                
                g2d.setColor(new Color(0, 0, 0, alpha));
                g2d.drawRoundRect(x + i, y + i, width - (i * 2), height - (i * 2), 10, 10);
            }
            
            // Draw border
            g2d.setColor(new Color(220, 220, 220));
            g2d.drawRoundRect(x, y, width - 1, height - 1, 10, 10);
            
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = shadowSize;
            return insets;
        }
    }
    
    /**
     * 当面板被显示时自动刷新推荐，确保与其他面板数据同步
     */
    public void onPanelShown() {
        // 检查是否有太旧的推荐（超过7天）
        boolean hasOldRecommendations = false;
        if (recommendationEngine != null) {
            List<Recommendation> recommendations = recommendationEngine.getActiveRecommendations();
            if (!recommendations.isEmpty()) {
                LocalDate now = LocalDate.now();
                for (Recommendation rec : recommendations) {
                    // 如果最老的推荐超过7天，触发刷新
                    if (java.time.temporal.ChronoUnit.DAYS.between(rec.getDateCreated(), now) > 7) {
                        hasOldRecommendations = true;
                        break;
                    }
                }
            } else {
                // 没有推荐时自动生成
                hasOldRecommendations = true;
            }
            
            // 如果有旧推荐或没有推荐，自动更新
            if (hasOldRecommendations) {
                // 更新状态
                statusLabel.setText(languageManager.getText(LanguageManager.UPDATING));
                
                // 在后台线程中更新推荐
                new Thread(() -> {
                    try {
                        // 触发所有类型的推荐更新，确保与节日数据等同步
                        recommendationEngine.generateAllRecommendations();
                        
                        // 在EDT线程更新UI
                        SwingUtilities.invokeLater(() -> {
                            // 显示更新后的推荐
                            List<Recommendation> updatedRecs = recommendationEngine.getActiveRecommendations();
                            if (!updatedRecs.isEmpty()) {
                                // 转换为显示格式
                                List<String> messages = new ArrayList<>();
                                for (Recommendation rec : updatedRecs) {
                                    if (!rec.isDismissed()) {
                                        messages.add(rec.getMessage());
                                    }
                                }
                                displayRecommendations(messages);
                            } else {
                                setDefaultContent();
                            }
                            
                            // 更新状态
                            statusLabel.setText(languageManager.getText(LanguageManager.READY) + ". " + 
                                             languageManager.getText(LanguageManager.WELCOME_CLICK));
                                             
                            // 更新主窗口状态
                            if (parentFrame != null) {
                                parentFrame.setCurrency(parentFrame.getCurrentCurrency());
                            }
                        });
                    } catch (Exception e) {
                        System.err.println("Error auto-updating recommendations: " + e.getMessage());
                        // 在EDT线程更新UI
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText(languageManager.getText(LanguageManager.ERROR_UPDATE));
                        });
                    }
                }).start();
            }
        }
    }
} 