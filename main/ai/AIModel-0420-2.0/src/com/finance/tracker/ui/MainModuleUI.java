package com.finance.tracker.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.finance.tracker.localization.LanguageManager;

/**
 * Main UI Class - Based on modern UI design
 */
public class MainModuleUI extends JFrame {
    // Constants
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;
    // Updated with a more modern color palette
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185); // Slightly darker blue
    private static final Color PRIMARY_DARK_COLOR = new Color(25, 80, 115);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94); // Darker secondary
    private static final Color ACCENT_COLOR = new Color(46, 204, 113); // Green accent
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Lighter background
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(229, 232, 236);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    
    // UI components
    private JPanel contentPanel;
    private JPanel menuPanel;
    private JPanel mainContentPanel;
    private JPanel statusBar;
    private CardLayout cardLayout;
    private JLabel titleLabel;
    private JLabel menuTitleLabel;
    private JLabel statusLabel;
    private JLabel timeLabel;
    
    // Functional panels
    private RecommendationPanel recommendationPanel;
    private HolidayPanel holidayPanel;
    private CurrencyPanel currencyPanel;
    private TransactionPanel transactionPanel;
    
    // Status information
    private String currentCurrency = "Yuan (¥)";
    private LocalDateTime lastRecommendationTime = null;
    
    // Panel name constants
    private static final String RECOMMENDATION_PANEL = "recommendation";
    private static final String HOLIDAY_PANEL = "holiday";
    private static final String CURRENCY_PANEL = "currency";
    private static final String TRANSACTION_PANEL = "transaction";
    
    // Language manager
    private LanguageManager languageManager;
    
    // Active menu button tracking
    private JButton activeMenuButton = null;
    
    /**
     * Constructor
     */
    public MainModuleUI() {
        // Initialize language manager
        languageManager = LanguageManager.getInstance();
        // Always use English
        languageManager.setLanguage(LanguageManager.Language.ENGLISH);
        
        // Set window basic properties
        setTitle(languageManager.getText(LanguageManager.TITLE));
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Set modern look and feel
        setupLookAndFeel();
        
        // Initialize UI components
        initComponents();
        
        // Display window
        setVisible(true);
        
        // Add a modern welcome animation
        showWelcomeAnimation();
    }
    
    /**
     * Show a modern welcome animation
     */
    private void showWelcomeAnimation() {
        // Create a fading welcome overlay with gradient background
        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(25, 80, 115, 240), 
                    getWidth(), getHeight(), new Color(41, 128, 185, 240)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle pattern overlay
                g2d.setColor(new Color(255, 255, 255, 15));
                for (int i = 0; i < getHeight(); i += 6) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
                
                g2d.dispose();
            }
        };
        overlay.setOpaque(false);
        overlay.setLayout(null); // Use absolute positioning
        
        // Create logo panel with financial icon
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circle background
                g2d.setColor(new Color(255, 255, 255, 240));
                g2d.fillOval(0, 0, 120, 120);
                
                // Draw chart bars
                g2d.setColor(ACCENT_COLOR);
                g2d.fillRect(30, 70, 12, 30);
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRect(54, 50, 12, 50);
                g2d.setColor(ACCENT_COLOR);
                g2d.fillRect(78, 30, 12, 70);
                
                // Draw trend line
                g2d.setColor(new Color(41, 128, 185));
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawLine(24, 65, 42, 45);
                g2d.drawLine(42, 45, 60, 60);
                g2d.drawLine(60, 60, 90, 25);
                
                // Draw circle at end of line
                g2d.fillOval(86, 21, 8, 8);
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(120, 120);
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setBounds((WIDTH - 120) / 2, HEIGHT / 2 - 150, 120, 120);
        logoPanel.setVisible(false); // Will be shown during animation
        
        // 使用支持Unicode的标签创建标题
        JLabel titleLabel = createUnicodeLabel(languageManager.getText(LanguageManager.TITLE), Font.BOLD, 32);
        titleLabel.setForeground(new Color(255, 255, 255, 0)); // Start transparent
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, HEIGHT / 2, WIDTH, 40);
        
        // 使用支持Unicode的标签创建副标题
        String subtitleText = "个人财务智能助手"; // 简单的中文副标题
        JLabel subtitleLabel = createUnicodeLabel(subtitleText, Font.PLAIN, 18);
        subtitleLabel.setForeground(new Color(255, 255, 255, 0)); // Start transparent
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBounds(0, HEIGHT / 2 + 50, WIDTH, 30);
        
        // Add components to overlay
        overlay.add(logoPanel);
        overlay.add(titleLabel);
        overlay.add(subtitleLabel);
        overlay.setBounds(0, 0, getWidth(), getHeight());
        
        // Add overlay to the glass pane
        JPanel glass = (JPanel) getGlassPane();
        glass.setLayout(null);
        glass.add(overlay);
        glass.setVisible(true);
        
        // 声明所有必要的变量和计时器
        final int[] logoScale = {70}; // Start at 70% size
        final int[] logoOpacity = {0};
        final int[] titleOpacity = {0};
        final int[] subtitleOpacity = {0};
        final boolean[] titleStarted = {false};
        final int[] overlayOpacity = {100};
        
        // 创建所有的计时器，确保它们在使用前已声明
        Timer logoTimer = new Timer(16, null);
        Timer textTimer = new Timer(16, null);
        Timer fadeOutTimer = new Timer(16, null);
        
        // 设置淡出计时器
        fadeOutTimer.addActionListener(e -> {
            overlayOpacity[0] -= 3;
            
            if (overlayOpacity[0] <= 0) {
                glass.setVisible(false);
                fadeOutTimer.stop();
            } else {
                // Fade out all elements
                float opacity = overlayOpacity[0] / 100.0f;
                titleLabel.setForeground(new Color(255, 255, 255, (int)(opacity * 255)));
                subtitleLabel.setForeground(new Color(255, 255, 255, (int)(opacity * 255)));
                
                // Redraw the panel
                overlay.repaint();
            }
        });
        
        // 设置文本动画计时器
        textTimer.addActionListener(e -> {
            // Start title animation after logo reaches 50% opacity
            if (logoOpacity[0] > 50 && !titleStarted[0]) {
                titleStarted[0] = true;
            }
            
            // Animate title opacity
            if (titleStarted[0] && titleOpacity[0] < 100) {
                titleOpacity[0] += 3;
                if (titleOpacity[0] > 100) titleOpacity[0] = 100;
                
                titleLabel.setForeground(new Color(255, 255, 255, titleOpacity[0] * 255 / 100));
            }
            
            // Start subtitle after title is 50% visible
            if (titleOpacity[0] > 50 && subtitleOpacity[0] < 100) {
                subtitleOpacity[0] += 3;
                if (subtitleOpacity[0] > 100) subtitleOpacity[0] = 100;
                
                subtitleLabel.setForeground(new Color(255, 255, 255, subtitleOpacity[0] * 255 / 100));
            }
            
            // Stop when complete
            if (titleOpacity[0] >= 100 && subtitleOpacity[0] >= 100) {
                textTimer.stop();
                
                // Start fade out timer after 1 second delay
                Timer delayTimer = new Timer(1000, event -> fadeOutTimer.start());
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        });
        
        // 设置Logo动画计时器
        logoTimer.addActionListener(e -> {
            // Increase scale and opacity over time with ease-out effect
            if (logoScale[0] < 100) {
                logoScale[0] += 2;
            }
            
            if (logoOpacity[0] < 100) {
                logoOpacity[0] += 4;
                if (logoOpacity[0] > 100) logoOpacity[0] = 100;
            }
            
            // Apply scale and opacity
            float scale = logoScale[0] / 100.0f;
            int size = (int)(120 * scale);
            logoPanel.setBounds((WIDTH - size) / 2, HEIGHT / 2 - (int)(150 * scale), size, size);
            logoPanel.setVisible(true);
            
            logoPanel.repaint();
            
            // Stop when complete
            if (logoScale[0] >= 100 && logoOpacity[0] >= 100) {
                logoTimer.stop();
            }
        });
        
        // Start animation sequence
        logoTimer.start();
        textTimer.start();
    }
    
    /**
     * Set modern UI look and feel
     */
    private void setupLookAndFeel() {
        try {
            // Try to set FlatLaf modern theme
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            // If FlatLaf is not available, fall back to system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Customize UI properties
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("Panel.arc", 10);
        UIManager.put("ScrollBar.thumbArc", 10);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("TabbedPane.selectedBackground", CARD_COLOR);
        UIManager.put("TabbedPane.contentAreaColor", CARD_COLOR);
        UIManager.put("TextField.background", new Color(248, 250, 252));
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        // Set content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        setContentPane(contentPanel);
        
        // Create top title bar
        JPanel titleBar = createTitleBar();
        contentPanel.add(titleBar, BorderLayout.NORTH);
        
        // Create center content area
        JPanel centerPanel = new JPanel(new BorderLayout(15, 0));
        centerPanel.setBackground(BACKGROUND_COLOR);
        
        // Create left menu
        menuPanel = createMenuPanel();
        centerPanel.add(menuPanel, BorderLayout.WEST);
        
        // Create main content area (using CardLayout for switching)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(CARD_COLOR);
        mainContentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Add content area with some padding
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(BACKGROUND_COLOR);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentWrapper.add(mainContentPanel, BorderLayout.CENTER);
        
        // Initialize functional panels
        initFunctionalPanels();
        
        centerPanel.add(contentWrapper, BorderLayout.CENTER);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Create bottom status bar
        statusBar = createStatusBar();
        contentPanel.add(statusBar, BorderLayout.SOUTH);
    }
    
    /**
     * Create top title bar
     */
    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setPreferredSize(new Dimension(WIDTH, 60));
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create gradient panel for title bar
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background with more modern look
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 128, 185), 
                    getWidth(), 0, new Color(25, 80, 115)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle pattern for depth
                g2d.setColor(new Color(255, 255, 255, 10));
                for (int i = 0; i < getHeight(); i += 4) {
                    g2d.drawLine(0, i, getWidth(), i);
                }
                
                g2d.dispose();
            }
        };
        gradientPanel.setLayout(new BorderLayout());
        gradientPanel.setOpaque(false);
        
        // Create left section with app icon
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setOpaque(false);
        
        // Add app icon
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(createFinancialIcon(30, 30));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        leftPanel.add(iconLabel);
        
        // Main title with shadow effect
        titleLabel = new JLabel(languageManager.getText(LanguageManager.TITLE));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add a button for settings/info on the right
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);
        
        JButton infoButton = createCircleButton("?", 28, 28, new Color(255, 255, 255, 80), Color.WHITE);
        infoButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "AI Financial Assistant v1.0\n" +
                "© 2023-2025 Finance Tracker Team",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        rightPanel.add(infoButton);
        
        // Add components to title bar
        gradientPanel.add(leftPanel, BorderLayout.WEST);
        gradientPanel.add(titleLabel, BorderLayout.CENTER);
        gradientPanel.add(rightPanel, BorderLayout.EAST);
        
        titleBar.add(gradientPanel, BorderLayout.CENTER);
        
        return titleBar;
    }
    
    /**
     * Create a circle button with icon or text
     */
    private JButton createCircleButton(String text, int width, int height, Color bgColor, Color fgColor) {
        // Create a custom JButton that paints itself as a circle
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Get appropriate color based on button state
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                // Draw circle background
                g2d.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                
                // Draw the text
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle textRect = fm.getStringBounds(getText(), g2d).getBounds();
                
                int textX = (getWidth() - textRect.width) / 2;
                int textY = (getHeight() - textRect.height) / 2 + fm.getAscent();
                
                g2d.setColor(fgColor);
                g2d.setFont(getFont());
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Don't paint a border
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(width, height);
            }
        };
        
        // Set button properties
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(fgColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        
        return button;
    }
    
    /**
     * Update all UI components with current language
     */
    private void updateLanguage() {
        // Update window title
        setTitle(languageManager.getText(LanguageManager.TITLE));
        
        // Update main title
        titleLabel.setText(languageManager.getText(LanguageManager.TITLE));
        
        // Update menu title
        menuTitleLabel.setText(languageManager.getText(LanguageManager.NAVIGATION));
        
        // Refresh panels
        if (recommendationPanel != null) {
            recommendationPanel.updateLanguage();
        }
        
        if (holidayPanel != null) {
            holidayPanel.updateLanguage();
        }
        
        if (currencyPanel != null) {
            currencyPanel.updateLanguage();
        }
        
        if (transactionPanel != null) {
            transactionPanel.updateLanguage();
        }
        
        // Update status bar
        updateStatusBar();
        
        // Update menu buttons
        refreshMenuButtons();
    }
    
    /**
     * Create left menu panel
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(220, HEIGHT));
        panel.setBackground(new Color(240, 242, 245));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Add logo at the top
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(createFinancialIcon(40, 40));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        logoPanel.add(logoLabel);
        
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.setMaximumSize(new Dimension(220, 70));
        panel.add(logoPanel);
        
        // Add title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        menuTitleLabel = createUnicodeLabel(languageManager.getText(LanguageManager.NAVIGATION), Font.BOLD, 16);
        menuTitleLabel.setForeground(SECONDARY_COLOR);
        titlePanel.add(menuTitleLabel);
        
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.setMaximumSize(new Dimension(220, 40));
        panel.add(titlePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Create menu button mapping with icons
        Map<String, String> menuItems = new HashMap<>();
        menuItems.put("recommendation", LanguageManager.GENERATE_AI);
        menuItems.put("holiday", LanguageManager.VIEW_HOLIDAYS);
        menuItems.put("currency", LanguageManager.CHANGE_CURRENCY);
        menuItems.put("transaction", LanguageManager.ADD_TRANSACTION);
        
        // Icon mappings for menu items
        Map<String, Icon> menuIcons = new HashMap<>();
        menuIcons.put("recommendation", createMenuIcon("recommendation", 18, 18));
        menuIcons.put("holiday", createMenuIcon("holiday", 18, 18));
        menuIcons.put("currency", createMenuIcon("currency", 18, 18));
        menuIcons.put("transaction", createMenuIcon("transaction", 18, 18));
        
        // Add menu buttons
        for (Map.Entry<String, String> entry : menuItems.entrySet()) {
            JButton button = createMenuButton(entry.getKey(), languageManager.getText(entry.getValue()), menuIcons.get(entry.getKey()));
            button.setName(entry.getValue()); // Set name for language updates
            panel.add(button);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            // Set first button as active by default
            if (activeMenuButton == null && entry.getKey().equals("recommendation")) {
                activeMenuButton = button;
                activeMenuButton.setBackground(PRIMARY_COLOR);
                activeMenuButton.setForeground(Color.WHITE);
            }
        }
        
        // Fill remaining space
        panel.add(Box.createVerticalGlue());
        
        // Add version info at bottom
        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setForeground(new Color(150, 150, 150));
        versionLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(versionLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        return panel;
    }
    
    /**
     * Create a custom icon for the financial app
     */
    private Icon createFinancialIcon(int width, int height) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circle background
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillOval(x, y, width, height);
                
                // Scale factors
                float sw = width / 120f;
                float sh = height / 120f;
                
                // Draw chart bars with scaling
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x + (int)(30 * sw), y + (int)(70 * sh), (int)(12 * sw), (int)(30 * sh));
                g2d.fillRect(x + (int)(54 * sw), y + (int)(50 * sh), (int)(12 * sw), (int)(50 * sh));
                g2d.fillRect(x + (int)(78 * sw), y + (int)(30 * sh), (int)(12 * sw), (int)(70 * sh));
                
                // Draw trend line
                g2d.setColor(ACCENT_COLOR);
                g2d.setStroke(new BasicStroke(2f * Math.min(sw, sh)));
                g2d.drawLine(
                    x + (int)(24 * sw), y + (int)(65 * sh), 
                    x + (int)(42 * sw), y + (int)(45 * sh)
                );
                g2d.drawLine(
                    x + (int)(42 * sw), y + (int)(45 * sh), 
                    x + (int)(60 * sw), y + (int)(60 * sh)
                );
                g2d.drawLine(
                    x + (int)(60 * sw), y + (int)(60 * sh), 
                    x + (int)(90 * sw), y + (int)(25 * sh)
                );
                
                // Draw circle at end of line
                g2d.fillOval(
                    x + (int)(86 * sw), y + (int)(21 * sh), 
                    (int)(8 * sw), (int)(8 * sh)
                );
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return width;
            }
            
            @Override
            public int getIconHeight() {
                return height;
            }
        };
    }
    
    /**
     * Create menu specific icons
     */
    private Icon createMenuIcon(String type, int width, int height) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color iconColor = c.getForeground();
                g2d.setColor(iconColor);
                
                switch (type) {
                    case "recommendation":
                        // AI/Light bulb icon
                        g2d.setStroke(new BasicStroke(1.5f));
                        // Light bulb body
                        g2d.drawOval(x + 3, y, width - 6, height - 8);
                        // Light bulb base
                        g2d.drawLine(x + width/2, y + height - 8, x + width/2, y + height - 2);
                        g2d.drawLine(x + width/2 - 3, y + height - 2, x + width/2 + 3, y + height - 2);
                        // Light rays
                        g2d.drawLine(x + width/2, y, x + width/2, y - 2);
                        g2d.drawLine(x + width - 2, y + height/2, x + width, y + height/2);
                        g2d.drawLine(x, y + height/2, x + 2, y + height/2);
                        break;
                        
                    case "holiday":
                        // Calendar icon
                        g2d.setStroke(new BasicStroke(1.5f));
                        // Calendar outline
                        g2d.drawRect(x + 2, y + 3, width - 4, height - 5);
                        // Calendar top tabs
                        g2d.drawLine(x + 6, y + 3, x + 6, y);
                        g2d.drawLine(x + width - 6, y + 3, x + width - 6, y);
                        // Calendar lines
                        g2d.drawLine(x + 2, y + 8, x + width - 2, y + 8);
                        // Mark a day
                        g2d.fillOval(x + width/2 - 2, y + height/2, 4, 4);
                        break;
                        
                    case "currency":
                        // Currency icon
                        // Yuan symbol
                        g2d.setStroke(new BasicStroke(1.8f));
                        g2d.drawLine(x + width/2, y + 2, x + width/2, y + height - 2);
                        g2d.drawLine(x + width/2 - 5, y + 5, x + width/2 + 5, y + 5);
                        g2d.drawLine(x + width/2 - 5, y + height/2, x + width/2 + 5, y + height/2);
                        break;
                        
                    case "transaction":
                        // Transaction list icon
                        g2d.setStroke(new BasicStroke(1.5f));
                        // List lines
                        g2d.drawLine(x + 4, y + 4, x + width - 4, y + 4);
                        g2d.drawLine(x + 4, y + height/2, x + width - 4, y + height/2);
                        g2d.drawLine(x + 4, y + height - 4, x + width - 4, y + height - 4);
                        // List bullet points
                        g2d.fillOval(x + 2, y + 2, 4, 4);
                        g2d.fillOval(x + 2, y + height/2 - 2, 4, 4);
                        g2d.fillOval(x + 2, y + height - 6, 4, 4);
                        break;
                }
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return width;
            }
            
            @Override
            public int getIconHeight() {
                return height;
            }
        };
    }
    
    /**
     * Create menu button
     */
    private JButton createMenuButton(String key, String text, Icon icon) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 45));
        button.setPreferredSize(new Dimension(200, 45));
        
        // Set style
        button.setFont(new Font("Dialog", Font.PLAIN, 14));
        button.setBackground(new Color(250, 250, 250));
        button.setForeground(SECONDARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add icon if provided
        if (icon != null) {
            button.setIcon(icon);
            button.setIconTextGap(10);
            button.setHorizontalAlignment(SwingConstants.LEFT);
        }
        
        // Custom button UI with rounded corners
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeMenuButton) {
                    button.setBackground(new Color(240, 240, 240));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeMenuButton) {
                    button.setBackground(new Color(250, 250, 250));
                }
            }
        });
        
        // Add click event
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update active button visual
                if (activeMenuButton != null) {
                    activeMenuButton.setBackground(new Color(250, 250, 250));
                    activeMenuButton.setForeground(SECONDARY_COLOR);
                }
                
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(Color.WHITE);
                activeMenuButton = button;
                
                // Add a simple slide animation effect
                showPanelWithAnimation(key);
                
                // Simulate updating status bar
                if (key.equals("recommendation")) {
                    lastRecommendationTime = LocalDateTime.now();
                    updateStatusBar();
                    // 自动更新推荐内容
                    if (recommendationPanel != null) {
                        recommendationPanel.onPanelShown();
                    }
                } else if (key.equals("currency")) {
                    updateStatusBar();
                }
            }
        });
        
        return button;
    }
    
    /**
     * Show panel with a simple slide animation
     */
    private void showPanelWithAnimation(String key) {
        // Simply show the panel for now (animation will be implemented in custom CardLayout)
        cardLayout.show(mainContentPanel, key);
    }
    
    /**
     * Create bottom status bar
     */
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        panel.setPreferredSize(new Dimension(WIDTH, 35));
        
        statusLabel = new JLabel(languageManager.getText(LanguageManager.READY) + " | " + 
                               languageManager.getText(LanguageManager.CURRENCY) + ": " + currentCurrency);
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(90, 100, 110));
        panel.add(statusLabel, BorderLayout.WEST);
        
        timeLabel = new JLabel(languageManager.getText(LanguageManager.LAST_RECOMMENDATION) + ": " + 
                             languageManager.getText(LanguageManager.NONE));
        timeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(90, 100, 110));
        panel.add(timeLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Update status bar information
     */
    private void updateStatusBar() {
        statusLabel.setText(languageManager.getText(LanguageManager.READY) + " | " + 
                          languageManager.getText(LanguageManager.CURRENCY) + ": " + currentCurrency);
        
        if (lastRecommendationTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            timeLabel.setText(languageManager.getText(LanguageManager.LAST_RECOMMENDATION) + ": " + 
                            lastRecommendationTime.format(formatter));
        } else {
            timeLabel.setText(languageManager.getText(LanguageManager.LAST_RECOMMENDATION) + ": " + 
                            languageManager.getText(LanguageManager.NONE));
        }
    }
    
    /**
     * Initialize functional panels
     */
    private void initFunctionalPanels() {
        try {
            // Create functional panels
            recommendationPanel = new RecommendationPanel(this);
            holidayPanel = new HolidayPanel(this);
            currencyPanel = new CurrencyPanel(this);
            transactionPanel = new TransactionPanel(this);
            
            // Add panels to card layout
            mainContentPanel.add(recommendationPanel, RECOMMENDATION_PANEL);
            mainContentPanel.add(holidayPanel, HOLIDAY_PANEL);
            mainContentPanel.add(currencyPanel, CURRENCY_PANEL);
            mainContentPanel.add(transactionPanel, TRANSACTION_PANEL);
            
            // Default to recommendation panel
            cardLayout.show(mainContentPanel, RECOMMENDATION_PANEL);
            
            System.out.println("All functional panels initialized");
        } catch (Exception e) {
            System.err.println("Error initializing functional panels: " + e.getMessage());
            e.printStackTrace();
            
            // Show error message dialog
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                    "Error initializing functional panels: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            });
        }
    }
    
    /**
     * Update currency setting
     */
    public void setCurrency(String currency) {
        this.currentCurrency = currency;
        updateStatusBar();
    }
    
    /**
     * Get current currency setting
     */
    public String getCurrentCurrency() {
        return this.currentCurrency;
    }
    
    /**
     * Get main content panel
     */
    public JPanel getMainContentPanel() {
        return this.mainContentPanel;
    }
    
    /**
     * Get language manager
     */
    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }
    
    /**
     * 创建支持Unicode文本的标签（解决中文乱码问题）
     */
    private JLabel createUnicodeLabel(String text, int style, int size) {
        JLabel label = new JLabel(text);
        
        // 尝试多种可能支持中文的字体
        Font font = null;
        
        // 可能支持中文的字体列表
        String[] fontNames = {
            Font.SANS_SERIF,         // 系统默认无衬线字体
            "Microsoft YaHei",       // 微软雅黑
            "WenQuanYi Micro Hei",   // 文泉驿微米黑
            "SimHei",                // 黑体
            "SimSun",                // 宋体
            "NSimSun",               // 新宋体
            "FangSong",              // 仿宋
            "KaiTi",                 // 楷体
            "STXihei",               // 华文细黑
            "STFangsong"             // 华文仿宋
        };
        
        // 尝试设置字体，直到找到支持的字体
        for (String fontName : fontNames) {
            try {
                font = new Font(fontName, style, size);
                // 简单测试是否支持中文
                if (font.canDisplayUpTo("中文测试") == -1) {
                    break; // 找到支持的字体
                }
            } catch (Exception e) {
                // 忽略异常，继续尝试下一个字体
            }
        }
        
        // 如果没有找到支持的字体，使用默认无衬线字体
        if (font == null) {
            font = new Font(Font.SANS_SERIF, style, size);
        }
        
        label.setFont(font);
        return label;
    }
    
    /**
     * Refresh menu button text based on current language
     */
    private void refreshMenuButtons() {
        for (Component comp : menuPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String key = button.getName();
                if (key != null && !key.isEmpty()) {
                    button.setText(languageManager.getText(key));
                }
            }
        }
    }
    
    /**
     * Main entry method
     */
    public static void main(String[] args) {
        // Start application in EDT thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainModuleUI();
            }
        });
    }
} 